package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThimbleBlockPlaceEvent extends ThimbleEvent {

    BlockState preState;
    BlockState postState;

    /**
     * Create a ThimbleEvent.
     *
     * @param causingEntity
     * @param pos
     * @param preState the state of the block before the current block was placed
     * @param postState the state of the block after it was placed
     */
    public ThimbleBlockPlaceEvent(LivingEntity causingEntity, BlockPos pos, BlockState preState, BlockState postState) {
        super(causingEntity, pos);

        this.preState = preState;
        this.postState = postState;
    }

    @Override
    public boolean revertRestoration(World world) {
        if (this.restored) {
            world.setBlockState(this.pos, this.postState);
            this.restored = false;
            return true;
        } else {
            return  false;
        }
    }

    @Override
    public boolean restoreEvent(World world) {
        if (!this.restored) {
            world.setBlockState(this.pos, this.preState);
            this.restored = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String stringified = "";

        if (this.restored)
            stringified = "*";

        stringified += this.ID + ", ";
        if (this.causingEntity != null) {
            stringified += this.causingEntity.getName().asString();
        } else {
            stringified += ThimbleEvent.NULL_ENTITY_STRING;
        }

        String posString = ThimbleEventLogger.getBlockPosShortString(this.pos);
        stringified += " placed " + this.postState.getBlock().getTranslationKey() + " at " + posString;
        stringified += ", replacing a " + this.preState.getBlock().getTranslationKey();

        return stringified;
    }
}
