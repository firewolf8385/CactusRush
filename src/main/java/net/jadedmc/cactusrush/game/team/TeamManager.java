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

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.game.arena.Arena;
import net.jadedmc.cactusrush.player.CactusPlayer;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.party.Party;
import net.jadedmc.jadedutils.player.CustomPlayerSet;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TeamManager {
    private final CactusRushPlugin plugin;
    private final Game game;
    private final Collection<Team> teams = new LinkedHashSet<>();
    private final List<TeamColor> availableColors = new ArrayList<>();

    public TeamManager(@NotNull final CactusRushPlugin plugin, @NotNull final Game game) {
        this.plugin = plugin;
        this.game = game;

        // Sets the 2 default colors.
        availableColors.add(TeamColor.YELLOW);
        availableColors.add(TeamColor.PURPLE);

        // Adds the rest of the available colors.
        availableColors.add(TeamColor.RED);
        availableColors.add(TeamColor.ORANGE);
        availableColors.add(TeamColor.GREEN);
        availableColors.add(TeamColor.BLUE);
        availableColors.add(TeamColor.AQUA);
        availableColors.add(TeamColor.PINK);
        availableColors.add(TeamColor.BLACK);
        availableColors.add(TeamColor.WHITE);
        availableColors.add(TeamColor.CYAN);
    }

    public Team createTeam(@NotNull final Collection<UUID> players, @NotNull final Arena.ArenaTeam arenaTeam, @NotNull final Game game) {
        // Primary color.
        {
            final List<CactusPlayer> coloredPlayers = new ArrayList<>();
            for(final UUID player : players) {
                final CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);

                if(cactusPlayer == null) {
                    continue;
                }

                if(!cactusPlayer.hasPrimaryTeamColor()) {
                    continue;
                }

                coloredPlayers.add(cactusPlayer);
            }

            Collections.shuffle(coloredPlayers);

            for(final CactusPlayer cactusPlayer : coloredPlayers) {
                if(availableColors.contains(cactusPlayer.getPrimaryTeamColor())) {
                    return createTeam(players, arenaTeam, cactusPlayer.getPrimaryTeamColor(), game);
                }
            }
        }

        // Secondary color.
        {
            final List<CactusPlayer> coloredPlayers = new ArrayList<>();
            for(final UUID player : players) {
                final CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);

                if(!cactusPlayer.hasSecondaryTeamColor()) {
                    continue;
                }

                coloredPlayers.add(cactusPlayer);
            }

            Collections.shuffle(coloredPlayers);

            for(final CactusPlayer cactusPlayer : coloredPlayers) {
                if(availableColors.contains(cactusPlayer.getSecondaryTeamColor())) {
                    return createTeam(players, arenaTeam, cactusPlayer.getSecondaryTeamColor(), game);
                }
            }
        }

        // Otherwise, gets the next available color.
        final TeamColor teamColor = availableColors.get(0);
        return createTeam(players, arenaTeam, teamColor, game);
    }

    /**
     * Creates a new team using a specific color.
     * @param players Players to add to the team.
     * @param arenaTeam Arena Team object.
     * @param teamColor Team Color to use.
     * @return Created team.
     */
    public Team createTeam(@NotNull final Collection<UUID> players, @NotNull final Arena.ArenaTeam arenaTeam, final TeamColor teamColor, @NotNull final Game game) {
        availableColors.remove(teamColor);

        final CustomPlayerSet<TeamPlayer> teamPlayers = new CustomPlayerSet<>();

        for(final UUID playerUUID : players) {
            final CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(playerUUID);
            teamPlayers.add(new TeamPlayer(playerUUID, cactusPlayer.getName(), cactusPlayer.getJadedPlayer().getRank(), game));
        }

        final Team team = new Team(teamPlayers, arenaTeam, teamColor);
        teams.add(team);
        return team;
    }

    public void generateTeams(@NotNull final Game game) {
        if(game.getMode() == Mode.DUEL) {
            generateDuelTeams();
            return;
        }

        final List<UUID> tempPlayers = new ArrayList<>(game.getPlayers());
        Collections.shuffle(tempPlayers);

        final List<ArrayList<UUID>> playerGroups = new ArrayList<>();
        List<Party> parties = new ArrayList<>();
        List<UUID> soloPlayers = new ArrayList<>();

        for(int i = 0; i < game.getMode().getTeamCount(); i++) {
            playerGroups.add(new ArrayList<>());
        }

        for(final UUID playerUUID : tempPlayers) {
            final Party party = JadedAPI.getParty(playerUUID);

            if(party == null) {
                soloPlayers.add(playerUUID);
                continue;
            }

            if(parties.contains(party)) {
                continue;
            }

            parties.add(party);
        }

        for(final Party party : parties) {
            final Collection<Player> onlinePlayers = party.getOnlinePlayers();

            if(onlinePlayers.size() > game.getMode().getTeamSize()) {
                for(final Player player : onlinePlayers) {
                    soloPlayers.add(player.getUniqueId());
                }
                continue;
            }

            List<UUID> smallestGroup = playerGroups.get(0);
            for(final List<UUID> group : playerGroups) {
                if(group.size() < smallestGroup.size()) {
                    smallestGroup = group;
                }
            }

            // Checks if the party can fit in the smallest team.
            if(smallestGroup.size() + onlinePlayers.size() <= game.getMode().getTeamSize()) {
                // If it can, adds them to the team.
                for(final Player player : onlinePlayers) {
                    smallestGroup.add(player.getUniqueId());
                }
            }
            else {
                // Otherwise, splits them into solo players.
                for(final Player player : onlinePlayers) {
                    soloPlayers.add(player.getUniqueId());
                }
            }
        }

        // Shuffle solo players.
        Collections.shuffle(soloPlayers);

        // Loop through solo players to assign them teams.
        while(soloPlayers.size() > 0) {
            List<UUID> smallestGroup = playerGroups.get(0);

            // Loop through each team to find the smallest.
            for(List<UUID> group : playerGroups) {
                if(group.size() < smallestGroup.size()) {
                    smallestGroup = group;
                }
            }

            // Adds the player to the smallest team.
            smallestGroup.add(soloPlayers.get(0));
            soloPlayers.remove(soloPlayers.get(0));
        }

        // Creates the team objects.
        int arenaTeamNumber = 0;
        for(final List<UUID> group : playerGroups) {
            createTeam(group, game.getArena().getTeams().get(arenaTeamNumber), game);
            arenaTeamNumber++;
        }
    }

    private void generateDuelTeams() {

    }

    @Nullable
    public Team getTeam(final Player player) {
        for(final Team team : this.teams) {
            if(team.getTeamPlayers().hasPlayer(player)) {
                return team;
            }
        }

        return null;
    }

    @Nullable
    public Team getTeam(final TeamColor teamColor) {
        for(final Team team : this.teams) {
            if(team.getColor() == teamColor) {
                return team;
            }
        }

        return null;
    }

    /**
     * Gets all stored teams.
     * @return A Collection of all teams.
     */
    public Collection<Team> getTeams() {
        return teams;
    }

    public Document getTeamsDocument() {
        final Document document = new Document();
        for(final Team team : this.teams) {
            document.append(team.getColor().toString(), team.toDocument());
        }

        return document;
    }

    public void loadTeamsDocument(@NotNull final Document document) {
        for(final String teamColor : document.keySet()) {
            final Document teamDocument = document.get(teamColor, Document.class);
            final Team team = new Team(document, this.game);
            this.teams.add(team);
        }
    }
}
