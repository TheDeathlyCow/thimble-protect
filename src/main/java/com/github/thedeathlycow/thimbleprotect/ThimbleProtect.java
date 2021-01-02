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

        this.registerCommands();
        
    }

    private void registerCommands() {
        System.out.println("Registering ThimbleProtect commands...");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("ping").executes(context -> {
                final Text text = new LiteralText("pong!");
                context.getSource().sendFeedback(text, true);
                return 1;
            }));
        });

        System.out.println("ThimbleProtect commands registered!");
    }
}
