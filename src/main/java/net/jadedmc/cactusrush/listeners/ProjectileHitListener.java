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
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

public class ProjectileHitListener implements Listener {
    private final CactusRushPlugin plugin;

    public ProjectileHitListener(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(@NotNull final ProjectileHitEvent event) {

        if(!(event.getEntity() instanceof Snowball snowball)) {
            return;
        }

        if(!(snowball.getShooter() instanceof Player player)) {
            return;
        }

        if(event.getHitEntity() != null) {
            return;
        }

        // Makes sure the player is in a game.
        final Game game = plugin.getGameManager().getLocalGames().getGame(player);
        if(game == null) {
            return;
        }

        ChatUtils.chat(player, "&aYou have missed your &f&lDeathball &athrow!");
        ChatUtils.chat(game.getSpectators(), game.getTeamManager().getTeam(player).getColor().getTextColor() + player.getName() + " &amissed their &f&lDeathball &athrow!");
    }
}