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
import net.jadedmc.cactusrush.game.team.TeamPlayer;
import net.jadedmc.cactusrush.player.CactusPlayer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RoundManager {
    private final CactusRushPlugin plugin;
    private final Game game;
    private Round currentRound;
    private final Map<Integer, Round> rounds = new HashMap<>();
    private int currentRoundNumber = 0;


    public RoundManager(@NotNull final CactusRushPlugin plugin, @NotNull final Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Nullable
    public Round getCurrentRound() {
        return currentRound;
    }

    @Nullable
    public Round getRound(final int roundNumber) {
        if(this.rounds.containsKey(roundNumber)) {
            return this.rounds.get(roundNumber);
        }

        return null;
    }


    public int getCurrentRoundNumber() {
        return currentRoundNumber;
    }

    public Document getRoundsDocument() {
        final Document document = new Document();

        for(final int roundNumber : this.rounds.keySet()) {
            final Round round = this.rounds.get(roundNumber);
            document.append(roundNumber + "", round.toDocument());
        }

        return document;
    }

    public void loadRoundsDocument(@NotNull final CactusRushPlugin plugin, @NotNull final Document document, @NotNull final Game game) {
        for(@NotNull final String key : document.keySet()) {
            final int roundNumber = Integer.parseInt(key);
            final Document roundDocument = document.get(key, Document.class);
            this.rounds.put(roundNumber, new Round(plugin, game, roundDocument));
        }
    }

    public void saveCurrentRound(final int roundNumber) {
        this.rounds.put(roundNumber, this.currentRound);
    }

    public void nextRound(final Team winner) {
        if(currentRound == null) {
            currentRoundNumber++;
            currentRound = new Round(this.plugin, this.game);
            return;
        }

        currentRound.setWinner(winner);
        rounds.put(currentRoundNumber, currentRound);
        currentRoundNumber++;
        currentRound = new Round(this.plugin, this.game);

        for(final Team team : this.game.getTeamManager().getTeams()) {
            for(final TeamPlayer teamPlayer : team.getTeamPlayers()) {
                final CactusPlayer cactusPlayer = teamPlayer.getCactusPlayer();

                if(cactusPlayer == null) {
                    continue;
                }

                cactusPlayer.addRoundPlayed(game.getMode().getId(), this.game.getArena().getFileName(), plugin.getAbilityManager().getAbility(teamPlayer.getUniqueId()).getId());
            }
        }
    }
}