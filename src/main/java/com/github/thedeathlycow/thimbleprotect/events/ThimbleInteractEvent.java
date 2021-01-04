package com.github.thedeathlycow.thimbleprotect.events;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThimbleInteractEvent extends ThimbleEvent {
    
    public ThimbleInteractEvent(Entity causingEntity, BlockPos pos, long tick) {
        super(causingEntity, pos, tick);
    }

    @Override
    public boolean revertRestoration(World world) {
        return false;
    }

    @Override
    public boolean restoreEvent(World world) {
        return false;
    }
}
