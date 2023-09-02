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
package net.jadedmc.cactusrush.game.arena.builder;

import net.jadedmc.cactusrush.game.Mode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Stores data of an arena that is still being set up.
 */
public class ArenaBuilder {
    private final Plugin plugin;
    private final Map<String, ArenaBuilderTeam> teams = new HashMap<>();
    private Location waitingArea;
    private int voidLevel;
    private String name;
    private String builders;
    private String id;
    private final Collection<Mode> modes = new HashSet<>();

    /**
     * Creates the arena builder.
     * @param plugin Instance of the plugin.
     */
    public ArenaBuilder(final Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a supported mode to the arena.
     * @param mode Mode to add.
     */
    public void addMode(Mode mode) {
        modes.add(mode);
    }

    /**
     * Adds a team with a given id to the arena.
     * @param id Id of the team.
     */
    public void addTeam(String id) {
        teams.put(id, new ArenaBuilderTeam());
    }

    /**
     * Check if the arena has a team already.
     * @param teamID Team to check.
     * @return Whether the arena has them already.
     */
    public boolean hasTeam(String teamID) {
        return teams.containsKey(teamID);
    }

    /**
     * Checks if the arena is ready to be saved.
     * @return  Whether the arena can be saved.
     */
    public boolean isSet() {
        // Make sure the id is set.
        if(id == null) {
            return false;
        }

        // Make sure the name is set.
        if(name == null) {
            return false;
        }

        // Make sure modes are set.
        if(modes.size() == 0) {
            return false;
        }

        // Make sure the waiting area is set.
        if(waitingArea == null) {
            return false;
        }

        // Makes sure there is at least 2 teams.
        if(teams.size() < 2) {
            return false;
        }

        // Check if each team is set up.
        for(ArenaBuilderTeam team : teams.values()) {
            if(!team.isSet()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get a team based on its id.
     * @param id Team's id.
     * @return Team object.
     */
    public ArenaBuilderTeam getTeam(String id) {
        return teams.get(id);
    }

    /**
     * Gets all currently stored teams.
     * @return All teams.
     */
    public Collection<ArenaBuilderTeam> getTeams() {
        return teams.values();
    }

    /**
     * Gets all modes the arena is set for.
     * @return All modes.
     */
    public Collection<Mode> getModes() {
        return modes;
    }

    /**
     * Set the builders of the arena.
     * @param builders Arena builders.
     */
    public void setBuilders(String builders) {
        this.builders = builders;
    }

    /**
     * Set the id of the arena.
     * @param id Arena id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set the name of the arena.
     * @param name Arena name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the waiting area of the arena.
     * @param waitingArea Arena waiting area.
     */
    public void setWaitingArea(Location waitingArea) {
        this.waitingArea = waitingArea;
    }

    /**
     * Set the void level of the arena.
     * @param voidLevel Arena void level.
     */
    public void setVoidLevel(int voidLevel) {
        this.voidLevel = voidLevel;
    }

    /**
     * Saves the Arena to a configuration file.
     */
    public void save() {

        try {
            File file = new File(plugin.getDataFolder(), "/arenas/" + id + ".yml");
            if(file.exists()) {
                file.delete();
            }

            file.createNewFile();

            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            configuration.set("name", name);
            configuration.set("builders", builders);
            configuration.set("voidLevel", voidLevel);

            // Waiting Area Location
            {
                ConfigurationSection waitingSection = configuration.createSection("waitingArea");
                waitingSection.set("world", Bukkit.getWorlds().get(0).getName());
                waitingSection.set("x", waitingArea.getX());
                waitingSection.set("y", waitingArea.getY());
                waitingSection.set("z", waitingArea.getZ());
                waitingSection.set("yaw", waitingArea.getYaw());
                waitingSection.set("pitch", waitingArea.getPitch());
            }

            // Save modes
            {
                List<String> modeStrings = new ArrayList<>();
                for(Mode mode : modes) {
                    modeStrings.add(mode.toString());
                }

                configuration.set("modes", modeStrings);
            }

            // Load all the teams.
            {
                ConfigurationSection teamsSection = configuration.createSection("teams");
                // Loop through each saved team.
                for(String teamID : teams.keySet()) {
                    ConfigurationSection teamSection = teamsSection.createSection(teamID);
                    ArenaBuilderTeam team = teams.get(teamID);

                    // Bounds 1
                    {
                        ConfigurationSection section = teamSection.createSection("bounds1");
                        section.set("world", Bukkit.getWorlds().get(0).getName());
                        section.set("x", team.getBounds1().getX());
                        section.set("y", team.getBounds1().getY());
                        section.set("z", team.getBounds1().getZ());
                    }

                    // Bounds 2
                    {
                        ConfigurationSection section = teamSection.createSection("bounds2");
                        section.set("world", Bukkit.getWorlds().get(0).getName());
                        section.set("x", team.getBounds2().getX());
                        section.set("y", team.getBounds2().getY());
                        section.set("z", team.getBounds2().getZ());
                    }

                    // Spawn Point
                    {
                        ConfigurationSection section = teamSection.createSection("spawnPoint");
                        section.set("world", Bukkit.getWorlds().get(0).getName());
                        section.set("x", team.getSpawnPoint().getX());
                        section.set("y", team.getSpawnPoint().getY());
                        section.set("z", team.getSpawnPoint().getZ());
                        section.set("yaw", team.getSpawnPoint().getYaw());
                        section.set("pitch", team.getSpawnPoint().getPitch());
                    }

                    // Blocks
                    {
                        ConfigurationSection goalsSection = teamSection.createSection("goals");
                        ConfigurationSection barriersSection = teamSection.createSection("barriers");
                        World world = waitingArea.getWorld();

                        Vector max = Vector.getMaximum(team.getBounds1().toVector(), team.getBounds2().toVector());
                        Vector min = Vector.getMinimum(team.getBounds1().toVector(), team.getBounds2().toVector());
                        int barrierCount = 1;
                        int goalCount = 1;
                        for (int i = min.getBlockX(); i <= max.getBlockX();i++) {
                            for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
                                for (int k = min.getBlockZ(); k <= max.getBlockZ();k++) {
                                    Block block = Objects.requireNonNull(world.getBlockAt(i,j,k));

                                    if(block.getType() == Material.GLASS) {
                                        ConfigurationSection blockSection = barriersSection.createSection(barrierCount + "");
                                        blockSection.set("world", Bukkit.getWorlds().get(0).getName());
                                        blockSection.set("x", i);
                                        blockSection.set("y", j);
                                        blockSection.set("z", k);
                                        barrierCount++;
                                    }
                                    else if(block.getType() == Material.SPONGE) {
                                        ConfigurationSection blockSection = goalsSection.createSection(goalCount + "");
                                        blockSection.set("world", Bukkit.getWorlds().get(0).getName());
                                        blockSection.set("x", i);
                                        blockSection.set("y", j);
                                        blockSection.set("z", k);
                                        goalCount++;
                                    }
                                }
                            }
                        }
                    }

                    // Score Room
                    {
                        ConfigurationSection scoreRoomSection = teamSection.createSection("scoreRoom");
                        ArenaBuilderTeam.ArenaBuilderTeamScoreRoom scoreRoom = team.getScoreRoom();

                        // Bounds 1
                        {
                            ConfigurationSection section = scoreRoomSection.createSection("bounds1");
                            section.set("world", Bukkit.getWorlds().get(0).getName());
                            section.set("x", scoreRoom.getBounds1().getX());
                            section.set("y", scoreRoom.getBounds1().getY());
                            section.set("z", scoreRoom.getBounds1().getZ());
                        }

                        // Bounds 2
                        {
                            ConfigurationSection section = scoreRoomSection.createSection("bounds2");
                            section.set("world", Bukkit.getWorlds().get(0).getName());
                            section.set("x", scoreRoom.getBounds2().getX());
                            section.set("y", scoreRoom.getBounds2().getY());
                            section.set("z", scoreRoom.getBounds2().getZ());
                        }

                        // Spawn
                        {
                            ConfigurationSection section = scoreRoomSection.createSection("spawnPoint");
                            section.set("world", Bukkit.getWorlds().get(0).getName());
                            section.set("x", scoreRoom.getSpawnPoint().getX());
                            section.set("y", scoreRoom.getSpawnPoint().getY());
                            section.set("z", scoreRoom.getSpawnPoint().getZ());
                            section.set("yaw", scoreRoom.getSpawnPoint().getYaw());
                            section.set("pitch", scoreRoom.getSpawnPoint().getPitch());
                        }

                        // Blocks
                        {
                            ConfigurationSection section = scoreRoomSection.createSection("blocks");
                            World world = waitingArea.getWorld();

                            Vector max = Vector.getMaximum(team.getScoreRoom().getBounds1().toVector(), team.getScoreRoom().getBounds2().toVector());
                            Vector min = Vector.getMinimum(team.getScoreRoom().getBounds1().toVector(), team.getScoreRoom().getBounds2().toVector());
                            int blockCount = 1;
                            for (int i = min.getBlockX(); i <= max.getBlockX();i++) {
                                for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
                                    for (int k = min.getBlockZ(); k <= max.getBlockZ();k++) {
                                        Block block = Objects.requireNonNull(world.getBlockAt(i,j,k));

                                        if(block.getType() == Material.GLASS) {
                                            ConfigurationSection blockSection = section.createSection(blockCount + "");
                                            blockSection.set("world", Bukkit.getWorlds().get(0).getName());
                                            blockSection.set("x", i);
                                            blockSection.set("y", j);
                                            blockSection.set("z", k);
                                            blockCount++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Saves the file.
            configuration.save(file);

        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}