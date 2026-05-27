package com.liyh.AncientWarcraft.item;

import com.liyh.AncientWarcraft.init.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class BlazeSwordItem extends SwordItem {
    private static final int DEFAULT_FIRE_ASPECT_LEVEL = 2;
    private static final int DEFAULT_SWEEPING_EDGE_LEVEL = 2;

    public BlazeSwordItem() {
        super(Tiers.DIAMOND, new net.minecraft.world.item.Item.Properties());
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(Items.BLAZE_ROD);
    }

    public static void applyDefaultEnchantments(ItemStack stack, RegistryAccess access) {
        if (!needsDefaultEnchantments(stack, access)) {
            return;
        }
        Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            mutable.set(enchantmentRegistry.getHolderOrThrow(Enchantments.FIRE_ASPECT), DEFAULT_FIRE_ASPECT_LEVEL);
            mutable.set(enchantmentRegistry.getHolderOrThrow(Enchantments.SWEEPING_EDGE), DEFAULT_SWEEPING_EDGE_LEVEL);
        });
    }

    public static boolean needsDefaultEnchantments(ItemStack stack, RegistryAccess access) {
        if (!isBlazeSword(stack) || stack.isEmpty()) {
            return false;
        }
        if (access.registry(Registries.ENCHANTMENT).isEmpty()) {
            return false;
        }
        Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> fireAspect = enchantmentRegistry.getHolderOrThrow(Enchantments.FIRE_ASPECT);
        Holder<Enchantment> sweepingEdge = enchantmentRegistry.getHolderOrThrow(Enchantments.SWEEPING_EDGE);
        return EnchantmentHelper.getItemEnchantmentLevel(fireAspect, stack) < DEFAULT_FIRE_ASPECT_LEVEL
                || EnchantmentHelper.getItemEnchantmentLevel(sweepingEdge, stack) < DEFAULT_SWEEPING_EDGE_LEVEL;
    }

    public static boolean isBlazeSword(ItemStack stack) {
        return stack.is(ModRegistries.BLAZE_SWORD.get());
    }
}
