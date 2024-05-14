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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listens to when a player is damaged.
 * Used for killing a player when they touch a cactus.
 */
public class EntityDamageListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public EntityDamageListener(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when an entity is damaged.
     * @param event Entity Damage Event
     */
    @EventHandler
    public void onDamage(@NotNull final EntityDamageEvent event) {
        // Make sure the entity is a player.
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }

        final Game game = plugin.getGameManager().getLocalGames().getGame(player);

        // Disables damage outside of games.
        if(game == null) {
            event.setCancelled(true);
            return;
        }

        // Prevent spectators from damaging players.
        if(game.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Processes damage.
        switch (event.getCause()) {
            // Kills a player if they are damaged by a cactus.
            case CONTACT -> {
                event.setDamage(0);
                game.spawnPlayer(player, GameDeathType.CACTUS);
            }

            // Damage a player if they are hit by an egg.
            case PROJECTILE -> {
                event.setDamage(1);
                player.setHealth(20);
            }

            // Cancels all other damage.
            default -> event.setCancelled(true);
        }
    }
}