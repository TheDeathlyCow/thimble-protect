package com.github.thedeathlycow.thimbleprotect.events;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;

public abstract class ThimbleEvent {

    protected boolean rolledBack;
    protected String causingEntity;
    protected String entityName;
    protected BlockPos pos;
    protected long time;
    protected String dimension;
    protected ThimbleType type;

    public ThimbleEvent(String causingEntity, BlockPos pos, String dimension, long time, ThimbleType type) {
        this.causingEntity = causingEntity;
        this.pos = pos;
        this.dimension = dimension;
        this.time = time;
        this.rolledBack = false;
        this.type = type;
    }

    /**
     * This consturctor should <b>ONLY</b> be called when reading from a JSON file.
     */
    public ThimbleEvent(String causingEntity, BlockPos pos, String dimension, long time, ThimbleType type, boolean rolledBack) {
        this.causingEntity = causingEntity;
        this.pos = pos;
        this.dimension = dimension;
        this.time = time;
        this.type = type;
        this.rolledBack = rolledBack;
    }

    public abstract void addToLog() throws IOException;

    public abstract boolean restore(World world);
//    {
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
//    }

    public abstract boolean rollback(World world);

    public void updateRollBack() {
        this.rolledBack = !this.rolledBack;
    }

    public boolean rollback(World world, boolean deleteEvent) {
        boolean couldRestore = this.rollback(world);
        if (couldRestore && deleteEvent) {
            ThimbleEventLogger.EventList.remove(this);
        }
        return couldRestore;
    }

    public boolean isWithinDistance(BlockPos pos, int range) {
        return this.getPos().isWithinDistance(pos, range);
    }

    public String getDimension() {
        return dimension;
    }

    public abstract Text toText();

    // * ====== START GETTER METHODS ====== * //

    public boolean isRolledBack() {
        return this.rolledBack;
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

    public enum ThimbleType {
        BLOCK_UPDATE,
        INTERACT
    }

}
