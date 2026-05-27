package com.liyh.AncientWarcraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class SoulFireHelper {
    private SoulFireHelper() {}

    public static boolean tryPlaceSoulFire(Level level, BlockPos pos) {
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !current.is(Blocks.SOUL_FIRE)) {
            return false;
        }
        BlockState support = level.getBlockState(pos.below());
        if (!SoulFireBlock.canSurviveOnBlock(support)) {
            return false;
        }
        return level.setBlock(pos, Blocks.SOUL_FIRE.defaultBlockState(), 11);
    }
}
