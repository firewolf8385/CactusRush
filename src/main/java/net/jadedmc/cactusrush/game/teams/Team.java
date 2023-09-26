/*
 * This file is part of Cactus Rush, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.game.teams;

import net.jadedmc.cactusrush.game.arena.ArenaTeam;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Represents a group of players working together in a Game.
 */
public class Team {
    private final Collection<Player> players;
    private final TeamColor color;
    private final ArenaTeam arenaTeam;
    private int score;

    /**
     * Creates a new team with specific players.
     * @param players Players to add to the team.
     */
    public Team(final Collection<Player> players, final ArenaTeam arenaTeam, final TeamColor color) {
        this.players = players;
        this.arenaTeam = arenaTeam;
        this.color = color;
        this.score = 0;
    }

    /**
     * Adds a point to the team's score.
     */
    public void addPoint() {
        score++;
    }

    /**
     * Gets the Arena Team object the team uses.
     * This stores all arena-based information, like spawns and boundaroes.
     * @return Arena Team object.
     */
    public ArenaTeam arenaTeam() {
        return arenaTeam;
    }

    /**
     * Gets the team color of the team.
     * This stores all color information, such as blocks and text color.
     * @return Team Color.
     */
    public TeamColor color() {
        return color;
    }

    /**
     * Get the score of the team in a formatted string.
     * @return Formatted string with the team's score.
     */
    public String formattedScore() {
        String formattedScore = color.textColor() + "[" + color.abbreviation() + "] ";

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
     * Gets all players that are on the team.
     * @return Collection of players currently on the team.
     */
    public Collection<Player> players() {
        return players;
    }

    /**
     * Removes a player from the team.
     * @param player Player to remove.
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    /**
     * Converts the team to a String.
     * @return String name of the team.
     */
    public String toString() {
        return color().textColor() + color.teamName();
    }

    /**
     * Gets the team's current score.
     * @return Team's score.
     */
    public int score() {
        return score;
    }
}
