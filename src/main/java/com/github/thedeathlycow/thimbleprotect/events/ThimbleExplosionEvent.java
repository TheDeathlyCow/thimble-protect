package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class ThimbleExplosionEvent extends ThimbleBlockUpdateEvent {

    /**
     * Create a ThimbleExplosionEvent.
     *
     * @param causingEntity
     * @param pos
     */
    public ThimbleExplosionEvent(Entity causingEntity, BlockPos pos, DimensionType dimension, long tick, BlockState state) {
        super(causingEntity, pos, dimension, tick);
        this.preState = state;
        this.postState = Blocks.AIR.getDefaultState();
    }

//    @Override
//    public boolean revertRestoration(World world) {
//        if (this.restored) {
//            world.setBlockState(this.pos, this.postState);
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
            stringified += "*";

        stringified = this.tick + ", ";

        if (this.causingEntity != null) {
            stringified += this.causingEntity.getDisplayName().asString();
        } else {
            stringified += ThimbleBlockUpdateEvent.NULL_ENTITY_STRING;
        }

        String posString = ThimbleEventLogger.getBlockPosShortString(this.pos);
        stringified += " blew up " + this.preState.getBlock().getTranslationKey() + " at " + posString;

        return stringified;
    }
}