package com.github.firewolf8385.cactusrush.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class EntityDamageByEntityListener implements Listener {
    private final Plugin plugin;

    public EntityDamageByEntityListener(Plugin plugin) {
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
    }
}
