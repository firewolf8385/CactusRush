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
import net.jadedmc.cactusrush.game.Mode;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

/**
 * This class runs a listener that is called every time a projectile is launched.
 * This is used to keep track of egg throws.
 */
public class ProjectileLaunchListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public ProjectileLaunchListener(CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event ProjectileLaunchEvent
     */
    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {

        // Processes egg velocity changes.
        if(event.getEntity() instanceof Egg egg) {
            // Makes sure it was a player who threw the egg.
            if(!(egg.getShooter() instanceof Player player)) {
                return;
            }

            // Fix eggs changing velocity depending on vertical velocity.
            egg.setVelocity(player.getLocation().getDirection().multiply(1.5));

            // Makes sure the player is in a game.
            Game game = plugin.gameManager().getGame(player);
            if(game == null) {
                return;
            }

            // Marks the egg as being thrown.
            game.addEggCooldown(player);

            // Statistic tracking.
            game.statisticsTracker().addEggThrown(player);
            if(game.mode() != Mode.DUEL) {
                plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addEggsThrown(game.mode().id(), game.arena().id());
            }
        }

        // Processes snowball velocity changes.
        if(event.getEntity() instanceof Snowball snowball) {
            // Makes sure it was a player who threw the snowball.
            if(!(snowball.getShooter() instanceof Player player)) {
                return;
            }

            // Fix snowballs changing velocity depending on vertical velocity.
            snowball.setVelocity(player.getLocation().getDirection().multiply(1.5));
        }
    }
}