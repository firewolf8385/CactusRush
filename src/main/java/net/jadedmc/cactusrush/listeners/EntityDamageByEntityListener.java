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
import net.jadedmc.cactusrush.game.GameDeathType;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

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
    public EntityDamageByEntityListener(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when an entity is damaged by another entity.
     * @param event Entity Damage By Entity event.
     */
    @EventHandler
    public void onDamageByEntity(@NotNull final EntityDamageByEntityEvent event) {

        // Prevent players from breaking item frames. Used in map repository.
        if(event.getEntity().getType() == EntityType.ITEM_FRAME && event.getDamager() instanceof Player player) {
            if(player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }

            return;
        }

        // Modifies egg knockback since it is very small in 1.9+.
        if (event.getDamager().getType() == EntityType.EGG && event.getEntity() instanceof Player) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
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

            final Snowball snowball = (Snowball) event.getDamager();

            // Makes sure a player threw the snowball.
            if(!(snowball.getShooter() instanceof Player shooter)) {
                return;
            }

            final Game game = plugin.getGameManager().getLocalGames().getGame(player);

            // Exit if not in a game.
            if(game == null) {
                return;
            }

            // Exit if a round isn't running.
            if(game.getGameState() != GameState.RUNNING) {
                return;
            }

            // Respawn the player.
            game.spawnPlayer(player, GameDeathType.ABILITY);
            if(game.getMode() != Mode.DUEL) {
                plugin.getCactusPlayerManager().getPlayer(shooter).addDeathballKill();
                JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_4").unlock(shooter);
            }

            final Team shooterTeam = game.getTeamManager().getTeam(shooter);
            final Team playerTeam = game.getTeamManager().getTeam(player);

            ChatUtils.chat(shooter, "&aYou killed &f" + playerTeam.getColor().getTextColor() + player.getName() + " &awith your &f&lDeathball&a!");
            ChatUtils.chat(player, "&aYou were killed by &f" + shooterTeam.getColor().getTextColor() + shooter.getName() + " &awith their &f&lDeathball&a!");
            ChatUtils.chat(game.getSpectators(), shooterTeam.getColor().getTextColor() + shooter.getName() + " &ahas killed " + playerTeam.getColor().getTextColor() + player.getName() + " &awith their &f&lDeathball&a!");
        }

        if(event.getDamager() instanceof Player damager) {
            final Game game = plugin.getGameManager().getLocalGames().getGame(damager);

            // Exit if the game is null.
            if(game == null) {
                return;
            }

            // Prevent spectators from damaging players.
            if(game.getSpectators().contains(damager.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}