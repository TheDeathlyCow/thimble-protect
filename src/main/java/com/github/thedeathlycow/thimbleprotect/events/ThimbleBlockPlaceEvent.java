package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class ThimbleBlockPlaceEvent extends ThimbleBlockUpdateEvent {


    /**
     * Create a ThimbleEvent.
     * @param causingEntity
     * @param pos
     * @param preState the state of the block before the current block was placed
     * @param postState the state of the block after it was placed
     */
    public ThimbleBlockPlaceEvent(String causingEntity, BlockPos pos, DimensionType dimension, long time, BlockState preState, BlockState postState) {
        super(causingEntity, pos, dimension, time, ThimbleSubType.BLOCK_PLACE);

        this.preState = preState;
        this.postState = postState;
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
        stringified += " placed " + this.postState.getBlock().getTranslationKey() + " at " + posString;
        stringified += ", replacing a " + this.preState.getBlock().getTranslationKey();

        return stringified;
    }
}
