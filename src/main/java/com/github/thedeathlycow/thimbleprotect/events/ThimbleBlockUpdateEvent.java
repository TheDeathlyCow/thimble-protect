package com.github.thedeathlycow.thimbleprotect.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ThimbleBlockUpdateEvent extends ThimbleEvent {

    public static final String NULL_ENTITY_STRING = "#entity";
    protected BlockState preState;
    protected BlockState postState;
    protected ThimbleSubType subType;

    public static final String BASE_FILE_PATH = "thimble/events/";

    public enum ThimbleSubType {
        BLOCK_PLACE,
        BLOCK_BREAK,
        EXPLOSION
    }

    /**
     * Create a ThimbleBlockUpdateEvent with a causing entity.
     *
     */
    public ThimbleBlockUpdateEvent(Entity causingEntity, BlockPos pos, DimensionType dimension, long time, ThimbleSubType subtype) {
        super(causingEntity, pos, dimension, time, ThimbleType.BLOCK_UPDATE);
        this.subType = subtype;
    }

    public boolean restore(World world) {
        if (this.rollbedBack) {
            world.setBlockState(this.pos, this.postState);
            this.rollbedBack = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean rollback(World world) {
        if (!this.rollbedBack && world.getDimension() == this.dimension) {
            world.setBlockState(this.pos, this.preState);
            this.rollbedBack = true;
            return true;
        } else {
            return false;
        }
    }

    public void addToLog() {

        FileWriter fileWriter = null;
        String serialised = "";
        try {
            int posX = this.getPos().getX();
            int posY = this.getPos().getY();
            int posZ = this.getPos().getZ();

            String parentFilepath = this.genParentDirectory();
            String filename = parentFilepath + posX + "." + posY + "." + posZ + ".json";

            fileWriter = new FileWriter(filename);

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .registerTypeHierarchyAdapter(ThimbleEvent.class, new ThimbleBlockUpdateEventSerializer());
            Gson eventGson = gsonBuilder.create();
            serialised = eventGson.toJson(this);
            fileWriter.write(serialised);

            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing ThimbleEvent to file: " + e);
        } finally {
            System.out.println("Wrote: " + serialised);
        }
    }

    private String genParentDirectory() {
        int posX = this.getPos().getX();
        int posZ = this.getPos().getZ();

        String regionFilename = BASE_FILE_PATH + "r" + posX / 512 + "," + posZ / 512;
        String chunkFilename = "c" + posX / 16 + "," + posZ / 16;

        File regionFile = new File(regionFilename);
        if (regionFile.mkdir()) {
            System.out.println("Created new region directory: " + regionFilename);
        }
        File chunkFile = new File(regionFilename + "/" + chunkFilename + "/");
        if (chunkFile.mkdir()) {
            System.out.println("Created new region directory: " + chunkFilename + "/");
        }

        return regionFilename + "/" + chunkFilename + "/";
    }

    @Override
    public String toString() {
        return "";
    }

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

    public ThimbleSubType getSubType() {
        return this.subType;
    }

}
