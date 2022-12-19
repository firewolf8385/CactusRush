package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEggThrowListener implements Listener {
    private final CactusRush plugin;

    public PlayerEggThrowListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEggThrow(PlayerEggThrowEvent event) {
        event.setHatching(false);

        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            return;
        }

        game.addEggCooldown(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(game.hasEggCooldown(player)) {
                player.getInventory().addItem(new ItemStack(Material.EGG));
                game.removeEggCooldown(player);
            }
        }, 3*20);
    }

}
