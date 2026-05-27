package com.liyh.AncientWarcraft.event;

import com.liyh.AncientWarcraft.AncientWarcraft;
import com.liyh.AncientWarcraft.item.BlazeSwordItem;
import com.liyh.AncientWarcraft.item.EncapsulatedSoulItem;
import com.liyh.AncientWarcraft.item.MagmaSwordItem;
import com.liyh.AncientWarcraft.init.ModRegistries;
import com.liyh.AncientWarcraft.item.SoulBladeItem;
import com.liyh.AncientWarcraft.item.SwordOfCenturionItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AncientWarcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModGameEvents {
    private ModGameEvents() {}

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack crafted = event.getCrafting();
        if (!EncapsulatedSoulItem.isEncapsulatedSoul(crafted)) {
            return;
        }
        ItemStack fresh = EncapsulatedSoulItem.createFreshStack(event.getEntity().registryAccess());
        fresh.setCount(crafted.getCount());
        crafted.applyComponents(fresh.getComponentsPatch());
    }

    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!SwordOfCenturionItem.isSwordOfCenturion(event.getItem())) {
            return;
        }
        SwordOfCenturionItem.tryLaunchWitherSkull(player, player.level());
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!SwordOfCenturionItem.isSwordOfCenturion(event.getLeft())) {
            return;
        }
        ItemStack right = event.getRight();
        if (right.isEmpty() || right.is(ModRegistries.ENERGY_CELL.get())) {
            return;
        }
        event.setOutput(ItemStack.EMPTY);
        event.setCost(0);
    }

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
                || SoulBladeItem.isSoulBlade(stack)
                || EncapsulatedSoulItem.isEncapsulatedSoul(stack)
                || SwordOfCenturionItem.isSwordOfCenturion(stack);
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
        if (EncapsulatedSoulItem.needsDefaultEnchantments(stack, access)) {
            EncapsulatedSoulItem.applyDefaultEnchantments(stack, access);
        }
        if (SwordOfCenturionItem.needsDefaultEnchantments(stack, access)) {
            SwordOfCenturionItem.applyDefaultEnchantments(stack, access);
        }
    }
}
