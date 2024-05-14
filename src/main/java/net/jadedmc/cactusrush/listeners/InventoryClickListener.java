package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * This class runs every time an inventory is clicked.
 * We use this to prevent players from moving items in their inventory.
 */
public class InventoryClickListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public InventoryClickListener(CactusRushPlugin plugin) {
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
        Game game = plugin.getGameManager().getLocalGames().getGame(player);

        // If they are not in a game, cancels the event.
        if(game == null) {

            // Cancels the event if they're not in creative.
            if(player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }

            return;
        }

        // Prevent moving items in a player's inventory.
        event.setCancelled(true);
    }
}