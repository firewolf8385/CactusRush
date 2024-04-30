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
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.lobby.LobbyScoreboard;
import net.jadedmc.jadedcore.events.LobbyJoinEvent;
import net.jadedmc.jadedutils.items.ItemBuilder;
import net.jadedmc.jadedchat.JadedChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LobbyJoinListener implements Listener {
    private final CactusRushPlugin plugin;

    public LobbyJoinListener(final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLobbyJoin(LobbyJoinEvent event) {
        Player player = event.getPlayer();

        player.getInventory().setItem(2, new ItemBuilder(Material.EMERALD).setDisplayName("&a&lShop").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lModes").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.PAPER).setDisplayName("&a&lStats").build());

        new LobbyScoreboard(plugin, player).update(player);

        if(JadedChat.getChannel(player).name().equalsIgnoreCase("GAME") || JadedChat.getChannel(player).name().equalsIgnoreCase("TEAM")) {
            JadedChat.setChannel(player, JadedChat.getDefaultChannel());
        }
    }
}
