package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.LocalDateTime;

public abstract class ThimbleEvent {

    private LivingEntity causingEntity;
    private BlockPos pos;
    private int ID;
    private LocalDateTime time;
    private long tick;
    public boolean restored;

    private BlockState preState;
    private BlockState postState;

    public static final String NULL_ENTITY_STRING = "#null";

    /**
     * Create a ThimbleEvent.
     * @param causingEntity
     * @param pos
     */
    public ThimbleEvent(LivingEntity causingEntity, BlockPos pos, long tick) {
        this.causingEntity = causingEntity;
        this.pos = pos;
        this.tick = tick;
        this.time = LocalDateTime.now();
        this.ID = this.generateID();
        this.restored = false;
        this.addToLog();
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

    public abstract boolean revertRestoration(World world);

    public abstract boolean restoreEvent(World world);

    @Override
    public abstract String toString();

    // * ====== START GETTER METHODS ====== * //

    /**
     * Gets the state of the block after the event.
     */
    public BlockState getPostState() {
        return this.postState;
    }

    /**
     * Gets the state of the block before the event.
     */
    public BlockState getPreState() {
        return this.preState;
    }

    /**
     * Returns the date and time this event occured at.
     * Use #getTick() to get the tick this event occured at instead.
     */
    public LocalDateTime getTime() {
        return this.time;
    }

    public int getID() {
        return this.ID;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public LivingEntity getCausingEntity() {
        return this.causingEntity;
    }

    /**
     * Returns the tick this event occured at.
     * Use #getTime() to get the date and time this event occured at instead.
     */
    public long getTick() {
        return this.tick;
    }

}