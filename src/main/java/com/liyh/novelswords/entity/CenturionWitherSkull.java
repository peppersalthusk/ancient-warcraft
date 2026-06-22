package com.liyh.AncientWarcraft.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class CenturionWitherSkull extends WitherSkull {
    private static final float DIRECT_HIT_DAMAGE = 8.0F;
    private static final float EXPLOSION_POWER = 1.0F;
    private static final int EXPLOSION_WITHER_DURATION = 15 * 20;
    private static final int EXPLOSION_WITHER_AMPLIFIER = 1;

    public CenturionWitherSkull(EntityType<? extends CenturionWitherSkull> type, Level level) {
        super(type, level);
        setDangerous(false);
    }

    @Override
    public float getBlockExplosionResistance(
            Explosion explosion,
            BlockGetter level,
            BlockPos pos,
            BlockState state,
            FluidState fluid,
            float resistance) {
        if (state.is(Blocks.BEDROCK) || state.is(Blocks.WITHER_ROSE)) {
            return resistance;
        }
        return 0.0F;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide) {
            result.getEntity().hurt(
                    level().damageSources().witherSkull(this, getOwner()), DIRECT_HIT_DAMAGE);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (!level().isClientSide) {
            double x = getX();
            double y = getY();
            double z = getZ();
            level().explode(
                    this,
                    level().damageSources().witherSkull(this, getOwner()),
                    new CenturionExplosionCalculator(this),
                    x,
                    y,
                    z,
                    EXPLOSION_POWER,
                    false,
                    Level.ExplosionInteraction.MOB);
            applyExplosionWither(x, y, z, EXPLOSION_POWER);
        }
        discard();
    }

    private void applyExplosionWither(double x, double y, double z, float power) {
        double radius = 2.0 * power;
        AABB area = new AABB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius);
        for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (living.isAlive()) {
                living.addEffect(new MobEffectInstance(
                        MobEffects.WITHER, EXPLOSION_WITHER_DURATION, EXPLOSION_WITHER_AMPLIFIER));
            }
        }
    }

    private static final class CenturionExplosionCalculator extends EntityBasedExplosionDamageCalculator {
        CenturionExplosionCalculator(CenturionWitherSkull skull) {
            super(skull);
        }

        @Override
        public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
            return true;
        }

        @Override
        public boolean shouldBlockExplode(
                Explosion explosion,
                BlockGetter level,
                BlockPos pos,
                BlockState state,
                float power) {
            return true;
        }
    }
}
