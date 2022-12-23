package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * This class runs every time an inventory is clicked.
 * We use this to prevent players from moving items in their inventory.
 */
public class InventoryClickListener implements Listener {
    private final CactusRush plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public InventoryClickListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerDropItemEvent.
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {

        // Makes sure it was actually a player who clicked the inventory.
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // Allow players in creative mode to move items around, in case they are building.
        if(player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // Check the game the player is in.
        Game game = plugin.getGameManager().getGame(player);

        // If they are not in a game, cancels the event.
        if(game == null) {

            // Cancels the event if they're not in creative.
            if(player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }

            return;
        }

        // Don't allow moving items if the game is not running or if the player has scored.
        if(game.getGameState() != GameState.RUNNING || game.getTeamManager().getTeam(player).getScoredPlayers().contains(player)) {
            event.setCancelled(true);
        }
    }
}