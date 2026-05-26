package com.liyh.novelswords.item;

import com.liyh.novelswords.init.ModRegistries;
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

public class MagmaSwordItem extends SwordItem {
    public MagmaSwordItem() {
        super(Tiers.STONE, new net.minecraft.world.item.Item.Properties());
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(Items.MAGMA_BLOCK);
    }

    @Override
    public int getEnchantmentValue() {
        return Tiers.STONE.getEnchantmentValue();
    }

    public static void applyDefaultEnchantments(ItemStack stack, RegistryAccess access) {
        if (!needsDefaultEnchantments(stack, access)) {
            return;
        }
        Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            mutable.set(enchantmentRegistry.getHolderOrThrow(Enchantments.FIRE_ASPECT), 1);
            mutable.set(enchantmentRegistry.getHolderOrThrow(Enchantments.SWEEPING_EDGE), 1);
        });
    }

    public static boolean needsDefaultEnchantments(ItemStack stack, RegistryAccess access) {
        if (!isMagmaSword(stack) || stack.isEmpty()) {
            return false;
        }
        if (access.registry(Registries.ENCHANTMENT).isEmpty()) {
            return false;
        }
        Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> fireAspect = enchantmentRegistry.getHolderOrThrow(Enchantments.FIRE_ASPECT);
        Holder<Enchantment> sweepingEdge = enchantmentRegistry.getHolderOrThrow(Enchantments.SWEEPING_EDGE);
        return EnchantmentHelper.getItemEnchantmentLevel(fireAspect, stack) < 1
                || EnchantmentHelper.getItemEnchantmentLevel(sweepingEdge, stack) < 1;
    }

    public static boolean isMagmaSword(ItemStack stack) {
        return stack.is(ModRegistries.MAGMA_SWORD.get());
    }
}
