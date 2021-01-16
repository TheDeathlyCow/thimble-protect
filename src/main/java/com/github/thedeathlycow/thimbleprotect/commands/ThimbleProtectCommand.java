package com.github.thedeathlycow.thimbleprotect.commands;

import com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleEvent;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Arrays;

import static com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger.EventList;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ThimbleProtectCommand {

    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("thimble")
                    .requires(source -> source.hasPermissionLevel(4))
                    .then(literal("lookup")
                            .then(argument("Lookup Count", IntegerArgumentType.integer(1))
                                    .executes(ThimbleProtectCommand::lookup)))
                    .then(literal("restore")
                            .then(argument("Restore Count", IntegerArgumentType.integer(1))
                                    .executes(ThimbleProtectCommand::restore)))
                    .then(literal("revert")
                            .then(argument("Revert Count", IntegerArgumentType.integer(1))
                                    .executes(ThimbleProtectCommand::revert)))
                    .then(literal("clearEvents")
                            .executes(ThimbleProtectCommand::clearEvents)));
        });
    }

    public static int lookup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (EventList.isEmpty()) {
            context.getSource().sendFeedback(new LiteralText("No events to lookup!"), false);
            return -1;
        }

        String messageString = "The last %d event(s) were: ";
        int lookUpCount = context.getArgument("Lookup Count", Integer.class);
        int lookedUp = 0;

        for (int i = EventList.size() - 1; i >= 0 && lookedUp < lookUpCount; i--) {
            try {
                messageString += "\n" + EventList.get(i).toString();
                lookedUp++;
            } catch (Exception e) {
                System.out.println("Exception: " + Arrays.toString(e.getStackTrace()));
            }
        }


        Text text = new LiteralText(String.format(messageString, lookedUp));
        context.getSource().sendFeedback(text, false);
        return 1;
    }

    public static int restore(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        if (EventList.isEmpty()) {
            context.getSource().sendFeedback(new LiteralText("No events to restore!"), false);
            return -1;
        }

        World world = context.getSource().getWorld();
        int restoreCount = context.getArgument("Restore Count", Integer.class);
        int restored = 0;
        for (int i = EventList.size() - 1; i >= 0 && restored < restoreCount; i--) {
            ThimbleEvent currentEvent = EventList.get(i);
            if (currentEvent.restoreEvent(world)) {
                restored++;
            }
        }

        context.getSource().sendFeedback(new LiteralText("Restored last " + restored + " event(s)!"), false);
        return 1;
    }

    public static int revert(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        if (EventList.isEmpty()) {
            context.getSource().sendFeedback(new LiteralText("No events to revert!"), false);
            return -1;
        }

        World world = context.getSource().getWorld();
        int revertCount = context.getArgument("Revert Count", Integer.class);
        int reverted = 0;
        for (int i = EventList.size() - 1; i >= 0 && reverted < revertCount; i--) {
            ThimbleEvent currentEvent = EventList.get(i);
            if (currentEvent.revertRestoration(world)) {
                reverted++;
            }
        }

        context.getSource().sendFeedback(new LiteralText("Reverted last " + reverted + " restored event(s)!"), false);
        return 1;
    }

    public static int clearEvents(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ThimbleEventLogger.saveEventsToFile();
        return 1;
    }
}
