package com.github.firewolf8385.cactusrush.game;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.arena.Arena;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedpartybukkit.JadedParty;
import net.jadedmc.jadedpartybukkit.party.Party;
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

    public void addToGame(Player player, int teams, int teamSize) {
        Party party = JadedParty.partyManager().getParty(player);
        if(party != null) {

            // Makes sure the player is the party leader.
            if(!party.getLeader().equals(player.getUniqueId())) {
                ChatUtils.chat(player, "&cYou are not the party leader!");
                return;
            }

            // Checks if all players are online.
            // If so, continues as normal.
            if(party.getOnlineCount() < party.getMembers().size() + 1) {
                // If not, summon party members and try again with a delay.
                JadedParty.partyManager().summonParty(party);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> addToGame(player, teams, teamSize), 20);
                player.closeInventory();
                return;
            }
        }

        Game game = plugin.getGameManager().getGame(player, teams, teamSize);

        if(game == null) {
            return;
        }

        game.addPlayer(player);
    }

    public void addToGame(Player player, int teams, int teamSize, String arena) {
        Party party = JadedParty.partyManager().getParty(player);
        if(party != null) {

            // Makes sure the player is the party leader.
            if(!party.getLeader().equals(player.getUniqueId())) {
                ChatUtils.chat(player, "&cYou are not the party leader!");
                return;
            }

            // Checks if all players are online.
            // If so, continues as normal.
            if(party.getOnlineCount() < party.getMembers().size() + 1) {
                // If not, summon party members and try again with a delay.
                JadedParty.partyManager().summonParty(party);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> addToGame(player, teams, teamSize), 20);
                player.closeInventory();
                return;
            }
        }

        Game game = plugin.getGameManager().getGame(player, teams, teamSize, arena);

        if(game == null) {
            return;
        }

        game.addPlayer(player);
    }

    public Game getGame(Player player, int teams, int teamSize) {
        List<Game> possibleGames = new ArrayList<>();

        // Check the size of the player's party.
        int partyMembers = 1;
        if(JadedParty.partyManager().getParty(player) != null) {
            partyMembers = JadedParty.partyManager().getParty(player).getPlayers().size();
        }

        for(Game game : games) {
            // Skip if the game is running.
            if(game.getGameState() != GameState.WAITING && game.getGameState() != GameState.COUNTDOWN) {
                continue;
            }

            // Makes sure the arena has the right amount of teams.
            if(game.getArena().getSpawns().size() != teams) {
                continue;
            }

            // Skip if the game is full.
            if((game.getPlayers().size() + partyMembers) > game.getArena().getMaxPlayers(teamSize)) {
                continue;
            }

            possibleGames.add(game);
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
            if(game.getPlayers().size() == 0) {
                continue;
            }

            if(game.getTeamSize() != teamSize) {
                continue;
            }

            possibleGamesWithPlayers.add(game);
        }

        // If there is a game with players waiting, return that one.
        if(!possibleGamesWithPlayers.isEmpty()) {
            return possibleGamesWithPlayers.get(0);
        }

        // Returns the top game of the shuffled list.
        Game game = possibleGames.get(0);
        game.setTeamSize(teamSize);
        return game;
    }

    public Game getGame(Player player, int teams, int teamSize, String arena) {
        List<Game> possibleGames = new ArrayList<>();

        // Check the size of the player's party.
        int partyMembers = 1;
        if(JadedParty.partyManager().getParty(player) != null) {
            partyMembers = JadedParty.partyManager().getParty(player).getPlayers().size();
        }

        for(Game game : games) {
            // Skip if the game is running.
            if(game.getGameState() != GameState.WAITING && game.getGameState() != GameState.COUNTDOWN) {
                continue;
            }

            // Makes sure the arena has the right amount of teams.
            if(game.getArena().getSpawns().size() != teams) {
                continue;
            }

            // Skip if the game is full.
            if((game.getPlayers().size() + partyMembers) > game.getArena().getMaxPlayers(teamSize)) {
                continue;
            }

            if(!game.getArena().getId().equals(arena)) {
                continue;
            }

            possibleGames.add(game);
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
            if(game.getPlayers().size() == 0) {
                continue;
            }

            if(game.getTeamSize() != teamSize) {
                continue;
            }

            possibleGamesWithPlayers.add(game);
        }

        // If there is a game with players waiting, return that one.
        if(!possibleGamesWithPlayers.isEmpty()) {
            return possibleGamesWithPlayers.get(0);
        }

        // Returns the top game of the shuffled list.
        Game game = possibleGames.get(0);
        game.setTeamSize(teamSize);
        return game;
    }

    /**
     * Get a random arena meeting certain criteria.
     * @param teams Number of teams the arena has.
     * @param players Number of players per team.
     * @return Random arena.
     */
    public Game getGame(int teams, int players) {
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
            if(game.getPlayers().contains(player) || game.getSpectators().contains(player)) {
                return game;
            }
        }

        return null;
    }

    public Collection<Game> getGames() {
        return games;
    }

    public int getPlaying(int teams, int teamSize) {
        int playing = 0;

        for(Game game : games) {
            if(game.getArena().getSpawns().size() == teams && game.getTeamSize() == teamSize) {
                playing += game.getPlayers().size();
            }
        }

        return playing;
    }

    public List<Game> getActiveGames() {
        List<Game> activeGames = new ArrayList<>();

        for(Game game : getGames()) {
            if(game.getGameState() == GameState.RUNNING || game.getGameState() == GameState.BETWEEN_ROUND) {
                activeGames.add(game);
            }
        }

        return activeGames;
    }
}