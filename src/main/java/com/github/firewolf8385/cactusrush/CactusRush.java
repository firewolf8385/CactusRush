package com.github.firewolf8385.cactusrush;

import com.github.firewolf8385.cactusrush.commands.AbstractCommand;
import com.github.firewolf8385.cactusrush.listeners.PlayerJoinListener;
import com.github.firewolf8385.cactusrush.player.CactusPlayerManager;
import com.github.firewolf8385.cactusrush.utils.scoreboard.ScoreboardUpdate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CactusRush extends JavaPlugin {
    private CactusPlayerManager cactusPlayerManager;
    private SettingsManager settingsManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        cactusPlayerManager = new CactusPlayerManager(this);
        settingsManager = new SettingsManager(this);

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
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Updates scoreboards every second
        new ScoreboardUpdate().runTaskTimer(this, 20L, 20L);
    }

    /**
     * Retrieves the object managing CactusPlayers.
     * @return CactusPlayer Manager.
     */
    public CactusPlayerManager getCactusPlayerManager() {
        return cactusPlayerManager;
    }

    /**
     *
     * @return Settings Manager.
     */
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
}