package com.cartrigger.shutdown;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import com.cartrigger.shutdown.commands.DeleteFileCommand;
import com.cartrigger.shutdown.commands.ListPathCommand;
import com.cartrigger.shutdown.commands.ShutdownCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownMod {
    public static final String MOD_ID = "shutdown";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Shutdown Mod initialized!");
        
        // Register commands
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, environment) -> {
            ShutdownCommand.register(dispatcher);
            DeleteFileCommand.register(dispatcher);
            ListPathCommand.register(dispatcher);
        });
    }
}