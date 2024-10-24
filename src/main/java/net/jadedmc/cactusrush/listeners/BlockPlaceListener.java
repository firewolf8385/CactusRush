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
package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listens to when a player places blocks. Used for tracking what blocks are placed in an arena.
 */
public class BlockPlaceListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public BlockPlaceListener(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when a block is placed.
     * @param event Block Place Event.
     */
    @EventHandler
    public void onPlace(@NotNull final BlockPlaceEvent event) {
        // Get the player who placed the block.
        final Player player = event.getPlayer();

        final Game game = plugin.getGameManager().getLocalGames().getGame(player);

        // Makes sure the player is in a game.
        if(game == null) {
            // Allow creative mode players to build.
            if(player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            event.setCancelled(true);
            return;
        }

        // Cancel if the player is a spectator.
        if(game.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Prevent players from placing the leave bed.
        if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.BETWEEN_ROUND) {
            event.setCancelled(true);
            return;
        }

        game.addPlacedBlock(event.getBlock());

        // Statistic tracking.
        game.getTeamManager().getTeamPlayer(player).addCactiPlaced();
    }
}