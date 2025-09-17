package com.cartrigger.shutdown.commands;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    
    public static boolean isCriticalPath(String path) {
        // List of critical paths that should not be deleted
        String[] criticalPaths = {
            "/windows", "/system32", "/boot", "/etc", "/usr", "/bin", "/sbin",
            "/lib", "/lib64", "/var", "/proc", "/sys", "/dev", "/root",
            "c:\\windows", "c:\\system32", "c:\\boot", "c:\\program files",
            "/applications", "/library", "/system"
        };
        
        for (String critical : criticalPaths) {
            if (path.startsWith(critical)) {
                return true;
            }
        }
        return false;
    }
    
    public static void deleteDirectory(File directory) throws Exception {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    Files.delete(file.toPath());
                }
            }
        }
        Files.delete(directory.toPath());
    }
    
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}