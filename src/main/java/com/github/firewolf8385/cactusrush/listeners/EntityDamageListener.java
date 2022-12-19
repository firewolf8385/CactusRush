package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {
    private final CactusRush plugin;

    public EntityDamageListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }

        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            event.setCancelled(true);
            return;
        }

        switch (event.getCause()) {
            case CONTACT -> {
                event.setDamage(0);
                game.spawnPlayer(player);
            }

            case PROJECTILE -> {
                event.setDamage(1);
                player.setHealth(20);
            }

            default -> event.setCancelled(true);
        }
    }

}