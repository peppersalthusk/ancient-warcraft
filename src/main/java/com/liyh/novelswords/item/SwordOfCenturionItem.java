package com.liyh.AncientWarcraft.item;

import com.liyh.AncientWarcraft.entity.CenturionWitherSkull;
import com.liyh.AncientWarcraft.init.ModRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
public class SwordOfCenturionItem extends SwordItem {
  private static final int MAX_DURABILITY = 10_000;
  public static final int CHARGE_DURATION_TICKS = 30;
  public static final int CHARGE_TOLERANCE_TICKS = 2;
  private static final float SKULL_SPEED = 1.6F;

  private static final int KNOCKBACK_LEVEL = 5;
  private static final int SHARPNESS_LEVEL = 10;
  private static final int FIRE_ASPECT_LEVEL = 2;
  private static final int SWEEPING_EDGE_LEVEL = 3;
  private static final int LOOTING_LEVEL = 2;
  private static final int MENDING_LEVEL = 1;

  public SwordOfCenturionItem() {
    super(ModTiers.SOUL, new Properties()
        .durability(MAX_DURABILITY)
        .component(DataComponents.LORE, new ItemLore(List.of(
            Component.translatable("item.ancient_warcraft.sword_of_centurion.description")
                .withStyle(ChatFormatting.GRAY)))));
  }

  @Override
  public boolean isEnchantable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
    return repairCandidate.is(ModRegistries.ENERGY_CELL.get());
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (!hasPurifiedSoul(player)) {
      return InteractionResultHolder.fail(stack);
    }
    player.startUsingItem(hand);
    return InteractionResultHolder.consume(stack);
  }

  @Override
  public int getUseDuration(ItemStack stack, LivingEntity entity) {
    return CHARGE_DURATION_TICKS;
  }

  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    return UseAnim.BOW;
  }

  @Override
  public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
    if (!(entity instanceof Player player)) {
      return;
    }
    if (timeLeft > CHARGE_TOLERANCE_TICKS) {
      return;
    }
    tryLaunchWitherSkull(player, level);
  }

  public static void tryLaunchWitherSkull(Player player, Level level) {
    if (level.isClientSide) {
      return;
    }
    if (!consumePurifiedSoul(player)) {
      return;
    }

    ServerLevel serverLevel = (ServerLevel) level;
    CenturionWitherSkull skull = ModRegistries.CENTURION_WITHER_SKULL.get().create(serverLevel);
    if (skull == null) {
      return;
    }
    skull.setOwner(player);
    skull.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
    skull.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, SKULL_SPEED, 0.0F);
    serverLevel.addFreshEntity(skull);
  }

  public static void applyDefaultEnchantments(ItemStack stack, RegistryAccess access) {
    if (access.registry(Registries.ENCHANTMENT).isEmpty()) {
      return;
    }
    if (!needsDefaultEnchantments(stack, access)) {
      return;
    }
    Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
    EnchantmentHelper.updateEnchantments(
        stack,
        mutable -> {
          mutable.set(
              enchantmentRegistry.getHolderOrThrow(Enchantments.KNOCKBACK), KNOCKBACK_LEVEL);
          mutable.set(
              enchantmentRegistry.getHolderOrThrow(Enchantments.SHARPNESS), SHARPNESS_LEVEL);
          mutable.set(
              enchantmentRegistry.getHolderOrThrow(Enchantments.FIRE_ASPECT), FIRE_ASPECT_LEVEL);
          mutable.set(
              enchantmentRegistry.getHolderOrThrow(Enchantments.SWEEPING_EDGE),
              SWEEPING_EDGE_LEVEL);
          mutable.set(
              enchantmentRegistry.getHolderOrThrow(Enchantments.LOOTING), LOOTING_LEVEL);
          mutable.set(
              enchantmentRegistry.getHolderOrThrow(Enchantments.MENDING), MENDING_LEVEL);
        });
  }

  public static boolean needsDefaultEnchantments(ItemStack stack, RegistryAccess access) {
    if (!isSwordOfCenturion(stack) || stack.isEmpty()) {
      return false;
    }
    if (access.registry(Registries.ENCHANTMENT).isEmpty()) {
      return false;
    }
    Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
    return enchantmentBelowLevel(
            enchantmentRegistry, stack, Enchantments.KNOCKBACK, KNOCKBACK_LEVEL)
        || enchantmentBelowLevel(
            enchantmentRegistry, stack, Enchantments.SHARPNESS, SHARPNESS_LEVEL)
        || enchantmentBelowLevel(
            enchantmentRegistry, stack, Enchantments.FIRE_ASPECT, FIRE_ASPECT_LEVEL)
        || enchantmentBelowLevel(
            enchantmentRegistry, stack, Enchantments.SWEEPING_EDGE, SWEEPING_EDGE_LEVEL)
        || enchantmentBelowLevel(
            enchantmentRegistry, stack, Enchantments.LOOTING, LOOTING_LEVEL)
        || enchantmentBelowLevel(
            enchantmentRegistry, stack, Enchantments.MENDING, MENDING_LEVEL);
  }

  private static boolean enchantmentBelowLevel(
      Registry<Enchantment> registry,
      ItemStack stack,
      net.minecraft.resources.ResourceKey<Enchantment> key,
      int requiredLevel) {
    Holder<Enchantment> holder = registry.getHolderOrThrow(key);
    return EnchantmentHelper.getItemEnchantmentLevel(holder, stack) < requiredLevel;
  }

  public static boolean isSwordOfCenturion(ItemStack stack) {
    return stack.is(ModRegistries.SWORD_OF_CENTURION.get());
  }

  private static boolean hasPurifiedSoul(Player player) {
    return player.getInventory().contains(ModRegistries.PURIFIED_SOUL.get().getDefaultInstance());
  }

  private static boolean consumePurifiedSoul(Player player) {
    if (player.getAbilities().instabuild) {
      return true;
    }
    for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
      ItemStack stack = player.getInventory().getItem(slot);
      if (stack.is(ModRegistries.PURIFIED_SOUL.get())) {
        stack.shrink(1);
        return true;
      }
    }
    return false;
  }
}
