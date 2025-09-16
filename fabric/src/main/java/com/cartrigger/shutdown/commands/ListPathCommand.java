package com.cartrigger.shutdown.commands;

import com.cartrigger.shutdown.fabric.ShutdownModFabric;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ListPathCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("listpath")
                .requires(source -> source.hasPermissionLevel(1)) // Require OP level 1
                .then(CommandManager.argument("directory", StringArgumentType.greedyString())
                        .executes(ListPathCommand::execute))
                .executes(ListPathCommand::executeCurrentDirectory)); // No argument = current directory
    }

    private static int executeCurrentDirectory(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String currentDir = System.getProperty("user.dir");
        return listDirectory(source, currentDir);
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String directoryPath = StringArgumentType.getString(context, "directory");
        return listDirectory(source, directoryPath);
    }

    private static int listDirectory(ServerCommandSource source, String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            File directory = path.toFile();
            
            if (!directory.exists()) {
                source.sendError(Text.literal("Directory does not exist: " + directoryPath));
                return 0;
            }
            
            if (!directory.isDirectory()) {
                source.sendError(Text.literal("Path is not a directory: " + directoryPath));
                return 0;
            }
            
            File[] files = directory.listFiles();
            if (files == null) {
                source.sendError(Text.literal("Cannot read directory: " + directoryPath));
                return 0;
            }
            
            source.sendFeedback(() -> Text.literal("§6=== Directory Listing: " + directoryPath + " ==="), false);
            source.sendFeedback(() -> Text.literal("§7Total items: " + files.length), false);
            source.sendFeedback(() -> Text.literal(""), false);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            for (File file : files) {
                try {
                    String name = file.getName();
                    String type = file.isDirectory() ? "§9[DIR]" : "§f[FILE]";
                    String size = file.isDirectory() ? "" : " (" + formatFileSize(file.length()) + ")";
                    String lastModified = dateFormat.format(new Date(file.lastModified()));
                    
                    String permissions = "";
                    if (file.canRead()) permissions += "r";
                    if (file.canWrite()) permissions += "w";
                    if (file.canExecute()) permissions += "x";
                    
                    source.sendFeedback(() -> Text.literal(
                        type + " §f" + name + size + " §7[" + permissions + "] " + lastModified
                    ), false);
                    
                } catch (Exception e) {
                    source.sendFeedback(() -> Text.literal("§c[ERROR] " + file.getName() + " - " + e.getMessage()), false);
                }
            }
            
            source.sendFeedback(() -> Text.literal(""), false);
            source.sendFeedback(() -> Text.literal("§6=== End of listing ==="), false);
            
            ShutdownModFabric.LOGGER.info("Directory listed: " + directoryPath + " by " + source.getName());
            
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to list directory: " + e.getMessage()));
            ShutdownModFabric.LOGGER.error("Error listing directory: " + directoryPath + " - " + e.getMessage());
            return 0;
        }
        
        return 1;
    }
    
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}