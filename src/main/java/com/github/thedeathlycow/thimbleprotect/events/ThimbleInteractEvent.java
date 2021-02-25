package com.github.thedeathlycow.thimbleprotect.events;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ThimbleInteractEvent extends ThimbleEvent {

    Block block;

    public ThimbleInteractEvent(String causingEntity, BlockPos pos, String dimension, long time) {
        super(causingEntity, pos, dimension, time, ThimbleType.INTERACT);
        this.dimension = dimension;
    }

    public ThimbleInteractEvent(String causingEntity, BlockPos pos, String dimension, long tick, Block block) {
        this(causingEntity, pos, dimension, tick);
        this.block = block;
    }

    public String toString() {
        return this.causingEntity + " interacted with " + this.block.getTranslationKey();
    }

    @Override
    public void addToLog() {

    }

    /**
     * Standard interaction events have nothing to restore!
     *
     * @return false
     */
    @Override
    public boolean restore(World world) {
        this.rolledBack = false;
        return false;
    }

    /**
     * Standard interaction events have nothing to restore!
     *
     * @return false
     */
    @Override
    public boolean rollback(World world) {
        this.rolledBack = true;
        return false;
    }
}
