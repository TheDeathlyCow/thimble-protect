package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class ThimbleExplosionEvent extends ThimbleBlockBreakEvent {

    /**
     * Create a ThimbleEvent.
     *
     * @param causingEntity
     * @param pos
     */
    public ThimbleExplosionEvent(LivingEntity causingEntity, BlockPos pos, long tick, BlockState state) {
        super(causingEntity, pos, tick, state);
    }

    @Override
    public String toString() {
        String stringified = "";

        if (this.restored)
            stringified = "*";

        stringified = this.tick + ", ";

        if (this.causingEntity != null) {
            stringified += this.causingEntity.getName().asString();
        } else {
            stringified += ThimbleEvent.NULL_ENTITY_STRING;
        }

        String posString = ThimbleEventLogger.getBlockPosShortString(this.pos);
        stringified += " blew up " + this.preState.getBlock().getTranslationKey() + " at " + posString;

        return stringified;
    }

}
