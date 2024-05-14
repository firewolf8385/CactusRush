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
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.events.JadedJoinEvent;
import net.jadedmc.jadedcore.minigames.Minigame;
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class JadedJoinListener implements Listener {
    private final CactusRushPlugin plugin;

    public JadedJoinListener(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJadedJoin(@NotNull final JadedJoinEvent event) {
        final Player player = event.getJadedPlayer().getPlayer();

        for(final Game game : plugin.getGameManager().getLocalGames()) {
            if(!game.getPlayers().contains(player.getUniqueId())) {
                continue;
            }

            game.addPlayer(player);
            return;
        }

        // Send the player back if their game cannot be found.
        if(!JadedAPI.getPlugin().lobbyManager().isLobbyWorld(player.getWorld())) {
            ChatUtils.chat(player, "<red>Game not found! Sending you back to the lobby.");
            JadedAPI.sendToLobby(player, Minigame.CACTUS_RUSH);
        }
    }
}