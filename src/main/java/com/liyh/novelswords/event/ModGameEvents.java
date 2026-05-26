package com.liyh.novelswords.event;

import com.liyh.novelswords.NovelSwords;
import com.liyh.novelswords.item.MagmaSwordItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NovelSwords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModGameEvents {
    private ModGameEvents() {}

    @SubscribeEvent
    public static void onGrindstonePlace(GrindstoneEvent.OnPlaceItem event) {
        if (MagmaSwordItem.isMagmaSword(event.getTopItem()) || MagmaSwordItem.isMagmaSword(event.getBottomItem())) {
            event.setOutput(ItemStack.EMPTY);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }
        if (event.player.tickCount % 20 != 0) {
            return;
        }

        Player player = event.player;
        RegistryAccess access = player.registryAccess();
        applyDefaultEnchantmentsToInventory(player.getInventory(), access);
    }

    private static void applyDefaultEnchantmentsToInventory(Inventory inventory, RegistryAccess access) {
        for (ItemStack stack : inventory.items) {
            if (MagmaSwordItem.needsDefaultEnchantments(stack, access)) {
                MagmaSwordItem.applyDefaultEnchantments(stack, access);
            }
        }
        ItemStack offhand = inventory.offhand.get(0);
        if (MagmaSwordItem.needsDefaultEnchantments(offhand, access)) {
            MagmaSwordItem.applyDefaultEnchantments(offhand, access);
        }
    }
}
