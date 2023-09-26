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
import net.jadedmc.cactusrush.game.abilitiy.Ability;
import net.jadedmc.cactusrush.gui.AbilitySelectorGUI;
import net.jadedmc.cactusrush.gui.ModeSelectorGUI;
import net.jadedmc.cactusrush.gui.ShopGUI;
import net.jadedmc.cactusrush.utils.LocationUtils;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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
    public PlayerInteractListener(CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerInteractEvent.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Exit if the item is null.
        if (event.getItem() == null)
            return;

        // Prevent eggs from being thrown when they aren't supposed to.
        if(event.getItem().getType() == Material.EGG) {
            Game game = plugin.gameManager().getGame(player);
            if(game == null) {
                return;
            }

            // Prevent throwing eggs if the round hasn't started.
            if(game.gameState() == GameState.BETWEEN_ROUND) {
                event.setCancelled(true);
            }
        }

        // Exit if item meta is null.
        if (event.getItem().getItemMeta() == null)
            return;

        // Process abilities.
        Ability ability = plugin.abilityManager().getAbility(event.getItem());
        if(ability != null) {

            // Exit if the click wasn't a right click.
            if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            Game game = plugin.gameManager().getGame(player);

            // Cancel interaction if the cooldown isn't finished.
            if(ability.getAbilityCooldown(player) != null && ability.getAbilityCooldown(player).seconds() > 0) {
                event.setCancelled(true);
                return;
            }

            // Only use the ability if a round is currently running.
            if(game.gameState() == GameState.RUNNING) {
                ability.useAbility(player, game);
            }

            return;
        }

        String item = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
        switch (item) {
            case "Leave" -> {
                Game game = plugin.gameManager().getGame(player);

                if (game == null) {
                    return;
                }

                game.removePlayer(player);
                player.teleport(LocationUtils.getSpawn(plugin));
            }

            case "Play Again" -> {
                Game game = plugin.gameManager().getGame(player);

                if(game == null) {
                    return;
                }

                plugin.gameManager().addToGame(player, game.mode());
            }

            case "Leave Match" -> {
                Game game = plugin.gameManager().getGame(player);

                if(game == null) {
                    return;
                }

                if(game.spectators().contains(player)) {
                    game.removeSpectator(player);
                }
            }

            case "Modes" -> {
                new ModeSelectorGUI(plugin).open(player);
            }

            case "Shop" -> {
                new ShopGUI(plugin).open(player);
            }


            case "Profile", "Cosmetics", "Stats" -> ChatUtils.chat(player, "&cThis feature is coming soon!");

            // Runs the Ability Selector Item.
            case "Ability Selector" -> {
                Game game = plugin.gameManager().getGame(player);

                // Exit if the player is not in a game.
                if (game == null) {
                    return;
                }

                // Only allow the player to change their ability before the round begins.
                if(game.gameState() == GameState.RUNNING || game.gameState() == GameState.END) {
                    ChatUtils.chat(player, "&cYou can only use that before the round begins!");
                    return;
                }

                new AbilitySelectorGUI(plugin, player).open(player);
            }
        }
    }
}