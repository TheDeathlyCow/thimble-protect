package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.time.LocalDateTime;

public abstract class ThimbleEvent {

    protected Entity causingEntity;
    protected BlockPos pos;
    protected long tick;
    protected int id;
    protected LocalDateTime time;
    public boolean restored;
    protected DimensionType dimension;

    public ThimbleEvent(Entity causingEntity, BlockPos pos, DimensionType dimension, long tick) {
        this.causingEntity = causingEntity;
        this.pos = pos;
        this.dimension = dimension;
        this.tick = tick;
        this.id = generateID();
        this.time = LocalDateTime.now();
        this.restored = false;
    }

    /**
     * TODO: Should return the ordinal number of the event in the database.
     */
    private int generateID() {
        return 0;
    }

    public void addToLog() {
        ThimbleEventLogger.addEventToLog(this);
    }


    // * ====== START GETTER METHODS ====== * //

    public DimensionType getDimension() {
        return dimension;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Entity getCausingEntity() {
        return this.causingEntity;
    }

    /**
     * Returns the tick this event occured at.
     * Use #getTime() to get the date and time this event occured at instead.
     */
    public long getTick() {
        return this.tick;
    }

    /**
     * Returns the date and time this event occured at.
     * Use #getTick() to get the tick this event occured at instead.
     */
    public LocalDateTime getTime() {
        return this.time;
    }
}
