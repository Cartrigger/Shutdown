package com.cartrigger.shutdown.fabric;

import com.cartrigger.shutdown.ShutdownMod;
import net.fabricmc.api.ModInitializer;

public class ShutdownModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ShutdownMod.init();
    }
}