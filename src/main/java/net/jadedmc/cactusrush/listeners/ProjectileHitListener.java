package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileHitListener implements Listener {
    private final CactusRushPlugin plugin;

    public ProjectileHitListener(CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {

        if(!(event.getEntity() instanceof Snowball snowball)) {
            return;
        }

        if(!(snowball.getShooter() instanceof Player player)) {
            return;
        }

        if(event.getHitEntity() != null) {
            return;
        }

        // Makes sure the player is in a game.
        Game game = plugin.getGameManager().getLocalGames().getGame(player);
        if(game == null) {
            return;
        }

        ChatUtils.chat(player, "&aYou have missed your &f&lDeathball &athrow!");
        ChatUtils.chat(game.getSpectators(), game.getTeamManager().getTeam(player).getColor().getTextColor() + player.getName() + " &amissed their &f&lDeathball &athrow!");
    }
}