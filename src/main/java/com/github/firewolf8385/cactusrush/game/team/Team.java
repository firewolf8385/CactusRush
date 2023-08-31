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
    private boolean playerLeft;

    /**
     * Creates a new team with specific players.
     * @param players Players to add to the team.
     */
    public Team(List<Player> players, TeamColor color) {
        this.players = players;
        this.remainingPlayers = new HashSet<>(players);
        this.color = color;

        score = 0;
        playerLeft = false;
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
     * Get the score of the team in a formatted string.
     * @return Formatted string with the team's score.
     */
    public String getFormattedScore() {
        String formattedScore = color.getChatColor() + "[" + color.getAbbreviation() + "] ";

        int count = 0;
        for(int i = 0; i < score; i++) {
            formattedScore += "⬤";
            count++;
        }

        formattedScore += "&7";
        for(int i = count; i < 3; i++) {
            formattedScore += "⬤";
        }

        formattedScore += " &8(" + score + "/3)";
        return formattedScore;
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
     * Get if a player left mid-round.
     * @return Whether a player left.
     */
    public boolean playerLeft() {
        return playerLeft;
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
        playerLeft = false;
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
     * Set if a player left during a round.
     * @param playerLeft Whether a player left.
     */
    public void setPlayerLeft(boolean playerLeft) {
        this.playerLeft = playerLeft;
    }

    /**
     * Get the string name of the team.
     * @return Team string name.
     */
    public String toString() {
        return color.getChatColor() + color.getName();
    }

    /**
     * Adds a scored player back into the arena.
     * @param player Player to add back into the arena.
     */
    public void unscorePlayer(Player player) {
        remainingPlayers.add(player);
        scoredPlayers.remove(player);
    }
}