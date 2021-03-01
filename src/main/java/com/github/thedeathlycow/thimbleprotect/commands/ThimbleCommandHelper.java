package com.github.thedeathlycow.thimbleprotect.commands;

import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEvent;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEventSerializer;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Contains a number of helper methods and variables for ThimbleProtectCommand.
 */
public class ThimbleCommandHelper {

    protected static int numChecked = 0;

    protected static final String distanceName = "distance";

    // * Start Helper Methods * //

    protected static List<ThimbleEvent> lookupEvents(CommandContext<ServerCommandSource> context) {
        return lookupEvents(context, false);
    }

    protected static List<ThimbleEvent> lookupEvents(CommandContext<ServerCommandSource> context, boolean updateRollback) {
        numChecked = 0;
        int distance = context.getArgument(distanceName, Integer.class);
        Vec3d pos = context.getSource().getPosition();
        String dimensionName = context.getSource().getWorld().getRegistryKey().getValue().toString();
        int[] chunkX = {((int) pos.getX() - distance) / 16, ((int) pos.getX() + distance) / 16};
        int[] chunkY = {((int) pos.getY() - distance) / 16, ((int) pos.getY() + distance) / 16};
        int[] chunkZ = {((int) pos.getZ() - distance) / 16, ((int) pos.getZ() + distance) / 16};

        ServerWorld world = context.getSource().getWorld();

        List<ThimbleEvent> foundEvents = new ArrayList<ThimbleEvent>();

        for (int dx = chunkX[0]; dx < chunkX[1] + 1; dx++) {
            for (int dy = chunkY[0]; dy < chunkY[1] + 1; dy++) {
                for (int dz = chunkZ[0]; dz < chunkZ[1] + 1; dz++) {
                    foundEvents.addAll(getBlockUpdateEventsFromFile(pos, dx, dy, dz, distance, dimensionName, null, ThimbleBlockUpdateEvent.ThimbleSubType.ALL, updateRollback));
                }
            }
        }

        return foundEvents;
    }

    /**
     * Returns a list of ThimbleBlockUpdateEvents that match a set of parameters.
     * @param originPos : Vec3d, the position at which the command was executed
     * @param chunkX : int, the X coordinate of the subchunk of the events we are looking at
     * @param chunkY : int, the Y coordinate of the subchunk of the events we are looking at
     * @param chunkZ : int, the Z coordinate of the subchunk of the events we are looking at
     * @param distance : int, the maximum distance which valid events can be from originPos
     * @param dimensionName : String, the namedspaced name of the dimension we are looking in
     * @param playerName : String, the name of the player or entity which valid events can be caused by (can be null)
     * @param subtype : enum, the subtype of the ThimbleBlockUpdateEvent event we are looking for.
     * @param updateRollback : boolean, whether or not we should update the rollback of the line. false means it will not be updated, true means it will be changed to it's opposite condition.
     * @return a list of valid ThimbleBlockUpdateEvent
     */
    protected static List<ThimbleBlockUpdateEvent> getBlockUpdateEventsFromFile(Vec3d originPos, int chunkX, int chunkY, int chunkZ, int distance, String dimensionName, String playerName, ThimbleBlockUpdateEvent.ThimbleSubType subtype, boolean updateRollback) {

        List<ThimbleBlockUpdateEvent> events = new ArrayList<ThimbleBlockUpdateEvent>();
        String[] dimension = dimensionName.split(":");
        String filename = "thimble/events/";

        try {
            filename += dimension[0] + "/" + dimension[1] + "/";
        } catch (IndexOutOfBoundsException e) {
            filename = "thimble/events/" + dimensionName + "/";
        }

        filename += String.format("r%s,%s/c%s,%s,%s.thimble", chunkX / 32, chunkZ / 32, chunkX, chunkY, chunkZ);

        BufferedReader inFile;
        StringBuffer writer;
        try {
            File file = new File(filename);
            inFile = new BufferedReader(new FileReader(file));
            writer = new StringBuffer();
        } catch (IOException e) {
            return events;
        }

        String line;
        try {
            while ((line = inFile.readLine()) != null) {
                numChecked++;

                ThimbleBlockUpdateEvent currEvent = ThimbleBlockUpdateEventSerializer.GSON.fromJson(line, ThimbleBlockUpdateEvent.class);

                if (meetsLookupRequirements(currEvent, originPos, distance, playerName, subtype)) {
                    events.add(currEvent);
                    if (updateRollback) {
                        currEvent = ThimbleBlockUpdateEventSerializer.GSON.fromJson(line, ThimbleBlockUpdateEvent.class);
                        currEvent.updateRollBack();
                    }
                }

                line = ThimbleBlockUpdateEventSerializer.GSON.toJson(currEvent);
                writer.append(line).append('\n');
            }

            inFile.close();

            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.write(writer.toString().getBytes());
            fileOut.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return events;
    }

    protected static boolean meetsLookupRequirements(ThimbleBlockUpdateEvent event, Vec3d originPos, int distance, String playerName, ThimbleBlockUpdateEvent.ThimbleSubType subType) {
        if (originPos.isInRange(new Vec3d(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()), distance)) {
            if (playerName != null) {
                return playerName.equals(event.getCausingEntity()) &&
                        (subType == ThimbleBlockUpdateEvent.ThimbleSubType.ALL || subType == event.getSubType());
            } else if (subType != ThimbleBlockUpdateEvent.ThimbleSubType.ALL) {
                return subType == event.getSubType();
            } else {
                return true;
            }
        }
        return false;
    }
}
