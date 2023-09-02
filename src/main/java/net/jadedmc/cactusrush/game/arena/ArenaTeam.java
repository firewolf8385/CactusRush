/*
 * This file is part of JadedChat, licensed under the MIT License.
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

import net.jadedmc.cactusrush.utils.BlockUtils;
import net.jadedmc.cactusrush.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a team configured for an Arena.
 */
public class ArenaTeam {
    private final Location bounds1;
    private final Location bounds2;
    private final ArenaTeamScoreRoom scoreRoom;
    private final Location spawnPoint;
    private final Collection<Block> goalBlocks = new HashSet<>();
    private final Collection<Block> barrierBlocks = new HashSet<>();

    /**
     * Creates the ArenaTeam.
     * @param config Configuration section of the team.
     */
    public ArenaTeam(ConfigurationSection config) {
        bounds1 = LocationUtils.fromConfig(Objects.requireNonNull(config.getConfigurationSection("bounds1")));
        bounds2 = LocationUtils.fromConfig(Objects.requireNonNull(config.getConfigurationSection("bounds2")));
        scoreRoom = new ArenaTeamScoreRoom(Objects.requireNonNull(config.getConfigurationSection("scoreRoom")));
        spawnPoint = LocationUtils.fromConfig(Objects.requireNonNull(config.getConfigurationSection("spawnPoint")));

        loadBarriers(Objects.requireNonNull(config.getConfigurationSection("barriers")));
        loadGoals(Objects.requireNonNull(config.getConfigurationSection("goals")));
    }

    /**
     * Loads the configured barriers for the team.
     * @param config Configuration section.
     */
    public void loadBarriers(ConfigurationSection config) {
        for(String blockID : config.getKeys(false)) {
            ConfigurationSection blockSection = config.getConfigurationSection(blockID);

            if(blockSection == null) {
                continue;
            }

            barrierBlocks.add(Bukkit.getWorlds().get(0).getBlockAt(LocationUtils.fromConfig(blockSection)));
        }
    }

    /**
     * Loads the configured goal blocks for the team.
     * @param config Configuration section.
     */
    public void loadGoals(ConfigurationSection config) {
        for(String blockID : config.getKeys(false)) {
            ConfigurationSection blockSection = config.getConfigurationSection(blockID);

            if(blockSection == null) {
                continue;
            }

            goalBlocks.add(Bukkit.getWorlds().get(0).getBlockAt(LocationUtils.fromConfig(blockSection)));
        }
    }

    /**
     * Get the barrier blocks of the team.
     * @return Team barrier blocks.
     */
    public Collection<Block> getBarrierBlocks(World world) {
        return BlockUtils.replaceWorld(world, barrierBlocks);
    }

    /**
     * Get bounds 1 of the team.
     * @return Bounds 1 location.
     */
    public Location getBounds1(World world) {
        return LocationUtils.replaceWorld(world, bounds1);
    }

    /**
     * Get bounds 2 of the team.
     * @return Bounds 2 location.
     */
    public Location getBounds2(World world) {
        return LocationUtils.replaceWorld(world, bounds2);
    }

    /**
     * Get the goal blocks of the team.
     * @return Team goal blocks.
     */
    public Collection<Block> getGoalBlocks(World world) {
        return BlockUtils.replaceWorld(world, goalBlocks);
    }

    /**
     * Gets the team's score room.
     * @return Score room of the team.
     */
    public ArenaTeamScoreRoom getScoreRoom() {
        return scoreRoom;
    }

    /**
     * Get the team's spawn point.
     * @return Spawn point of the team.
     */
    public Location getSpawnPoint(World world) {
        return LocationUtils.replaceWorld(world, spawnPoint);
    }

    /**
     * Represents the score room of the team.
     */
    private static class ArenaTeamScoreRoom {
        private Location bounds1;
        private Location bounds2;
        private Location spawnPoint;
        private final Collection<Block> scoreRoomBlocks = new HashSet<>();

        /**
         * Creates the Arena Score Room.
         * @param config Configuration Section to load it from.
         */
        public ArenaTeamScoreRoom(ConfigurationSection config) {
            loadBounds1(config.getConfigurationSection("bounds1"));
            loadBounds2(config.getConfigurationSection("bounds2"));
            loadSpawnPoint(config.getConfigurationSection("spawnPoint"));
            loadBlocks(config.getConfigurationSection("blocks"));
        }

        /**
         * Get the score room bounds 1.
         * @return Bounds 1 location.
         */
        public Location getBounds1(World world) {
            return LocationUtils.replaceWorld(world, bounds1);
        }

        /**
         * Get the score room bounds 2.
         * @return Bounds 2 location.
         */
        public Location getBounds2(World world) {
            return LocationUtils.replaceWorld(world, bounds2);
        }

        /**
         * Get the blocks that the score room is made up of.
         * @return Score room blocks.
         */
        public Collection<Block> getScoreRoomBlocks(World world) {
            return BlockUtils.replaceWorld(world, scoreRoomBlocks);
        }

        /**
         * Get the spawn point for the score room.
         * @return Score room spawn point.
         */
        public Location getSpawnPoint(World world) {
            return LocationUtils.replaceWorld(world, spawnPoint);
        }

        /**
         * Loads the score room blocks.
         * @param config Configuration Section.
         */
        private void loadBlocks(ConfigurationSection config) {
            for(String blockID : config.getKeys(false)) {
                ConfigurationSection blockSection = config.getConfigurationSection(blockID);

                if(blockSection == null) {
                    continue;
                }

                scoreRoomBlocks.add(Bukkit.getWorlds().get(0).getBlockAt(LocationUtils.fromConfig(blockSection)));
            }
        }

        /**
         * Load the score room bounds 1 from a configuration section.
         * @param config Configuration Section.
         */
        private void loadBounds1(ConfigurationSection config) {
            bounds1 = LocationUtils.fromConfig(config);
        }

        /**
         * Load the score room bounds 2 from a configuration section.
         * @param config Configuration Section.
         */
        private void loadBounds2(ConfigurationSection config) {
            bounds2 = LocationUtils.fromConfig(config);
        }

        /**
         * Load the score room spawn point from a configuration section.
         * @param config Configuration Section.
         */
        private void loadSpawnPoint(ConfigurationSection config) {
            spawnPoint = LocationUtils.fromConfig(config);
        }
    }
}