package net.jadedmc.cactusrush.game.arenas.builder;

import net.jadedmc.cactusrush.game.Mode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaBuilder {
    private final Plugin plugin;
    private final Map<String, ArenaBuilderTeam> teams = new HashMap<>();
    private Location waitingArea;
    private Location spectatorArea;
    private int voidLevel;
    private String name;
    private String builders;
    private String id;
    private final Collection<Mode> modes = new HashSet<>();

    public ArenaBuilder(final Plugin plugin) {
        this.plugin = plugin;
    }

    public void addMode(Mode mode) {
        modes.add(mode);
    }
    public void addTeam(String id) {
        teams.put(id, new ArenaBuilderTeam());
    }

    public boolean hasTeam(String teamID) {
        return teams.containsKey(teamID);
    }
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

        // Make sure the specate area is set.
        if(spectatorArea == null) {
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

    public ArenaBuilderTeam getTeam(String id) {
        return teams.get(id);
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
     * Set the specator area of the arena.
     * @param spectatorArea Arena spectate area.
     */
    public void setSpectatorArea(Location spectatorArea) {
        this.spectatorArea = spectatorArea;
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
                waitingSection.set("world", Objects.requireNonNull(waitingArea.getWorld()).getName());
                waitingSection.set("x", waitingArea.getX());
                waitingSection.set("y", waitingArea.getY());
                waitingSection.set("z", waitingArea.getZ());
                waitingSection.set("yaw", waitingArea.getYaw());
                waitingSection.set("pitch", waitingArea.getPitch());
            }

            // Spectate Area Location
            {
                ConfigurationSection spectateSection = configuration.createSection("spectateArea");
                spectateSection.set("world", Objects.requireNonNull(spectatorArea.getWorld()).getName());
                spectateSection.set("x", spectatorArea.getX());
                spectateSection.set("y", spectatorArea.getY());
                spectateSection.set("z", spectatorArea.getZ());
                spectateSection.set("yaw", spectatorArea.getYaw());
                spectateSection.set("pitch", spectatorArea.getPitch());
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
                    ConfigurationSection teamSection = teamsSection.createSection(id);
                    ArenaBuilderTeam team = teams.get(teamID);

                    // Bounds 1
                    {
                        ConfigurationSection section = teamSection.createSection("bounds1");
                        section.set("world", Objects.requireNonNull(team.getBounds1().getWorld()).getName());
                        section.set("x", team.getBounds1().getX());
                        section.set("y", team.getBounds1().getY());
                        section.set("z", team.getBounds1().getZ());
                        section.set("yaw", team.getBounds1().getYaw());
                        section.set("pitch", team.getBounds1().getPitch());
                    }

                    // Bounds 2
                    {
                        ConfigurationSection section = teamSection.createSection("bounds2");
                        section.set("world", Objects.requireNonNull(team.getBounds2().getWorld()).getName());
                        section.set("x", team.getBounds2().getX());
                        section.set("y", team.getBounds2().getY());
                        section.set("z", team.getBounds2().getZ());
                        section.set("yaw", team.getBounds2().getYaw());
                        section.set("pitch", team.getBounds2().getPitch());
                    }

                    // Spawn Point
                    {
                        ConfigurationSection section = teamSection.createSection("spawnPoint");
                        section.set("world", Objects.requireNonNull(team.getSpawnPoint().getWorld()).getName());
                        section.set("x", team.getSpawnPoint().getX());
                        section.set("y", team.getSpawnPoint().getY());
                        section.set("z", team.getSpawnPoint().getZ());
                        section.set("yaw", team.getSpawnPoint().getYaw());
                        section.set("pitch", team.getSpawnPoint().getPitch());
                    }

                    // Score Room
                    {
                        ConfigurationSection scoreRoomSection = teamSection.createSection("scoreRoom");
                        ArenaBuilderTeam.ArenaBuilderTeamScoreRoom scoreRoom = team.getScoreRoom();

                        // Bounds 1
                        {
                            ConfigurationSection section = scoreRoomSection.createSection("bounds1");
                            section.set("world", Objects.requireNonNull(scoreRoom.getBounds1().getWorld()).getName());
                            section.set("x", scoreRoom.getBounds1().getX());
                            section.set("y", scoreRoom.getBounds1().getY());
                            section.set("z", scoreRoom.getBounds1().getZ());
                            section.set("yaw", scoreRoom.getBounds1().getYaw());
                            section.set("pitch", scoreRoom.getBounds1().getPitch());
                        }

                        // Bounds 2
                        {
                            ConfigurationSection section = scoreRoomSection.createSection("bounds2");
                            section.set("world", Objects.requireNonNull(scoreRoom.getBounds2().getWorld()).getName());
                            section.set("x", scoreRoom.getBounds2().getX());
                            section.set("y", scoreRoom.getBounds2().getY());
                            section.set("z", scoreRoom.getBounds2().getZ());
                            section.set("yaw", scoreRoom.getBounds2().getYaw());
                            section.set("pitch", scoreRoom.getBounds2().getPitch());
                        }

                        // Spawn
                        {
                            ConfigurationSection section = scoreRoomSection.createSection("spawnPoint");
                            section.set("world", Objects.requireNonNull(scoreRoom.getSpawnPoint().getWorld()).getName());
                            section.set("x", scoreRoom.getSpawnPoint().getX());
                            section.set("y", scoreRoom.getSpawnPoint().getY());
                            section.set("z", scoreRoom.getSpawnPoint().getZ());
                            section.set("yaw", scoreRoom.getSpawnPoint().getYaw());
                            section.set("pitch", scoreRoom.getSpawnPoint().getPitch());
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