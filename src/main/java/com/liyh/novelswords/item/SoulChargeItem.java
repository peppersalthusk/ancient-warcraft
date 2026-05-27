package com.liyh.AncientWarcraft.item;

import com.liyh.AncientWarcraft.util.SoulFireHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SoulChargeItem extends Item {
    public SoulChargeItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos firePos = context.getClickedPos().relative(context.getClickedFace());
        if (!SoulFireHelper.tryPlaceSoulFire(level, firePos)) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide && context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
