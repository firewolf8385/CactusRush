/*
 * This file is part of CactusRush, licensed under the MIT License.
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
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.player;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.jadedutils.player.CustomPlayerSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages the creation, retrieval, and destruction of
 * CactusPlayers.
 */
public class CactusPlayerManager {
    private final CactusRushPlugin plugin;
    private final CustomPlayerSet<CactusPlayer> players = new CustomPlayerSet<>();

    public CactusPlayerManager(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a player to the CactusPlayer cache.
     * @param player Player to cache.
     * @return CactusPlayer completable future.
     */
    public CompletableFuture<CactusPlayer> addPlayer(@NotNull final Player player) {
        return CompletableFuture.supplyAsync(() -> {
            final CactusPlayer cactusPlayer = new CactusPlayer(plugin, player.getUniqueId());
            this.players.add(cactusPlayer);
            return cactusPlayer;
        });
    }

    /**
     * Gets the CactusPlayer object of a player.
     * @param player Player to get CactusPlayer of.
     * @return Player's CactusPlayer.
     */
    public CactusPlayer getPlayer(@NotNull final Player player) {
        return getPlayer(player.getUniqueId());
    }

    /**
     * Gets the CactusPlayer object of a player, based off their UUID.
     * @param uuid UUID of the player.
     * @return Player's CactusPlayer.
     */
    public CactusPlayer getPlayer(@NotNull final UUID uuid) {
        return players.getPlayer(uuid);
    }

    /**
     * Gets a list of all cached CactusPlayers.
     * @return All saved CactusPlayer objects.
     */
    public CustomPlayerSet<CactusPlayer> getPlayers() {
        return players;
    }

    /**
     * Removes a CactusPlayer.
     * @param player Player to remove CactusPlayer of.
     */
    public void removePlayer(@NotNull final Player player) {
        players.removePlayer(player.getUniqueId());
    }
}