package com.cartrigger.shutdown.fabric;

import com.cartrigger.shutdown.commands.DeleteFileCommand;
import com.cartrigger.shutdown.commands.ListPathCommand;
import com.cartrigger.shutdown.commands.ShutdownCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownModFabric implements ModInitializer {
    public static final String MOD_ID = "shutdown";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Shutdown Mod (Fabric) initialized!");
        
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ShutdownCommand.register(dispatcher);
            DeleteFileCommand.register(dispatcher);
            ListPathCommand.register(dispatcher);
        });
    }
}