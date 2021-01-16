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

public abstract class ThimbleBlockUpdateEvent extends ThimbleEvent {

    public static final String NULL_ENTITY_STRING = "#entity";
    public boolean restored;
    protected LocalDateTime time;
    protected long tick;
    protected BlockState preState;
    protected BlockState postState;

    /**
     * Create a ThimbleBlockUpdateEvent with a causing entity.
     *
     */
    public ThimbleBlockUpdateEvent(Entity causingEntity, BlockPos pos, DimensionType dimension, long tick) {
        super(causingEntity, pos, dimension, tick);
        this.time = LocalDateTime.now();
        this.restored = false;
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

}
