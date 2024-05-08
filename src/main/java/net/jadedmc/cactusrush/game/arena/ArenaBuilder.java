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

import net.jadedmc.cactusrush.CactusRushPlugin;;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Rotatable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class ArenaBuilder {
    private final CactusRushPlugin plugin;
    private String waitingArea = null;
    private String name;
    private String builders;
    private String id;
    private int voidLevel = -1;
    private final Collection<String> modes = new HashSet<>();
    private final Map<ArenaBuilderTeam.TeamColor, ArenaBuilderTeam> teams = new HashMap<>();
    private boolean editMode = false;
    private final World world;

    public ArenaBuilder(@NotNull final CactusRushPlugin plugin, @NotNull final World world) {
        this.plugin = plugin;
        this.world = world;
        this.builders = "JadedMC";
    }

    public ArenaBuilder(@NotNull final CactusRushPlugin plugin, @NotNull final Arena arena, @NotNull final World world) {
        this.plugin = plugin;
        this.world = world;

        this.id = arena.getFileName();
        this.name = arena.getName();
        this.builders = arena.getBuilders();
        this.voidLevel = arena.getVoidLevel();
        this.editMode = true;
    }

    public void addMode(String mode) {
        this.modes.add(mode.toUpperCase());
    }

    public final boolean editModeEnabled() {
        return editMode;
    }

    public boolean isReady() {
        teams.clear();

        if(this.voidLevel == -1) {
            ChatUtils.broadcast(this.world, "<red><bold>Error</bold> <dark_gray>» <red>Void level not set!");
            return false;
        }

        // Find Signs
        for(final Chunk chunk : world.getLoadedChunks()) {
            for(int x = 0; x < 16; x++) {
                // Cap search height at y = 80, for optimizations.
                for(int y = voidLevel; y < 100; y++) {
                    for(int z = 0; z < 16; z++) {
                        final Block block = chunk.getBlock(x,y,z);

                        // Only check oak signs, because why not.
                        if(block.getType() != Material.OAK_SIGN) {
                            continue;
                        }

                        Sign sign = (Sign) block.getState();
                        String[] lines = sign.getLines();

                        String signData = "";
                        for(String line : lines) {
                            signData += line + ",";
                        }
                        System.out.println("Sign Found: " + signData);

                        // Skip empty signs.
                        if(lines.length < 2) {
                            continue;
                        }

                        // Only look for spawn signs, for now.
                        if(!lines[0].toLowerCase().equalsIgnoreCase("[Spawn]")) {
                            continue;
                        }

                        // Set the waiting area spawn if the sign is found.
                        if(lines[1].equalsIgnoreCase("waiting area")) {
                            Rotatable rotatable = (Rotatable) sign.getBlockData();
                            Vector vector = rotatable.getRotation().getDirection();
                            final double _2PI = 2 * Math.PI;
                            final double signX = vector.getX();
                            final double signZ = vector.getZ();

                            double theta = Math.atan2(-signX, signZ);
                            float yaw = (float) Math.toDegrees((theta + _2PI) % _2PI);

                            waitingArea = "world," + block.getX() + "," + block.getY() + "," + block.getZ() + "," + yaw + ",0";
                            continue;
                        }

                        // Skip other signs that are too short.
                        if(lines.length < 3) {
                            continue;
                        }

                        final ArenaBuilderTeam.TeamColor teamColor = ArenaBuilderTeam.TeamColor.valueOf(lines[1].toUpperCase());
                        ArenaBuilderTeam team;

                        if(teams.containsKey(teamColor)) {
                            team = teams.get(teamColor);
                        }
                        else {
                            team = new ArenaBuilderTeam(teamColor);
                            teams.put(teamColor, team);
                        }

                        Rotatable rotatable = (Rotatable) sign.getBlockData();
                        Vector vector = rotatable.getRotation().getDirection();
                        final double _2PI = 2 * Math.PI;
                        final double signX = vector.getX();
                        final double signZ = vector.getZ();

                        double theta = Math.atan2(-signX, signZ);
                        float yaw = (float) Math.toDegrees((theta + _2PI) % _2PI);

                        String locationString = "world," + block.getX() + "," + block.getY() + "," + block.getZ() + "," + yaw + ",0";

                        switch(lines[2].toLowerCase()) {
                            case "team" -> team.setTeamSpawn(locationString);
                            case "score room" -> team.setScoreRoomSpawn(locationString);
                        }
                    }
                }
            }
        }

        if(this.teams.size() < 2) {
            ChatUtils.broadcast(this.world, "<red><bold>Error</bold> <dark_gray>» <red>Not enough teams found!");
            return false;
        }

        for(final ArenaBuilderTeam team : this.teams.values()) {
            if(team.getTeamSpawn().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red><bold>Error</bold> <dark_gray>» <red>Team spawn missing for " + team.getTeamColor().toString() + "!");
                return false;
            }

            if(team.getScoreRoomSpawn().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red><bold>Error</bold> <dark_gray>» <red>Score Room spawn missing for " + team.getTeamColor().toString() + "!");
                return false;
            }
        }

        // Find team blocks.
        for(Chunk chunk : world.getLoadedChunks()) {
            for(int x = 0; x < 16; x++) {
                for(int y = 0; y < 100; y++) {
                    for(int z = 0; z < 16; z++) {
                        final Block block = chunk.getBlock(x, y, z);
                        final Material material = block.getType();
                        final String locationString = "world," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0,0";

                        for(ArenaBuilderTeam team : teams.values()) {
                            if(material == team.getTeamColor().getGoalMaterial()) {
                                team.addGoalBlock(locationString);
                                continue;
                            }

                            if(material == team.getTeamColor().getBarrierMaterial()) {
                                team.addBarrierBlock(locationString);
                                continue;
                            }

                            if(material == team.getTeamColor().getScoreRoomMaterial()) {
                                team.addScoreRoomBlock(locationString);
                            }
                        }
                    }
                }
            }
        }

        // Check if each team is fully set up.
        for(final ArenaBuilderTeam team : this.teams.values()) {
            // Make sure the team has a spawn point.
            if(team.getTeamSpawn().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red><bold>Error</bold> <dark_gray>» <red>Team spawn point missing for " + team.getTeamColor().toString() + "!");
                return false;
            }

            // Make sure the team has a score room spawn.
            if(team.getScoreRoomSpawn().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red><bold>Error</bold> <dark_gray>» <red>Score Room spawn point missing for " + team.getTeamColor().toString() + "!");
                return false;
            }

            // Make sure the team has goal blocks.
            if(team.getGoalBlocks().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red><bold>Error</bold> <dark_gray>» <red>Goal blocks missing for " + team.getTeamColor().toString() + "!");
                return false;
            }

            // Make sure the team has barriers.
            if(team.getBarrierBlocks().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red><bold>Error</bold> <dark_gray>» <red>Barrier blocks missing for " + team.getTeamColor().toString() + "!");
                return false;
            }

            // Make sure the team has a score room.
            if(team.getScoreRoomBlocks().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red><bold>Error</bold> <dark_gray>» <red>Team spawn missing for " + team.getTeamColor().toString() + "!");
                return false;
            }
        }

        return true;
    }

    public void save() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final Document document = new Document("fileName", id)
                    .append("name", name)
                    .append("builders", builders)
                    .append("voidLevel", voidLevel)
                    .append("modes", modes)
                    .append("waitingArea", waitingArea);

            final Document teamsDocument = new Document();
            for(ArenaBuilderTeam team : teams.values()) {
                teamsDocument.append(team.getTeamColor().toString(), team.toDocument());
            }

            document.append("teams", teamsDocument);

            // Add the document to MongoDB
            if(!editMode) {
                JadedAPI.getMongoDB().client().getDatabase("cactusrush").getCollection("maps").insertOne(document);
            }
            else {
                // Replaces the existing file.
                Document old = JadedAPI.getMongoDB().client().getDatabase("cactusrush").getCollection("maps").find(eq("fileName", id)).first();
                JadedAPI.getMongoDB().client().getDatabase("cactusrush").getCollection("maps").replaceOne(old, document);
            }

            File worldFolder = world.getWorldFolder();

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                world.getPlayers().forEach(player -> JadedAPI.getPlugin().lobbyManager().sendToLobby(player));
                Bukkit.unloadWorld(world, true);

                // Saves the world to MongoDB.
                JadedAPI.getPlugin().worldManager().saveWorld(worldFolder, id);

                // Load the new arena.
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    JadedAPI.getRedis().publish("cactusrush", "arena " + id);
                });
            });
        });
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVoidLevel(int voidLevel) {
        this.voidLevel = voidLevel;
    }

    /**
     * Stores per-team data about an arena.
     * Contains information such as team spawn and goal locations.
     */
    public final static class ArenaBuilderTeam {
        private final Collection<String> goalBlocks = new HashSet<>();
        private final Collection<String> barrierBlocks = new HashSet<>();
        private final Collection<String> scoreRoomBlocks = new HashSet<>();
        private String teamSpawn;
        private String scoreRoomSpawn;
        private final TeamColor teamColor;

        /**
         * Creates a team with a given team color.
         * @param teamColor Team color.
         */
        public ArenaBuilderTeam(final TeamColor teamColor) {
            this.teamColor = teamColor;
            teamSpawn = "";
            scoreRoomSpawn = "";
        }

        /**
         * Add a block to the barrier block list.
         * @param barrierBlock Block to add.
         */
        public void addBarrierBlock(String barrierBlock) {
            barrierBlocks.add(barrierBlock);
        }

        /**
         * Add a block to the goal block list.
         * @param goalBlock Block to add.
         */
        public void addGoalBlock(String goalBlock) {
            goalBlocks.add(goalBlock);
        }

        /**
         * Add a block to the score room block list.
         * @param scoreRoomBlock Block to add.
         */
        public void addScoreRoomBlock(String scoreRoomBlock) {
            scoreRoomBlocks.add(scoreRoomBlock);
        }

        /**
         * Get the barrier block locations.
         * @return All barrier blocks.
         */
        public Collection<String> getBarrierBlocks() {
            return barrierBlocks;
        }

        /**
         * Get the goal block locations.
         * @return All goal blocks.
         */
        public Collection<String> getGoalBlocks() {
            return goalBlocks;
        }

        /**
         * Get the score room block locations.
         * @return All score room blocks.
         */
        public Collection<String> getScoreRoomBlocks() {
            return scoreRoomBlocks;
        }

        /**
         * Get the place where team members spawn when they score a goal.
         * @return Team score room spawn point.
         */
        public String getScoreRoomSpawn() {
            return scoreRoomSpawn;
        }

        /**
         * Gets the color of the Team.
         * @return Team color.
         */
        public TeamColor getTeamColor() {
            return teamColor;
        }

        /**
         * Get the point in which team members spawn.
         * @return Team spawn point.
         */
        public String getTeamSpawn() {
            return teamSpawn;
        }

        /**
         * Set the place in which team members spawn when they score a goal.
         * @param scoreRoomSpawn Team score room spawn point.
         */
        public void setScoreRoomSpawn(final String scoreRoomSpawn) {
            this.scoreRoomSpawn = scoreRoomSpawn;
        }

        /**
         * Set the place in which team members spawn.
         * @param teamSpawn Team spawn point.
         */
        public void setTeamSpawn(final String teamSpawn) {
            this.teamSpawn = teamSpawn;
        }

        /**
         * Creates a Bson document containing the team information.
         * @return Document storing all team data.
         */
        public Document toDocument() {
            return new Document()
                    .append("teamSpawn", teamSpawn)
                    .append("scoreRoomSpawn", scoreRoomSpawn)
                    .append("goals", goalBlocks)
                    .append("barriers", barrierBlocks)
                    .append("scoreRoomBlocks", scoreRoomBlocks);
        }

        /**
         * Represents the color of a team. Used to differentiate 2 or more teams when creating an arena.
         */
        public enum TeamColor {
            YELLOW(Material.YELLOW_WOOL, Material.YELLOW_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS),
            PURPLE(Material.PURPLE_WOOL, Material.PURPLE_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS);

            private final Material goalMaterial;
            private final Material barrierMaterial;
            private final Material scoreRoomMaterial;

            /**
             * Creates the team.
             * @param goalMaterial Material the team's goal platform is made of.
             * @param barrierMaterial Material the team's barriers are made of.
             * @param scoreRoomMaterial Material the team' score room is made of.
             */
            TeamColor(Material goalMaterial, Material barrierMaterial, Material scoreRoomMaterial) {
                this.goalMaterial = goalMaterial;
                this.barrierMaterial = barrierMaterial;
                this.scoreRoomMaterial = scoreRoomMaterial;
            }

            /**
             * Gets the Barrier Material of the team.
             * @return Team Barrier Material.
             */
            public final Material getBarrierMaterial() {
                return barrierMaterial;
            }

            /**
             * Gets the Goal Material of the team.
             * @return Team Goal Material.
             */
            public final Material getGoalMaterial() {
                return goalMaterial;
            }

            /**
             * Gets the Score Room Material of the team.
             * @return Team Score Room Material.
             */
            public final Material getScoreRoomMaterial() {
                return scoreRoomMaterial;
            }
        }
    }
}