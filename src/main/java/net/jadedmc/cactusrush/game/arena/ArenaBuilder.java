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
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bson.Document;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    }

    public final boolean editModeEnabled() {
        return editMode;
    }

    public CompletableFuture<Boolean> isSet() {
        return CompletableFuture.supplyAsync(() -> {

        });
    }

    public void save() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

        });
    }

    public boolean isReady() {
        teams.clear();

        if(waitingArea == null) {
            return false;
        }

        // Find Signs
        for(final Chunk chunk : world.getLoadedChunks()) {
            for(int x = 0; x < 16; x++) {
                // Cap search height at y = 80, for optimizations.
                for(int y = voidLevel; y < 80; y++) {
                    for(int z = 0; z < 16; z++) {
                        final Block block = chunk.getBlock(x,y,z);

                        if(block.getType() != Material.OAK_SIGN) {
                            continue;
                        }

                        Sign sign = (Sign) block.getState();
                        String[] lines = sign.getLines();

                        if(lines.length < 3) {
                            continue;
                        }

                        if(!lines[0].toLowerCase().equalsIgnoreCase("[Spawn]")) {
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

                        switch(lines[1].toLowerCase()) {
                            case "team" -> team.setTeamSpawn(locationString);
                            case "score room" -> team.setScoreRoomSpawn(locationString);
                        }
                    }
                }
            }
        }

        if(this.teams.size() < 2) {
            ChatUtils.broadcast(this.world, "<red>Not enough teams found!");
            return false;
        }

        for(final ArenaBuilderTeam team : this.teams.values()) {
            if(team.getTeamSpawn().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red>Team spawn missing for " + team.getTeamColor().toString() + "!");
                return false;
            }

            if(team.getScoreRoomSpawn().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red>Score Room spawn missing for " + team.getTeamColor().toString() + "!");
                return false;
            }
        }

        // Find team blocks.
        for(Chunk chunk : world.getLoadedChunks()) {
            for(int x = 0; x < 16; x++) {
                for(int y = 0; y < (world.getMaxHeight() - 1); y++) {
                    for(int z = 0; z < 16; z++) {
                        Block block = chunk.getBlock(x, y, z);
                        Material material = block.getType();
                        String locationString = "world," + block.getX() + "," + block.getY() + "," + block.getZ() + "," + yaw + ",0";

                        for(ArenaBuilderTeam team : teams.values()) {
                            if(material == team.getTeamColor().getGoalMaterial()) {
                                team.addGoalBlock(locationString);
                                continue;
                            }

                            if(material == team.getTeamColor().barrierMaterial) {
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

        for(final ArenaBuilderTeam team : this.teams.values()) {
            if(team.getGoalBlocks().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red>Goal blocks missing for " + team.getTeamColor().toString() + "!");
                return false;
            }

            if(team.getBarrierBlocks().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red>Barrier blocks missing for " + team.getTeamColor().toString() + "!");
                return false;
            }

            if(team.getScoreRoomBlocks().isEmpty()) {
                ChatUtils.broadcast(this.world, "<red>Team spawn missing for " + team.getTeamColor().toString() + "!");
                return false;
            }
        }

        return true;
    }

    public final static class ArenaBuilderTeam {
        private final Collection<String> goalBlocks = new HashSet<>();
        private final Collection<String> barrierBlocks = new HashSet<>();
        private final Collection<String> scoreRoomBlocks = new HashSet<>();
        private String teamSpawn;
        private String scoreRoomSpawn;
        private final TeamColor teamColor;

        public ArenaBuilderTeam(final TeamColor teamColor) {
            this.teamColor = teamColor;
            teamSpawn = "";
            scoreRoomSpawn = "";
        }

        public void addGoalBlock(String goalBlock) {
            goalBlocks.add(goalBlock);
        }

        public void addBarrierBlock(String barrierBlock) {
            barrierBlocks.add(barrierBlock);
        }

        public void addScoreRoomBlock(String scoreRoomBlock) {
            scoreRoomBlocks.add(scoreRoomBlock);
        }

        public String getTeamSpawn() {
            return teamSpawn;
        }

        public String getScoreRoomSpawn() {
            return scoreRoomSpawn;
        }

        public TeamColor getTeamColor() {
            return teamColor;
        }

        public Collection<String> getGoalBlocks() {
            return goalBlocks;
        }

        public Collection<String> getBarrierBlocks() {
            return barrierBlocks;
        }

        public Collection<String> getScoreRoomBlocks() {
            return scoreRoomBlocks;
        }

        public void setTeamSpawn(final String teamSpawn) {
            this.teamSpawn = teamSpawn;
        }

        public void setScoreRoomSpawn(final String scoreRoomSpawn) {
            this.scoreRoomSpawn = scoreRoomSpawn;
        }

        public Document toDocument() {
            final Document document = new Document()
                    .append("teamSpawn", teamSpawn)
                    .append("scoreRoomSpawn", scoreRoomSpawn)
                    .append("goals", goalBlocks)
                    .append("barriers", barrierBlocks)
                    .append("scoreRoomBlocks", scoreRoomBlocks);
        }

        public enum TeamColor {
            YELLOW(Material.YELLOW_WOOL, Material.YELLOW_STAINED_GLASS, Material.YELLOW_STAINED_GLASS_PANE),
            PURPLE(Material.PURPLE_WOOL, Material.PURPLE_STAINED_GLASS, Material.PURPLE_STAINED_GLASS_PANE);

            private final Material goalMaterial;
            private final Material barrierMaterial;
            private final Material scoreRoomMaterial;
            TeamColor(Material goalMaterial, Material barrierMaterial, Material scoreRoomMaterial) {
                this.goalMaterial = goalMaterial;
                this.barrierMaterial = barrierMaterial;
                this.scoreRoomMaterial = scoreRoomMaterial;
            }

            public Material getGoalMaterial() {
                return goalMaterial;
            }

            public Material getBarrierMaterial() {
                return barrierMaterial;
            }

            public Material getScoreRoomMaterial() {
                return scoreRoomMaterial;
            }
        }
    }
}