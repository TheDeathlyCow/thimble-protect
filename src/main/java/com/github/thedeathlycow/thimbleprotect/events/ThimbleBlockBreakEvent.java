package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class ThimbleBlockBreakEvent extends ThimbleBlockUpdateEvent {

    /**
     * Create a ThimbleEvent.
     *
     * @param causingEntity the entity/player that broke the block
     * @param pos           the position that the block was broken at
     * @param state         block state of block that was broken
     */
    public ThimbleBlockBreakEvent(LivingEntity causingEntity, BlockPos pos, DimensionType dimension, long tick, BlockState state) {
        super(causingEntity, pos, dimension, tick);
        this.preState = state;
        this.postState = Blocks.AIR.getDefaultState();
    }

//    @Override
//    public boolean revertRestoration(World world) {
//        if (this.restored) {
//            world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
//            this.restored = false;
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean restoreEvent(World world) {
//        if (!this.restored && world.getDimension() == this.dimension) {
//            world.setBlockState(this.pos, this.preState);
////            this.state.getBlock().onPlaced(world, this.pos, this.state, this.causingEntity, null);
//            this.restored = true;
//            return true;
//        } else {
//            return false;
//        }
//    }

    @Override
    public String toString() {
        String stringified = "";

        if (this.restored)
            stringified = "*";

        stringified = this.tick + ", ";

        if (this.causingEntity != null) {
            stringified += this.causingEntity.getName().asString();
        } else {
            stringified += ThimbleBlockUpdateEvent.NULL_ENTITY_STRING;
        }

        String posString = ThimbleEventLogger.getBlockPosShortString(this.pos);
        stringified += " broke " + this.preState.getBlock().getTranslationKey() + " at " + posString;

        return stringified;
    }
}
