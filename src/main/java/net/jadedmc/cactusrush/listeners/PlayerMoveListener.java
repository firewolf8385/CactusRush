package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameDeathType;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.team.Team;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerMoveListener implements Listener {
    private final CactusRushPlugin plugin;

    public PlayerMoveListener(CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getLocalGames().getGame(player);

        // Exit if the player is not in a game.
        if(game == null) {
            return;
        }

        // Exit if the player is a spectator.
        if(game.getSpectators().contains(player.getUniqueId())) {
            return;
        }


        // Kill the player if they fall below the void level.
        if(player.getLocation().getBlockY() < game.getArena().getVoidLevel()) {
            switch (game.getGameState()) {
                case WAITING, COUNTDOWN -> player.teleport(game.getArena().getWaitingArea(game.getWorld()));
                case RUNNING, BETWEEN_ROUND, END -> game.spawnPlayer(player, GameDeathType.VOID);
            }
            return;
        }

        // Exit if the game is not running.
        if(game.getGameState() != GameState.RUNNING && game.getGameState() != GameState.BETWEEN_ROUND) {
            return;
        }

        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        Block block2 = block.getRelative(BlockFace.DOWN);

        // Loop through each team to check their goal blocks.
        boolean found = false;
        for(Team team : game.getTeamManager().getTeams()) {
            // Loop through the team's goal blocks.
            for(Block goalBlock : team.getArenaTeam().getGoalBlocks(game.getWorld())) {
                // Gives the player jump boost if standing on their own blocks.
                if(game.getTeamManager().getTeam(player).equals(team) && (block.equals(goalBlock) || block2.equals(goalBlock))) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
                    found = true;
                    break;
                }
                else if(block.getType() == Material.LIGHT || block2.getType() == Material.LIGHT) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
                    found = true;
                }
                else {
                    // Otherwise, remove the jump boost.
                    if(player.hasPotionEffect(PotionEffectType.JUMP) && player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() < 5) {
                        player.removePotionEffect(PotionEffectType.JUMP);
                    }
                }

                if(block.equals(goalBlock)) {
                    game.playerScored(player);
                    found = true;
                    break;
                }
            }

            if(found) break;
        }
    }
}