package com.liyh.AncientWarcraft.item;

import com.liyh.AncientWarcraft.init.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class SoulBladeItem extends SwordItem {
    private static final int MAX_DURABILITY = 2000;

    /** Sword baseline (+3) + tier bonus (+5) = +8 attack → displayed damage 9. */
    private static final Tier SOUL_BLADE_TIER = new Tier() {
        @Override
        public int getUses() {
            return MAX_DURABILITY;
        }

        @Override
        public float getSpeed() {
            return Tiers.DIAMOND.getSpeed();
        }

        @Override
        public float getAttackDamageBonus() {
            return 5.0F;
        }

        @Override
        public int getEnchantmentValue() {
            return Tiers.DIAMOND.getEnchantmentValue();
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };
    private static final int DEFAULT_FIRE_ASPECT_LEVEL = 2;
    private static final int DEFAULT_SWEEPING_EDGE_LEVEL = 3;
    private static final int DEFAULT_SMITE_LEVEL = 3;
    private static final int DEFAULT_KNOCKBACK_LEVEL = 1;

    public SoulBladeItem() {
        super(SOUL_BLADE_TIER, new net.minecraft.world.item.Item.Properties().durability(MAX_DURABILITY));
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(Items.ECHO_SHARD);
    }

    public static void applyDefaultEnchantments(ItemStack stack, RegistryAccess access) {
        if (access.registry(Registries.ENCHANTMENT).isEmpty()) {
            return;
        }
        if (!needsDefaultEnchantments(stack, access)) {
            return;
        }
        Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            mutable.set(enchantmentRegistry.getHolderOrThrow(Enchantments.FIRE_ASPECT), DEFAULT_FIRE_ASPECT_LEVEL);
            mutable.set(enchantmentRegistry.getHolderOrThrow(Enchantments.SWEEPING_EDGE), DEFAULT_SWEEPING_EDGE_LEVEL);
            mutable.set(enchantmentRegistry.getHolderOrThrow(Enchantments.SMITE), DEFAULT_SMITE_LEVEL);
            mutable.set(enchantmentRegistry.getHolderOrThrow(Enchantments.KNOCKBACK), DEFAULT_KNOCKBACK_LEVEL);
        });
    }

    public static boolean needsDefaultEnchantments(ItemStack stack, RegistryAccess access) {
        if (!isSoulBlade(stack) || stack.isEmpty()) {
            return false;
        }
        if (access.registry(Registries.ENCHANTMENT).isEmpty()) {
            return false;
        }
        Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> fireAspect = enchantmentRegistry.getHolderOrThrow(Enchantments.FIRE_ASPECT);
        Holder<Enchantment> sweepingEdge = enchantmentRegistry.getHolderOrThrow(Enchantments.SWEEPING_EDGE);
        Holder<Enchantment> smite = enchantmentRegistry.getHolderOrThrow(Enchantments.SMITE);
        Holder<Enchantment> knockback = enchantmentRegistry.getHolderOrThrow(Enchantments.KNOCKBACK);
        return EnchantmentHelper.getItemEnchantmentLevel(fireAspect, stack) < DEFAULT_FIRE_ASPECT_LEVEL
                || EnchantmentHelper.getItemEnchantmentLevel(sweepingEdge, stack) < DEFAULT_SWEEPING_EDGE_LEVEL
                || EnchantmentHelper.getItemEnchantmentLevel(smite, stack) < DEFAULT_SMITE_LEVEL
                || EnchantmentHelper.getItemEnchantmentLevel(knockback, stack) < DEFAULT_KNOCKBACK_LEVEL;
    }

    public static boolean isSoulBlade(ItemStack stack) {
        return stack.is(ModRegistries.SOUL_BLADE.get());
    }
}
