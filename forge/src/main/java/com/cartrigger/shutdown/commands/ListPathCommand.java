package com.cartrigger.shutdown.commands;

import com.cartrigger.shutdown.forge.ShutdownModForge;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ListPathCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("listpath")
                .requires(source -> source.hasPermission(1)) // Require OP level 1
                .then(Commands.argument("directory", StringArgumentType.greedyString())
                        .executes(ListPathCommand::execute))
                .executes(ListPathCommand::executeCurrentDirectory)); // No argument = current directory
    }

    private static int executeCurrentDirectory(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String currentDir = System.getProperty("user.dir");
        return listDirectory(source, currentDir);
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String directoryPath = StringArgumentType.getString(context, "directory");
        return listDirectory(source, directoryPath);
    }

    private static int listDirectory(CommandSourceStack source, String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            File directory = path.toFile();
            
            if (!directory.exists()) {
                source.sendFailure(Component.literal("Directory does not exist: " + directoryPath));
                return 0;
            }
            
            if (!directory.isDirectory()) {
                source.sendFailure(Component.literal("Path is not a directory: " + directoryPath));
                return 0;
            }
            
            File[] files = directory.listFiles();
            if (files == null) {
                source.sendFailure(Component.literal("Cannot read directory: " + directoryPath));
                return 0;
            }
            
            source.sendSuccess(() -> Component.literal("§6=== Directory Listing: " + directoryPath + " ==="), false);
            source.sendSuccess(() -> Component.literal("§7Total items: " + files.length), false);
            source.sendSuccess(() -> Component.literal(""), false);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            for (File file : files) {
                try {
                    String name = file.getName();
                    String type = file.isDirectory() ? "§9[DIR]" : "§f[FILE]";
                    String size = file.isDirectory() ? "" : " (" + FileUtil.formatFileSize(file.length()) + ")";
                    String lastModified = dateFormat.format(new Date(file.lastModified()));
                    
                    String permissions = "";
                    if (file.canRead()) permissions += "r";
                    if (file.canWrite()) permissions += "w";
                    if (file.canExecute()) permissions += "x";
                    
                    source.sendSuccess(() -> Component.literal(
                        type + " §f" + name + size + " §7[" + permissions + "] " + lastModified
                    ), false);
                    
                } catch (Exception e) {
                    source.sendSuccess(() -> Component.literal("§c[ERROR] " + file.getName() + " - " + e.getMessage()), false);
                }
            }
            
            source.sendSuccess(() -> Component.literal(""), false);
            source.sendSuccess(() -> Component.literal("§6=== End of listing ==="), false);
            
            ShutdownModForge.LOGGER.info("Directory listed: " + directoryPath + " by " + source.getTextName());
            
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to list directory: " + e.getMessage()));
            ShutdownModForge.LOGGER.error("Error listing directory: " + directoryPath + " - " + e.getMessage());
            return 0;
        }
        
        return 1;
    }
}