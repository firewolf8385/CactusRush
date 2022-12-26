package com.github.firewolf8385.cactusrush;

import com.github.firewolf8385.cactusrush.commands.AbstractCommand;
import com.github.firewolf8385.cactusrush.game.GameManager;
import com.github.firewolf8385.cactusrush.game.arena.ArenaManager;
import com.github.firewolf8385.cactusrush.listeners.*;
import com.github.firewolf8385.cactusrush.player.CactusPlayerManager;
import com.github.firewolf8385.cactusrush.utils.LevelUtils;
import com.github.firewolf8385.cactusrush.utils.scoreboard.ScoreboardUpdate;
import net.jadedmc.jadedcore.JadedAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class CactusRush extends JavaPlugin {
    private ArenaManager arenaManager;
    private CactusPlayerManager cactusPlayerManager;
    private GameManager gameManager;
    private SettingsManager settingsManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        cactusPlayerManager = new CactusPlayerManager(this);
        settingsManager = new SettingsManager(this);
        arenaManager = new ArenaManager(this);
        gameManager = new GameManager(this);

        // If PlaceholderAPI is installed, enables placeholders
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
        }
        else {
            // If not, logs a warning and disables the plugin
            Bukkit.getLogger().warning("CactusRush requires PlaceholderAPI to be installed.");
            getServer().getPluginManager().disablePlugin(this);
        }

        // Register commands.
        AbstractCommand.registerCommands(this);

        // Registers listeners.
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEggThrowListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileLaunchListener(this), this);

        // Updates scoreboards every second
        new ScoreboardUpdate().runTaskTimer(this, 20L, 20L);

        // Registers utilities.
        new LevelUtils(this);

        // Create MySQL Tables
        loadTables();
    }

    /**
     * Retrieves the object managing arenas.
     * @return Arena Manager.
     */
    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    /**
     * Retrieves the object managing CactusPlayers.
     * @return CactusPlayer Manager.
     */
    public CactusPlayerManager getCactusPlayerManager() {
        return cactusPlayerManager;
    }

    /**
     * Retrieves the object managing configuration files.
     * @return Settings Manager.
     */
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * Create mysql tables if they do not exist.
     */
    private void loadTables() {
        // Run database tasks async.
        getServer().getScheduler().runTaskAsynchronously(this, ()-> {
            try {
                // cactus_rush_players
                {
                    PreparedStatement cactus_rush_players = JadedAPI.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS cactus_rush_players (" +
                            "uuid VARCHAR(36)," +
                            "level INT DEFAULT 1," +
                            "experience INT DEFAULT 0," +
                            "coins INT DEFAULT 0," +
                            "PRIMARY KEY (uuid)" +
                            ");");
                    cactus_rush_players.execute();
                }

                // cactus_rush_statistics
                {
                    PreparedStatement cactus_rush_statistics = JadedAPI.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS cactus_rush_statistics (" +
                            "uuid VARCHAR(36)," +
                            "wins INT DEFAULT 0," +
                            "losses INT DEFAULT 0," +
                            "winStreak INT DEFAULT 0," +
                            "bestWinStreak INT DEFAULT 0," +
                            "cactiPlaced INT DEFAULT 0," +
                            "cactiBroke INT DEFAULT 0," +
                            "eggsThrown INT DEFAULT 0," +
                            "goals INT DEFAULT 0," +
                            "gamesPlayed INT DEFAULT 0," +
                            "respawns INT DEFAULT 0," +
                            "roundsPlayed INT DEFAULT 0," +
                            "PRIMARY KEY (uuid)" +
                            ");");
                    cactus_rush_statistics.execute();
                }
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}