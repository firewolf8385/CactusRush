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
import net.jadedmc.cactusrush.game.GameDeathType;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

/**
 * Listens for when an entity is damaged by another entity.
 * Used for detecting the Deathball ability, and preventing players from killing each other.
 */
public class EntityDamageByEntityListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public EntityDamageByEntityListener(final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when an entity is damaged by another entity.
     * @param event Entity Damage By Entity event.
     */
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        // Modifies egg knockback since it is very small in 1.9+.
        if (event.getDamager().getType() == EntityType.EGG && event.getEntity() instanceof Player) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                final Player player = (Player) event.getEntity();
                final Vector plrV = player.getVelocity();
                final Vector velocity = new Vector(plrV.getX() * 1.2, plrV.getY() * 1.2, plrV.getZ() * 1.2);
                player.setVelocity(velocity);
            }, 0L);

        }

        // Processes the Deathball ability.
        if(event.getDamager().getType() == EntityType.SNOWBALL) {
            // Makes sure a player was hit by the snowball.
            if(!(event.getEntity() instanceof Player player)) {
                return;
            }

            Snowball snowball = (Snowball) event.getDamager();

            // Makes sure a player threw the snowball.
            if(!(snowball.getShooter() instanceof Player shooter)) {
                return;
            }

            Game game = plugin.gameManager().getGame(player);

            // Exit if not in a game.
            if(game == null) {
                return;
            }

            // Exit if a round isn't running.
            if(game.gameState() != GameState.RUNNING) {
                return;
            }

            // Respawn the player.
            game.spawnPlayer(player, GameDeathType.ABILITY);
            if(game.mode() != Mode.DUEL) {
                plugin.cactusPlayerManager().getPlayer(shooter).statisticsTracker().addDeathballKill();
            }
            ChatUtils.chat(shooter, "&aYou killed &f" + player.getName() + " &awith your &f&lDeathball&a!");
            ChatUtils.chat(player, "&aYou were killed by &f" + shooter.getName() + " &awith their &f&lDeathball&a!");

            for(Player spectator : game.spectators()) {
                ChatUtils.chat(spectator, game.teamManager().getTeam(shooter).color().textColor() + shooter.getName() + " &ahas killed " + game.teamManager().getTeam(player).color().textColor() + player.getName() + " &awith their &f&lDeathball&a!");
            }
        }

        if(event.getDamager() instanceof Player damager) {
            Game game = plugin.gameManager().getGame(damager);

            // Exit if the game is null.
            if(game == null) {
                return;
            }

            // Prevent spectators from damaging players.
            if(game.spectators().contains(damager)) {
                event.setCancelled(true);
            }
        }
    }
}
