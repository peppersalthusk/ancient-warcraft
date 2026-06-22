package com.liyh.AncientWarcraft.client;

import com.liyh.AncientWarcraft.AncientWarcraft;
import com.liyh.AncientWarcraft.init.ModRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = AncientWarcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ClientModEvents() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("{} client setup", AncientWarcraft.MOD_ID);
        event.enqueueWork(() -> {
            EntityRenderers.register(ModRegistries.SOUL_FIREBALL.get(), ThrownItemRenderer::new);
            EntityRenderers.register(
                    ModRegistries.CENTURION_WITHER_SKULL.get(), WitherSkullRenderer::new);
        });
    }
}
