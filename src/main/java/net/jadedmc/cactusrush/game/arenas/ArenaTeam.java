package net.jadedmc.cactusrush.game.arenas;

import net.jadedmc.cactusrush.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
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

        // Loop through the region to find the team goal blocks and barrier blocks.
        Vector max = Vector.getMaximum(bounds1.toVector(), bounds2.toVector());
        Vector min = Vector.getMinimum(bounds1.toVector(), bounds2.toVector());
        for (int i = min.getBlockX(); i <= max.getBlockX();i++) {
            for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
                for (int k = min.getBlockZ(); k <= max.getBlockZ();k++) {
                    Block block = Objects.requireNonNull(spawnPoint.getWorld()).getBlockAt(i,j,k);

                    switch (block.getType()) {
                        case SPONGE -> goalBlocks.add(block);
                        case GLASS -> barrierBlocks.add(block);
                    }
                }
            }
        }
    }

    /**
     * Get the barrier blocks of the team.
     * @return Team barrier blocks.
     */
    public Collection<Block> getBarrierBlocks() {
        return barrierBlocks;
    }

    /**
     * Get bounds 1 of the team.
     * @return Bounds 1 location.
     */
    public Location getBounds1() {
        return bounds1;
    }

    /**
     * Get bounds 2 of the team.
     * @return Bounds 2 location.
     */
    public Location getBounds2() {
        return bounds2;
    }

    /**
     * Get the goal blocks of the team.
     * @return Team goal blocks.
     */
    public Collection<Block> getGoalBlocks() {
        return goalBlocks;
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
    public Location getSpawnPoint() {
        return spawnPoint;
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

            // Loop through the region to find score room blocks.
            Vector max = Vector.getMaximum(bounds1.toVector(), bounds2.toVector());
            Vector min = Vector.getMinimum(bounds1.toVector(), bounds2.toVector());
            for (int i = min.getBlockX(); i <= max.getBlockX();i++) {
                for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
                    for (int k = min.getBlockZ(); k <= max.getBlockZ();k++) {
                        Block block = Objects.requireNonNull(spawnPoint.getWorld()).getBlockAt(i,j,k);

                        if(block.getType() == Material.GLASS) {
                            scoreRoomBlocks.add(block);
                        }
                    }
                }
            }
        }

        /**
         * Get the score room bounds 1.
         * @return Bounds 1 location.
         */
        public Location getBounds1() {
            return bounds1;
        }

        /**
         * Get the score room bounds 2.
         * @return Bounds 2 location.
         */
        public Location getBounds2() {
            return bounds2;
        }

        /**
         * Get the blocks that the score room is made up of.
         * @return Score room blocks.
         */
        public Collection<Block> getScoreRoomBlocks() {
            return scoreRoomBlocks;
        }

        /**
         * Get the spawn point for the score room.
         * @return Score room spawn point.
         */
        public Location getSpawnPoint() {
            return spawnPoint;
        }

        /**
         * Load the score room bounds 1 from a configuration section.
         * @param config Configuration Section.
         */
        public void loadBounds1(ConfigurationSection config) {
            bounds1 = LocationUtils.fromConfig(config);
        }

        /**
         * Load the score room bounds 2 from a configuration section.
         * @param config Configuration Section.
         */
        public void loadBounds2(ConfigurationSection config) {
            bounds2 = LocationUtils.fromConfig(config);
        }

        /**
         * Load the score room spawn point from a configuration section.
         * @param config Configuration Section.
         */
        public void loadSpawnPoint(ConfigurationSection config) {
            spawnPoint = LocationUtils.fromConfig(config);
        }
    }
}