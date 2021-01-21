package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import com.google.gson.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.io.FileWriter;

public abstract class ThimbleEvent {

    public boolean rollbedBack;
    protected String causingEntity;
    protected BlockPos pos;
    protected int id;
    protected long time;
    protected DimensionType dimension;
    protected ThimbleType type;

    public enum ThimbleType {
        BLOCK_UPDATE,
        INTERACT
    }

    public ThimbleEvent(String causingEntity, BlockPos pos, DimensionType dimension, long time, ThimbleType type) {
        this.causingEntity = causingEntity;
        this.pos = pos;
        this.dimension = dimension;
        this.id = generateID();
        this.time = time;
        this.rollbedBack = false;
        this.type = type;
    }

    /**
     * TODO: Should return the ordinal number of the event in the database.
     */
    private int generateID() {
        return 0;
    }

    public void addToLog() {
//        try {
//            int posX = this.getPos().getX();
//            int posY = this.getPos().getY();
//            int posZ = this.getPos().getZ();
//
//            FileWriter outFile = new FileWriter("thimble/events/" + posX + "." + posY + "." + posZ + ".json");
//            GsonBuilder gsonBuilder = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .disableHtmlEscaping()
//                    .registerTypeHierarchyAdapter(ThimbleEvent.class, new ThimbleBlockUpdateEventSerializer());
//            Gson eventGson = gsonBuilder.create();
//
//            String serialised = eventGson.toJson(this);
//            System.out.println("Wrote: " + serialised);
//            outFile.write(serialised);
//            outFile.close();
//        } catch (Exception e) {
//            System.out.println("Error writing ThimbleEvent to file: " + e);
////            e.printStackTrace();
//        }
    }

    public abstract boolean restore(World world);

    public abstract boolean rollback(World world);

    public boolean rollback(World world, boolean deleteEvent) {
        boolean couldRestore = this.rollback(world);
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

    public String getCausingEntity() {
        return this.causingEntity;
    }

    /**
     * Returns the UNIX timestamp this event occured at
     */
    public long getTime() {
        return this.time;
    }

    public ThimbleType getType() {
        return this.type;
    }

}
