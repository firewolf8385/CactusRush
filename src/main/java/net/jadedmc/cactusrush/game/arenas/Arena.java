package net.jadedmc.cactusrush.game.arenas;

import com.github.firewolf8385.cactusrush.utils.xseries.XBlock;
import com.github.firewolf8385.cactusrush.utils.xseries.XMaterial;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * Represents an area in which a game is played.
 */
public class Arena {
    private final Map<Block, XMaterial> blocks = new HashMap<>();
    private final String builders;
    private final String id;
    private final Collection<Mode> modes = new HashSet<>();
    private final String name;
    private final Location spectateArea;
    private final List<ArenaTeam> teams = new ArrayList<>();
    private final Location waitingArea;
    private final int voidLevel;

    /**
     * Creates the arena.
     * @param file File the arena configuration is stored in.
     */
    public Arena(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        id = file.getName();
        name = config.getString("name");

        if(config.isSet("builders")) {
            builders = config.getString("builders");
        }
        else {
            builders = "JadedMC";
        }

        waitingArea = LocationUtils.fromConfig(Objects.requireNonNull(config.getConfigurationSection("waitingArea")));
        spectateArea = LocationUtils.fromConfig(Objects.requireNonNull(config.getConfigurationSection("spectateArea")));
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
     * Add a block to the reset list.
     * @param block Block to add.
     * @param material Material it should reset to.
     */
    public void addBlock(Block block, XMaterial material) {
        blocks.put(block, material);
    }

    /**
     * Get the builders of the arena.
     * @return Arena builders.
     */
    public String getBuilders() {
        return builders;
    }

    /**
     * Get the id of the arena.
     * @return Arena id.
     */
    public String getId() {
        return id;
    }

    /**
     * Get the modes the arena supports.
     * @return Modes supported by the arena.
     */
    public Collection<Mode> getModes() {
        return modes;
    }

    /**
     * Gets the name of the arena.
     * @return Arena name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the arena spectate area.
     * @return Arena spectate area.
     */
    public Location getSpectateArea() {
        return spectateArea;
    }

    /**
     * Get the teams configured for the arena.
     * @return Configured teams.
     */
    public List<ArenaTeam> getTeams() {
        return teams;
    }

    /**
     * Gets the waiting area spawn location.
     * @return Waiting area spawn location.
     */
    public Location getWaitingArea() {
        return waitingArea;
    }

    /**
     * Get the void level of the arena.
     * @return Arena void level.
     */
    public int getVoidLevel() {
        return voidLevel;
    }

    /**
     * Removes a block from the stored reset list.
     * @param block Block to remove.
     */
    public void removeBlock(Block block) {
        blocks.remove(block);
    }

    /**
     * Resets the arena.
     */
    public void resetArena() {
        for(Block block : blocks.keySet()) {
            XBlock.setType(block, blocks.get(block));
        }
    }
}