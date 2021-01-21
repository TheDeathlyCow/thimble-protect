package com.github.thedeathlycow.thimbleprotect.events;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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
    public ThimbleBlockUpdateEvent(String causingEntity, BlockPos pos, String dimension, long time, ThimbleSubType subtype) {
        super(causingEntity, pos, dimension, time, ThimbleType.BLOCK_UPDATE);
        this.subType = subtype;
    }

    /**
     * This consturctor should <b>ONLY</b> be called when reading from a JSON file.
     */
    public ThimbleBlockUpdateEvent(String causingEntity, long time, ThimbleSubType subType, boolean rolledBack) {
        super(causingEntity, time, ThimbleType.BLOCK_UPDATE, rolledBack);
        this.subType = subType;
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
        if (!this.rollbedBack) {
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
        int posX = this.getPos().getX();
        int posY = this.getPos().getY();
        int posZ = this.getPos().getZ();
        try {
            String parentFilepath = this.genParentDirectory(posX, posZ);
            String filename = parentFilepath + posX + "." + posY + "." + posZ + ".thimble";

            fileWriter = new FileWriter(filename, true);

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .registerTypeHierarchyAdapter(ThimbleBlockUpdateEvent.class, new ThimbleBlockUpdateEventSerializer());
            Gson eventGson = gsonBuilder.create();

            serialised = eventGson.toJson(this);
            fileWriter.write(serialised + "\n");

            fileWriter.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error writing ThimbleEvent to file: " + e);
            e.printStackTrace();
        }
    }

    private String genParentDirectory(int posX, int posZ) {

        String regionFilename = BASE_FILE_PATH + "r" + posX / 512 + "," + posZ / 512;
        String chunkFilename = "c" + posX / 16 + "," + posZ / 16;

        File regionFile = new File(regionFilename);
        if (regionFile.mkdir()) {
            System.out.println("[ThimbleProtect] Created new region directory: " + regionFilename);
        }
        File chunkFile = new File(regionFilename + "/" + chunkFilename + "/");
        if (chunkFile.mkdir()) {
            System.out.println("[ThimbleProtect] Created new region directory: " + chunkFilename + "/");
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

    // * ====== START SETTER METHODS ====== * //

    public void setPreState(BlockState preState) {
        this.preState = preState;
    }

    public void setPostSTate(BlockState postState) {
        this.postState = postState;
    }

    public void setRolledBack(boolean rolledBack) {
        this.rollbedBack = rolledBack;
    }

}
