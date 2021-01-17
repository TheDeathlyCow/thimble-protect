package com.github.thedeathlycow.thimbleprotect.events;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class ThimbleChestInteractEvent extends ThimbleInteractEvent {

    public ThimbleChestInteractEvent(Entity causingEntity, BlockPos pos, DimensionType dimension, long time) {
        super(causingEntity, pos, dimension, time);
    }
}
