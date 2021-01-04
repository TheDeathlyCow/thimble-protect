package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ThimbleExplosionEvent extends ThimbleEvent {

    /**
     * Create a ThimbleExplosionEvent.
     *
     * @param causingEntity
     * @param pos
     */
    public ThimbleExplosionEvent(Entity causingEntity, BlockPos pos, long tick, BlockState state) {
        super(causingEntity, pos, tick);
        this.preState = state;
        this.postState = Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean revertRestoration(World world) {
        if (this.restored) {
            world.setBlockState(this.pos, this.postState);
            this.restored = false;
            return true;
        } else {
            return false;
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
            stringified += "*";

        stringified = this.tick + ", ";

        if (this.causingEntity != null) {
            stringified += this.causingEntity.getDisplayName().asString();
        } else {
            stringified += ThimbleEvent.NULL_ENTITY_STRING;
        }

        String posString = ThimbleEventLogger.getBlockPosShortString(this.pos);
        stringified += " blew up " + this.preState.getBlock().getTranslationKey() + " at " + posString;

        return stringified;
    }
}