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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for when a player breaks a block. Used to track broken blocks in a game.
 */
public class BlockBreakListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public BlockBreakListener(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when a block is broken.
     * @param event Block Break Event.
     */
    @EventHandler
    public void onBreak(@NotNull final BlockBreakEvent event) {
        final Player player = event.getPlayer();

        // Allow the player to break blocks if they are in creative mode.
        if(player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final Game game = plugin.getGameManager().getLocalGames().getGame(player);

        // Prevent players from breaking blocks outside a game.
        if(game == null) {
            event.setCancelled(true);
            return;
        }

        // Prevent breaking blocks if a round isn't active.
        if(game.getGameState() != GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }

        // Only allow the player to break cacti.
        if(event.getBlock().getType() != Material.CACTUS) {
            event.setCancelled(true);
            return;
        }

        // Add a cactus to the player's inventory only if they have less than 64.
        for(final ItemStack item : player.getInventory().getContents()) {
            if(item == null) {
                continue;
            }

            if(item.getType() == Material.CACTUS) {
                if(item.getAmount() < 64) {
                    player.getInventory().addItem(new ItemStack(Material.CACTUS));
                }
            }
        }

        event.setDropItems(false);
        game.removePlacedBlock(event.getBlock());

        // Statistics Tracking
        game.getTeamManager().getTeamPlayer(player).addCactiBroken();
    }
}