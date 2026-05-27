package com.liyh.AncientWarcraft.entity;

import com.liyh.AncientWarcraft.init.ModRegistries;
import com.liyh.AncientWarcraft.util.SoulFireHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SoulFireball extends ThrowableItemProjectile {
    public SoulFireball(EntityType<? extends SoulFireball> type, Level level) {
        super(type, level);
    }

    public SoulFireball(Level level, double x, double y, double z, Vec3 velocity) {
        super(ModRegistries.SOUL_FIREBALL.get(), x, y, z, level);
        setDeltaMovement(velocity);
    }

    @Override
    protected Item getDefaultItem() {
        return ModRegistries.SOUL_CHARGE.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide) {
            return;
        }

        BlockPos firePos = BlockPos.containing(result.getLocation());
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) result;
            firePos = blockHit.getBlockPos().relative(blockHit.getDirection());
        } else if (result.getType() == HitResult.Type.ENTITY) {
            firePos = ((EntityHitResult) result).getEntity().blockPosition();
        }

        SoulFireHelper.tryPlaceSoulFire(level(), firePos);
        discard();
    }
}
