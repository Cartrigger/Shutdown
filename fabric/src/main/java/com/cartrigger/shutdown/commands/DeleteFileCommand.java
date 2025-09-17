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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DeleteFileCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("deletefile")
                .requires(source -> source.hasPermissionLevel(2)) // Require OP level 2
                .then(CommandManager.argument("path", StringArgumentType.greedyString())
                        .executes(DeleteFileCommand::execute)));
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String filePath = StringArgumentType.getString(context, "path");
        
        try {
            Path path = Paths.get(filePath);
            File file = path.toFile();
            
            if (!file.exists()) {
                source.sendError(Text.literal("File or directory does not exist: " + filePath));
                return 0;
            }
            
            // Security check - prevent deletion of critical system files
            String absolutePath = file.getAbsolutePath().toLowerCase();
            if (FileUtil.isCriticalPath(absolutePath)) {
                source.sendError(Text.literal("Cannot delete critical system files or directories"));
                ShutdownModFabric.LOGGER.warn("Attempted to delete critical path: " + absolutePath + " by " + source.getName());
                return 0;
            }
            
            if (file.isDirectory()) {
                // Delete directory and all contents
                FileUtil.deleteDirectory(file);
                source.sendFeedback(() -> Text.literal("Successfully deleted directory: " + filePath), true);
            } else {
                // Delete single file
                Files.delete(path);
                source.sendFeedback(() -> Text.literal("Successfully deleted file: " + filePath), true);
            }
            
            ShutdownModFabric.LOGGER.info("File/directory deleted: " + filePath + " by " + source.getName());
            
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to delete: " + e.getMessage()));
            ShutdownModFabric.LOGGER.error("Error deleting file: " + filePath + " - " + e.getMessage());
            return 0;
        }
        
        return 1;
    }
}