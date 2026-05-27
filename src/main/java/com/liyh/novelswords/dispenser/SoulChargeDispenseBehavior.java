package com.liyh.AncientWarcraft.dispenser;

import com.liyh.AncientWarcraft.entity.SoulFireball;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class SoulChargeDispenseBehavior extends DefaultDispenseItemBehavior {
    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        ServerLevel level = source.level();
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        Position position = DispenserBlock.getDispensePosition(source);
        double speed = 0.1;
        Vec3 velocity = new Vec3(
                direction.getStepX() * speed + level.random.nextGaussian() * 0.01,
                direction.getStepY() * speed + level.random.nextGaussian() * 0.01,
                direction.getStepZ() * speed + level.random.nextGaussian() * 0.01
        );

        SoulFireball fireball = new SoulFireball(level, position.x(), position.y(), position.z(), velocity);
        fireball.setItem(stack.copyWithCount(1));
        level.addFreshEntity(fireball);
        stack.shrink(1);
        return stack;
    }

    @Override
    protected void playSound(BlockSource source) {
        source.level().levelEvent(1018, source.pos(), 0);
    }
}
