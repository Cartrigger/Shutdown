package com.cartrigger.shutdown.forge;

import com.cartrigger.shutdown.commands.DeleteFileCommand;
import com.cartrigger.shutdown.commands.ListPathCommand;
import com.cartrigger.shutdown.commands.ShutdownCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("shutdown")
public class ShutdownModForge {
    public static final String MOD_ID = "shutdown";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ShutdownModForge() {
        LOGGER.info("Shutdown Mod (Forge) initialized!");
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