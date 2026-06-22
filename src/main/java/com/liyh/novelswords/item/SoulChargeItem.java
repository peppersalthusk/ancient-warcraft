package com.liyh.AncientWarcraft.item;

import com.liyh.AncientWarcraft.entity.SoulFireball;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class SoulChargeItem extends Item implements ProjectileItem {
    public SoulChargeItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        boolean flag = false;
        if (!CampfireBlock.canLight(blockstate)
                && !CandleBlock.canLight(blockstate)
                && !CandleCakeBlock.canLight(blockstate)) {
            blockpos = blockpos.relative(context.getClickedFace());
            if (BaseFireBlock.canBePlacedAt(level, blockpos, context.getHorizontalDirection())) {
                this.playSound(level, blockpos);
                level.setBlockAndUpdate(blockpos, BaseFireBlock.getState(level, blockpos));
                level.gameEvent(context.getPlayer(), GameEvent.BLOCK_PLACE, blockpos);
                flag = true;
            }
        } else {
            this.playSound(level, blockpos);
            level.setBlockAndUpdate(blockpos, blockstate.setValue(BlockStateProperties.LIT, true));
            level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
            flag = true;
        }

        if (flag) {
            context.getItemInHand().shrink(1);
        }

        return flag ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.FAIL;
    }

    private void playSound(Level level, BlockPos pos) {
        level.playSound(
                null,
                pos,
                SoundEvents.FIRECHARGE_USE,
                SoundSource.BLOCKS,
                1.0F,
                (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        var random = level.getRandom();
        double d0 = random.triangle((double) direction.getStepX(), 0.11485000000000001D);
        double d1 = random.triangle((double) direction.getStepY(), 0.11485000000000001D);
        double d2 = random.triangle((double) direction.getStepZ(), 0.11485000000000001D);
        Vec3 vec3 = new Vec3(d0, d1, d2);
        SoulFireball soulFireball = new SoulFireball(level, pos.x(), pos.y(), pos.z(), vec3.normalize());
        soulFireball.setItem(stack);
        return soulFireball;
    }

    @Override
    public void shoot(Projectile projectile, double x, double y, double z, float power, float uncertainty) {}

    @Override
    public DispenseConfig createDispenseConfig() {
        return DispenseConfig.builder()
                .positionFunction((BlockSource source, Direction direction) ->
                        DispenserBlock.getDispensePosition(source, 1.0, Vec3.ZERO))
                .uncertainty(6.6666665F)
                .power(1.0F)
                .overrideDispenseEvent(1018)
                .build();
    }
}
