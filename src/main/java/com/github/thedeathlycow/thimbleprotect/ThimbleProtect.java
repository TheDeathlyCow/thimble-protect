package com.github.thedeathlycow.thimbleprotect;

import com.github.thedeathlycow.thimbleprotect.commands.ThimbleProtectCommand;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.DoorBlock;

import static net.minecraft.server.command.CommandManager.argument;
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
                                    .executes(ThimbleProtectCommand::revert))));
        });

        System.out.println("ThimbleProtect commands registered!");
    }
}
