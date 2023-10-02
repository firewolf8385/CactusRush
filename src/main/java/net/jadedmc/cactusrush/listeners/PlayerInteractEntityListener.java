package net.jadedmc.cactusrush.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {

    @EventHandler
    public void onInteractAtItemFrame(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getRightClicked() instanceof ItemFrame) {
            event.setCancelled(true);
        }
    }
}