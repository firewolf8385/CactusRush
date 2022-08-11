package com.github.firewolf8385.cactusrush.game.arena;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.team.TeamColor;
import com.github.firewolf8385.cactusrush.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

/**
 * Represents an arena that a game is held in.
 */
public class Arena {
    private final String id;
    private final String name;
    private final int teamSize;
    private final Location waitingArea;
    private final Map<TeamColor, Location> spawns = new LinkedHashMap<>();
    private final Map<TeamColor, Collection<Location>> barriers = new HashMap<>();
    private final Map<TeamColor, Collection<Location>> goals = new HashMap<>();
    private final Collection<Block> blocks = new HashSet<>();

    /**
     * Creates the arena object.
     * @param plugin Instance of the plugin.
     * @param id id of the arena.
     */
    public Arena(CactusRush plugin, String id) {
        this.id = id;

        FileConfiguration config = plugin.getSettingsManager().getArenas();

        name = config.getString("Arenas." + id + ".Name");
        teamSize = config.getInt("Arenas." + id + ".TeamSize");
        waitingArea = LocationUtils.fromConfig(config, "Arenas." + id + ".Waiting");

        // Loads the teams and their spawns.
        ConfigurationSection teamsSection = config.getConfigurationSection("Arenas." + id + ".Teams");
        teamsSection.getKeys(false).forEach(teamName -> {
            TeamColor team = TeamColor.valueOf(teamName);

            // Loads the team spawns.
            Location spawn = LocationUtils.fromConfig(config, "Arenas." + id + ".Teams." + teamName + ".Spawn");
            spawns.put(team, spawn);

            // Loads the team barriers.
            ConfigurationSection barrierSection = config.getConfigurationSection("Arenas." + id + ".Teams." + teamName + ".Barriers");
            Collection<Location> teamBarriers = new HashSet<>();
            barrierSection.getKeys(false).forEach(barrier -> teamBarriers.add(LocationUtils.fromConfig(config, "Arenas." + id + ".Teams." + teamName + ".Barriers." + barrier)));
            barriers.put(team, teamBarriers);

            // Loads the team goal blocks.
            ConfigurationSection goalSection = config.getConfigurationSection("Arenas." + id + ".Teams." + teamName + ".Goals");
            Collection<Location> teamGoals = new HashSet<>();
            goalSection.getKeys(false).forEach(goal -> teamGoals.add(LocationUtils.fromConfig(config, "Arenas." + id + ".Teams." + teamName + ".Goals." + goal)));
            goals.put(team, teamGoals);
        });
    }

    /**
     * Adds a block to the cache of modified blocks.
     * @param block Blocks to add to the cache.
     */
    public void addBlock(Block block) {
        blocks.add(block);
    }

    /**
     * Get a map of the barrier locations for each team.
     * @return Barrier locations of each team.
     */
    public Map<TeamColor, Collection<Location>> getBarriers() {
        return barriers;
    }

    /**
     * Get a map of the goal locations for each team.
     * @return Goal locations of each team.
     */
    public Map<TeamColor, Collection<Location>> getGoals() {
        return goals;
    }

    /**
     * Gets the id of the arena.
     * @return Id of the arena.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the maximum numbers of players in the arena.
     * @return Maximum amount of players.
     */
    public int getMaxPlayers() {
        return (teamSize * spawns.size());
    }

    /**
     * Gets the minimum number of players required to start the game.
     * @return Minumum number of players.
     */
    public double getMinPlayers() {
        double max = getMaxPlayers();

        return ((max/4.0) * 3.0) * 1;
    }

    /**
     * Get the name of the arena.
     * @return Get the name of the arena.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all teams and their spawns.
     * @return Map of teams with their spawns.
     */
    public Map<TeamColor, Location> getSpawns() {
        return spawns;
    }

    /**
     * Get the max number of players on each team.
     * @return Maximum size of each team.
     */
    public int getTeamSize() {
        return teamSize;
    }

    /**
     * Get the spawn for the waiting area.
     * @return Waiting area spawn.
     */
    public Location getWaitingArea() {
        return waitingArea;
    }

    /**
     * Resets the arena.
     */
    public void reset() {
        blocks.forEach(block -> block.setType(Material.AIR));
    }
}