package com.github.thedeathlycow.thimbleprotect.commands;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import com.github.thedeathlycow.thimbleprotect.ThimbleProtect;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEvent;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEventSerializer;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger.EventList;
import static com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEvent.ThimbleSubType;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ThimbleProtectCommand {

    private static String distanceName = "distance (in chunks)";
    private static int numChecked = 0;

    public static void registerCommand() {

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("thimble")
                    .requires(source -> source.hasPermissionLevel(4))
                    .then(literal("lookup")
                            .then(argument(distanceName, IntegerArgumentType.integer(1, 100))
                                    .executes(ThimbleProtectCommand::lookup)))
                    .then(literal("rollback")
                            .then(argument(distanceName, IntegerArgumentType.integer(1))
                                    .executes(ThimbleProtectCommand::rollback)))
                    .then(literal("restore")
                            .then(argument(distanceName, IntegerArgumentType.integer(1))
                                    .executes(ThimbleProtectCommand::restore)))
                    .then(literal("clearEvents")
                            .executes(ThimbleProtectCommand::clearEvents))
                    .then(literal("reloadConfig")
                            .executes(ThimbleProtectCommand::reloadConfig)));
        });
    }

    // * Methods for individual commands * //

    public static int reloadConfig(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String outputString = "";
        try {
            ThimbleProtect.readConfig();
            outputString = "ThimbleProtect Config reloaded!\n" + ThimbleProtect.CONFIG.toString();
        } catch (IOException e) {
            outputString = "Error reloading config: " + e;
            e.printStackTrace();
        } finally {
            context.getSource().sendFeedback(new LiteralText(outputString), false);
        }

        return 1;
    }

    public static int lookup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        numChecked = 0;

        int distance = context.getArgument(distanceName, Integer.class);
        Vec3d pos = context.getSource().getPosition();
        String dimensionName = context.getSource().getWorld().getRegistryKey().getValue().toString();
        int chunkX = ((int) pos.getX()) / 16;
        int chunkY = ((int) pos.getY()) / 16;
        int chunkZ = ((int) pos.getZ()) / 16;

        String message = " --- ThimbleProtect Lookup ---\n";
        int howManyFound = 0;

        ServerWorld world = context.getSource().getWorld();

        for (int dx = (chunkX - distance); dx < (chunkX + distance); dx++) {
            for (int dy = (chunkY - distance); dy < (chunkY + distance); dy++) {
                for (int dz = (chunkZ - distance); dz < (chunkZ + distance); dz++) {

                    List<ThimbleBlockUpdateEvent> foundEvents = getBlockUpdateEventsFromFile(pos, dx, dy, dz, distance, dimensionName);
                    howManyFound += foundEvents.size();
                    for (ThimbleBlockUpdateEvent event : foundEvents) {
                        String name = event.getCausingEntity();
                        Entity causingEntity = world.getEntity(UUID.fromString(name));

                        if (causingEntity != null) {
                            name = causingEntity.getEntityName();
                        }

                        message += "\n" + name + " " + event.getPos().toString() + " " + event.getDimension() + " " + event.getTime()
                                + " " + event.getSubType();
                    }
                }
            }
        }

        message += "\nFound " + howManyFound + " event(s)...";

        context.getSource().sendFeedback(new LiteralText(message), false);
        context.getSource().sendFeedback(new LiteralText("Checked " + numChecked + " blocks..."), false);
        return 1;
    }

    public static int rollback(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        if (EventList.isEmpty()) {
            context.getSource().sendFeedback(new LiteralText("No events to rollback!"), false);
            return -1;
        }

        World world = context.getSource().getWorld();

        int restoreCount = context.getArgument("Rollback Count", Integer.class);
        int restored = 0;
        for (int i = EventList.size() - 1; i >= 0 && restored < restoreCount; i--) {
            ThimbleEvent currentEvent = EventList.get(i);
            if (currentEvent.rollback(world)) {
                restored++;
            }
        }

        context.getSource().sendFeedback(new LiteralText("Rolled back last " + restored + " event(s)!"), false);
        return 1;
    }

    public static int restore(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        if (EventList.isEmpty()) {
            context.getSource().sendFeedback(new LiteralText("No events to restore!"), false);
            return -1;
        }

        World world = context.getSource().getWorld();
        int revertCount = context.getArgument("Restore Count", Integer.class);
        int reverted = 0;
        for (int i = EventList.size() - 1; i >= 0 && reverted < revertCount; i--) {
            ThimbleEvent currentEvent = EventList.get(i);
            if (currentEvent.restore(world)) {
                reverted++;
            }
        }

        context.getSource().sendFeedback(new LiteralText("Restored last " + reverted + " rolled back event(s)!"), false);
        return 1;
    }

    public static int clearEvents(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ThimbleEventLogger.saveEventsToFile();
        return 1;
    }


    // * Start Helper Methods * //

    private static List<ThimbleBlockUpdateEvent> getBlockUpdateEventsFromFile(Vec3d originPos, int chunkX, int chunkY, int chunkZ, int distance, String dimensionName) {
        return getBlockUpdateEventsFromFile(originPos, chunkX, chunkY, chunkZ, distance, dimensionName, null, ThimbleSubType.ALL);
    }

    private static List<ThimbleBlockUpdateEvent> getBlockUpdateEventsFromFile(Vec3d originPos, int chunkX, int chunkY, int chunkZ, int distance, String dimensionName, String playerName, ThimbleSubType subtype) {

        List<ThimbleBlockUpdateEvent> events = new ArrayList<ThimbleBlockUpdateEvent>();
        String[] dimension = dimensionName.split(":");
        String filename = "thimble/events/";

        try {
            filename += dimension[0] + "/" + dimension[1] + "/";
        } catch (IndexOutOfBoundsException e) {
            filename = "thimble/events/" + dimensionName + "/";
        }

        filename += String.format("r%s,%s/c%s,%s,%s.thimble", chunkX / 32, chunkZ / 32, chunkX, chunkY, chunkZ);

        Scanner inFile = null;
        try {
            inFile = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            return events;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ThimbleBlockUpdateEvent.class, new ThimbleBlockUpdateEventSerializer())
                .create();
        while (inFile.hasNextLine()) {
            numChecked++;
            String line = inFile.nextLine();
            ThimbleBlockUpdateEvent currEvent = gson.fromJson(line, ThimbleBlockUpdateEvent.class);

            if (meetsLookupRequirements(currEvent, originPos, distance, playerName, subtype)) {
                events.add(currEvent);
            }
        }

        inFile.close();

        return events;
    }

    private static boolean meetsLookupRequirements(ThimbleBlockUpdateEvent event, Vec3d originPos, int distance, String playerName, ThimbleSubType subType) {
        if (originPos.isInRange((Position) event.getPos(), distance)) {
            if (playerName != null) {
                return playerName.equals(event.getCausingEntity()) &&
                        (subType == ThimbleSubType.ALL || subType == event.getSubType());
            } else if (subType != ThimbleSubType.ALL) {
                return subType == event.getSubType();
            } else {
                return true;
            }
        }
        return false;
    }

}
