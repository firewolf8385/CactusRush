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
package net.jadedmc.cactusrush.game.round;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.cactusrush.game.team.TeamColor;
import net.jadedmc.cactusrush.game.team.TeamPlayer;
import net.jadedmc.jadedutils.player.CustomPlayerSet;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public class Round {
    private Team winner;
    private final CustomPlayerSet<RoundPlayer> players = new CustomPlayerSet<>();

    public Round(@NotNull final CactusRushPlugin plugin, @NotNull final Game game) {
        this.winner = null;

        for(final Team team : game.getTeamManager().getTeams()) {
            for(final TeamPlayer teamPlayer : team.getTeamPlayers().values()) {
                this.players.add(new RoundPlayer(teamPlayer.getUniqueId(), teamPlayer.getName(), plugin.getAbilityManager().getAbility(teamPlayer.getUniqueId())));
            }
        }
    }

    public Round(@NotNull final CactusRushPlugin plugin, @NotNull final Game game, @NotNull final Document document) {
        final String winnerName = document.getString("winner");
        if(winnerName.equalsIgnoreCase("ACTIVE")) {
            winner = null;
        }
        else {
            winner = game.getTeamManager().getTeam(TeamColor.valueOf(winnerName));
        }

        final Document statsDocument = document.get("stats", Document.class);
        for(String player : statsDocument.keySet()) {
            final Document playerDocument = statsDocument.get(player, Document.class);
            players.add(new RoundPlayer(plugin, playerDocument));
        }
    }

    public CustomPlayerSet<RoundPlayer> getPlayers() {
        return players;
    }

    public Document toDocument() {
        final Document document = new Document();

        // Add the winner of the rounds.
        if(winner == null) {
            document.append("winner", "ACTIVE");
        }
        else {
            document.append("winner", winner.getColor().toString());
        }

        final Document statsDocument = new Document();
        for(final RoundPlayer player : players) {
            statsDocument.append(player.getUniqueId().toString(), player.toDocument());
        }
        document.append("stats", statsDocument);

        return document;
    }

    public void setWinner(@NotNull final Team winner) {
        this.winner = winner;
    }
}
