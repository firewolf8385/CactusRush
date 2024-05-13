package net.jadedmc.cactusrush.game.round;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.cactusrush.game.team.TeamColor;
import net.jadedmc.cactusrush.game.team.TeamPlayer;
import net.jadedmc.jadedutils.player.CustomPlayerSet;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Round {
    private Team winner;
    private final CustomPlayerSet<RoundPlayer> players = new CustomPlayerSet<>();

    public Round(@NotNull final CactusRushPlugin plugin, @NotNull final Game game) {
        this.winner = null;

        for(final Team team : game.getTeamManager().getTeams()) {
            for(final TeamPlayer teamPlayer : team.getTeamPlayers()) {
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
