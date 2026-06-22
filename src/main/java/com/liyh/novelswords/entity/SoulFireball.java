package com.liyh.AncientWarcraft.entity;

import com.liyh.AncientWarcraft.init.ModRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoulFireball extends SmallFireball {
    public SoulFireball(EntityType<? extends SoulFireball> type, Level level) {
        super(type, level);
    }

    public SoulFireball(Level level, double x, double y, double z, Vec3 velocity) {
        super(ModRegistries.SOUL_FIREBALL.get(), level);
        this.setPos(x, y, z);
        this.setDeltaMovement(velocity);
    }
}
