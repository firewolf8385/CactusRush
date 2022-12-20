package com.github.firewolf8385.cactusrush.game;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.arena.Arena;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages all active games.
 */
public class GameManager {
    private final CactusRush plugin;
    private final Collection<Game> games = new HashSet<>();

    /**
     * Creates the Game Manager and loads all available arenas.
     * @param plugin Instance of the plugin.
     */
    public GameManager(CactusRush plugin) {
        this.plugin = plugin;

        for(Arena arena : plugin.getArenaManager().getArenas()) {
            games.add(new Game(plugin, arena));
        }
    }

    /**
     * Get a random arena meeting certain criteria.
     * @param teams Number of teams the arena has.
     * @param players Number of players per team.
     * @return Random arena.
     */
    public Game getGame(Player player, int teams, int players) {
        int partyMembers = 1;

        List<Game> possibleGames = new ArrayList<>();

        for(Game game : games) {
            // Skip if the game is running.
            if(game.getGameState() != GameState.WAITING && game.getGameState() != GameState.COUNTDOWN) {
                continue;
            }

            // Skip if the game is full.
            if((game.getPlayers().size() + partyMembers) > game.getArena().getMaxPlayers()) {
                continue;
            }

            // Add to the list
            if(game.getArena().getSpawns().size() == teams && game.getArena().getTeamSize() == players) {
                possibleGames.add(game);
            }
        }

        // Shuffles list of possible games.
        Collections.shuffle(possibleGames);

        // Returns null if no games are available.
        if(possibleGames.size() == 0) {
            return null;
        }

        // Checks if any of these games have players waiting.
        List<Game> possibleGamesWithPlayers = new ArrayList<>();
        for(Game game : possibleGames) {
            if(game.getPlayers().size() > 0) {
                possibleGamesWithPlayers.add(game);
            }
        }

        // If there is a game with players waiting, return that one.
        if(!possibleGamesWithPlayers.isEmpty()) {
            return possibleGamesWithPlayers.get(0);
        }

        // Returns the top game of the shuffled list.
        return possibleGames.get(0);
    }

    /**
     * Gets the game the player is currently in.
     * If they are not in a game, returns null.
     * @param player Player to get game of.
     * @return Game they are in.
     */
    public Game getGame(Player player) {
        for(Game game : games) {
            if(game.getPlayers().contains(player)) {
                return game;
            }
        }

        return null;
    }

    public Collection<Game> getGames() {
        return games;
    }
}