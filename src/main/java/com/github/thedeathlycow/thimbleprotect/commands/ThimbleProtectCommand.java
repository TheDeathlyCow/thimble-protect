package com.github.thedeathlycow.thimbleprotect.commands;

import com.github.thedeathlycow.thimbleprotect.ThimbleEvent;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import static com.github.thedeathlycow.thimbleprotect.ThimbleEvent.ThimbleEventType;
import static com.github.thedeathlycow.thimbleprotect.ThimbleEventLogger.EventList;

public class ThimbleProtectCommand {

    public static int lookup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ThimbleEvent lastEvent = null;
        String messageString = "The last event was: ";
        try {
            lastEvent = EventList.get(EventList.size() - 1);
            messageString += lastEvent.toString();
        } catch (IndexOutOfBoundsException e) {
            messageString += "no events so far...";
        }
        Text text = new LiteralText(messageString);
        context.getSource().sendFeedback(text, true);
        return 1;
    }

    public static int restore(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ThimbleEvent lastEvent = null;

        try {
            lastEvent = EventList.get(EventList.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            context.getSource().sendFeedback(new LiteralText("&cNo events to restore!"), false);
            return -1;
        }


        World world = context.getSource().getWorld();
        int restoreCount = context.getArgument("Restore Count", Integer.class);
        for (int i = 0; !EventList.isEmpty() && i < restoreCount; i++) {
            try {
                ThimbleEvent currentEvent = EventList.get(EventList.size() - 1);
                if (currentEvent.type == ThimbleEventType.EXPLOSION || currentEvent.type == ThimbleEventType.BLOCK_BREAK) {
                    world.setBlockState(currentEvent.pos, currentEvent.state);
                }
                EventList.remove(EventList.size() - 1);
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }


        context.getSource().sendFeedback(new LiteralText("Restored last " + restoreCount + " event(s)!"), false);
        return 1;
    }
}
