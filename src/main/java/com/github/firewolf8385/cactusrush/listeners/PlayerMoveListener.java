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

        if(game.getSpectators().contains(player)) {
            return;
        }

        if(player.getLocation().getBlockY() < game.getArena().getVoidLevel()) {
            switch (game.getGameState()) {
                case WAITING, COUNTDOWN -> player.teleport(game.getArena().getWaitingArea());
                case RUNNING, BETWEEN_ROUND, END -> game.spawnPlayer(player);
            }
            return;
        }

        if(game.getGameState() != GameState.RUNNING && game.getGameState() != GameState.BETWEEN_ROUND) {
            return;
        }

        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if(!game.getArena().isGoal(block)) {
            if(player.hasPotionEffect(PotionEffectType.JUMP) && player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() < 5) {
                player.removePotionEffect(PotionEffectType.JUMP);
            }
            return;
        }

        Team team = game.getTeamManager().getTeam(player);



        if(game.getArena().getGoals().get(team.getColor()).contains(block.getLocation())) {
            if(player.hasPotionEffect(PotionEffectType.JUMP)) {
                return;
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
            return;
        }
        else {
            player.removePotionEffect(PotionEffectType.JUMP);
        }

        if(!player.isOnGround()) {
            return;
        }

        game.playerScored(player);
    }
}
