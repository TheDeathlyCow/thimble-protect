package com.github.thedeathlycow.thimbleprotect;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.time.LocalDateTime;

public class ThimbleEvent {

    public enum ThimbleEventType {
        BLOCK_BREAK,
        BLOCK_PLACE,
        EXPLOSION
    }

    private BlockState state;
    private Entity causingEntity;
    private BlockPos pos;
    private int ID;
    private LocalDateTime time;
    private ThimbleEventType type;

    /**
     * Create a ThimbleEvent.
     * @param state
     * @param causingEntity
     * @param pos
     */
    public ThimbleEvent(BlockState state, Entity causingEntity, BlockPos pos, ThimbleEventType type) {
        this.state = state;
        this.causingEntity = causingEntity;
        this.pos = pos;
        this.type = type;
        this.time = LocalDateTime.now();
        this.ID = this.generateID();

        this.addToLog();
    }

    private int generateID() {
        return ThimbleEventLogger.MaxSavedEventID + ThimbleEventLogger.EventList.size();
    }

    public void addToLog() {

        ThimbleEventLogger.addEventToLog(this);
    }

    @Override
    public String toString() {
        String stringified = this.ID + ": ";
        if (this.causingEntity != null) {
            stringified += this.causingEntity.getName().asString();
        } else {
            stringified += "#entity";
        }

        switch(this.type) {
            case BLOCK_BREAK:
                stringified += " broke ";
                break;
            case BLOCK_PLACE:
                stringified += " placed ";
                break;
            case EXPLOSION:
                stringified += " blew up ";
                break;
            default:
                stringified = "[ERROR]";
                break;
        }

        stringified += this.state.toString() + " at " + this.pos.toString();
        return stringified;

    }

}
