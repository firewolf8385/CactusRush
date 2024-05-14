package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.Mode;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Listens to when a player places blocks. Used for tracking what blocks are placed in an arena.
 */
public class BlockPlaceListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public BlockPlaceListener(final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when a block is placed.
     * @param event Block Place Event.
     */
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        // Get the player who placed the block.
        Player player = event.getPlayer();

        Game game = plugin.getGameManager().getLocalGames().getGame(player);

        // Makes sure the player is in a game.
        if(game == null) {
            // Allow creative mode players to build.
            if(player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            event.setCancelled(true);
            return;
        }

        // Cancel if the player is a spectator.
        if(game.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Prevent players from placing the leave bed.
        if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.BETWEEN_ROUND) {
            event.setCancelled(true);
            return;
        }

        game.addPlacedBlock(event.getBlock());

        // Statistic tracking.
        game.getTeamManager().getTeam(player).getTeamPlayers().getPlayer(player).addCactiPlaced();
    }
}