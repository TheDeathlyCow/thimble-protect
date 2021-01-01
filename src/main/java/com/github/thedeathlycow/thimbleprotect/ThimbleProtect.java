package com.github.thedeathlycow.thimbleprotect;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class ThimbleProtect implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("Initializing ThimbleProtect!");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("foo").executes(context -> {
                final Text text = new LiteralText("bar");

                context.getSource().getMinecraftServer().getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, context.getSource().getPlayer().getUuid());
                return 1;
            }));
        });
        
    }

}
