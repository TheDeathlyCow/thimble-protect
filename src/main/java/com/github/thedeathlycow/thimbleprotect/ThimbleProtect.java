package com.github.thedeathlycow.thimbleprotect;

import com.github.thedeathlycow.thimbleprotect.commands.ThimbleProtectCommand;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.DoorBlock;

import java.io.*;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ThimbleProtect implements ModInitializer {

    public static final String MODID = "thimble-protect";

    public static ThimbleConfig CONFIG;

    @Override
    public void onInitialize() {
        System.out.println("Initializing ThimbleProtect...");

        this.registerCommands();
        this.createDirectories();

        try {
            this.readConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Config Settings: " + CONFIG.toString());

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
        String failedMessage = "Failed to create %s directory (file may already exist).";
        String successMessage = "Created %s directory.";

        File thimbleFile = new File("thimble");
        if (thimbleFile.mkdir()) {
            System.out.println(String.format(successMessage, "thimble"));
        } else {
            System.out.println(String.format(failedMessage, "thimble"));
        }
        File eventFile = new File("thimble/events");
        if (eventFile.mkdir()) {
            System.out.println(String.format(successMessage, "events"));
        } else {
            System.out.println(String.format(failedMessage, "events"));
        }
    }

    private void readConfig() throws IOException {
        System.out.println("Reading config...");
        FileReader configFile = null;
        try {
            configFile = new FileReader("thimble/config.json");
        } catch (FileNotFoundException e) {
            System.out.println("Config not found, creating new config...");
            this.createNewConfig();
            System.out.println("New config created!");
            return;
        }
        CONFIG = new Gson().fromJson(configFile, ThimbleConfig.class);
        configFile.close();
        System.out.println("Config loaded!");
    }

    private void createNewConfig() throws IOException {
        CONFIG = ThimbleConfig.createDefaultConfig();
        FileWriter writer = new FileWriter("thimble/config.json");
        writer.write(new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(CONFIG));
        writer.close();
    }
}
