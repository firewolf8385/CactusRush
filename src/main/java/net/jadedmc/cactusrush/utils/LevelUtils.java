/*
 * This file is part of CactusRush, licensed under the MIT License.
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
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.utils;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.jadedutils.MathUtils;
import net.jadedmc.jadedutils.chat.StringUtils;
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
    public LevelUtils(final CactusRushPlugin pl) {
        plugin = pl;
    }

    /**
     * Get the formatted string of a certain level.
     * @param level Level to get string of.
     * @return Formatted string of the level.
     */
    public static String getFormattedLevel(final int level) {
        String formattedLevel = "";

        // Makes sure the level settings exist.
        final ConfigurationSection section = plugin.getConfigManager().getLevels().getConfigurationSection("Levels");
        if(section == null) {
            return null;
        }

        // Loops through each level.
        for(final String group : section.getKeys(false)) {
            // Checks if the given level is higher than the read value.
            if(Integer.parseInt(group) <= level) {
                // If so, gets the format and applies it
                formattedLevel = plugin.getConfigManager().getLevels().getString("Levels." + group + ".Format").replace("%level%", level + "");
            }
        }

        // Returns the formatted level with color codes translated.
        return StringUtils.translateLegacyMessage(formattedLevel);
    }

    /**
     * Gets a formatted version of an amount of experience.
     * @param experience Amount of experience.
     * @return Formatted String of that amount.
     */
    public static String getFormattedExperience(final int experience) {
        return  MathUtils.format(experience);
    }

    /**
     * <b>Deprecated because why is this here?</b>
     * Gets a formmatted version of the required experience of a given level.
     * @param level Level to get required experience for.
     * @return Formatted String of that amount.
     */
    @Deprecated
    public static String getFormattedRequiredExperience(final int level) {
        return  MathUtils.format(getRequiredExperience(level));
    }

    /**
     * Get the amount of experience required to reach a given level.
     * @param level Level to get experience amount of.
     * @return Amount of experience needed to obtain.
     */
    public static int getRequiredExperience(final int level) {
        int requiredExperience = 0;

        final ConfigurationSection section = plugin.getConfigManager().getLevels().getConfigurationSection("Levels");

        if(section == null) {
            return -1;
        }

        for(final String group : section.getKeys(false)) {
            if(Integer.parseInt(group) <= level) {
                requiredExperience = plugin.getConfigManager().getLevels().getInt("Levels." + group + ".NextLevelXP");
            }
        }

        return requiredExperience;
    }

    /**
     * Creates a small formatted level bar based on the current experience and current level.
     * @param currentExperience Current experience the player has.
     * @param level Current level of the player.
     * @return Formatted small experience bar.
     */
    public static String getSmallLevelBar(final int currentExperience, final int level) {
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