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
package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Listens to when a player places blocks. Used for tracking what blocks are placed in an arena.
 */
public class BlockPlaceListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public BlockPlaceListener(final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when a block is placed.
     * @param event Block Place Event.
     */
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        // Get the player who placed the block.
        Player player = event.getPlayer();

        // Makes sure the player is in a game.
        if(plugin.gameManager().getGame(player) == null) {
            // Allow creative mode players to build.
            if(player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            event.setCancelled(true);
            return;
        }

        Game game = plugin.gameManager().getGame(player);

        // Cancel if the player is a spectator.
        if(game.spectators().contains(player)) {
            event.setCancelled(true);
            return;
        }

        // Prevent players from placing the leave bed.
        if(game.gameState() == GameState.WAITING) {
            event.setCancelled(true);
            return;
        }

        game.addPlacedBlock(event.getBlock());

        // Statistic tracking.
        game.statisticsTracker().addPlacedCacti(player);
        plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addCactiPlaced(game.mode().id(), game.arena().id());
    }
}