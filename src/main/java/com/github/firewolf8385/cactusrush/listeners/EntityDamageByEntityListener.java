package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class EntityDamageByEntityListener implements Listener {
    private final CactusRush plugin;

    public EntityDamageByEntityListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getDamager().getType() == EntityType.EGG && event.getEntity() instanceof Player) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                final Player player = (Player) event.getEntity();
                final Vector plrV = player.getVelocity();
                final Vector velocity = new Vector(plrV.getX() * 1.2, plrV.getY() * 1.2, plrV.getZ() * 1.2);
                player.setVelocity(velocity);
            }, 0L);

        }

        if(event.getDamager().getType() == EntityType.SNOWBALL) {

            if(!(event.getEntity() instanceof Player player)) {
                return;
            }

            Snowball snowball = (Snowball) event.getDamager();

            if(!(snowball.getShooter() instanceof Player shooter)) {
                return;
            }

            Game game = plugin.getGameManager().getGame(player);

            if(game == null) {
                return;
            }

            if(game.getGameState() != GameState.RUNNING) {
                return;
            }

            game.spawnPlayer(player);
            ChatUtils.chat(shooter, "&aYou killed &f" + player.getName() + " &awith your &f&lDeathball&a!");
            ChatUtils.chat(player, "&aYou were killed by &f" + shooter.getName() + " &awith their &f&lDeathball&a!");
        }
    }
}
