package com.github.firewolf8385.cactusrush.player;

import com.github.firewolf8385.cactusrush.CactusRush;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * This class manages the creation, retrieval, and destruction of
 * CactusPlayers.
 */
public class CactusPlayerManager {
    private final CactusRush plugin;
    private final Map<UUID, CactusPlayer> players = new HashMap<>();

    /**
     * Creeates the CactusPlayer Manager.
     * @param plugin Instance of the plugin.
     */
    public CactusPlayerManager(CactusRush plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a player to the CactusPlayer cache.
     * @param player Player to cache.
     */
    public void addPlayer(Player player) {
        players.put(player.getUniqueId(), new CactusPlayer(plugin, player.getUniqueId()));
    }

    /**
     * Gets the CactusPlayer object of a player.
     * @param player Player to get CactusPlayer of.
     * @return Player's CactusPlayer.
     */
    public CactusPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    /**
     * Gets a list of all cached CactusPlayers.
     * @return All saved CactusPlayer objects.
     */
    public List<CactusPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    /**
     * Removes a CactusPlayer.
     * @param player Player to remove CactusPlayer of.
     */
    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }
}