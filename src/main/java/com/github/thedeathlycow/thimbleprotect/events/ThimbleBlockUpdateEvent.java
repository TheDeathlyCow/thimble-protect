package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleProtect;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
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
        if (this.rolledBack) {
            world.setBlockState(this.pos, this.postState);
            this.rolledBack = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean rollback(World world) {
        if (!this.rolledBack) {
            world.setBlockState(this.pos, this.preState);
            this.rolledBack = true;
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

    public MutableText toText() {
        return this.toText(this.causingEntity);
    }

    public MutableText toText(String name) {

        Style nounStyle = Style.EMPTY.withFormatting(Formatting.DARK_PURPLE);
        Style verbStyle = Style.EMPTY.withFormatting(Formatting.WHITE);;
        Style posStyle = Style.EMPTY.withFormatting(Formatting.GRAY);

        if (this.rolledBack) {
            nounStyle = nounStyle.withFormatting(Formatting.STRIKETHROUGH);
            verbStyle = verbStyle.withFormatting(Formatting.STRIKETHROUGH);
            posStyle = posStyle.withFormatting(Formatting.STRIKETHROUGH);
        }

        MutableText neat = new LiteralText(this.getPos().getX() + ", " + this.getPos().getY() + ", " + this.getPos().getZ() + ": ").fillStyle(posStyle);

        neat.append(new LiteralText(name).fillStyle(nounStyle)).append(" ");
        String verb = "";
        switch (this.getSubType()) {
            case BLOCK_BREAK:
                neat.append(new LiteralText("broke ").fillStyle(verbStyle)).append(new LiteralText(Registry.BLOCK.getId(this.getPreState().getBlock()).toString()).fillStyle(nounStyle));
                break;
            case BLOCK_PLACE:
                neat.append(new LiteralText("placed ").fillStyle(verbStyle)).append(new LiteralText(Registry.BLOCK.getId(this.getPostState().getBlock()).toString()).fillStyle(nounStyle));
                break;
            case EXPLOSION:
                neat.append(new LiteralText("blew up ").fillStyle(verbStyle)).append(new LiteralText(Registry.BLOCK.getId(this.getPostState().getBlock()).toString()).fillStyle(nounStyle));
                break;
            default:
                neat.append(new LiteralText("changed ").fillStyle(verbStyle)).append(new LiteralText(Registry.BLOCK.getId(this.getPostState().getBlock()).toString()).fillStyle(Style.EMPTY.withColor(TextColor.parse("light_purple"))));
                break;
        }

        return neat;
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
        this.rolledBack = rolledBack;
    }

    public enum ThimbleSubType {
        BLOCK_PLACE,
        BLOCK_BREAK,
        EXPLOSION,
        ALL
    }

}
