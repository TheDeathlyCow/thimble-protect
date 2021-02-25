package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleProtect;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class ThimbleBlockUpdateEvent extends ThimbleEvent {

    public static final String NULL_ENTITY_STRING = "#entity";
    public static final String BASE_FILE_PATH = "thimble/events/";
    protected BlockState preState;
    protected BlockState postState;
    protected ThimbleSubType subType;

    /**
     * Create a ThimbleBlockUpdateEvent with a causing entity.
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

    public void addToLog() throws IOException {

        FileWriter fileWriter = null;
        String serialised = "";
        int posX = this.getPos().getX();
        int posY = this.getPos().getY();
        int posZ = this.getPos().getZ();
        try {
            String filename = this.genParentDirectory(posX, posY, posZ);
            fileWriter = new FileWriter(filename, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .registerTypeHierarchyAdapter(ThimbleBlockUpdateEvent.class, new ThimbleBlockUpdateEventSerializer());
            Gson eventGson = gsonBuilder.create();

            serialised = eventGson.toJson(this);

            if (fileWriter != null) {
                fileWriter.write(serialised + "\n");
                fileWriter.close();
            }

        }
    }

    private String genParentDirectory(int posX, int posY, int posZ) {

        String directoryPath = "";
        try {
            directoryPath = BASE_FILE_PATH + this.getDimension().split(":")[0]
                    + "/" + this.getDimension().split(":")[1]
                    + "/" + "r" + posX / 512 + "," + posZ / 512;
        } catch (IndexOutOfBoundsException e) {
            ThimbleProtect.Print("ERROR - Dimenion name has no namespace!");
            directoryPath = BASE_FILE_PATH + this.getDimension().split(":")[0]
                    + "/" + "r" + posX / 512 + "," + posZ / 512;
        }

        File direc = new File(directoryPath);
        direc.mkdirs();

        String chunkFilename = "c" + posX / 16 + "," + posY / 16 + "," + posZ / 16 + ".thimble";

        return directoryPath + "/" + chunkFilename;
    }

    @Override
    public String toString() {
        return this.causingEntity + " " + this.getPos().toString() + " " + this.getDimension() + " " + this.getTime()
                + " " + this.getSubType();
    }

    public String toNeatString() {
        return this.toNeatString(this.causingEntity);
    }

    public String toNeatString(String name) {
        StringBuilder neat = new StringBuilder(this.getPos().getX() + ", " + this.getPos().getY() + ", " + this.getPos().getZ() + ": ");
        neat.append(name).append(" ");
        String verb = "";
        switch(this.getSubType()) {
            case BLOCK_BREAK:
                verb = "broke " + Registry.BLOCK.getId(this.getPreState().getBlock());
                break;
            case BLOCK_PLACE:
                verb = "placed " + Registry.BLOCK.getId(this.getPostState().getBlock());
                break;
            case EXPLOSION:
                verb = "blew up " + Registry.BLOCK.getId(this.getPostState().getBlock());
                break;
            default:
                verb = "changed " + Registry.BLOCK.getId(this.getPostState().getBlock());
                break;
        }
        neat.append(verb);

        return neat.toString();
    }

    /**
     * Gets the state of the block after the event.
     */
    public BlockState getPostState() {
        return this.postState;
    }

    // * ====== START GETTER METHODS ====== * //

    public void setPostState(BlockState postState) {
        this.postState = postState;
    }

    /**
     * Gets the state of the block before the event.
     */
    public BlockState getPreState() {
        return this.preState;
    }

    public void setPreState(BlockState preState) {
        this.preState = preState;
    }

    // * ====== START SETTER METHODS ====== * //

    public ThimbleSubType getSubType() {
        return this.subType;
    }

    public void setRolledBack(boolean rolledBack) {
        this.rollbedBack = rolledBack;
    }

    public enum ThimbleSubType {
        BLOCK_PLACE,
        BLOCK_BREAK,
        EXPLOSION,
        ALL
    }

}
