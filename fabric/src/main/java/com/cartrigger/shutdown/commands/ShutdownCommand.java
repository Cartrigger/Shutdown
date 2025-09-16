package com.cartrigger.shutdown.commands;

import com.cartrigger.shutdown.fabric.ShutdownModFabric;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.IOException;

public class ShutdownCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("shutdown")
                .requires(source -> source.hasPermissionLevel(2)) // Require OP level 2
                .executes(ShutdownCommand::execute));
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        
        try {
            source.sendFeedback(() -> Text.literal("Shutting down the computer in 5 seconds..."), true);
            ShutdownModFabric.LOGGER.info("Shutdown command executed by: " + source.getName());
            
            // Schedule shutdown in a separate thread to avoid blocking
            new Thread(() -> {
                try {
                    Thread.sleep(5000); // 5 second delay
                    String os = System.getProperty("os.name").toLowerCase();
                    
                    ProcessBuilder processBuilder;
                    if (os.contains("win")) {
                        // Windows shutdown command
                        processBuilder = new ProcessBuilder("shutdown", "/s", "/t", "0");
                    } else if (os.contains("mac")) {
                        // macOS shutdown command
                        processBuilder = new ProcessBuilder("sudo", "shutdown", "-h", "now");
                    } else {
                        // Linux/Unix shutdown command
                        processBuilder = new ProcessBuilder("sudo", "shutdown", "-h", "now");
                    }
                    
                    Process process = processBuilder.start();
                    process.waitFor();
                    
                } catch (IOException | InterruptedException e) {
                    ShutdownModFabric.LOGGER.error("Failed to execute shutdown command: " + e.getMessage());
                }
            }).start();
            
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to execute shutdown: " + e.getMessage()));
            ShutdownModFabric.LOGGER.error("Error in shutdown command: " + e.getMessage());
            return 0;
        }
        
        return 1;
    }
}