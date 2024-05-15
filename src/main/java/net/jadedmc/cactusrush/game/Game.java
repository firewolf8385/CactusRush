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
import net.jadedmc.cactusrush.game.round.RoundPlayer;
import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.cactusrush.game.team.TeamManager;
import net.jadedmc.cactusrush.game.team.TeamPlayer;
import net.jadedmc.cactusrush.player.CactusPlayer;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.minigames.Minigame;
import net.jadedmc.jadedcore.player.JadedPlayer;
import net.jadedmc.jadedutils.FileUtils;
import net.jadedmc.jadedutils.Timer;
import net.jadedmc.jadedutils.chat.ChatUtils;
import net.jadedmc.jadedutils.items.ItemBuilder;
import net.jadedmc.nanoid.NanoID;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
    private GameCountdown gameCountdown;
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
        players.forEach(playerUUID -> {
            final Player player = plugin.getServer().getPlayer(playerUUID);
            if(player != null) {
                JadedChat.setChannel(player, JadedChat.getChannel("GAME"));
            }
        });

        // Update games played statistic.
        if(mode != Mode.DUEL) {
            players.forEach(player -> plugin.getCactusPlayerManager().getPlayer(player).addGamePlayed(mode.getId(), arena.getFileName()));
        }

        // Starts the first round.
        gameTimer.start();
        this.roundManager.nextRound(null);
        startRound();
    }

    public void startRound() {
        gameState = GameState.BETWEEN_ROUND;

        // Clear egg cooldown.
        for(final Team team : this.teamManager.getTeams()) {
            team.getTeamPlayers().forEach(TeamPlayer::removeEggCooldown);
        }

        // Resets the arena.
        resetArena();

        // Spawns players
        for(final UUID playerUUID : players) {
            final Player player = plugin.getServer().getPlayer(playerUUID);

            if(player != null) {
                spawnPlayer(player);

                Titles.sendTitle(player, ChatColor.translateAlternateColorCodes('&', "&e&lPRE ROUND"), ChatColor.translateAlternateColorCodes('&', "&bGet ready to fight!"));
                player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 2);
            }
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

        updateRedis();
    }

    public void runRound() {
        this.gameState = GameState.RUNNING;

        for(final UUID playerUUID : this.players) {
            final Player player = plugin.getServer().getPlayer(playerUUID);

            if(player == null) {
                continue;
            }

            // Close inventory on round start.
            player.closeInventory();

            // Display round start message.
            Titles.sendTitle(player, ChatColor.translateAlternateColorCodes('&', "&a&lROUND START"), ChatColor.translateAlternateColorCodes('&', "&bRound " + this.roundManager.getCurrentRoundNumber()));
            player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);
            }, 3);

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);
            }, 6);
        }

        // Removes the barriers
        for(final Team team : this.teamManager.getTeams()) {
            for(final Block block : team.getArenaTeam().getBarrierBlocks(world)) {
                block.setType(Material.AIR);
            }
        }

        updateRedis();
    }

    public void endRound(final Team winner) {
        gameState = GameState.BETWEEN_ROUND;

        // Gives the winning team a point.
        winner.addPoint();

        // Checks if there is only one team left.
        if(this.teamManager.getTeams().size() == 1) {
            endGame(winner);
            return;
        }

        for(final Team team : this.teamManager.getTeams()) {
            if(team.equals(winner)) {
                team.getTeamPlayers().forEach(teamPlayer -> {
                    final Player player = plugin.getServer().getPlayer(teamPlayer.getUniqueId());

                    if(player != null) {
                        Titles.sendTitle(player, 10,60,10, ChatColor.translateAlternateColorCodes('&', getFormattedGameScores()), ChatColor.translateAlternateColorCodes('&', "&a&lROUND WON!"));
                        player.playSound(player.getLocation(), XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.parseSound(), 1, (float) 0.8);
                    }
                });
            }
            else {
                team.getTeamPlayers().forEach(teamPlayer -> {
                    teamPlayer.playSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 0.8f);
                    final Player player = plugin.getServer().getPlayer(teamPlayer.getUniqueId());

                    if(player != null) {
                        Titles.sendTitle(player, ChatColor.translateAlternateColorCodes('&', getFormattedGameScores()), ChatColor.translateAlternateColorCodes('&', "&c&lROUND LOST!"));
                    }
                });
            }
        }

        // Display round stats.
        for(final UUID playerUUID : this.players) {
            final RoundPlayer roundPlayer = this.roundManager.getCurrentRound().getPlayers().getPlayer(playerUUID);
            final Player player = plugin.getServer().getPlayer(playerUUID);

            if(player == null) {
                continue;
            }

            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.chat(player, ChatUtils.centerText("&a&lRound #" + this.roundManager.getCurrentRoundNumber() + " Stats"));
            ChatUtils.chat(player, ChatUtils.centerText(ChatUtils.centerText("&aCacti Placed: &f" + roundPlayer.getCactiPlaced())));
            ChatUtils.chat(player, ChatUtils.centerText("&aCacti Broken: &f" + roundPlayer.getCactiBroken()));
            ChatUtils.chat(player, ChatUtils.centerText("&aEggs Thrown: &f" + roundPlayer.getEggsThrown()));
            ChatUtils.chat(player, ChatUtils.centerText("&aGoals: &f" + roundPlayer.getGoalsScored()));
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            plugin.getAbilityManager().getAbility(player).removeCooldown(player);
        }

        // Checks if a team has enough points to win.
        if(winner.getScore() >= 3) {
            endGame(winner);
            return;
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            this.roundManager.nextRound(winner);
            this.startRound();
        }, 5*20);
        updateRedis();
    }

    public void endGame(Team winner) {
        gameState = GameState.END;
        gameTimer.stop();
        sendMessage("&aGame Over");
        sendMessage("&aWinner: " + winner.getColor().getTextColor() + winner.getColor().name());

        // TODO: This is gross
        for(Team team : teamManager.getTeams()) {
            if(team.equals(winner)) {
                team.getTeamPlayers().forEach(teamPlayer -> {
                    if(mode != Mode.DUEL) {
                        teamPlayer.getCactusPlayer().addWin(mode.getId(), arena.getFileName());
                        teamPlayer.getCactusPlayer().addCoins(100, "Win");
                    }

                    Player player = plugin.getServer().getPlayer(teamPlayer.getUniqueId());

                    if(player != null) {
                        Titles.sendTitle(player, 10,60,10, ChatColor.translateAlternateColorCodes('&', "&a&lVICTORY!"), ChatColor.translateAlternateColorCodes('&', winner + " &ahas won the game!"));
                    }
                });
            }
            else {
                team.getTeamPlayers().forEach(teamPlayer -> {
                    if(mode != Mode.DUEL) {
                        teamPlayer.getCactusPlayer().addLoss(mode.getId(), arena.getFileName());
                    }
                    Player player = plugin.getServer().getPlayer(teamPlayer.getUniqueId());
                    if(player != null) {
                        Titles.sendTitle(player, 10,60,10, ChatColor.translateAlternateColorCodes('&', "&c&lGAME OVER!"), ChatColor.translateAlternateColorCodes('&', winner + " &ahas won the game!"));
                    }
                });
            }
        }

        for(UUID playerUUID : players) {
            final Player player = plugin.getServer().getPlayer(playerUUID);

            if(player == null) {
                continue;
            }

            final TeamPlayer teamPlayer = this.teamManager.getTeam(playerUUID).getTeamPlayers().getPlayer(playerUUID);
            player.removePotionEffect(PotionEffectType.JUMP);


            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.chat(player, ChatUtils.centerText("&a&lGame Stats"));
            ChatUtils.chat(player, ChatUtils.centerText("&aCacti Placed: &f" + teamPlayer.getCactiPlaced()));
            ChatUtils.chat(player, ChatUtils.centerText("&aCacti Broken: &f" + teamPlayer.getCactiBroke()));
            ChatUtils.chat(player, ChatUtils.centerText("&aEggs Thrown: &f" + teamPlayer.getEggsThrown()));
            ChatUtils.chat(player, ChatUtils.centerText("&aGoals: &f" + teamPlayer.getGoalsScored()));
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }

        updateRedis();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(Team team : teamManager.getTeams()) {
                team.getTeamPlayers().forEach(teamPlayer -> {
                    int coinsReward = 0;

                    coinsReward += (teamPlayer.getGoalsScored() * 30);

                    if(team.equals(winner)) {
                        coinsReward += 100;
                    }

                    int timeReward = (int) (gameTimer.toMinutes() * 12.0);
                    if(mode != Mode.DUEL) {
                        teamPlayer.getCactusPlayer().addCoins(timeReward);
                    }
                    coinsReward += timeReward;

                    int xpReward = 0;

                    xpReward += (teamPlayer.getGoalsScored() * 50);
                    xpReward += (int) (gameTimer.toMinutes() * 25.0);

                    if(team.equals(winner)) {
                        xpReward += 150;
                    }

                    final Player player = plugin.getServer().getPlayer(teamPlayer.getUniqueId());

                    if(player != null) {
                        ChatUtils.chat(teamPlayer.getUniqueId(), "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                        ChatUtils.chat(player, ChatUtils.centerText("&a&lReward Summary"));
                        ChatUtils.chat(player, "");
                        ChatUtils.chat(player, "  &7You Earned:");

                        if(mode != Mode.DUEL) {
                            ChatUtils.chat(player, "    &f• &6" + coinsReward + " Cactus Rush Coins");
                            ChatUtils.chat(player, "    &f• &b" + xpReward + " Cactus Rush Experience");
                        }
                        else {
                            ChatUtils.chat(player, "    &f• &cNo rewards earned because the game was a duel.");
                            ChatUtils.chat(player, " ");
                        }

                        ChatUtils.chat(player, "");
                        ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

                        int finalXpReward = xpReward;

                        if(mode != Mode.DUEL) {
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getCactusPlayerManager().getPlayer(player).addExperience(finalXpReward), 20);
                        }

                        ItemBuilder leaveItem = new ItemBuilder(Material.RED_BED).setDisplayName("&c&lLeave");
                        player.getInventory().setItem(8, leaveItem.build());

                        if(mode != Mode.DUEL) {
                            ItemBuilder playAgain = new ItemBuilder(Material.PAPER).setDisplayName("&a&lPlay Again");
                            player.getInventory().setItem(7, playAgain.build());
                        }

                        if(mode != Mode.DUEL) {
                            if(team.equals(winner)) {
                                JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_1").unlock(player);
                            }

                            for(Team otherTeam : teamManager.getTeams()) {
                                if(team.equals(winner)) {
                                    continue;
                                }

                                if(!teamManager.getTeam(player).equals(winner)) {
                                    continue;
                                }

                                for(TeamPlayer opponent : otherTeam.getTeamPlayers()) {
                                    JadedPlayer opponentJadedPlayer = opponent.getCactusPlayer().getJadedPlayer();
                                    if(opponentJadedPlayer.getRank().isStaffRank()) {
                                        JadedAPI.getPlugin().achievementManager().getAchievement("general_3").unlock(player);
                                    }
                                }
                            }
                        }
                        else {
                            if(team.equals(winner)) {
                                JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_2").unlock(player);
                            }
                        }

                        if(mode != Mode.DUEL) {
                            if(teamPlayer.getCactiBroke() >= 100) {
                                JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_5").unlock(player);
                            }

                            if(teamPlayer.getEggsThrown() >= 200) {
                                JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_6").unlock(player);
                            }

                            if(teamPlayer.getCactiPlaced() >= 300) {
                                JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_7").unlock(player);
                            }
                        }
                    }
                });
            }
        }, 3*20);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            new HashSet<>(players).forEach(playerUUID -> {
                final Player player = plugin.getServer().getPlayer(playerUUID);

                if(player != null) {
                    JadedAPI.sendToLobby(player, Minigame.CACTUS_RUSH);
                }
            });

            new HashSet<>(spectators).forEach(spectatorUUID -> {
                final Player spectator = plugin.getServer().getPlayer(spectatorUUID);

                if(spectator != null) {
                    JadedAPI.sendToLobby(spectator, Minigame.CACTUS_RUSH);
                }
            });

            // Deletes the game
            this.deleteGame();
        }, 5*20);
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
        // Spawns the player if the game is already running.
        if(this.gameState != GameState.WAITING && this.gameState != GameState.COUNTDOWN) {
            spawnPlayer(player);
            return;
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

    /**
     * Gets the game score, formatted with colors.
     * @return Formatted game score.
     */
    public String getFormattedGameScores() {
        int divisions = teamManager.getTeams().size() - 1;

        StringBuilder scores = new StringBuilder();

        int count = 0;
        for(Team team : teamManager.getTeams()) {
            scores.append(team.getColor().getTextColor()).append(team.getScore());

            if(count < divisions) {
                count++;
                scores.append(" &8- ");
            }
        }

        return scores.toString();
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
        final TeamPlayer teamPlayer = team.getTeamPlayers().getPlayer(player.getUniqueId());
        player.teleport(team.getArenaTeam().getTeamSpawn(world));

        // Remove egg cooldown
        teamPlayer.removeEggCooldown();

        if(reason == GameDeathType.NONE) {
            return;
        }

        // Updates death statistic.
        teamPlayer.addDeath(reason);
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

    /**
     * Runs when a player scores.
     * @param player Player who scored.
     */
    public void playerScored(Player player) {
        // Prevents stuff from breaking if the round is already over.
        if(gameState == GameState.END || gameState == GameState.BETWEEN_ROUND) {
            return;
        }

        Team team = teamManager.getTeam(player);
        sendMessage(team.getColor().getTextColor() + player.getName() + " &ascored!");

        team.getTeamPlayers().forEach(teamMember -> {
            final Player teammate = plugin.getServer().getPlayer(teamMember.getUniqueId());

            if(teammate != null) {
                teammate.playSound(teammate.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 2);
            }
        });

        if(mode != Mode.DUEL) {
            plugin.getCactusPlayerManager().getPlayer(player).addCoins(30, "Goal Scored");
        }

        player.getInventory().clear();
        player.teleport(team.getArenaTeam().getScoreRoomSpawn(world));

        // Statistic Tracking
        team.getTeamPlayers().getPlayer(player).addGoalScored();

        endRound(team);
    }

    public void removePlayer(Player player) {
        // Removes the player if they are a spectator.
        if(spectators.contains(player.getUniqueId())) {
            // TODO: Spectate
            //removeSpectator(player);
            return;
        }

        // Processes leaving a game that has not started.
        if(gameState == GameState.WAITING || gameState == GameState.COUNTDOWN) {
            players.remove(player.getUniqueId());

            JadedPlayer jadedPlayer = JadedAPI.getJadedPlayer(player);
            if(jadedPlayer != null) {
                sendMessage("&f" + jadedPlayer.getRank().getChatPrefix() + player.getName() + " &ahas left the game! (&f"+ players.size() + "&a/&f" + mode.getMaxPlayerCount() + "&a)");
            }

            // Stops the countdown if the game has too few players.
            if(players.size() < mode.getMinPlayerCount() && gameState == GameState.COUNTDOWN) {
                sendMessage("&cNot enough players! Countdown reset.");
                gameCountdown.cancel();
                gameCountdown = new GameCountdown(plugin, this);
                gameState = GameState.WAITING;
            }

            // If the game is empty, delete it.
            if(players.size() == 0) {
                this.deleteGame();
            }
            else {
                updateRedis();
            }

            return;
        }

        if(this.gameState == GameState.END) {
            return;
        }

        // Save player statistics if they were a part of the game.
        if(players.contains(player.getUniqueId()) && mode != Mode.DUEL) {
            players.remove(player.getUniqueId());

            // Update statistics.
            CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);
            cactusPlayer.addPlayTime(mode.getId(), arena.getFileName(), gameTimer.toSeconds());

            // Save statistics.
            cactusPlayer.updateModeStatistics(mode.getId());
            cactusPlayer.updateArenaStatistics(arena.getFileName());
            cactusPlayer.updateAbilityStatistics();
        }

        final Team team = this.teamManager.getTeam(player);
        sendMessage(team.getColor().getTextColor() + player.getName() + " <green>left the game!");
        updateRedis();
    }

    public void deleteGame() {
        // Remove the game from the local games cache.
        plugin.getGameManager().getLocalGames().remove(this);

        // Unload the world.
        final File worldFolder = world.getWorldFolder();
        Bukkit.unloadWorld(world, false);

        // Update redis and delete the world.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            JadedAPI.getRedis().del("cactusrush:games:" + nanoID.toString());
            FileUtils.deleteDirectory(worldFolder);
        });
    }
}