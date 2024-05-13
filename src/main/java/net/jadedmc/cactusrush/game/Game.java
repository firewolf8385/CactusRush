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
package net.jadedmc.cactusrush.game;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.arena.Arena;
import net.jadedmc.cactusrush.game.round.RoundManager;
import net.jadedmc.cactusrush.game.team.TeamManager;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.nanoid.NanoID;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Game {
    private final CactusRushPlugin plugin;
    private final Arena arena;
    private GameState gameState;
    private final Mode mode;
    private final RoundManager roundManager;
    private final TeamManager teamManager;
    private final Collection<UUID> players = new HashSet<>();
    private final Collection<UUID> spectators = new HashSet<>();
    private final NanoID nanoID;


    public Game(@NotNull final CactusRushPlugin plugin, @NotNull final Document document) {
        this.plugin = plugin;
        this.teamManager = new TeamManager(plugin, this);
        this.roundManager = new RoundManager(plugin, this);

        this.nanoID = NanoID.fromString(document.getString("nanoID"));
        this.arena = plugin.getArenaManager().getArena(document.getString("arena"));
        this.mode = Mode.valueOf(document.getString("mode"));
        this.gameState = GameState.valueOf(document.getString("gameState"));

        for(final String playerUUID : document.getList("players", String.class)) {
            players.add(UUID.fromString(playerUUID));
        }

        for(final String playerUUID : document.getList("spectators", String.class)) {
            spectators.add(UUID.fromString(playerUUID));
        }

        final Document teamsDocument = document.get("teams", Document.class);
        this.teamManager.loadTeamsDocument(teamsDocument);

        final Document roundsDocument = document.get("rounds", Document.class);
        this.roundManager.loadRoundsDocument(plugin, roundsDocument, this);
    }

    public Arena getArena() {
        return arena;
    }

    public Mode getMode() {
        return mode;
    }

    public Collection<UUID> getPlayers() {
        return players;
    }

    public RoundManager getRoundManager() {
        return roundManager;
    }

    public Collection<UUID> getSpectators() {
        return spectators;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public Document toDocument() {
        final Document document = new Document()
                .append("nanoID", nanoID.toString())
                .append("server", JadedAPI.getCurrentInstance().getName())
                .append("arena", arena.getName())
                .append("mode", mode.toString())
                .append("gameState", gameState.toString())
                .append("round", this.roundManager.getCurrentRoundNumber());

        final List<String> playersList = new ArrayList<>();
        for(final UUID playerUUID : this.players) {
            playersList.add(playerUUID.toString());
        }
        document.append("players", playersList);

        final List<String> spectatorsList = new ArrayList<>();
        for(final UUID spectatorUUID : spectators) {
            spectatorsList.add(spectatorUUID.toString());
        }
        document.append("spectators", spectatorsList);
        document.append("teams", this.teamManager.getTeamsDocument());
        document.append("rounds", this.roundManager.getRoundsDocument());

        return document;
    }
}