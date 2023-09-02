/*
 * This file is part of JadedChat, licensed under the MIT License.
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
package net.jadedmc.cactusrush.game.arena.builder;

import org.bukkit.Location;

/**
 * Represents a team in an arena that is still being set up.
 */
public class ArenaBuilderTeam {
    private final ArenaBuilderTeamScoreRoom scoreRoom;
    private Location bounds1;
    private Location bounds2;
    private Location spawnPoint;

    /**
     * Creates the team builder.
     */
    public ArenaBuilderTeam() {
        // Assign the score room.
        scoreRoom = new ArenaBuilderTeamScoreRoom();
    }

    /**
     * Get the bounds 1 location of the team spawn.
     * @return Bounds 1 location.
     */
    public Location getBounds1() {
        return bounds1;
    }

    /**
     * Get the bounds 2 location of the team spawn.
     * @return Bounds 2 location.
     */
    public Location getBounds2() {
        return bounds2;
    }

    /**
     * Get the spawn point of the team.
     * @return Team spawn point.
     */
    public Location getSpawnPoint() {
        return spawnPoint;
    }

    /**
     * Get the score room of the team.
     * @return Team score room.
     */
    public ArenaBuilderTeamScoreRoom getScoreRoom() {
        return scoreRoom;
    }

    /**
     * Get if the team has been set up completely.
     * @return Whether the team was set up.
     */
    public boolean isSet() {
        // Make sure bounds 1 is set.
        if(bounds1 == null) {
            return false;
        }

        // Make sure bounds 2 is set.
        if(bounds2 == null) {
            return false;
        }

        // Make sure the spawn point is set.
        if(spawnPoint == null) {
            return false;
        }

        // Return whether the score room is set.
        return scoreRoom.isSet();
    }

    /**
     * Set the bounds 1 location of the team.
     * @param bounds1 Bounds 1 location.
     */
    public void setBounds1(Location bounds1) {
        this.bounds1 = bounds1;
    }

    /**
     * Set the bounds 2 location of the team.
     * @param bounds2 Bounds 2 location.
     */
    public void setBounds2(Location bounds2) {
        this.bounds2 = bounds2;
    }

    /**
     * Set the spawn point of the team.
     * @param spawnPoint Team spawn point.
     */
    public void setSpawnPoint(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    /**
     * Represents a score room that is still being set up.
     */
    public static class ArenaBuilderTeamScoreRoom {
        private Location bounds1;
        private Location bounds2;
        private Location spawnPoint;

        /**
         * Get the bounds 1 location of the score room.
         * @return Bounds 1 location.
         */
        public Location getBounds1() {
            return bounds1;
        }

        /**
         * Get the bounds 2 location of the score room.
         * @return Bounds 2 location.
         */
        public Location getBounds2() {
            return bounds2;
        }

        /**
         * Get the spawn point of the score room.
         * @return Score room spawn point.
         */
        public Location getSpawnPoint() {
            return spawnPoint;
        }

        /**
         * Get if the score room is set up.
         * @return Whether the score room was set up.
         */
        public boolean isSet() {
            // Make sure bounds 1 was set.
            if(bounds1 == null) {
                return false;
            }

            // Make sure bounds 2 was set.
            if(bounds2 == null) {
                return false;
            }

            // Make sure the spawn point was set.
            if(spawnPoint == null) {
                return false;
            }

            // All score room aspects have been set.
            return true;
        }

        /**
         * Set the bounds 1 of the score room.
         * @param bounds1 Bounds 1 location.
         */
        public void setBounds1(Location bounds1) {
            this.bounds1 = bounds1;
        }

        /**
         * Set the bounds 2 of the score room.
         * @param bounds2 Bounds 2 location.
         */
        public void setBounds2(Location bounds2) {
            this.bounds2 = bounds2;
        }

        /**
         * GEt the spawn point of the score room.
         * @param spawnPoint Spawn point location.
         */
        public void setSpawnPoint(Location spawnPoint) {
            this.spawnPoint = spawnPoint;
        }
    }
}