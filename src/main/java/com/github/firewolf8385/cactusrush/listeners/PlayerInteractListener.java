package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    private final CactusRush plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerInteractListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerInteractEvent.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Prevent players from moving items around in their inventory.
        if (player.getOpenInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getType() != InventoryType.CREATIVE) {
            if (player.getName().contains("*")) {
                if (player.getOpenInventory().getType() != InventoryType.PLAYER) {
                    event.setCancelled(true);
                    return;
                }
            }

            // Exit if the item is null.
            if (event.getItem() == null)
                return;

            // Exit if item meta is null.
            if (event.getItem().getItemMeta() == null)
                return;

            String item = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
            switch (item) {
                case "Respawn" -> {
                    Game game = plugin.getGameManager().getGame(player);

                    if (game == null) {
                        return;
                    }

                    if (game.getGameState() != GameState.RUNNING) {
                        return;
                    }

                    game.getTeamManager().getTeam(player).unscorePlayer(player);
                    game.spawnPlayer(player);
                }
            }
        }
    }
}