package com.liyh.novelswords;

import com.liyh.novelswords.init.ModRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NovelSwords.MOD_ID)
public class NovelSwords {
    public static final String MOD_ID = "novel_swords";

    public NovelSwords(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ModRegistries.register(modBus);
    }
}
