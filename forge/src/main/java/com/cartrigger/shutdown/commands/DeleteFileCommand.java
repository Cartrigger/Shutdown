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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DeleteFileCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("deletefile")
                .requires(source -> source.hasPermission(2)) // Require OP level 2
                .then(Commands.argument("path", StringArgumentType.greedyString())
                        .executes(DeleteFileCommand::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String filePath = StringArgumentType.getString(context, "path");
        
        try {
            Path path = Paths.get(filePath);
            File file = path.toFile();
            
            if (!file.exists()) {
                source.sendFailure(Component.literal("File or directory does not exist: " + filePath));
                return 0;
            }
            
            // Security check - prevent deletion of critical system files
            String absolutePath = file.getAbsolutePath().toLowerCase();
            if (FileUtil.isCriticalPath(absolutePath)) {
                source.sendFailure(Component.literal("Cannot delete critical system files or directories"));
                ShutdownModForge.LOGGER.warn("Attempted to delete critical path: " + absolutePath + " by " + source.getTextName());
                return 0;
            }
            
            if (file.isDirectory()) {
                // Delete directory and all contents
                FileUtil.deleteDirectory(file);
                source.sendSuccess(() -> Component.literal("Successfully deleted directory: " + filePath), true);
            } else {
                // Delete single file
                Files.delete(path);
                source.sendSuccess(() -> Component.literal("Successfully deleted file: " + filePath), true);
            }
            
            ShutdownModForge.LOGGER.info("File/directory deleted: " + filePath + " by " + source.getTextName());
            
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to delete: " + e.getMessage()));
            ShutdownModForge.LOGGER.error("Error deleting file: " + filePath + " - " + e.getMessage());
            return 0;
        }
        
        return 1;
    }
}