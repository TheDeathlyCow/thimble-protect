package com.github.thedeathlycow.thimbleprotect.commands;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import com.github.thedeathlycow.thimbleprotect.ThimbleProtect;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEvent;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockUpdateEventSerializer;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
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

    private static String distanceName = "distance";
    private static int numChecked = 0;

    public static void registerCommand() {

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("thimble")
                    .requires(source -> source.hasPermissionLevel(4))
                    .then(literal("lookup")
                            .then(argument(distanceName, IntegerArgumentType.integer(1, 100))
                                    .executes(ThimbleProtectCommand::lookup)))
                    .then(literal("rollback")
                            .then(argument(distanceName, IntegerArgumentType.integer(1, 100))
                                    .executes(ThimbleProtectCommand::rollback)))
                    .then(literal("restore")
                            .then(argument(distanceName, IntegerArgumentType.integer(1, 100))
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

    /**
     * Looks up ThimbleEvents and sends a message to the player with all the events found.
     *
     * TODO: Split those events up into separate pages.
     * @param context Command context.
     * @return 1
     * @throws CommandSyntaxException
     */
    public static int lookup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        List<ThimbleEvent> foundEvents = lookupEvents(context);

        // Send header to player
        MutableText header = new LiteralText(" --- ").fillStyle(Style.EMPTY.withColor(TextColor.parse("aqua")))
                .append(new LiteralText("ThimbleProtect Lookup").fillStyle(Style.EMPTY.withColor(TextColor.parse("light_purple"))))
                .append(new LiteralText(" --- ").fillStyle(Style.EMPTY.withColor(TextColor.parse("aqua"))));
        context.getSource().sendFeedback(header, false);

        // Convert event results into a message.
        MutableText message = new LiteralText("");
        for (ThimbleEvent event : foundEvents) {
            message.append(event.toText()).append("\n");
        }
        // Tell the player how many results are in the list.
        message.append("Found ").append("" + foundEvents.size()).append(" event(s)...");

        // Send message to player.
        context.getSource().sendFeedback(message, false);

        // Tell the player (and operators) how many events were checked.
        context.getSource().sendFeedback(new LiteralText("[ThimbleProtect] Checked " + numChecked + " events..."), true);
        return 1;
    }


    public static int rollback(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        List<ThimbleEvent> foundEvents = lookupEvents(context);

        int rolledBackEvents = 0;
        ServerWorld world = context.getSource().getWorld();
        for (ThimbleEvent event : foundEvents) {
            if (event.rollback(world)) {
                rolledBackEvents ++;
            }
        }

        MutableText message = new LiteralText("[ThimbleProtect] Rolledback ")
                .fillStyle(Style.EMPTY.withFormatting(Formatting.DARK_PURPLE));
        message.append(new LiteralText("" + rolledBackEvents)
                .fillStyle(Style.EMPTY.withFormatting(Formatting.LIGHT_PURPLE)));
        message.append(new LiteralText(" event(s).")
                .fillStyle(Style.EMPTY.withFormatting(Formatting.DARK_PURPLE)));

        context.getSource().sendFeedback(message, true);
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

    private static List<ThimbleEvent> lookupEvents(CommandContext<ServerCommandSource> context) {
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
                    foundEvents.addAll(getBlockUpdateEventsFromFile(pos, dx, dy, dz, distance, dimensionName));
                }
            }
        }

        return foundEvents;
    }

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
            ThimbleBlockUpdateEvent currEvent = ThimbleBlockUpdateEventSerializer.GSON.fromJson(line, ThimbleBlockUpdateEvent.class);

            if (meetsLookupRequirements(currEvent, originPos, distance, playerName, subtype)) {
                events.add(currEvent);
            }
        }

        inFile.close();

        return events;
    }

    private static boolean meetsLookupRequirements(ThimbleBlockUpdateEvent event, Vec3d originPos, int distance, String playerName, ThimbleSubType subType) {
        if (originPos.isInRange(new Vec3d(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()), distance)) {
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
