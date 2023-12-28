/*
 * This file is part of Cactus Rush, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.utils;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.jadedutils.MathUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A collection of utilities for dealing with the Cactus Rush leveling system.
 */
public class LevelUtils {
    private static CactusRushPlugin plugin;

    /**
     * Creates the utility class.
     * @param pl Plugin instance.
     */
    public LevelUtils(CactusRushPlugin pl) {
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
        ConfigurationSection section = plugin.settingsManager().getLevels().getConfigurationSection("Levels");
        if(section == null) {
            return null;
        }

        // Loops through each level.
        for(String group : section.getKeys(false)) {
            // Checks if the given level is higher than the read value.
            if(Integer.parseInt(group) <= level) {
                // If so, gets the format and applies it
                formattedLevel = plugin.settingsManager().getLevels().getString("Levels." + group + ".Format").replace("%level%", level + "");
            }
        }

        // Returns the formatted level with color codes translated.
        return StringUtils.translateLegacyMessage(formattedLevel);
    }

    public static String getFormattedExperience(int experience) {
        return  MathUtils.format(experience);
    }

    public static String getFormattedRequiredExperience(int level) {
        return  MathUtils.format(getRequiredExperience(level));
    }

    public static int getRequiredExperience(int level) {
        int requiredExperience = 0;


        ConfigurationSection section = plugin.settingsManager().getLevels().getConfigurationSection("Levels");

        if(section == null) {
            return -1;
        }

        for(String group : section.getKeys(false)) {
            if(Integer.parseInt(group) <= level) {
                requiredExperience = plugin.settingsManager().getLevels().getInt("Levels." + group + ".NextLevelXP");
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

        return StringUtils.translateLegacyMessage(bar);
    }
}