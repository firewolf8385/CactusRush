package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This class runs a listener that is called every time a projectile is launched.
 * This is used to keep track of egg throws.
 */
public class ProjectileLaunchListener implements Listener {
    private final CactusRush plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public ProjectileLaunchListener(CactusRush plugin) {
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
            Game game = plugin.getGameManager().getGame(player);
            if(game == null) {
                return;
            }

            // Marks the egg as being thrown.
            game.addEggCooldown(player);
            game.addEggThrown(player);
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