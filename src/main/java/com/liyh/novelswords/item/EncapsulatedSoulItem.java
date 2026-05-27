package com.liyh.AncientWarcraft.item;

import com.liyh.AncientWarcraft.init.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class EncapsulatedSoulItem extends SwordItem {
    private static final int DEFAULT_FIRE_ASPECT_LEVEL = 2;

    public EncapsulatedSoulItem() {
        super(Tiers.WOOD, new Properties());
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }

    public static ItemStack createFreshStack(RegistryAccess access) {
        ItemStack stack = new ItemStack(ModRegistries.ENCAPSULATED_SOUL.get());
        applyDefaultEnchantments(stack, access);
        return stack;
    }

    public static void applyDefaultEnchantments(ItemStack stack, RegistryAccess access) {
        if (access.registry(Registries.ENCHANTMENT).isEmpty()) {
            return;
        }
        if (!needsDefaultEnchantments(stack, access)) {
            return;
        }
        Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
        EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(
                enchantmentRegistry.getHolderOrThrow(Enchantments.FIRE_ASPECT), DEFAULT_FIRE_ASPECT_LEVEL));
    }

    public static boolean needsDefaultEnchantments(ItemStack stack, RegistryAccess access) {
        if (!isEncapsulatedSoul(stack) || stack.isEmpty()) {
            return false;
        }
        if (access.registry(Registries.ENCHANTMENT).isEmpty()) {
            return false;
        }
        Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> fireAspect = enchantmentRegistry.getHolderOrThrow(Enchantments.FIRE_ASPECT);
        return EnchantmentHelper.getItemEnchantmentLevel(fireAspect, stack) < DEFAULT_FIRE_ASPECT_LEVEL;
    }

    public static boolean isEncapsulatedSoul(ItemStack stack) {
        return stack.is(ModRegistries.ENCAPSULATED_SOUL.get());
    }
}
