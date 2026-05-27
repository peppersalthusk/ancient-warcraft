package com.liyh.AncientWarcraft.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public final class ModTiers {
  /** Sword baseline (+4) + 38 bonus = 42 attack damage. */
  public static final Tier SOUL = new Tier() {
    @Override
    public int getUses() {
      return Tiers.NETHERITE.getUses();
    }

    @Override
    public float getSpeed() {
      return 10.0F;
    }

    @Override
    public float getAttackDamageBonus() {
      return 38.0F;
    }

    @Override
    public int getEnchantmentValue() {
      return 20;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
      return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
    }

    @Override
    public Ingredient getRepairIngredient() {
      return Ingredient.EMPTY;
    }
  };

  private ModTiers() {}
}
