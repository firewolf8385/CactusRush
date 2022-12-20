package com.github.firewolf8385.cactusrush;

import com.github.firewolf8385.cactusrush.commands.AbstractCommand;
import com.github.firewolf8385.cactusrush.game.GameManager;
import com.github.firewolf8385.cactusrush.game.arena.ArenaManager;
import com.github.firewolf8385.cactusrush.listeners.*;
import com.github.firewolf8385.cactusrush.player.CactusPlayerManager;
import com.github.firewolf8385.cactusrush.utils.LevelUtils;
import com.github.firewolf8385.cactusrush.utils.scoreboard.ScoreboardUpdate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
        Bukkit.getPluginManager().registerEvents(new PlayerEggThrowListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        // Updates scoreboards every second
        new ScoreboardUpdate().runTaskTimer(this, 20L, 20L);

        // Registers utilities.
        new LevelUtils(this);
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
}