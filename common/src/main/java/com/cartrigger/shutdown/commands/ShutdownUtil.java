package com.cartrigger.shutdown.commands;

import java.io.IOException;

public class ShutdownUtil {
    
    public static void executeShutdown() {
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
                System.err.println("Failed to execute shutdown command: " + e.getMessage());
            }
        }).start();
    }
}