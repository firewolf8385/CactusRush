package net.jadedmc.cactusrush.game.round;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.team.Team;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RoundManager {
    private final CactusRushPlugin plugin;
    private final Game game;
    private Round currentRound;
    private Map<Integer, Round> rounds = new HashMap<>();
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

    public void nextRound(@NotNull final Team winner) {
        if(currentRound == null) {
            return;
        }

        currentRound.setWinner(winner);
        rounds.put(currentRoundNumber, currentRound);
        currentRoundNumber++;
        currentRound = new Round(this.plugin, this.game);
    }
}