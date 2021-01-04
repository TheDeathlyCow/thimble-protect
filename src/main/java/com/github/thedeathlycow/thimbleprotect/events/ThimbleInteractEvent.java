package com.github.thedeathlycow.thimbleprotect.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ThimbleInteractEvent extends ThimbleEvent {

    DimensionType dimension;
    Block block;

    public ThimbleInteractEvent(Entity causingEntity, BlockPos pos, DimensionType dimension, long tick) {
        super(causingEntity, pos, dimension, tick);
        this.dimension = dimension;
    }

    public ThimbleInteractEvent(Entity causingEntity, BlockPos pos, DimensionType dimension, long tick, Block block) {
        this(causingEntity, pos, dimension, tick);
        this.block = block;
    }

    public String toString() {
        return causingEntity.getName().toString() + " interacted with " + this.block.getTranslationKey();
    }
}
