package com.liyh.AncientWarcraft.event;

import com.liyh.AncientWarcraft.AncientWarcraft;
import com.liyh.AncientWarcraft.item.BlazeSwordItem;
import com.liyh.AncientWarcraft.item.MagmaSwordItem;
import com.liyh.AncientWarcraft.item.SoulBladeItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AncientWarcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModGameEvents {
    private ModGameEvents() {}

    @SubscribeEvent
    public static void onGrindstonePlace(GrindstoneEvent.OnPlaceItem event) {
        if (isProtectedSword(event.getTopItem()) || isProtectedSword(event.getBottomItem())) {
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

    private static boolean isProtectedSword(ItemStack stack) {
        return MagmaSwordItem.isMagmaSword(stack)
                || BlazeSwordItem.isBlazeSword(stack)
                || SoulBladeItem.isSoulBlade(stack);
    }

    private static void applyDefaultEnchantmentsToInventory(Inventory inventory, RegistryAccess access) {
        for (ItemStack stack : inventory.items) {
            applyDefaultEnchantmentsIfNeeded(stack, access);
        }
        applyDefaultEnchantmentsIfNeeded(inventory.offhand.get(0), access);
    }

    private static void applyDefaultEnchantmentsIfNeeded(ItemStack stack, RegistryAccess access) {
        if (MagmaSwordItem.needsDefaultEnchantments(stack, access)) {
            MagmaSwordItem.applyDefaultEnchantments(stack, access);
        }
        if (BlazeSwordItem.needsDefaultEnchantments(stack, access)) {
            BlazeSwordItem.applyDefaultEnchantments(stack, access);
        }
        if (SoulBladeItem.needsDefaultEnchantments(stack, access)) {
            SoulBladeItem.applyDefaultEnchantments(stack, access);
        }
    }
}
