package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.io.FileWriter;
import java.time.LocalDateTime;

public class ThimbleBlockUpdateEvent extends ThimbleEvent {

    public static final String NULL_ENTITY_STRING = "#entity";
    public boolean restored;
    protected BlockState preState;
    protected BlockState postState;

    /**
     * Create a ThimbleBlockUpdateEvent with a causing entity.
     *
     */
    public ThimbleBlockUpdateEvent(Entity causingEntity, BlockPos pos, DimensionType dimension, long time) {
        super(causingEntity, pos, dimension, time);
    }

    public boolean revertRestoration(World world) {
        if (this.restored) {
            world.setBlockState(this.pos, this.postState);
            this.restored = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean restoreEvent(World world) {
        if (!this.restored && world.getDimension() == this.dimension) {
            world.setBlockState(this.pos, this.preState);
            this.restored = true;
            return true;
        } else {
            return false;
        }
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

}