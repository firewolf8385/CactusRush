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
package net.jadedmc.cactusrush.game;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.arena.Arena;
import net.jadedmc.cactusrush.game.arena.ArenaChunkGenerator;
import net.jadedmc.cactusrush.utils.FileUtils;
import net.jadedmc.jadedchat.utils.ChatUtils;
import net.jadedmc.jadedpartybukkit.JadedParty;
import net.jadedmc.jadedpartybukkit.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Manages the creation of games and assigning them to players.
 */
public class GameManager {
    private final CactusRushPlugin plugin;
    private final Collection<Game> activeGames = new HashSet<>();

    /**
     * Creates the Game Manager.
     * @param plugin Instance of the plugin.
     */
    public GameManager(CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets all currently existing games.
     * @return All active games.
     */
    public Collection<Game> activeGames() {
        return activeGames;
    }

    /**
     * Manually add a game. Used in duels.
     * @param game Game to add.
     */
    public void addGame(Game game) {
        activeGames.add(game);
    }

    /**
     * Adds a player and their party to a game with a set arena and mode.
     * @param player Player to add.
     * @param arena Arena the game should be using.
     * @param mode Mode the game should be using.
     */
    public void addToGame(Player player, Arena arena, Mode mode) {
        player.closeInventory();
        ChatUtils.chat(player, "&aSending you to the game...");

        Party party = JadedParty.partyManager().getParty(player);
        int partySize = 1;

        if(party != null) {
            // Makes sure the player is the party leader.
            if(!party.getLeader().equals(player.getUniqueId())) {
                ChatUtils.chat(player, "&cYou are not the party leader!");
                return;
            }

            // Makes sure the party isn't too big.
            if(party.getPlayers().size() > mode.maxPlayerCount()) {
                ChatUtils.chat(player, "&cYour party is too big for that mode!");
                return;
            }

            // Checks if all players are online.
            // If so, continues as normal.
            if(party.getOnlineCount() < party.getPlayers().size()) {
                // If not, summon party members and try again with a delay.
                JadedParty.partyManager().summonParty(party);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> addToGame(player, arena, mode), 20);
                player.closeInventory();
                return;
            }

            // Update party size.
            partySize = party.getPlayers().size();
        }

        // Adds the player(s) to a game.
        findGame(arena, mode, partySize).thenAccept(game -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                System.out.println("Game Received");
                activeGames.add(game);
                if(party != null) {
                    System.out.println("Party Not Null");
                    Collection<Player> partyPlayers = new HashSet<>();
                    for(UUID playerUUID : party.getPlayers()) {
                        partyPlayers.add(Bukkit.getPlayer(playerUUID));
                    }

                    game.addPlayers(partyPlayers);
                }
                else {
                    game.addPlayer(player);
                }
            });
        });
    }

    /**
     * Adds a player and their party to a game with a random arena and a set mode.
     * @param player Player to add to the game.
     * @param mode Mode the game should use.
     */
    public void addToGame(Player player, Mode mode) {
        player.closeInventory();
        ChatUtils.chat(player, "&aSending you to the game...");

        Party party = JadedParty.partyManager().getParty(player);
        int partySize = 1;

        if(party != null) {
            // Makes sure the player is the party leader.
            if(!party.getLeader().equals(player.getUniqueId())) {
                ChatUtils.chat(player, "&cYou are not the party leader!");
                return;
            }

            // Makes sure the party isn't too big.
            if(party.getPlayers().size() > mode.maxPlayerCount()) {
                ChatUtils.chat(player, "&cYour party is too big for that mode!");
                return;
            }

            // Checks if all players are online.
            // If so, continues as normal.
            if(party.getOnlineCount() < party.getPlayers().size()) {
                // If not, summon party members and try again with a delay.
                JadedParty.partyManager().summonParty(party);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> addToGame(player, mode), 20);
                player.closeInventory();
                return;
            }

            // Update party size.
            partySize = party.getPlayers().size();
        }

        // Adds the player(s) to a game.
        findGame(mode, partySize).thenAccept(game -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                activeGames.add(game);

                if(party != null) {
                    Collection<Player> partyPlayers = new HashSet<>();
                    for(UUID playerUUID : party.getPlayers()) {
                        partyPlayers.add(Bukkit.getPlayer(playerUUID));
                    }

                    for(Player partyPlayer : partyPlayers) {
                        Game tempGame = plugin.gameManager().getGame(partyPlayer);
                        if(tempGame != null) {
                            tempGame.removePlayer(partyPlayer);
                        }
                    }

                    game.addPlayers(partyPlayers);
                }
                else {
                    Game tempGame = plugin.gameManager().getGame(player);
                    if(tempGame != null) {
                        tempGame.removePlayer(player);
                    }

                    game.addPlayer(player);
                }
            });
        });
    }

    /**
     * Creates a game
     * @param arena Arena the game should use.
     * @param mode Mode the game should use.
     * @return Created game.
     */
    public CompletableFuture<Game> createGame(Arena arena, Mode mode) {
        UUID gameUUID = UUID.randomUUID();

        // Makes a copy of the arena with the generated uuid.
        CompletableFuture<File> arenaCopy = arena.arenaFile().createCopy(gameUUID.toString());

        // Creates the game.
        CompletableFuture<Game> gameCreation = CompletableFuture.supplyAsync(() -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                WorldCreator worldCreator = new WorldCreator(gameUUID.toString());
                worldCreator.generator(new ArenaChunkGenerator());
                Bukkit.createWorld(worldCreator);
            });

            // Wait for the world to be generated.
            boolean loaded = false;
            World world = null;
            while(!loaded) {
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                for(World w : Bukkit.getWorlds()) {
                    if(w.getName().equals(gameUUID.toString())) {
                        loaded = true;
                        world = w;
                        break;
                    }
                }
            }

            return new Game(plugin, arena, world, mode, gameUUID);
        });

        return arenaCopy.thenCompose(file -> gameCreation);
    }

    /**
     * Deletes a game that is no longer needed.
     * This also deletes its temporary world folder.
     * @param game Game to delete.
     */
    public void deleteGame(Game game) {
        activeGames.remove(game);
        File worldFolder = game.world().getWorldFolder();
        Bukkit.unloadWorld(game.world(), false);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            FileUtils.deleteDirectory(worldFolder);
        });
    }

    /**
     * Find a game based on a set of search requirements.
     * @param arena Arena the game should use.
     * @param mode Mode the game should use.
     * @param minimumSlots Minimum number of available slots the game should have.
     * @return Game. If none found creates a new game.
     */
    public CompletableFuture<Game> findGame(Arena arena, Mode mode, int minimumSlots) {
        List<Game> possibleGames = new ArrayList<>();

        for(Game possibleGame : activeGames) {
            // Make sure the game hasn't already started.
            if(possibleGame.gameState() != GameState.WAITING && possibleGame.gameState() != GameState.COUNTDOWN) {
                continue;
            }

            // Make sure the game is the right mode.
            if(possibleGame.mode() != mode) {
                continue;
            }

            // Make sure the game has the right arena.
            if(possibleGame.arena() != arena) {
                continue;
            }

            if(mode.maxPlayerCount() - possibleGame.players().size() < minimumSlots) {
                continue;
            }

            // Add to the list of possible games.
            possibleGames.add(possibleGame);
        }

        // Shuffles list of possible games.
        Collections.shuffle(possibleGames);

        // Returns null if no games are available.
        if(possibleGames.size() == 0) {
            return createGame(arena, mode);
        }

        List<Game> possibleGamesWithPlayers = new ArrayList<>();
        for(Game game : possibleGames) {
            if(game.players().size() == 0) {
                continue;
            }

            possibleGamesWithPlayers.add(game);
        }

        // If there is a game with players waiting, return that one.
        if(!possibleGamesWithPlayers.isEmpty()) {
            return CompletableFuture.supplyAsync(() -> possibleGamesWithPlayers.get(0));
        }

        // Otherwise, returns the top game of the shuffled list.
        return CompletableFuture.supplyAsync(() -> possibleGames.get(0));
    }

    /**
     * Finds a game based on search requirements.
     * @param mode Mode the game should use.
     * @param minimumSlots Minimum number of slots the game should have.
     * @return Found game. Creates a new game if none found.
     */
    public CompletableFuture<Game> findGame(Mode mode, int minimumSlots) {
        List<Game> possibleGames = new ArrayList<>();

        for(Game possibleGame : activeGames) {
            // Make sure the game hasn't already started.
            if(possibleGame.gameState() != GameState.WAITING && possibleGame.gameState() != GameState.COUNTDOWN) {
                continue;
            }

            // Make sure the game is the right mode.
            if(possibleGame.mode() != mode) {
                continue;
            }

            if(mode.maxPlayerCount() - possibleGame.players().size() < minimumSlots) {
                continue;
            }

            // Add to the list of possible games.
            possibleGames.add(possibleGame);
        }

        // Shuffles list of possible games.
        Collections.shuffle(possibleGames);

        // Returns null if no games are available.
        if(possibleGames.size() == 0) {
            // Find all possible arenas for the given mode.
            List<Arena> possibleArenas = new ArrayList<>();
            for(Arena arena : plugin.arenaManager().getArenas()) {
                if(arena.modes().contains(mode)) {
                    possibleArenas.add(arena);
                }
            }

            // Exit and print a warning message if no arenas were found.
            if(possibleArenas.size() == 0) {
                return null;
            }

            // Shuffle the possible arenas.
            Collections.shuffle(possibleArenas);

            // Create a new game using the randomly picked arena.
            return createGame(possibleArenas.get(0), mode);
        }

        List<Game> possibleGamesWithPlayers = new ArrayList<>();
        for(Game game : possibleGames) {
            if(game.players().size() == 0) {
                continue;
            }

            possibleGamesWithPlayers.add(game);
        }

        // If there is a game with players waiting, return that one.
        if(!possibleGamesWithPlayers.isEmpty()) {
            return CompletableFuture.supplyAsync(() -> possibleGamesWithPlayers.get(0));
        }

        // Otherwise, returns the top game of the shuffled list.
        return CompletableFuture.supplyAsync(() -> possibleGames.get(0));
    }

    /**
     * Get the game a given player is currently in.
     * Null if not in a game.
     * @param player Player to get game of.
     * @return Game they are in.
     */
    public Game getGame(Player player) {
        // Makes a copy of the active games to prevent ConcurrentModificationException.
        List<Game> games = new ArrayList<>(activeGames);

        // Loop through each game looking for the player.
        for(Game game : games) {
            if(game.players().contains(player) || game.spectators().contains(player)) {
                return game;
            }
        }

        return null;
    }

    /**
     * Get the number of players playing a specific mode.
     * @param mode Mode to get player count of.
     * @return Player count in that mode.
     */
    public int playing(Mode mode) {
        int playing = 0;

        for(Game game : activeGames) {
            if(game.mode() != mode) {
                continue;
            }

            playing += game.players().size();
        }

        return playing;
    }
}