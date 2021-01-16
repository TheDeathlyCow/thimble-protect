package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import com.google.gson.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

public abstract class ThimbleEvent {

    public boolean restored;
    protected Entity causingEntity;
    protected BlockPos pos;
    protected long tick;
    protected int id;
    protected LocalDateTime time;
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
        try {
            int posX = this.getPos().getX();
            int posY = this.getPos().getY();
            int posZ = this.getPos().getZ();

            FileWriter outFile = new FileWriter("thimble/events/" + posX + "." + posY + "." + posZ + ".json");
            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .registerTypeHierarchyAdapter(ThimbleEvent.class, new ThimbleEventSerializer());
            Gson eventGson = gsonBuilder.create();

            String serialised = eventGson.toJson(this);
            System.out.println("Wrote: " + serialised);
            outFile.write(serialised);
            outFile.close();
        } catch (Exception e) {
            System.out.println("Error writing ThimbleEvent to file: " + e);
//            e.printStackTrace();
        }
    }

    public abstract boolean revertRestoration(World world);

    public abstract boolean restoreEvent(World world);

    public boolean restoreEvent(World world, boolean deleteEvent) {
        boolean couldRestore = this.restoreEvent(world);
        if (couldRestore && deleteEvent) {
            ThimbleEventLogger.EventList.remove(this);
        }
        return couldRestore;
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
