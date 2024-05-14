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
import net.jadedmc.cactusrush.game.ability.Ability;
import net.jadedmc.cactusrush.gui.AbilitySelectorGUI;
import net.jadedmc.cactusrush.gui.ModeSelectorGUI;
import net.jadedmc.cactusrush.gui.ShopGUI;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.minigames.Minigame;
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for when a player interacts with an item.
 * Used to process clickable items in the lobby.
 */
public class PlayerInteractListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerInteractListener(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerInteractEvent.
     */
    @EventHandler
    public void onInteract(@NotNull final PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        // Exit if the item is null.
        if (event.getItem() == null) {
            return;
        }

        // Prevent eggs from being thrown when they aren't supposed to.
        if(event.getItem().getType() == Material.EGG || event.getItem().getType() == Material.SNOWBALL) {
            final Game game = plugin.getGameManager().getLocalGames().getGame(player);
            if(game == null) {
                return;
            }

            // Prevent throwing eggs if the round hasn't started.
            if(game.getGameState() == GameState.BETWEEN_ROUND) {
                event.setCancelled(true);
            }
        }

        // Exit if item meta is null.
        if (event.getItem().getItemMeta() == null)
            return;

        // Process abilities.
        final Ability ability = plugin.getAbilityManager().getAbility(event.getItem());
        if(ability != null) {

            // Exit if the click wasn't a right click.
            if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            final Game game = plugin.getGameManager().getLocalGames().getGame(player);

            // Cancel interaction if the cooldown isn't finished.
            if(ability.getAbilityCooldown(player) != null && ability.getAbilityCooldown(player).getSeconds() > 0) {
                event.setCancelled(true);
                return;
            }

            // Only use the ability if a round is currently running.
            if(game.getGameState() == GameState.RUNNING) {
                ability.useAbility(player, game);
            }

            return;
        }

        final String item = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
        switch (item) {
            case "Leave" -> {
                final Game game = plugin.getGameManager().getLocalGames().getGame(player);

                if (game == null) {
                    return;
                }

                //game.removePlayer(player);
                //player.teleport(LocationUtils.getSpawn(plugin));
                JadedAPI.sendToLobby(player, Minigame.CACTUS_RUSH);
            }

            case "Play Again" -> {
                final Game game = plugin.getGameManager().getLocalGames().getGame(player);

                if(game == null) {
                    return;
                }

                plugin.getGameManager().addToGame(player, game.getMode());
            }

            case "Leave Match" -> {
                final Game game = plugin.getGameManager().getLocalGames().getGame(player);

                if(game == null) {
                    return;
                }

                if(game.getSpectators().contains(player.getUniqueId())) {
                    JadedAPI.sendToLobby(player, Minigame.CACTUS_RUSH);
                }
            }

            case "Modes" -> {
                new ModeSelectorGUI(plugin).open(player);
            }

            case "Shop" -> {
                new ShopGUI(plugin).open(player);
            }


            case "Stats" -> ChatUtils.chat(player, "&cThis feature is coming soon!");

            // Runs the Ability Selector Item.
            case "Ability Selector" -> {
                final Game game = plugin.getGameManager().getLocalGames().getGame(player);

                // Exit if the player is not in a game.
                if (game == null) {
                    return;
                }

                // Only allow the player to change their ability before the round begins.
                if(game.getGameState() == GameState.RUNNING || game.getGameState() == GameState.END) {
                    ChatUtils.chat(player, "&cYou can only use that before the round begins!");
                    return;
                }

                new AbilitySelectorGUI(plugin, player).open(player);
            }
        }
    }
}