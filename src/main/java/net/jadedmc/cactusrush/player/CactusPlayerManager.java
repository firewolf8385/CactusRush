/*
 * This file is part of Cactus Rush, licensed under the MIT License.
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
package net.jadedmc.cactusrush.player;

import net.jadedmc.cactusrush.CactusRushPlugin;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * This class manages the creation, retrieval, and destruction of
 * CactusPlayers.
 */
public class CactusPlayerManager {
    private final CactusRushPlugin plugin;
    private final Map<UUID, CactusPlayer> players = new HashMap<>();

    /**
     * Creeates the CactusPlayer Manager.
     * @param plugin Instance of the plugin.
     */
    public CactusPlayerManager(CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a player to the CactusPlayer cache.
     * @param player Player to cache.
     */
    public void addPlayer(Player player) {
        players.put(player.getUniqueId(), new CactusPlayer(plugin, player));
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
    public List<CactusPlayer> players() {
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