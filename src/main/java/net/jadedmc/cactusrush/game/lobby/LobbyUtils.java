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
package net.jadedmc.cactusrush.game.lobby;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.utils.LocationUtils;
import net.jadedmc.cactusrush.utils.item.ItemBuilder;
import net.jadedmc.jadedcore.features.items.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * A collection of repeated tasks used related to the lobby.
 */
public class LobbyUtils {

    /**
     * Sends a player to the lobby.
     * @param plugin Instance of the plugin.
     * @param player Player to send to the lobby.
     */
    public static void sendToLobby(CactusRushPlugin plugin, Player player) {
        player.teleport(LocationUtils.getSpawn(plugin));
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);

        player.setAllowFlight(false);
        player.setFlying(false);
        player.setCollidable(true);

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.JUMP);

        new LobbyScoreboard(plugin, player).update(player);

        giveLobbyItems(player);
    }

    /**
     *
     * @param player
     */
    public static void giveLobbyItems(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, CustomItem.GAME_SELECTOR.toItemStack());
        player.getInventory().setItem(2, new ItemBuilder(Material.EMERALD).setDisplayName("&a&lShop").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lModes").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.PAPER).setDisplayName("&a&lStats").build());
    }
}