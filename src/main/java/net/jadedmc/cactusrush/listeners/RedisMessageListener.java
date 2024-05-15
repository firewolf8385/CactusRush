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
package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.arena.Arena;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.events.RedisMessageEvent;
import net.jadedmc.nanoid.NanoID;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RedisMessageListener implements Listener {
    private final CactusRushPlugin plugin;

    public RedisMessageListener(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRedisMessage(@NotNull final RedisMessageEvent event) {
        System.out.println(event.getChannel() + " " + event.getMessage());

        if(!event.getChannel().equalsIgnoreCase("cactusrush")) {
            return;
        }

        final String[] args = event.getMessage().split(" ");

        switch(args[0].toLowerCase()) {
            case "arena" -> {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    final String arenaID = args[1];
                    System.out.println("Arena Update Received: " + arenaID);
                    plugin.getArenaManager().loadArena(arenaID);
                });
            }
            case "create" -> {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    final NanoID nanoID = NanoID.fromString(args[1]);
                    final Document gameDocument = Document.parse(JadedAPI.getRedis().get("cactusrush:games:" + nanoID));
                    final Game game = new Game(plugin, gameDocument);

                    if(!game.getServer().equalsIgnoreCase(JadedAPI.getCurrentInstance().getName())) {
                        return;
                    }

                    plugin.getGameManager().getLocalGames().add(game);
                    final CompletableFuture<World> worldFuture = JadedAPI.getPlugin().worldManager().copyWorld(game.getArena().getFileName(), nanoID.toString());
                    worldFuture.thenAccept(world -> {
                        game.setWorld(world);

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            world.getBlockAt(game.getArena().getWaitingArea(world)).setType(Material.AIR);
                            for(Arena.ArenaTeam arenaTeam : game.getArena().getTeams()) {
                                world.getBlockAt(arenaTeam.getTeamSpawn(world)).setType(Material.AIR);
                                world.getBlockAt(arenaTeam.getScoreRoomSpawn(world)).setType(Material.AIR);
                            }
                        });

                        final StringBuilder builder = new StringBuilder();
                        game.getPlayers().forEach(player -> builder.append(player.toString()).append(","));

                        System.out.println("connect " + builder.substring(0, builder.length() - 1) + " " + game.getServer());
                        JadedAPI.getRedis().publish("proxy", "connect " + builder.substring(0, builder.length() - 1) + " " + game.getServer());
                    });
                });
            }

            case "addplayers" -> {
                final String[] players = args[2].split(",");
                final NanoID gameID = NanoID.fromString(args[1]);
                final Game game = plugin.getGameManager().getLocalGames().getGame(gameID);

                System.out.println("Adding players " + Arrays.toString(players));

                if(game == null) {
                    System.out.println("Null Game");
                    System.out.println("Games Found: " + plugin.getGameManager().getLocalGames().size());
                    return;
                }

                if(game.getGameState() != GameState.WAITING && game.getGameState() != GameState.COUNTDOWN) {
                    System.out.println("Wrong GameState");
                    return;
                }

                final Collection<UUID> playerUUIDs = new HashSet<>();
                for(final String playerUUID : players) {
                    playerUUIDs.add(UUID.fromString(playerUUID));
                }

                game.getPlayers().addAll(playerUUIDs);
                game.updateRedis();
                JadedAPI.getRedis().publish("proxy", "connect " + args[2] + " " + game.getServer());
                System.out.println("connect " + args[1] + game.getServer() + " " + game.getServer());
            }
        }
    }
}