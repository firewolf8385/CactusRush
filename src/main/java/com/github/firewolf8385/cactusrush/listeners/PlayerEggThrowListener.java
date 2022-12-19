package com.github.firewolf8385.cactusrush.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

public class PlayerEggThrowListener implements Listener {

    @EventHandler
    public void onEggThrow(PlayerEggThrowEvent event) {
        event.setHatching(false);
    }

}
