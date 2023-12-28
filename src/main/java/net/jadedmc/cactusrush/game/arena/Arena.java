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
package net.jadedmc.cactusrush.game.arena;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.game.arena.file.ArenaFile;
import net.jadedmc.jadedutils.FileUtils;
import net.jadedmc.cactusrush.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * Represents an area in which a game is played.
 */
public class Arena {
    private final String id;
    private final List<String> builders = new ArrayList<>();
    private final Collection<Mode> modes = new HashSet<>();
    private final String name;
    private final ArenaFile arenaFile;
    private final List<ArenaTeam> teams = new ArrayList<>();
    private final int voidLevel;
    private final Location waitingArea;
    private final File configFile;

    /**
     * Creates the arena.
     * @param plugin Instance of the plugin.
     * @param configFile Configuration file for the arena.
     */
    public Arena(CactusRushPlugin plugin, File configFile) {
        this.configFile = configFile;
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        id = FileUtils.removeFileExtension(configFile.getName(), true);
        arenaFile = plugin.arenaManager().arenaFileManager().loadArenaFile(id);
        name = config.getString("name");

        if(config.isSet("builders")) {
            builders.addAll(config.getStringList("builders"));
        }
        else {
            builders.add("JadedMC");
        }

        waitingArea = LocationUtils.fromConfig(Objects.requireNonNull(config.getConfigurationSection("waitingArea")));
        voidLevel = config.getInt("voidLevel");

        // Load the modes the arena supports.
        for(String mode : config.getStringList("modes")) {
            modes.add(Mode.valueOf(mode));
        }

        // Load the configured teams.
        for(String team : Objects.requireNonNull(config.getConfigurationSection("teams")).getKeys(false)) {
            teams.add(new ArenaTeam(Objects.requireNonNull(config.getConfigurationSection("teams." + team))));
        }
    }

    /**
     * Gets the file the arena is stored in.
     * @return Arena file.
     */
    public ArenaFile arenaFile() {
        return arenaFile;
    }

    /**
     * Gets the builders of the arena.
     * @return Arena builders.
     */
    public List<String> builders() {
        return builders;
    }

    /**
     * Get the arena's configuration file.
     * @return Arena config file.
     */
    public File configFile() {
        return configFile;
    }

    /**
     * Gets the id of the arena.
     * @return Arena id.
     */
    public String id() {
        return id;
    }

    /**
     * Gets the modes the arena is compatible with.
     * @return Compatible modes.
     */
    public Collection<Mode> modes() {
        return modes;
    }

    /**
     * Gets the name of the arena.
     * @return Arena name.
     */
    public String name() {
        return name;
    }

    /**
     * Gets the teams of the arena.
     * @return Arena teams.
     */
    public List<ArenaTeam> teams() {
        return teams;
    }

    /**
     * Get the waiting area of the arena in a specific world.
     * @param world World to get waiting area of.
     * @return Waiting area location.
     */
    public Location waitingArea(World world) {
        return LocationUtils.replaceWorld(world, waitingArea);
    }

    /**
     * Gets the y-level in which players die.
     * @return Arena void level.
     */
    public int voidLevel() {
        return voidLevel;
    }
}