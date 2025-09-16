package com.cartrigger.shutdown.neoforge;

import com.cartrigger.shutdown.commands.DeleteFileCommand;
import com.cartrigger.shutdown.commands.ListPathCommand;
import com.cartrigger.shutdown.commands.ShutdownCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("shutdown")
public class ShutdownModNeoForge {
    public static final String MOD_ID = "shutdown";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ShutdownModNeoForge() {
        LOGGER.info("Shutdown Mod (NeoForge) initialized!");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class CommandRegistry {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            ShutdownCommand.register(event.getDispatcher());
            DeleteFileCommand.register(event.getDispatcher());
            ListPathCommand.register(event.getDispatcher());
        }
    }
}