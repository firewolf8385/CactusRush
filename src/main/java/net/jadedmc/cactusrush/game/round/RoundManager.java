package net.jadedmc.cactusrush.game.round;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RoundManager {
    private Round currentRound;
    private Map<Integer, Round> rounds = new HashMap<>();


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

    public Document getRoundsDocument() {
        final Document document = new Document();

        for(final int roundNumber : this.rounds.keySet()) {
            final Round round = this.rounds.get(roundNumber);
            document.append(roundNumber + "", round.toDocument());
        }

        return document;
    }

    public void loadRoundsDocument(@NotNull final Document document) {
        for(@NotNull final String key : document.keySet()) {
            final int roundNumber = Integer.parseInt(key);
            final Document roundDocument = document.get(key, Document.class);
            this.rounds.put(roundNumber, new Round(roundDocument));
        }
    }

    public void saveCurrentRound(final int roundNumber) {
        this.rounds.put(roundNumber, this.currentRound);
    }
}