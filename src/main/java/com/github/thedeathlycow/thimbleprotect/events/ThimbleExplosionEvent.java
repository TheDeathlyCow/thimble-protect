package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class ThimbleExplosionEvent extends ThimbleBlockBreakEvent {

    /**
     * Create a ThimbleEvent.
     *
     * @param causingEntity
     * @param pos
     */
    public ThimbleExplosionEvent(LivingEntity causingEntity, BlockPos pos, BlockState state) {
        super(causingEntity, pos, state);
    }

    @Override
    public String toString() {
        String stringified = "";

        if (this.restored)
            stringified = "*";

        stringified = this.ID + ", ";

        if (this.causingEntity != null) {
            stringified += this.causingEntity.getName().asString();
        } else {
            stringified += ThimbleEvent.NULL_ENTITY_STRING;
        }

        String posString = ThimbleEventLogger.getBlockPosShortString(this.pos);
        stringified += " blew up " + this.state.getBlock().getTranslationKey() + " at " + posString;

        return stringified;
    }

}
