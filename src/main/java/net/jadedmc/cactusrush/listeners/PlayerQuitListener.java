package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This class runs a listener that is called whenever a player leaves the server.
 * This is important for removing players from any collections they are stored in.
 */
public class PlayerQuitListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerQuitListener(CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Removes a player from a game if they are in one.
        Game game = plugin.getGameManager().getLocalGames().getGame(player);
        if(game != null) {
            game.removePlayer(player);
        }

        plugin.getCactusPlayerManager().removePlayer(player);
    }
}