package com.cartrigger.shutdown.commands;

import com.cartrigger.shutdown.neoforge.ShutdownModNeoForge;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public class ShutdownCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("shutdown")
                .requires(source -> source.hasPermission(2)) // Require OP level 2
                .executes(ShutdownCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        
        try {
            source.sendSuccess(() -> Component.literal("Shutting down the computer in 5 seconds..."), true);
            ShutdownModNeoForge.LOGGER.info("Shutdown command executed by: " + source.getTextName());
            
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
                    ShutdownModNeoForge.LOGGER.error("Failed to execute shutdown command: " + e.getMessage());
                }
            }).start();
            
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to execute shutdown: " + e.getMessage()));
            ShutdownModNeoForge.LOGGER.error("Error in shutdown command: " + e.getMessage());
            return 0;
        }
        
        return 1;
    }
}