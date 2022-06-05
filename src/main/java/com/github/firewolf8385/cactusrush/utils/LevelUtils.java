package com.github.firewolf8385.cactusrush.utils;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A collection of utilities for dealing with the Cactus Rush leveling system.
 */
public class LevelUtils {
    private static CactusRush plugin;

    /**
     * Creates the utility class.
     * @param pl Plugin instance.
     */
    public LevelUtils(CactusRush pl) {
        plugin = pl;
    }

    /**
     * Get the formatted string of a certain level.
     * @param level Level to get string of.
     * @return Formatted string of the level.
     */
    public static String getFormattedLevel(int level) {
        String formattedLevel = "";

        // Makes sure the level settings exist.
        ConfigurationSection section = plugin.getSettingsManager().getLevels().getConfigurationSection("Levels");
        if(section == null) {
            return null;
        }

        // Loops through each level.
        for(String group : section.getKeys(false)) {
            // Checks if the given level is higher than the read value.
            if(Integer.parseInt(group) <= level) {
                // If so, gets the format and applies it
                formattedLevel = plugin.getSettingsManager().getLevels().getString("Levels." + group + ".Format").replace("%level%", level + "");
            }
        }

        // Returns the formatted level with color codes translated.
        return ChatUtils.translate(formattedLevel);
    }

    public static String getFormattedExperience(int experience) {
        return  MathUtils.format(experience);
    }

    public static String getFormattedRequiredExperience(int level) {
        return  MathUtils.format(getRequiredExperience(level));
    }

    public static int getRequiredExperience(int level) {
        int requiredExperience = 0;


        ConfigurationSection section = plugin.getSettingsManager().getLevels().getConfigurationSection("Levels");

        if(section == null) {
            return -1;
        }

        for(String group : section.getKeys(false)) {
            if(Integer.parseInt(group) <= level) {
                requiredExperience = plugin.getSettingsManager().getLevels().getInt("Levels." + group + ".NextLevelXP");
            }
        }

        return requiredExperience;
    }

    public static String getSmallLevelBar(int currentExperience, int level) {
        int maxExperience = getRequiredExperience(level);
        int perSquare = maxExperience/10;

        String bar = "&8[&b";

        int squares = 0;
        for(int i = currentExperience; i > 0; i-= perSquare) {
            bar += "■";
            squares++;
        }

        bar += "&7";

        for(int i = squares; i < 10; i++) {
            bar += "■";
        }

        bar += "&8]";

        return ChatUtils.translate(bar);
    }
}