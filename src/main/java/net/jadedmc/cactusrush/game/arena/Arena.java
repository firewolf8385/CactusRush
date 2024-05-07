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
package net.jadedmc.cactusrush.game.arena;

import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.jadedutils.LocationUtils;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents an area where a Cactus Rush game is played.
 * Stored and loaded through MongoDB.
 */
public final class Arena {
    private final String fileName;
    private final String name;
    private final String builders;
    private final Collection<Mode> modes = new HashSet<>();
    private final Collection<ArenaTeam> teams = new HashSet<>();
    private final Location waitingArea;
    private final int voidLevel;

    /**
     * Creates the Arena object using a given Bson document holding the configuration info.
     * @param document Configuration document.
     */
    public Arena(@NotNull final Document document) {
        this.fileName = document.getString("fileName");
        this.name = document.getString("name");
        this.builders = document.getString("builders");
        this.waitingArea = LocationUtils.fromString(document.getString("waitingArea"));
        this.voidLevel = document.getInteger("voidLevel");

        // Load applicable modes.
        for(final String mode : document.getList("modes", String.class)) {
            this.modes.add(Mode.valueOf(mode));
        }

        // Load team data.
        final Document teamsDocument = document.get("teams", Document.class);
        for(final String team : teamsDocument.keySet()) {
            final Document teamDocument = teamsDocument.get(team, Document.class);
            this.teams.add(new ArenaTeam(teamDocument));
        }
    }

    /**
     * Gets a String storing the names of the Arena's builders.
     * @return Arena's builders.
     */
    @NotNull
    public String getBuilders() {
        return this.builders;
    }

    /**
     * Gets the name of the file that holds the arena world.
     * @return Arena world filename.
     */
    @NotNull
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Gets the modes in which this arena can be used.
     * @return All modes the arena is use with.
     */
    @NotNull
    public Collection<Mode> getModes() {
        return this.modes;
    }

    /**
     * Gets the name of the arena.
     * @return Arena name.
     */
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Gets the stored team data of the arena.
     * @return ArenaTeam object.
     */
    @NotNull
    public Collection<ArenaTeam> getTeams() {
        return this.teams;
    }

    /**
     * Gets the y-level in which players should be killed by "the void".
     * @return Arena void level.
     */
    public int getVoidLevel() {
        return this.voidLevel;
    }

    /**
     * Get the spawn location of the waiting area for the arena, based on a given world.
     * Done so that multiple copies of the arena can be used at the same time.
     * @param world World to get the location for.
     * @return Resulting spawn location.
     */
    @NotNull
    public Location getWaitingArea(@NotNull final World world) {
        return LocationUtils.replaceWorld(world, this.waitingArea);
    }

    /**
     * Stores per-team data about an arena.
     * Contains information such as team spawn and goal locations.
     */
    public final static class ArenaTeam {
        private final Collection<Location> goalBlocks = new HashSet<>();
        private final Collection<Location> barrierBlocks = new HashSet<>();
        private final Collection<Location> scoreRoomBlocks = new HashSet<>();
        private final Location teamSpawn;
        private final Location scoreRoomSpawn;

        /**
         * Creates the team.
         * @param document Document containing team data.
         */
        public ArenaTeam(@NotNull final Document document) {
            // Load spawns.
            teamSpawn = LocationUtils.fromString(document.getString("teamSpawn"));
            scoreRoomSpawn = LocationUtils.fromString(document.getString("scoreRoomSpawn"));

            // Load goal blocks.
            for(final String goalLocation : document.getList("goals", String.class)) {
                final Location location = LocationUtils.fromString(goalLocation);
                goalBlocks.add(location);
            }

            // Load barrier blocks.
            for(final String barrierLocation : document.getList("barriers", String.class)) {
                final Location location = LocationUtils.fromString(barrierLocation);
                barrierBlocks.add(location);
            }

            // Load score room blocks
            for(final String scoreRoomBlockLocation : document.getList("scoreRoomBlocks", String.class)) {
                final Location location = LocationUtils.fromString(scoreRoomBlockLocation);
                scoreRoomBlocks.add(location);
            }
        }

        /**
         * Get the barrier blocks of the team, based on a given world.
         * Done so that multiple copies of the arena can be used at the same time.
         * @param world World to get the blocks for.
         * @return Resulting barrier block locations.
         */
        @NotNull
        public Collection<Block> getBarrierBlocks(@NotNull final World world) {
            final Collection<Block> blocks = new HashSet<>();
            this.barrierBlocks.forEach(location -> blocks.add(location.getBlock()));
            return blocks;
        }

        /**
         * Get the goal blocks of the team, based on a given world.
         * Done so that multiple copies of the arena can be used at the same time.
         * @param world World to get the blocks for.
         * @return Resulting goal block locations.
         */
        @NotNull
        public Collection<Block> getGoalBlocks(@NotNull final World world) {
            final Collection<Block> blocks = new HashSet<>();
            this.goalBlocks.forEach(location -> blocks.add(location.getBlock()));
            return blocks;
        }

        /**
         * Get the score room blocks of the team, based on a given world.
         * Done so that multiple copies of the arena can be used at the same time.
         * @param world World to get the blocks for.
         * @return Resulting score room block locations.
         */
        @NotNull
        public Collection<Block> getScoreRoomBlocks(@NotNull final World world) {
            final Collection<Block> blocks = new HashSet<>();
            this.scoreRoomBlocks.forEach(location -> blocks.add(location.getBlock()));
            return blocks;
        }

        /**
         * Get the score room spawn of the team, based on a given world.
         * Done so that multiple copies of the arena can be used at the same time.
         * @param world World to get the spawn location for.
         * @return Resulting score room spawn location.
         */
        @NotNull
        public Location getScoreRoomSpawn(@NotNull final World world) {
            return LocationUtils.replaceWorld(world, this.scoreRoomSpawn);
        }

        /**
         * Get the arena spawn location of the team, based on a given world.
         * Done so that multiple copies of the arena can be used at the same time.
         * @param world World to get the spawn location for.
         * @return Resulting arena spawn location.
         */
        @NotNull
        public Location getTeamSpawn(@NotNull final World world) {
            return LocationUtils.replaceWorld(world, this.teamSpawn);
        }
    }
}