package com.cartrigger.shutdown.forge;

import com.cartrigger.shutdown.ShutdownMod;
import net.minecraftforge.fml.common.Mod;

@Mod(ShutdownMod.MOD_ID)
public class ShutdownModForge {
    public ShutdownModForge() {
        ShutdownMod.init();
    }
}