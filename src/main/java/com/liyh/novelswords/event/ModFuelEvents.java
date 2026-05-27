package com.liyh.AncientWarcraft.event;

import com.liyh.AncientWarcraft.AncientWarcraft;
import com.liyh.AncientWarcraft.init.ModRegistries;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AncientWarcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModFuelEvents {
    public static final int ANCIENT_CORE_BURN_TIME = 2742 * 20;

    private ModFuelEvents() {}

    @SubscribeEvent
    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().is(ModRegistries.ANCIENT_CORE.get())) {
            event.setBurnTime(ANCIENT_CORE_BURN_TIME);
        }
    }
}
