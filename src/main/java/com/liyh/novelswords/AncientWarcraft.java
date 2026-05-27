package com.liyh.AncientWarcraft;

import com.liyh.AncientWarcraft.init.ModRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AncientWarcraft.MOD_ID)
public class AncientWarcraft {
    public static final String MOD_ID = "ancient_warcraft";

    public AncientWarcraft(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ModRegistries.register(modBus);
    }
}
