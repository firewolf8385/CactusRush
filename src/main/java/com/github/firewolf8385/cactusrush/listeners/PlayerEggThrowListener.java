package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

public class PlayerEggThrowListener implements Listener {
    private final CactusRush plugin;

    public PlayerEggThrowListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEggThrow(PlayerEggThrowEvent event) {
        event.setHatching(false);
    }

}
