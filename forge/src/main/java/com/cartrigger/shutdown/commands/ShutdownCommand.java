package com.cartrigger.shutdown.commands;

import com.cartrigger.shutdown.forge.ShutdownModForge;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

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
            ShutdownModForge.LOGGER.info("Shutdown command executed by: " + source.getTextName());
            
            ShutdownUtil.executeShutdown();
            
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to execute shutdown: " + e.getMessage()));
            ShutdownModForge.LOGGER.error("Error in shutdown command: " + e.getMessage());
            return 0;
        }
        
        return 1;
    }
}