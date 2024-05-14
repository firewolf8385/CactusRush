package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameDeathType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Listens to when a player is damaged.
 * Used for killing a player when they touch a cactus.
 */
public class EntityDamageListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public EntityDamageListener(final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when an entity is damaged.
     * @param event Entity Damage Event
     */
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        // Make sure the entity is a player.
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }

        Game game = plugin.getGameManager().getLocalGames().getGame(player);

        // Disables damage outside of games.
        if(game == null) {
            event.setCancelled(true);
            return;
        }

        // Prevent spectators from damaging players.
        if(game.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Processes damage.
        switch (event.getCause()) {
            // Kills a player if they are damaged by a cactus.
            case CONTACT -> {
                event.setDamage(0);
                game.spawnPlayer(player, GameDeathType.CACTUS);
            }

            // Damage a player if they are hit by an egg.
            case PROJECTILE -> {
                event.setDamage(1);
                player.setHealth(20);
            }

            // Cancels all other damage.
            default -> event.setCancelled(true);
        }
    }
}