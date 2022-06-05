package com.github.firewolf8385.cactusrush;

import com.github.firewolf8385.cactusrush.player.CactusPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CactusRush extends JavaPlugin {
    private CactusPlayerManager cactusPlayerManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        cactusPlayerManager = new CactusPlayerManager(this);

        // If PlaceholderAPI is installed, enables placeholders
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
        }
        else {
            // If not, logs a warning and disables the plugin
            Bukkit.getLogger().warning("CactusRush requires PlaceholderAPI to be installed.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Retrieves the object managing CactusPlayers.
     * @return CactusPlayer Manager.
     */
    public CactusPlayerManager getCactusPlayerManager() {
        return cactusPlayerManager;
    }
}