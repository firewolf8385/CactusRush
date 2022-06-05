package com.github.firewolf8385.cactusrush;

import com.github.firewolf8385.cactusrush.player.CactusPlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CactusRush extends JavaPlugin {
    private CactusPlayerManager cactusPlayerManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        cactusPlayerManager = new CactusPlayerManager(this);
    }

    /**
     * Retrieves the object managing CactusPlayers.
     * @return CactusPlayer Manager.
     */
    public CactusPlayerManager getCactusPlayerManager() {
        return cactusPlayerManager;
    }
}