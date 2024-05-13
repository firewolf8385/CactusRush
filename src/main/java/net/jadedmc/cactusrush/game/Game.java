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

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.arena.Arena;
import net.jadedmc.cactusrush.game.round.RoundManager;
import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.cactusrush.game.team.TeamManager;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.player.JadedPlayer;
import net.jadedmc.jadedutils.Timer;
import net.jadedmc.jadedutils.chat.ChatUtils;
import net.jadedmc.jadedutils.items.ItemBuilder;
import net.jadedmc.nanoid.NanoID;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Game {
    private final CactusRushPlugin plugin;
    private final Arena arena;
    private GameState gameState;
    private final String server;
    private final Mode mode;
    private final RoundManager roundManager;
    private final TeamManager teamManager;
    private final Collection<UUID> players = new HashSet<>();
    private final Collection<UUID> spectators = new HashSet<>();
    private final NanoID nanoID;
    private final GameCountdown gameCountdown;
    private World world;
    private final Timer gameTimer;
    private final Map<Block, Integer> placedBlocks = new HashMap<>();


    public Game(@NotNull final CactusRushPlugin plugin, @NotNull final Document document) {
        this.plugin = plugin;
        this.teamManager = new TeamManager(plugin, this);
        this.roundManager = new RoundManager(plugin, this);

        this.nanoID = NanoID.fromString(document.getString("nanoID"));
        this.arena = plugin.getArenaManager().getArena(document.getString("arena"));
        this.mode = Mode.valueOf(document.getString("mode"));
        this.gameState = GameState.valueOf(document.getString("gameState"));
        this.server = document.getString("server");

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
        this.gameCountdown = new GameCountdown(plugin, this);
        this.gameTimer = new Timer(plugin);
    }

    public void startGame() {
        this.teamManager.generateTeams(this);
        this.setupArena();

        // Sets the player's chat channel.
        players.forEach(player -> JadedChat.setChannel(, JadedChat.getChannel("GAME")));

        // Update games played statistic.
        if(mode != Mode.DUEL) {
            players.forEach(player -> plugin.getCactusPlayerManager().getPlayer(player).addGamePlayed(mode.getId(), arena.getFileName()));
        }

        // Starts the first round.
        gameTimer.start();
        startRound();
    }

    public void startRound() {
        gameState = GameState.BETWEEN_ROUND;
        this.roundManager.nextRound(null);

        // Clear egg cooldown.
        eggCooldown.clear();

        // Resets the arena.
        resetArena();

        // Spawns players
        for(final UUID playerUUID : players) {
            final Player player = plugin.getServer().getPlayer(playerUUID);
            spawnPlayer(player);

            Titles.sendTitle(player, ChatColor.translateAlternateColorCodes('&', "&e&lPRE ROUND"), ChatColor.translateAlternateColorCodes('&', "&bGet ready to fight!"));
            player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 2);
        }

        BukkitRunnable roundCountdown = new  BukkitRunnable() {
            int counter = 10;
            public void run() {
                if(counter == 10) {
                    sendMessage("&aRound is starting in " + counter + " seconds...");
                }

                if(gameState == GameState.END) {
                    cancel();
                }

                if(counter <= 5 && counter > 0) {
                    sendMessage("&aRound is starting in " + counter + " seconds...");
                    for (final UUID playerUUID : players) {
                        // TODO: Could error if player not online. Add method to get collection of all online players objects and use in loop instead.
                        final Player player = plugin.getServer().getPlayer(playerUUID);
                        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_HAT.parseSound(), 1, 1);
                    }
                }
                if(counter == 0) {
                    for(final UUID playerUUID  : players) {
                        // TODO: Could error if player not online. Add method to get collection of all online players objects and use in loop instead.
                        final Player player = plugin.getServer().getPlayer(playerUUID);
                        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_HAT.parseSound(), 1, 1);
                    }

                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> runRound(), 1);
                    cancel();
                }

                counter--;
            }
        };
        roundCountdown.runTaskTimer(plugin, 0, 20);
    }

    /**
     * Add a placed block.
     * Used to reset the arena after each round.
     * @param block
     */
    public void addPlacedBlock(Block block) {
        int id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            block.setType(Material.AIR);
        }, 60*20);

        placedBlocks.put(block, id);
    }

    public void addPlayer(@NotNull final Player player) {
        if(this.gameState != GameState.WAITING && this.gameState != GameState.COUNTDOWN) {
            // TODO:
        }

        player.teleport(arena.getWaitingArea(world));

        // Setup Inventory
        player.getInventory().clear();
        player.getInventory().setItem(8, new ItemBuilder(Material.RED_BED).setDisplayName("&c&lLeave").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lAbility Selector").build());

        new GameScoreboard(plugin, player, this).update(player);

        final JadedPlayer jadedPlayer = JadedAPI.getJadedPlayer(player);

        // Process duel stuff.
        if(mode == Mode.DUEL) {
            sendMessage(jadedPlayer.getRank().getRankColor() + player.getName() + " &ahas joined the game!");
            return;
        }

        sendMessage(jadedPlayer.getRank().getRankColor() + player.getName() + " &ahas joined the game! (&f" + players.size() + "&a/&f" + mode.getMaxPlayerCount() + "&a)");

        // Checks if the game has enough players to start
        if(players.size() >= mode.getMaxPlayerCount() && gameCountdown.seconds() == 30) {
            // If so, starts the countdown.
            gameCountdown.start();
            gameState = GameState.COUNTDOWN;
        }

        // Checks if the game is 100% full.
        if(players.size() == mode.getMaxPlayerCount() && gameCountdown.seconds() > 5) {
            // If so, shortens the countdown to 5 seconds.
            gameCountdown.seconds(5);
        }
    }

    public Arena getArena() {
        return arena;
    }

    public GameCountdown getGameCountdown() {
        return gameCountdown;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Mode getMode() {
        return mode;
    }

    public NanoID getNanoID() {
        return nanoID;
    }

    public Collection<UUID> getPlayers() {
        return players;
    }

    public RoundManager getRoundManager() {
        return roundManager;
    }

    public String getServer() {
        return server;
    }

    public Collection<UUID> getSpectators() {
        return spectators;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public World getWorld() {
        return world;
    }

    /**
     * Removes a placed block from the block tracker.
     * Used to limit lag caused by arena resets.
     * @param block Block to remove from the tracker.
     */
    public void removePlacedBlock(Block block) {
        if(placedBlocks.containsKey(block)) {
            plugin.getServer().getScheduler().cancelTask(placedBlocks.get(block));
            placedBlocks.remove(block);
            block.setType(Material.AIR);
        }
    }

    /**
     * Resets the arena.
     * Clears placed blocks and replaces barriers.
     */
    private void resetArena() {
        // Reset placed blocks.
        new ArrayList<>(placedBlocks.keySet()).forEach(this::removePlacedBlock);
        placedBlocks.clear();

        for(final Team team : teamManager.getTeams()) {
            // Reset barrier blocks:
            for(final Block block : team.getArenaTeam().getBarrierBlocks(world)) {
                block.setType(Material.GLASS_PANE);
            }
        }
    }

    public void setWorld(@NotNull final World world) {
        this.world = world;
    }

    public void sendMessage(@NotNull final String message) {
        ChatUtils.chat(players, message);
        ChatUtils.chat(spectators, message);
    }

    /**
     * Sets up the arena for the first time.
     * Replaces goal blocks and score room walls.
     */
    private void setupArena() {
        for(final Team team : this.teamManager.getTeams()) {
            // Set goal blocks:
            for(final Block block : team.getArenaTeam().getGoalBlocks(world)) {
                block.setType(team.getColor().goalMaterial());
            }

            // Set score room walls:
            for(final Block block : team.getArenaTeam().getScoreRoomBlocks(world)) {
                block.setType(team.getColor().getScoreRoomMaterial());
            }
        }
    }

    /**
     * Respawns a player with no death reason.
     * @param player Player to respawn.
     */
    public void spawnPlayer(Player player) {
        spawnPlayer(player, GameDeathType.NONE);
    }

    /**
     * Respawns a player.
     * @param player Player being respawned.
     * @param reason Reason the player died, if there is one.
     */
    public void spawnPlayer(Player player, GameDeathType reason) {
        // Remove ability potion effects.
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.SPEED);

        // Reset player attributes.
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(19);
        player.setGameMode(GameMode.SURVIVAL);

        // Reset Inventory:
        player.getInventory().clear();
        player.getInventory().setItem(0, new ItemStack(Material.CACTUS, 64));
        player.getInventory().setItem(1, new ItemStack(Material.EGG));
        plugin.getAbilityManager().getAbility(player).giveItem(player);
        player.getInventory().setItem(8, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lAbility Selector").build());

        // Teleport to team spawn.
        final Team team = teamManager.getTeam(player);
        player.teleport(team.getArenaTeam().getTeamSpawn(world));

        // Remove egg cooldown
        removeEggCooldown(player);

        if(reason == GameDeathType.NONE) {
            return;
        }

        // Updates death statistic.
        this.getTeamManager().getTeamPlayer(player).addDeath(reason);
    }

    public Document toDocument() {
        final Document document = new Document()
                .append("nanoID", this.nanoID.toString())
                .append("server", this.server)
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

    public void updateRedis() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            JadedAPI.getRedis().set("cactusrush:games:" + nanoID.toString(), this.toDocument().toJson());
        });
    }
}