package com.github.firewolf8385.cactusrush;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Allows easy access to plugin configuration
 * files. Stores spawn and arena locations.
 */
public class SettingsManager {
    private FileConfiguration arenas;
    private final File arenasFile;
    private FileConfiguration config;
    private final File configFile;
    private FileConfiguration levels;
    private final File levalsFile;

    /**
     * Creates or loads the existing configuration files.
     * @param plugin Instance of the plugin.
     */
    public SettingsManager(CactusRush plugin) {
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        configFile = new File(plugin.getDataFolder(), "config.yml");
        plugin.saveConfig();

        levalsFile = new File(plugin.getDataFolder(), "levels.yml");
        levels = YamlConfiguration.loadConfiguration(levalsFile);
        if(!levalsFile.exists()) {
            plugin.saveResource("levels.yml", false);
        }

        arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        arenas = YamlConfiguration.loadConfiguration(arenasFile);
        if(!arenasFile.exists()) {
            plugin.saveResource("arenas.yml", false);
        }
    }

    /**
     * Get the arena configuration file.
     * @return Arena configuration file.
     */
    public FileConfiguration getArenas() {
        return arenas;
    }

    /**
     * Get the main configuration file.
     * @return Main configuration file.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Get the levels configuration file.
     * @return Levels configuration file.
     */
    public FileConfiguration getLevels() {
        return levels;
    }

    /**
     * Allows us to save the config file after changes are made.
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        }
        catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This updates the config in case changes are made.
     */
    public void reloadConfig() {
        saveConfig();
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Allows us to save the arena config file after changes are made.
     */
    public void saveArenas() {
        try {
            arenas.save(arenasFile);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Allows us to save the config file after changes are made.
     */
    public void saveLevels() {
        try {
            levels.save(levalsFile);
        }
        catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * THis updates the arenas file in case changes are made.
     */
    public void reloadArenas() {
        saveArenas();
        arenas = YamlConfiguration.loadConfiguration(arenasFile);
    }

    /**
     * This updates the config in case changes are made.
     */
    public void reloadLevels() {
        saveConfig();
        levels = YamlConfiguration.loadConfiguration(levalsFile);
    }
}