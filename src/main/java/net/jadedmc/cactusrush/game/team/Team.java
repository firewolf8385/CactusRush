/*
 * This file is part of CactusRush, licensed under the MIT License.
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
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.game.team;

import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.arena.Arena;
import net.jadedmc.jadedutils.player.CustomPlayerSet;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a group of players working together in a Game.
 */
public class Team {
    private final CustomPlayerSet<TeamPlayer> teamPlayers;
    private final TeamColor color;
    private final Arena.ArenaTeam arenaTeam;
    private int score;

    public Team(@NotNull final CustomPlayerSet<TeamPlayer> teamPlayers, @NotNull final Arena.ArenaTeam arenaTeam, final TeamColor color) {
        this.teamPlayers = teamPlayers;
        this.arenaTeam = arenaTeam;
        this.color = color;
        this.score = 0;
    }

    public Team(@NotNull final Document document, @NotNull final Game game) {
        this.color = TeamColor.valueOf(document.getString("color"));
        this.score = document.getInteger("score");

        this.teamPlayers = new CustomPlayerSet<>();
        final Document playersDocument = document.get("players", Document.class);
        for(final String playerUUID : playersDocument.keySet()) {
            final Document playerDocument = playersDocument.get(playerUUID, Document.class);
            teamPlayers.add(new TeamPlayer(playerDocument, game));
        }

        this.arenaTeam = null;
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
    public Arena.ArenaTeam getArenaTeam() {
        return arenaTeam;
    }

    /**
     * Gets the team color of the team.
     * This stores all color information, such as blocks and text color.
     * @return Team Color.
     */
    public TeamColor getColor() {
        return color;
    }

    /**
     * Get the score of the team in a formatted string.
     * @return Formatted string with the team's score.
     */
    public String getFormattedScore() {
        String formattedScore = color.getTextColor() + "[" + color.getAbbreviation() + "] ";

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
    public CustomPlayerSet<TeamPlayer> getTeamPlayers() {
        return teamPlayers;
    }

    /**
     * Removes a player from the team.
     * @param player Player to remove.
     */
    public void removePlayer(@NotNull final Player player) {
        teamPlayers.removePlayer(player);
    }

    /**
     * Converts the team to a String.
     * @return String name of the team.
     */
    public String toString() {
        return color.getTextColor() + color.getTeamName();
    }

    /**
     * Gets the team's current score.
     * @return Team's score.
     */
    public int getScore() {
        return score;
    }

    public Document toDocument() {
        final Document document = new Document()
                .append("color", this.color.toString())
                .append("score", this.score);

        final Document playersDocument = new Document();
        for(final TeamPlayer teamPlayer : this.teamPlayers) {
            playersDocument.append(teamPlayer.getUniqueId().toString(), teamPlayer.toDocument());
        }
        document.append("players", playersDocument);

        return document;
    }
}