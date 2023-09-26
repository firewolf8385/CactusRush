/*
 * This file is part of Cactus Rush, licensed under the MIT License.
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
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.game.teams;

import net.jadedmc.cactusrush.game.arena.ArenaTeam;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages the creation of teams.
 */
public class TeamManager {
    private final Collection<Team> teams = new LinkedHashSet<>();
    private final List<TeamColor> availableColors = new ArrayList<>();

    /**
     * Creates the Team Manager and sets up available colors.
     */
    public TeamManager() {
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
    }

    /**
     * Creates a new team using the next available color.
     * @param players Players to add to the team.
     * @param arenaTeam Arena Team object.
     * @return Created team.
     */
    public Team createTeam(Collection<Player> players, ArenaTeam arenaTeam) {
        // TODO: Allow parties to chose their own color.
        if(false) {

        }

        // Otherwise, gets the next available color.
        TeamColor teamColor = availableColors.get(0);
        return createTeam(players, arenaTeam, teamColor);
    }

    /**
     * Creates a new team using a specific color.
     * @param players Players to add to the team.
     * @param arenaTeam Arena Team object.
     * @param teamColor Team Color to use.
     * @return Created team.
     */
    public Team createTeam(Collection<Player> players, ArenaTeam arenaTeam, TeamColor teamColor) {
        availableColors.remove(teamColor);
        Team team = new Team(players, arenaTeam, teamColor);
        teams.add(team);
        return team;
    }

    /**
     * Get the team a player is currently on.
     * Returns null if none found.
     * @param player Player to get team of.
     * @return Team the player is on.
     */
    public Team getTeam(Player player) {
        for(Team team : teams) {
            if(team.players().contains(player)) {
                return team;
            }
        }

        return null;
    }

    /**
     * Get all currently active teams.
     * @return All teams.
     */
    public Collection<Team> teams() {
        return teams;
    }
}