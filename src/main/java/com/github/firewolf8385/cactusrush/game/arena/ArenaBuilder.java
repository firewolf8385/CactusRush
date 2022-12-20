package com.github.firewolf8385.cactusrush.game.arena;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.team.TeamColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Caches data about the arena currently being set up.
 * Once completed, it saves the arena to arenas.yml and loads it into memory.
 */
public class ArenaBuilder {
    private final CactusRush plugin;
    private final String id;
    private String name;
    private final Map<TeamColor, Location> teamSpawns = new HashMap<>();
    private final Map<TeamColor, Collection<Location>> barriers = new HashMap<>();
    private final Map<TeamColor, Collection<Location>> teamGoals = new HashMap<>();
    private final Map<TeamColor, Location> scoreRooms = new HashMap<>();
    private Location waitingArea;
    private int teamSize;
    private int voidLevel = -99;

    /**
     * Creates the Arena Builder.
     * @param plugin Instance of the plugin.
     * @param id id of the Arena.
     */
    public ArenaBuilder(CactusRush plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    /**
     * Adds a block to a team's barrier.
     * @param team Team to add barrier to.
     * @param location Location of the block to add to the barrier.
     */
    public void addBarrier(TeamColor team, Location location) {
        if(barriers.containsKey(team)) {
            barriers.get(team).add(location);
        }
        else {
            Collection<Location> barrier = new HashSet<>();
            barrier.add(location);
            barriers.put(team, barrier);
        }
    }

    /**
     * Adds a scoring block to the arena.
     * @param location Location of the block.
     */
    public void addGoal(TeamColor team, Location location) {
        if(teamGoals.containsKey(team)) {
            teamGoals.get(team).add(location);
            return;
        }

        teamGoals.put(team, new HashSet<>());
        addGoal(team, location);
    }

    /**
     * Adds a score room to the arena.
     * @param team Team to add the score room too.
     * @param location Location of the score room spawn.
     */
    public void addScoreRoom(TeamColor team, Location location) {
        scoreRooms.put(team, location);
    }

    /**
     * Adds a team spawn to the arena.
     * @param team Team to add the spawn to.
     * @param spawn Spawn to add.
     */
    public void addSpawn(TeamColor team, Location spawn) {
        teamSpawns.put(team, spawn);
    }

    /**
     * Get a map of the barrier locations for each team.
     * @return Barrier locations of each team.
     */
    public Map<TeamColor, Collection<Location>> getBarriers() {
        return barriers;
    }

    /**
     * Get the locations of scoring blocks.
     * @return All scoring block locations.
     */
    public Map<TeamColor, Collection<Location>> getGoals() {
        return teamGoals;
    }

    /**
     * Gets the name of the arena.
     * @return Arena's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all the team score rooms.
     * @return Map of the team score rooms.
     */
    public Map<TeamColor, Location> getScoreRooms() {
        return scoreRooms;
    }

    /**
     * Gets all the team spawns.
     * @return Map of the team spawns.
     */
    public Map<TeamColor, Location> getSpawns() {
        return teamSpawns;
    }

    /**
     * Get the number of players each team can have.
     * @return Size of each team.
     */
    public int getTeamSize() {
        return teamSize;
    }

    /**
     * Get the y level in which a player should die.
     * @return Void level.
     */
    public int getVoidLevel() {
        return voidLevel;
    }

    /**
     * Get the spawn location of the waiting area.
     * @return Waiting area spawn location.
     */
    public Location getWaitingArea() {
        return waitingArea;
    }

    /**
     * Sets the name of the arena.
     * @param name Name of the Arena.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the amount of players each team can have.
     * @param teamSize New size of each team.
     */
    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    /**
     * Set the y level in which a player should die.
     * @param voidLevel Void level of the arena.
     */
    public void setVoidLevel(int voidLevel) {
        this.voidLevel = voidLevel;
    }

    /**
     * Set the spawn location of the waiting area.
     * @param waitingArea Waiting area spawn.
     */
    public void setWaitingArea(Location waitingArea) {
        this.waitingArea = waitingArea;
    }

    /**
     * Saves the arena and loads it into memory.
     */
    public void save() {
        // Creates a spot for the new arena.
        ConfigurationSection arenaSection;
        if(plugin.getSettingsManager().getArenas().getConfigurationSection("Arenas") == null) {
            arenaSection = plugin.getSettingsManager().getArenas().createSection("Arenas").createSection(id);
        }
        else {
            arenaSection = plugin.getSettingsManager().getArenas().getConfigurationSection("Arenas").createSection(id);
        }
        arenaSection.set("Name", name);
        arenaSection.set("TeamSize", teamSize);
        arenaSection.set("VoidLevel", voidLevel);

        // Adds the Waiting Area location.
        ConfigurationSection waitingSection = arenaSection.createSection("Waiting");
        waitingSection.set("World", waitingArea.getWorld().getName());
        waitingSection.set("X", waitingArea.getX());
        waitingSection.set("Y", waitingArea.getY());
        waitingSection.set("Z", waitingArea.getZ());
        waitingSection.set("Yaw", waitingArea.getYaw());
        waitingSection.set("Pitch", waitingArea.getPitch());

        // Adds all the team colors.
        ConfigurationSection teamsSection = arenaSection.createSection("Teams");
        for(TeamColor team : teamSpawns.keySet()) {
            // Adds the team spawn.
            ConfigurationSection teamSection = teamsSection.createSection(team.toString());
            ConfigurationSection spawnSection = teamSection.createSection("Spawn");
            spawnSection.set("World", teamSpawns.get(team).getWorld().getName());
            spawnSection.set("X", teamSpawns.get(team).getX());
            spawnSection.set("Y", teamSpawns.get(team).getY());
            spawnSection.set("Z", teamSpawns.get(team).getZ());
            spawnSection.set("Yaw", teamSpawns.get(team).getYaw());
            spawnSection.set("Pitch", teamSpawns.get(team).getPitch());

            // Adds the team score room.
            ConfigurationSection scoreRoomSection = teamSection.createSection("ScoreRoom");
            scoreRoomSection.set("World", scoreRooms.get(team).getWorld().getName());
            scoreRoomSection.set("X", scoreRooms.get(team).getX());
            scoreRoomSection.set("Y", scoreRooms.get(team).getY());
            scoreRoomSection.set("Z", scoreRooms.get(team).getZ());
            scoreRoomSection.set("Yaw", scoreRooms.get(team).getYaw());
            scoreRoomSection.set("Pitch", scoreRooms.get(team).getPitch());

            {
                // Adds the goal locations.
                ConfigurationSection goalSection = teamSection.createSection("Goals");
                int count = 0;
                for (Location location : teamGoals.get(team)) {
                    count++;
                    ConfigurationSection section = goalSection.createSection(count + "");
                    section.set("World", location.getWorld().getName());
                    section.set("X", location.getX());
                    section.set("Y", location.getY());
                    section.set("Z", location.getZ());
                }
            }

            {
                // Adds the team barriers.
                ConfigurationSection barriersSection = teamSection.createSection("Barriers");
                int count = 0;
                for (Location location : barriers.get(team)) {
                    count++;
                    ConfigurationSection barrierSection = barriersSection.createSection(count + "");
                    barrierSection.set("World", location.getWorld().getName());
                    barrierSection.set("X", location.getX());
                    barrierSection.set("Y", location.getY());
                    barrierSection.set("Z", location.getZ());
                }
            }
        }

        // Saves and updates arenas.yml
        plugin.getSettingsManager().reloadArenas();

        // Loads arena into memory.
        plugin.getArenaManager().loadArena(id);

        // Clears the current arena builder.
        plugin.getArenaManager().setArenaBuilder(null);
    }
}