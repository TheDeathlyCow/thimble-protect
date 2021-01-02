package com.github.thedeathlycow.thimbleprotect;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class ThimbleProtect implements ModInitializer {

    public static final String MODID = "thimble-protect";

    @Override
    public void onInitialize() {
        System.out.println("Initializing ThimbleProtect...");

        this.registerCommands();

        System.out.println("ThimbleProtect initialised!");
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

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("getCurrentLog").executes(context -> {
                String message = "Last 1024 Thimble Events:";
                for (ThimbleEvent event : ThimbleEventLogger.EventList) {
                    message += "\n" + event;
                }
                final Text text = new LiteralText(message);
                context.getSource().sendFeedback(text, false);
                return 1;
            }));
        });

        System.out.println("ThimbleProtect commands registered!");
    }
}
