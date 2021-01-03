package com.github.thedeathlycow.thimbleprotect.commands;

import com.github.thedeathlycow.thimbleprotect.events.ThimbleBlockBreakEvent;
import com.github.thedeathlycow.thimbleprotect.events.ThimbleEvent;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import static com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger.EventList;

public class ThimbleProtectCommand {

    public static int lookup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (EventList.isEmpty()) {
            context.getSource().sendFeedback(new LiteralText("No events to lookup!"), false);
            return -1;
        }

        String messageString = "The last %d event(s) were: ";
        int lookUpCount = context.getArgument("Lookup Count", Integer.class);
        int lookedUp = 0;
        for (int i = EventList.size() - 1; i >= 0 && lookedUp < lookUpCount; i--) {
            messageString += "\n" + EventList.get(i).toString();
            lookedUp++;
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
            if (!currentEvent.restored) {
                restored ++;
                currentEvent.restoreEvent(world);
            }
        }

        context.getSource().sendFeedback(new LiteralText("Restored last " + restored + " event(s)!"), false);
        return 1;
    }

    public static int revert(CommandContext<ServerCommandSource> context) throws  CommandSyntaxException {

        if (EventList.isEmpty()) {
            context.getSource().sendFeedback(new LiteralText("No events to revert!"), false);
            return -1;
        }

        World world = context.getSource().getWorld();
        int revertCount = context.getArgument("Revert Count", Integer.class);
        int reverted = 0;
        for (int i = EventList.size() - 1; i >= 0 && reverted < revertCount; i--) {
            ThimbleEvent currentEvent = EventList.get(i);
            if (currentEvent.restored) {
                reverted ++;
                currentEvent.revertRestoration(world);
            }
        }

        context.getSource().sendFeedback(new LiteralText("Reverted last " + reverted + " restored event(s)!"), false);
        return 1;
    }
}
