package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import com.github.firewolf8385.cactusrush.game.team.Team;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerMoveListener implements Listener {
    private final CactusRush plugin;

    public PlayerMoveListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            return;
        }

        if(player.getLocation().getBlockY() < game.getArena().getVoidLevel()) {
            switch (game.getGameState()) {
                case WAITING, COUNTDOWN -> player.teleport(game.getArena().getWaitingArea());
                case RUNNING, BETWEEN_ROUND, END -> game.spawnPlayer(player);
            }
            return;
        }

        if(game.getGameState() != GameState.RUNNING) {
            return;
        }

        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if(!game.getArena().isGoal(block)) {
            return;
        }

        Team team = game.getTeamManager().getTeam(player);

        if(game.getArena().getGoals().get(team.getColor()).contains(block.getLocation())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10, 1));
            return;
        }

        game.playerScored(player);
    }
}
