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
import net.jadedmc.nanoid.NanoID;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class Game {
    private final CactusRushPlugin plugin;
    private final Arena arena;
    private GameState gameState;
    private final Mode mode;
    private final RoundManager roundManager = new RoundManager();
    private final TeamManager teamManager;
    private final Collection<UUID> players = new HashSet<>();
    private final Collection<UUID> spectators = new HashSet<>();
    private final NanoID nanoID;


    public Game(@NotNull final CactusRushPlugin plugin, @NotNull final Document document) {
        this.plugin = plugin;
        teamManager = new TeamManager(plugin, this);

        this.nanoID = NanoID.fromString(document.getString("nanoID"));
        this.arena = plugin.getArenaManager().getArena(document.getString("arena"));
        this.mode = Mode.valueOf(document.getString("mode"));
        this.gameState = GameState.valueOf(document.getString("gameState"));
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
}