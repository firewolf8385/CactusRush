package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for when a player breaks a block. Used to track broken blocks in a game.
 */
public class BlockBreakListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public BlockBreakListener(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when a block is broken.
     * @param event Block Break Event.
     */
    @EventHandler
    public void onBreak(@NotNull final BlockBreakEvent event) {
        final Player player = event.getPlayer();

        // Allow the player to break blocks if they are in creative mode.
        if(player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final Game game = plugin.getGameManager().getLocalGames().getGame(player);

        // Prevent players from breaking blocks outside a game.
        if(game == null) {
            event.setCancelled(true);
            return;
        }

        // Prevent breaking blocks if a round isn't active.
        if(game.getGameState() != GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }

        // Only allow the player to break cacti.
        if(event.getBlock().getType() != Material.CACTUS) {
            event.setCancelled(true);
            return;
        }

        // Add a cactus to the player's inventory only if they have less than 64.
        for(final ItemStack item : player.getInventory().getContents()) {
            if(item == null) {
                continue;
            }

            if(item.getType() == Material.CACTUS) {
                if(item.getAmount() < 64) {
                    player.getInventory().addItem(new ItemStack(Material.CACTUS));
                }
            }
        }

        event.setDropItems(false);
        game.removePlacedBlock(event.getBlock());

        // Statistics Tracking
        game.getTeamManager().getTeam(player).getTeamPlayers().getPlayer(player).addCactiBroken();
    }
}