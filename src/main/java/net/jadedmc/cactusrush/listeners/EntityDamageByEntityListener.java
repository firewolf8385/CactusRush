package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameDeathType;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

/**
 * Listens for when an entity is damaged by another entity.
 * Used for detecting the Deathball ability, and preventing players from killing each other.
 */
public class EntityDamageByEntityListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the listener.
     * @param plugin Instance of the plugin.
     */
    public EntityDamageByEntityListener(final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when an entity is damaged by another entity.
     * @param event Entity Damage By Entity event.
     */
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {

        if(event.getEntity().getType() == EntityType.ITEM_FRAME && event.getDamager() instanceof Player player) {
            if(player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }

            return;
        }

        // Modifies egg knockback since it is very small in 1.9+.
        if (event.getDamager().getType() == EntityType.EGG && event.getEntity() instanceof Player) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                final Player player = (Player) event.getEntity();
                final Vector plrV = player.getVelocity();
                final Vector velocity = new Vector(plrV.getX() * 1.2, plrV.getY() * 1.2, plrV.getZ() * 1.2);
                player.setVelocity(velocity);
            }, 0L);

        }

        // Processes the Deathball ability.
        if(event.getDamager().getType() == EntityType.SNOWBALL) {
            // Makes sure a player was hit by the snowball.
            if(!(event.getEntity() instanceof Player player)) {
                return;
            }

            Snowball snowball = (Snowball) event.getDamager();

            // Makes sure a player threw the snowball.
            if(!(snowball.getShooter() instanceof Player shooter)) {
                return;
            }

            Game game = plugin.getGameManager().getLocalGames().getGame(player);

            // Exit if not in a game.
            if(game == null) {
                return;
            }

            // Exit if a round isn't running.
            if(game.getGameState() != GameState.RUNNING) {
                return;
            }

            // Respawn the player.
            game.spawnPlayer(player, GameDeathType.ABILITY);
            if(game.getMode() != Mode.DUEL) {
                plugin.getCactusPlayerManager().getPlayer(shooter).addDeathballKill();
                JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_4").unlock(shooter);
            }

            final Team shooterTeam = game.getTeamManager().getTeam(shooter);
            final Team playerTeam = game.getTeamManager().getTeam(player);

            ChatUtils.chat(shooter, "&aYou killed &f" + playerTeam.getColor().getTextColor() + player.getName() + " &awith your &f&lDeathball&a!");
            ChatUtils.chat(player, "&aYou were killed by &f" + shooterTeam.getColor().getTextColor() + shooter.getName() + " &awith their &f&lDeathball&a!");
            ChatUtils.chat(game.getSpectators(), shooterTeam.getColor().getTextColor() + shooter.getName() + " &ahas killed " + playerTeam.getColor().getTextColor() + player.getName() + " &awith their &f&lDeathball&a!");
        }

        if(event.getDamager() instanceof Player damager) {
            Game game = plugin.getGameManager().getLocalGames().getGame(damager);

            // Exit if the game is null.
            if(game == null) {
                return;
            }

            // Prevent spectators from damaging players.
            if(game.getSpectators().contains(damager.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}