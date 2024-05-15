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
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.minigames.Minigame;
import net.jadedmc.jadedcore.networking.Instance;
import net.jadedmc.jadedcore.networking.InstanceStatus;
import net.jadedmc.jadedcore.networking.InstanceType;
import net.jadedmc.jadedcore.party.Party;
import net.jadedmc.jadedcore.party.PartyRole;
import net.jadedmc.jadedutils.chat.ChatUtils;
import net.jadedmc.nanoid.NanoID;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GameManager {
    private final CactusRushPlugin plugin;
    private final GameSet localGames = new GameSet();

    public GameManager(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a player and their party to a game with a set arena and mode.
     * @param player Player to add.
     * @param arena Arena the game should be using.
     * @param mode Mode the game should be using.
     */
    public void addToGame(@NotNull final Player player, @NotNull final Arena arena, final Mode mode) {
        player.closeInventory();
        ChatUtils.chat(player, "<green>Sending you to the game...");
        System.out.println("Known 1");

        final Party party = JadedAPI.getParty(player.getUniqueId());
        int partySize = 1;

        if(party != null) {
            // Makes sure the player is the party leader.
            if(party.getPlayer(player).getRole() != PartyRole.LEADER) {
                ChatUtils.chat(player, "<red>You are not the party leader!");
                return;
            }

            // Makes sure the party isn't too big.
            if(party.getPlayers().size() > mode.getMaxPlayerCount()) {
                ChatUtils.chat(player, "<red>Your party is too big for that mode!");
                return;
            }

            // Update party size.
            partySize = party.getPlayers().size();
        }

        int finalPartySize = partySize;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final GameSet games = this.getRemoteGames();
            final Collection<Game> possibleGames = new HashSet<>();
            for(final Game game : games) {
                if(game.getGameState() != GameState.WAITING && game.getGameState() != GameState.COUNTDOWN) {
                    continue;
                }

                if(game.getMode() != mode) {
                    continue;
                }

                if(game.getArena() != arena) {
                    continue;
                }

                if(game.getPlayers().size() + finalPartySize > mode.getMaxPlayerCount()) {
                    continue;
                }

                possibleGames.add(game);
            }

            if(possibleGames.size() == 0) {
                createGame(player, arena, mode, finalPartySize);
                return;
            }

            Game mostPlayersGame = possibleGames.stream().findFirst().get();
            while(games.iterator().hasNext()) {
                final Game game = games.iterator().next();
                if(game.getPlayers().size() > mostPlayersGame.getPlayers().size()) {
                    mostPlayersGame = game;
                }
            }

            final StringBuilder playerUUIDs = new StringBuilder();
            if(party != null) {
                party.getPlayers().forEach(partyPlayer -> playerUUIDs.append(partyPlayer.getUniqueID()).append(","));
            }
            else {
                playerUUIDs.append(player.getUniqueId()).append(",");
            }

            JadedAPI.getRedis().publish("cactusrush", "addplayers " + mostPlayersGame.getNanoID().toString() + " " + playerUUIDs.substring(0, playerUUIDs.length() - 1));
        });
    }

    /**
     * Adds a player and their party to a game with a set mode.
     * @param player Player to add.
     * @param mode Mode the game should be using.
     */
    public void addToGame(@NotNull final Player player, final Mode mode) {
        player.closeInventory();
        ChatUtils.chat(player, "<green>Finding game...");
        System.out.println("Random 1");

        final Party party = JadedAPI.getParty(player.getUniqueId());
        int partySize = 1;

        if(party != null) {
            // Makes sure the player is the party leader.
            if(party.getPlayer(player).getRole() != PartyRole.LEADER) {
                ChatUtils.chat(player, "<red>You are not the party leader!");
                return;
            }

            // Makes sure the party isn't too big.
            if(party.getPlayers().size() > mode.getMaxPlayerCount()) {
                ChatUtils.chat(player, "<red>Your party is too big for that mode!");
                return;
            }

            // Update party size.
            partySize = party.getPlayers().size();
        }

        System.out.println("Random 2");

        int finalPartySize = partySize;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            System.out.println("Random 3");
            final GameSet games = this.getRemoteGames();
            final Collection<Game> possibleGames = new HashSet<>();
            for(final Game game : games) {
                if(game.getGameState() != GameState.WAITING && game.getGameState() != GameState.COUNTDOWN) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> System.out.println("Not matching GameState"));
                    continue;
                }

                if(game.getMode() != mode) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> System.out.println("Not matching mode"));
                    continue;
                }

                if(game.getPlayers().size() + finalPartySize > mode.getMaxPlayerCount()) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> System.out.println("Not enough room"));
                    continue;
                }

                possibleGames.add(game);
            }

            System.out.println("Random 4");

            if(possibleGames.size() == 0) {
                plugin.getServer().getScheduler().runTask(plugin, () -> System.out.println("No open games found"));
                // Find all possible arenas for the given mode.
                List<Arena> possibleArenas = new ArrayList<>();
                for(Arena arena : plugin.getArenaManager().getArenas()) {
                    if(arena.getModes().contains(mode)) {
                        possibleArenas.add(arena);
                    }
                }

                // Exit and print a warning message if no arenas were found.
                if(possibleArenas.size() == 0) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> System.out.println("New Game: No arenas found"));
                    return;
                }

                // Shuffle the possible arenas.
                Collections.shuffle(possibleArenas);

                createGame(player, possibleArenas.get(0), mode, finalPartySize);
                return;
            }

            System.out.println("Random 5");

            Game mostPlayersGame = possibleGames.stream().findFirst().get();
            System.out.println("Random 5.1");
            for (Game possibleGame : possibleGames) {
                System.out.println("Random 5.2");
                System.out.println("Random 5.3");
                if (possibleGame.getPlayers().size() > mostPlayersGame.getPlayers().size()) {
                    System.out.println("Random 5.4");
                    mostPlayersGame = possibleGame;
                    System.out.println("Random 5.5");
                }
                System.out.println("Random 5.6");
            }

            System.out.println("Random 6");

            final StringBuilder playerUUIDs = new StringBuilder();
            if(party != null) {
                party.getPlayers().forEach(partyPlayer -> playerUUIDs.append(partyPlayer.getUniqueID().toString()).append(","));
            }
            else {
                playerUUIDs.append(player.getUniqueId()).append(",");
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> System.out.println("Sending players to game"));
            JadedAPI.getRedis().publish("cactusrush", "addplayers " + mostPlayersGame.getNanoID().toString() + " " + playerUUIDs.substring(0, playerUUIDs.length() - 1));
        });
    }

    public void createGame(@NotNull final Player player, @NotNull final Arena arena, final Mode mode, final int partySize) {
        JadedAPI.getInstanceMonitor().getInstancesAsync().thenAccept(instances -> {
            final NanoID nanoID = new NanoID();

            String serverName = "";
            {
                System.out.println("Servers Found: " + instances.size());
                int count = 999;

                // Loop through all online servers looking for a server to send the game to.
                for(final Instance instance : instances) {
                    // Make sure the server is a Cactus Rush server.
                    if(instance.getMinigame() != Minigame.CACTUS_RUSH) {
                        continue;
                    }

                    // Make sure the server isn't a lobby server.
                    if(instance.getType() != InstanceType.GAME) {
                        continue;
                    }

                    if(instance.getStatus() != InstanceStatus.ONLINE) {
                        continue;
                    }

                    // Make sure there is room for another game.
                    if(instance.getOnline() + partySize > instance.getCapacity()) {
                        continue;
                    }

                    //
                    if(instance.getOnline() < count) {
                        count = instance.getOnline();
                        serverName = instance.getName();
                    }
                }

                // If no server is found, give up ¯\_(ツ)_/¯
                if(count == 999) {
                    ChatUtils.chat(player, "<red>Could not find an available server. Please try again in a bit.");
                    return;
                }
            }

            final List<String> players = new ArrayList<>();
            final List<String> spectators = new ArrayList<>();
            final Party party = JadedAPI.getParty(player.getUniqueId());
            if(party != null) {
                party.getPlayers().forEach(partyPlayer -> players.add(partyPlayer.getUniqueID().toString()));
            }
            else {
                players.add(player.getUniqueId().toString());
            }

            System.out.println("Writing Document...");
            // Create the document to eventually send to Redis.
            final Document document = new Document()
                    .append("nanoID", nanoID.toString())
                    .append("arena", arena.getFileName())
                    .append("mode", mode.toString())
                    .append("gameState", GameState.WAITING.toString())
                    .append("server", serverName)
                    .append("round", 0)
                    .append("players", players)
                    .append("spectators", spectators)
                    .append("teams", new Document())
                    .append("rounds", new Document());

            // Update Redis
            JadedAPI.getRedis().set("cactusrush:games:" + nanoID, document.toJson());
            JadedAPI.getRedis().publish("cactusrush", "create " + nanoID);
        }).whenComplete((result, error) -> error.printStackTrace());
    }

    @NotNull
    public final GameSet getLocalGames() {
        return localGames;
    }

    /**
     * Retrieves a Set of all parties stored in Redis.
     * <b>Warning: Database operation. Call asynchronously.</b>
     * @return Set containing Parties grabbed from Redis.
     */
    @NotNull
    public GameSet getRemoteGames() {
        final GameSet remoteGames = new GameSet();

        final Set<String> keys = JadedAPI.getRedis().keys("cactusrush:games:*");
        for(final String key : keys) {
            final Document gameDocument = Document.parse(JadedAPI.getRedis().get(key));
            remoteGames.add(new Game(plugin, gameDocument));
        }

        return remoteGames;
    }
}