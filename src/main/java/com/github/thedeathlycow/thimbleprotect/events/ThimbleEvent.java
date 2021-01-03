package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.LocalDateTime;

public abstract class ThimbleEvent {

    public LivingEntity causingEntity;
    public BlockPos pos;
    public int ID;
    public LocalDateTime time;
    public boolean restored;

    public static final String NULL_ENTITY_STRING = "#null";

    /**
     * Create a ThimbleEvent.
     * @param causingEntity
     * @param pos
     */
    public ThimbleEvent(LivingEntity causingEntity, BlockPos pos) {
        this.causingEntity = causingEntity;
        this.pos = pos;
        this.time = LocalDateTime.now();
        this.ID = this.generateID();
        this.restored = false;
        this.addToLog();
    }

    private int generateID() {
        return 0;
    }

    public void addToLog() {
        ThimbleEventLogger.addEventToLog(this);
    }

    public abstract  boolean revertRestoration(World world);

    public abstract boolean restoreEvent(World world);

    @Override
    public abstract String toString();

}
