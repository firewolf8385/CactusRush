package com.github.firewolf8385.cactusrush.game.team;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a group of players
 * working together in a Game.
 */
public class Team {
    private final List<Player> players;
    private final Set<Player> remainingPlayers;
    private final Set<Player> scoredPlayers = new HashSet<>();
    private final TeamColor color;
    private int score;

    /**
     * Creates a new team with specific players.
     * @param players Players to add to the team.
     */
    public Team(List<Player> players, TeamColor color) {
        this.players = players;
        this.remainingPlayers = new HashSet<>(players);
        this.color = color;

        score = 0;
    }

    /**
     * Adds a point to the team's score.
     */
    public void addPoint() {
        score++;
    }

    /**
     * Get the color of the team.
     * @return Color of the team.
     */
    public TeamColor getColor() {
        return color;
    }

    /**
     * Gets all players on the team, alive and dead.
     * @return All players on the team.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Get all players on the team that haven't scored.
     * @return All remaining players.
     */
    public Set<Player> getRemainingPlayers() {
        return remainingPlayers;
    }

    /**
     * Gets the team's score.
     * @return the team's score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets all players on the team who have scored.
     * @return All scored players.
     */
    public Set<Player> getScoredPlayers() {
        return scoredPlayers;
    }

    /**
     * Remove a player from the team.
     * @param player Player to remove.
     */
    public void removePlayer(Player player) {
        getPlayers().remove(player);
        getRemainingPlayers().remove(player);
        getScoredPlayers().remove(player);
    }

    /**
     * Clears dead players and resets alive players.
     */
    public void reset() {
        remainingPlayers.addAll(scoredPlayers);
        scoredPlayers.clear();
    }

    /**
     * Add a player to the scored list,
     * and remove them from the remaining list.
     * @param player Player to mark as scored.
     */
    public void scorePlayer(Player player) {
        remainingPlayers.remove(player);
        scoredPlayers.add(player);
    }

    /**
     * Get the string name of the team.
     * @return Team string name.
     */
    public String toString() {
        return color.getChatColor() + color.getName();
    }
}