package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This class runs a listener that is called whenever a player leaves the server.
 * This is important for removing players from any collections they are stored in.
 */
public class PlayerQuitListener implements Listener {
    private final CactusRush plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerQuitListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerQuitEvent
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getCactusPlayerManager().removePlayer(player);
    }
}