package com.github.thedeathlycow.thimbleprotect;

import com.github.thedeathlycow.thimbleprotect.commands.ThimbleProtectCommand;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.DoorBlock;

import java.io.File;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ThimbleProtect implements ModInitializer {

    public static final String MODID = "thimble-protect";

    @Override
    public void onInitialize() {
        System.out.println("Initializing ThimbleProtect...");

        this.registerCommands();
        this.createDirectories();

        System.out.println("ThimbleProtect initialised!");
    }

    public static void Print(String message) {
        System.out.println("[ThimbleProtect]: " + message);
    }

    private void registerCommands() {
        System.out.println("Registering ThimbleProtect commands...");
        ThimbleProtectCommand.registerCommand();
        System.out.println("ThimbleProtect commands registered!");
    }

    private void createDirectories() {
        File thimbleFile = new File("thimble");
        boolean createdFile = thimbleFile.mkdir();

        if (createdFile) {
            System.out.println("Created thimble directory.");
        } else {
            System.out.println("Failed to create thimble directory (file may already exist).");
        }

        File eventFile = new File("thimble/events");
        createdFile = eventFile.mkdir();

        if (createdFile) {
            System.out.println("Created events directory.");
        } else {
            System.out.println("Failed to create events directory (file may already exist).");
        }

    }
}
