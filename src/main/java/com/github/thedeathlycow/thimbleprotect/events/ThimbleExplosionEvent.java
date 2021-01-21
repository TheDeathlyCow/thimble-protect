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
    public ThimbleExplosionEvent(String causingEntity, BlockPos pos, String dimension, long time, BlockState state) {
        super(causingEntity, pos, dimension, time, ThimbleSubType.EXPLOSION);
        this.preState = state;
        this.postState = Blocks.AIR.getDefaultState();
    }

    @Override
    public String toString() {
        String stringified = "";

        if (this.rollbedBack)
            stringified += "Restored: ";

        stringified += this.time + ", ";

        if (this.causingEntity != null) {
            stringified += this.causingEntity;
        } else {
            stringified += ThimbleBlockUpdateEvent.NULL_ENTITY_STRING;
        }

        String posString = ThimbleEventLogger.getBlockPosShortString(this.pos);
        stringified += " blew up " + this.preState.getBlock().getTranslationKey() + " at " + posString;

        return stringified;
    }
}