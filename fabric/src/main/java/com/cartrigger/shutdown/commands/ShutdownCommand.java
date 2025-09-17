package com.cartrigger.shutdown.commands;

import com.cartrigger.shutdown.fabric.ShutdownModFabric;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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
            
            ShutdownUtil.executeShutdown();
            
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to execute shutdown: " + e.getMessage()));
            ShutdownModFabric.LOGGER.error("Error in shutdown command: " + e.getMessage());
            return 0;
        }
        
        return 1;
    }
}