package net.jadedmc.cactusrush.game.round;

import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.jadedutils.player.CustomPlayerSet;
import org.bson.Document;

public class Round {
    private Team winner;
    private final CustomPlayerSet<RoundPlayer> players = new CustomPlayerSet<>();

    public Round(final Document document) {

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
}
