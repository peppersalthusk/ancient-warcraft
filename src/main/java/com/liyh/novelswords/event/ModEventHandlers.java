package com.liyh.novelswords.event;

import com.liyh.novelswords.NovelSwords;
import com.liyh.novelswords.init.ModRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = NovelSwords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEventHandlers {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ModEventHandlers() {}

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("{} common setup", NovelSwords.MOD_ID);
    }

    @SubscribeEvent
    public static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModRegistries.MAGMA_SWORD.get());
        }
    }
}
