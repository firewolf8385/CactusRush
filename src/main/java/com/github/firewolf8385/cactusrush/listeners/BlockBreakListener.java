package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {
    private final CactusRush plugin;

    public BlockBreakListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Game game = plugin.getGameManager().getGame(player);
        if(game == null) {
            event.setCancelled(true);
            return;
        }

        if(game.getGameState() != GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }

        if(event.getBlock().getType() != Material.CACTUS) {
            event.setCancelled(true);
            return;
        }

        game.addBrokenCacti(player);
        event.setDropItems(false);

        // Add a cactus to the player's inventory only if they have less than 64.
        for(ItemStack item : player.getInventory().getContents()) {
            if(item == null) {
                continue;
            }

            if(item.getType() == Material.CACTUS) {
                if(item.getAmount() < 64) {
                    player.getInventory().addItem(new ItemStack(Material.CACTUS));
                }
            }
        }
    }
}
