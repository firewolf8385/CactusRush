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

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import me.clip.placeholderapi.PlaceholderAPI;
import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.arena.Arena;
import net.jadedmc.cactusrush.game.lobby.LobbyUtils;
import net.jadedmc.cactusrush.game.teams.Team;
import net.jadedmc.cactusrush.game.teams.TeamManager;
import net.jadedmc.cactusrush.player.CactusPlayer;
import net.jadedmc.jadedcore.player.JadedPlayer;
import net.jadedmc.jadedutils.Timer;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedutils.items.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents an active game of Cactus Rush.
 */
public class Game {
    private final CactusRushPlugin plugin;
    private final TeamManager teamManager;
    private final Collection<Player> players = new HashSet<>();
    private final Arena arena;
    private final Mode mode;
    private final UUID uuid;
    private final World world;
    private GameState gameState;
    private GameCountdown gameCountdown;
    private final Timer gameTimer;
    private int round;
    private final Map<Block, Integer> placedBlocks = new HashMap<>();
    private final GameStatisticsTracker statisticsTracker;
    private final Map<Player, Integer> eggCooldown = new HashMap<>();
    private final Collection<Player> spectators = new HashSet<>();
    private final Map<Integer, Round> rounds = new HashMap<>();
    private final Collection<GamePlayer> gamePlayers = new HashSet<>();


    /**
     * Creates the game.
     * @param plugin Instance of the plugin.
     * @param arena Arena the game is using.
     * @param world World the game takes place in.
     * @param mode Mode the game is using.
     * @param uuid Unique ID for the game.
     */
    public Game(final CactusRushPlugin plugin, final Arena arena, final World world, final Mode mode, final UUID uuid) {
        this.plugin = plugin;
        this.arena = arena;
        this.uuid = uuid;
        this.mode = mode;
        this.world = world;

        this.gameState = GameState.WAITING;
        this.gameCountdown = new GameCountdown(plugin, this);
        this.gameTimer = new Timer(plugin);
        this.round = 0;
        this.statisticsTracker = new GameStatisticsTracker(plugin, this);

        this.teamManager = new TeamManager(plugin);
    }

    public void startGame() {
        createTeams();
        setupArena();

        // Loop through all players to add default stats.
        players.forEach(statisticsTracker::addPlayer);

        // Sets the player's chat channel.
        players.forEach(player -> JadedChat.setChannel(player, JadedChat.getChannel("GAME")));

        // Update games played statistic.
        if(mode != Mode.DUEL) {
            players.forEach(player -> plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addGamePlayed(mode.id(), arena.id()));
        }

        // Starts the first round.
        gameTimer.start();
        startRound();
    }

    public void startRound() {
        gameState = GameState.BETWEEN_ROUND;
        round++;
        rounds.put(round, new Round(this, round));

        statisticsTracker.resetRound();

        // Clear egg cooldown.
        eggCooldown.clear();

        // Resets the arena.
        resetArena();

        // Spawns players
        for(Player player : players) {
            spawnPlayer(player);

            // Statistic tracking.
            if(mode != Mode.DUEL) {
                plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addRoundPlayed(mode.id(), arena.id(), plugin.abilityManager().getAbility(player).id());
            }
        }

        for(Player player : players) {
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
                    for (Player player : players) {
                        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_HAT.parseSound(), 1, 1);
                    }
                }
                if(counter == 0) {
                    for(Player player : players) {
                        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_HAT.parseSound(), 1, 1);
                    }

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> runRound(), 1);
                    cancel();
                }

                counter--;
            }
        };
        roundCountdown.runTaskTimer(plugin, 0, 20);
    }

    public void runRound() {
        gameState = GameState.RUNNING;

        for(Player player : players) {
            // Close inventory on round start.
            player.closeInventory();

            // Display round start message.
            Titles.sendTitle(player, ChatColor.translateAlternateColorCodes('&', "&a&lROUND START"), ChatColor.translateAlternateColorCodes('&', "&bRound " + round));
            player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(Player player : players) {
                player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);
            }
        }, 3);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(Player player : players) {
                player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);
            }
        }, 6);

        // Removes the barriers
        for(Team team : teamManager.teams()) {
            for(Block block : team.arenaTeam().getBarrierBlocks(world)) {
                block.setType(Material.AIR);
            }
        }
    }

    public void endRound(Team winner) {
        gameState = GameState.BETWEEN_ROUND;

        // Gives the winning team a point.
        winner.addPoint();

        // Checks if there is only one team left.
        if(teamManager.teams().size() == 1) {
            endGame(winner);
            return;
        }

        for(Team team : teamManager.teams()) {
            if(team.equals(winner)) {
                team.players().forEach(player -> {
                    Titles.sendTitle(player, 10,60,10, ChatColor.translateAlternateColorCodes('&', formattedGameScores()), ChatColor.translateAlternateColorCodes('&', "&a&lROUND WON!"));
                    player.playSound(player.getLocation(), XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.parseSound(), 1, (float) 0.8);
                });
            }
            else {
                team.players().forEach(player -> {
                    Titles.sendTitle(player, ChatColor.translateAlternateColorCodes('&', formattedGameScores()), ChatColor.translateAlternateColorCodes('&', "&c&lROUND LOST!"));
                    player.playSound(player.getLocation(), XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.parseSound(), 1, (float) 0.5);
                });
            }
        }

        // Display round stats.
        for(Player player : players) {
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.centeredChat(player, "&a&lRound #" + round + " Stats");
            ChatUtils.centeredChat(player, "&aCacti Placed: &f" + statisticsTracker.getRoundCactiPlaced(player));
            ChatUtils.centeredChat(player, "&aCacti Broken: &f" + statisticsTracker.getRoundCactiBroken(player));
            ChatUtils.centeredChat(player, "&aEggs Thrown: &f" + statisticsTracker.getRoundEggsThrown(player));
            ChatUtils.centeredChat(player, "&aGoals: &f" + statisticsTracker.getRoundGoalsScored(player));
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            plugin.abilityManager().getAbility(player).removeCooldown(player);
        }

        // Checks if a team has enough points to win.
        if(winner.score() >= 3) {
            endGame(winner);
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::startRound, 5*20);
    }

    public void endGame(Team winner) {
        gameState = GameState.END;
        gameTimer.stop();
        sendMessage("&aGame Over");
        sendMessage("&aWinner: " + winner.color().textColor() + winner.color().name());

        for(Team team : teamManager.teams()) {
            if(team.equals(winner)) {
                team.players().forEach(player -> {
                    if(mode != Mode.DUEL) {
                        plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addWin(mode.id(), arena.id());
                        plugin.cactusPlayerManager().getPlayer(player).addCoins(100, "Win");
                    }
                    Titles.sendTitle(player, 10,60,10, ChatColor.translateAlternateColorCodes('&', "&a&lVICTORY!"), ChatColor.translateAlternateColorCodes('&', winner + " &ahas won the game!"));
                });
            }
            else {
                team.players().forEach(player -> {
                    if(mode != Mode.DUEL) {
                        plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addLoss(mode.id(), arena.id());
                    }
                    Titles.sendTitle(player, 10,60,10, ChatColor.translateAlternateColorCodes('&', "&c&lGAME OVER!"), ChatColor.translateAlternateColorCodes('&', winner + " &ahas won the game!"));
                });
            }
        }

        for(Player player : players) {
            int cactiPlaced = statisticsTracker.getGameCactiPlaced(player);
            int cactiBroken = statisticsTracker.getGameCactiBroken(player);
            int eggsThrown = statisticsTracker.getGameEggsThrown(player);
            int goals = statisticsTracker.getGameGoalsScored(player);

            player.removePotionEffect(PotionEffectType.JUMP);


            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.centeredChat(player, "&a&lGame Stats");
            ChatUtils.centeredChat(player, "&aCacti Placed: &f" + cactiPlaced);
            ChatUtils.centeredChat(player, "&aCacti Broken: &f" + cactiBroken);
            ChatUtils.centeredChat(player, "&aEggs Thrown: &f" + eggsThrown);
            ChatUtils.centeredChat(player, "&aGoals: &f" + goals);
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(Team team : teamManager.teams()) {
                team.players().forEach(player -> {
                    int coinsReward = 0;

                    coinsReward += (statisticsTracker.getGameGoalsScored(player) * 30);

                    if(team.equals(winner)) {
                        coinsReward += 100;
                    }

                    int timeReward = (int) (gameTimer.toMinutes() * 12.0);
                    if(mode != Mode.DUEL) {
                        plugin.cactusPlayerManager().getPlayer(player).addCoins(timeReward);
                    }
                    coinsReward += timeReward;

                    int xpReward = 0;

                    xpReward += (statisticsTracker.getGameGoalsScored(player) * 50);
                    xpReward += (int) (gameTimer.toMinutes() * 25.0);

                    if(team.equals(winner)) {
                        xpReward += 150;
                    }

                    ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    ChatUtils.centeredChat(player, "&a&lReward Summary");
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
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.cactusPlayerManager().getPlayer(player).addExperience(finalXpReward), 20);
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

                        for(Team otherTeam : teamManager.teams()) {
                            if(team.equals(winner)) {
                                continue;
                            }

                            if(!teamManager.getTeam(player).equals(winner)) {
                                continue;
                            }

                            for(Player opponent : otherTeam.players()) {
                                JadedPlayer opponentJadedPlayer = JadedAPI.getJadedPlayer(opponent);
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

                    if(mode == Mode.DUEL) {
                        if(statisticsTracker.getGameCactiBroken(player) >= 100) {
                            JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_5").unlock(player);
                        }

                        if(statisticsTracker.getGameEggsThrown(player) > 200) {
                            JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_6").unlock(player);
                        }

                        if(statisticsTracker.getGameCactiPlaced(player) > 300) {
                            JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_7").unlock(player);
                        }
                    }
                });
            }
        }, 3*20);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            new HashSet<>(players).forEach(this::removePlayer);
            new HashSet<>(spectators).forEach(this::removeSpectator);
            plugin.gameManager().deleteGame(this);
        }, 5*20);
    }

    /**
     * Adds a player to the egg cooldown.
     * When the cooldown runs out, gives them a new egg.
     * @param player Player to add to the cooldown.
     */
    public void addEggCooldown(Player player) {
        int id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(eggCooldown.containsKey(player)) {
                if(gameState == GameState.RUNNING) {
                    player.getInventory().addItem(new ItemStack(Material.EGG));
                }

                eggCooldown.remove(player);
            }
        }, 30);
        eggCooldown.put(player, id);
    }

    /**
     * Adds a player to the game.
     * @param player Player to add to the game.
     */
    public void addPlayer(Player player) {
        players.add(player);
        statisticsTracker.addPlayer(player);
        player.teleport(arena.waitingArea(world));

        // Setup Inventory
        player.getInventory().clear();
        player.getInventory().setItem(8, new ItemBuilder(Material.RED_BED).setDisplayName("&c&lLeave").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lAbility Selector").build());

        new GameScoreboard(plugin, player, this).update(player);

        JadedPlayer jadedPlayer = JadedAPI.getJadedPlayer(player);

        // Process duel stuff.
        if(mode == Mode.DUEL) {
            sendMessage(jadedPlayer.getRank().getRankColor() + player.getName() + " &ahas joined the game!");
            return;
        }

        sendMessage(jadedPlayer.getRank().getRankColor() + player.getName() + " &ahas joined the game! (&f" + players.size() + "&a/&f" + mode.maxPlayerCount() + "&a)");

        // Checks if the game has enough players to start
        if(players.size() >= mode.minPlayerCount() && gameCountdown.seconds() == 30) {
            // If so, starts the countdown.
            gameCountdown.start();
            gameState = GameState.COUNTDOWN;
        }

        // Checks if the game is 100% full.
        if(players.size() == mode.maxPlayerCount() && gameCountdown.seconds() > 5) {
            // If so, shortens the countdown to 5 seconds.
            gameCountdown.seconds(5);
        }
    }

    /**
     * Add a spectator to the game.
     * @param player Spectator to add.
     */
    public void addSpectator(Player player) {
        spectators.add(player);

        player.teleport(arena.waitingArea(world));
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setMaxHealth(20.0);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);
        new GameScoreboard(plugin, player, this).addPlayer(player);
        JadedChat.setChannel(player, JadedChat.getChannel("GAME"));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), false);

        // Prevents player from interfering.
        player.setCollidable(false);

        ItemStack leave = new ItemBuilder(Material.RED_BED)
                .setDisplayName("&cLeave Match")
                .build();
        player.getInventory().setItem(8, leave);

        // Delayed to prevent TeleportFix from making visible again.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for(Player pl : players) {
                pl.hidePlayer(plugin, player);
            }

            for(Player spectator : spectators) {
                spectator.hidePlayer(plugin, player);
            }

            player.setAllowFlight(true);
            player.setFlying(true);
        }, 2);

        // Gives an achievement.
        JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_3").unlock(player);
    }

    /**
     * Adds a collection of players at the same time.
     * @param toAdd Players to add.
     */
    public void addPlayers(Collection<Player> toAdd) {
        toAdd.forEach(this::addPlayer);
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

    /**
     * Get the arena the game is using.
     * @return Game's arena.
     */
    public Arena arena() {
        return arena;
    }

    /**
     * Splits the players into teams.
     */
    private void createTeams() {
        if(mode == Mode.DUEL) {
            createDuelTeams();
            return;
        }

        List<Player> tempPlayers = new ArrayList<>(players);
        Collections.shuffle(tempPlayers);

        List<ArrayList<Player>> teams = new ArrayList<>();
        //List<Party> parties = new ArrayList<>();
        List<Player> soloPlayers = new ArrayList<>();

        for(int i = 0; i < mode.teamCount(); i++) {
            System.out.println("Added Team: " + i);
            teams.add(new ArrayList<>());
        }

        // Loops through all players looking for parties.
        for(Player player : players) {
            //Party party = JadedParty.partyManager().getParty(player);

            // Makes sure the player has a party.
            //if(party == null) {
                // If they don't, add them to the solo players list.
                soloPlayers.add(player);
                System.out.println("Solo player added: " + player.getName());
                continue;
            //}

            // Makes sure the party isn't already listed.
            //if(parties.contains(party)) {
                //continue;
            //}

            //parties.add(party);
        }

        // Loop through parties to assign them to teams.
        /*
        for(Party party : parties) {
            // If the party is too big, add the members as solo players.
            if(party.getPlayers().size() > mode.teamSize()) {
                for(UUID member : party.getPlayers()) {
                    soloPlayers.add(Bukkit.getPlayer(member));
                }
                continue;
            }

            // Finds the smallest party available to put the party.
            List<Player> smallestTeam = teams.get(0);
            // Loop through each team to find the smallest.
            for(List<Player> team : teams) {
                if(team.size() < smallestTeam.size()) {
                    smallestTeam = team;
                }
            }

            // Checks if the party can fit in the smallest team.
            if(smallestTeam.size() + party.getPlayers().size() <= mode.teamSize()) {
                // If it can, adds them to the team.
                for(UUID member : party.getPlayers()) {
                    smallestTeam.add(Bukkit.getPlayer(member));
                }
            }
            else {
                // Otherwise, splits them into solo players.
                for(UUID member : party.getPlayers()) {
                    soloPlayers.add(Bukkit.getPlayer(member));
                }
            }
        }
         */

        // Shuffle solo players.
        Collections.shuffle(soloPlayers);

        // Loop through solo players to assign them teams.
        while(soloPlayers.size() > 0) {
            List<Player> smallestTeam = teams.get(0);

            // Loop through each team to find the smallest.
            for(List<Player> team : teams) {
                if(team.size() < smallestTeam.size()) {
                    smallestTeam = team;
                }
            }

            // Adds the player to the smallest team.
            smallestTeam.add(soloPlayers.get(0));
            soloPlayers.remove(soloPlayers.get(0));
        }

        // Creates the team objects.
        int arenaTeamNumber = 0;
        for(List<Player> teamPlayers : teams) {
            teamManager.createTeam(teamPlayers, arena.teams().get(arenaTeamNumber));
            arenaTeamNumber++;
        }
    }

    private void createDuelTeams() {
        List<Player> tempPlayers = new ArrayList<>(players);
        Collections.shuffle(tempPlayers);

        List<ArrayList<Player>> teams = new ArrayList<>();
        //List<Party> parties = new ArrayList<>();
        List<Player> soloPlayers = new ArrayList<>();

        for(int i = 0; i < mode.teamCount(); i++) {
            System.out.println("Added Team: " + i);
            teams.add(new ArrayList<>());
        }

        // Loops through all players looking for parties.
        for(Player player : players) {
            //Party party = JadedParty.partyManager().getParty(player);

            // Makes sure the player has a party.
            //if(party == null) {
                // If they don't, add them to the solo players list.
                soloPlayers.add(player);
                System.out.println("Solo player added: " + player.getName());
                //continue;
            //}

            // Makes sure the party isn't already listed.
            //if(parties.contains(party)) {
                //continue;
            //}

            //parties.add(party);
        }

        // Loop through parties to assign them to teams.
        /*
        for(Party party : parties) {

            // Finds the smallest party available to put the party.
            List<Player> smallestTeam = teams.get(0);
            // Loop through each team to find the smallest.
            for(List<Player> team : teams) {
                if(team.size() < smallestTeam.size()) {
                    smallestTeam = team;
                }
            }

            // Adds  all players in the party to the smallest team.
            for(UUID member : party.getPlayers()) {
                smallestTeam.add(Bukkit.getPlayer(member));
            }
        }
         */

        // Shuffle solo players.
        Collections.shuffle(soloPlayers);

        // Loop through solo players to assign them teams.
        while(soloPlayers.size() > 0) {
            List<Player> smallestTeam = teams.get(0);

            // Loop through each team to find the smallest.
            for(List<Player> team : teams) {
                if(team.size() < smallestTeam.size()) {
                    smallestTeam = team;
                }
            }

            // Adds the player to the smallest team.
            smallestTeam.add(soloPlayers.get(0));
            soloPlayers.remove(soloPlayers.get(0));
        }

        // Creates the team objects.
        int arenaTeamNumber = 0;
        for(List<Player> teamPlayers : teams) {
            teamManager.createTeam(teamPlayers, arena.teams().get(arenaTeamNumber));
            arenaTeamNumber++;
        }
    }

    /**
     * Gets the game score, formatted with colors.
     * @return Formatted game score.
     */
    public String formattedGameScores() {
        int divisions = teamManager.teams().size() - 1;

        StringBuilder scores = new StringBuilder();

        int count = 0;
        for(Team team : teamManager.teams()) {
            scores.append(team.color().textColor()).append(team.score());

            if(count < divisions) {
                count++;
                scores.append(" &8- ");
            }
        }

        return scores.toString();
    }

    /**
     * Get the game's countdown timer.
     * @return Countdown timer.
     */
    public GameCountdown gameCountdown() {
        return gameCountdown;
    }

    /**
     * Get the game's current game state.
     * @return Current game state.
     */
    public GameState gameState() {
        return gameState;
    }

    /**
     * Check if a player currently has an egg cooldown.
     * @param player Player to check for an egg cooldown.
     * @return Whether they have an egg cooldown.
     */
    public boolean hasEggCooldown(Player player) {
        return eggCooldown.containsKey(player);
    }

    /**
     * Gets the mode the game is using.
     * @return Game's mode.
     */
    public Mode mode() {
        return mode;
    }

    /**
     * Gets all players currently part of the game.
     * @return All players in the game.
     */
    public Collection<Player> players() {
        return players;
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
        sendMessage(team.color().textColor() + player.getName() + " &ascored!");

        team.players().forEach(teamMember -> teamMember.playSound(teamMember.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 2));
        if(mode != Mode.DUEL) {
            plugin.cactusPlayerManager().getPlayer(player).addCoins(30, "Goal Scored");
        }

        player.getInventory().clear();
        player.teleport(team.arenaTeam().getScoreRoom().getSpawnPoint(world));

        // Statistic Tracking
        statisticsTracker.addGoalScored(player);
        if(mode != Mode.DUEL) {
            plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addGoalsScored(mode.id(), arena.id());
        }

        endRound(team);
    }

    /**
     * Removes a player from the egg cooldown.
     * Allows them to obtain another egg.
     * @param player Player to remove from the egg cooldown.
     */
    public void removeEggCooldown(Player player) {
        if(eggCooldown.containsKey(player)) {
            plugin.getServer().getScheduler().cancelTask(eggCooldown.get(player));
            eggCooldown.remove(player);
        }
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
     * Removes a player from the game.
     * @param player Player to remove.
     */
    public void removePlayer(Player player) {
        // Removes the player if they are a spectator.
        if(spectators.contains(player)) {
            removeSpectator(player);
            return;
        }

        // Save player statistics if they were a part of the game.
        if(players.contains(player) && mode != Mode.DUEL) {
            players.remove(player);

            // Update statistics.
            CactusPlayer cactusPlayer = plugin.cactusPlayerManager().getPlayer(player);
            cactusPlayer.statisticsTracker().addPlayTime(mode.id(), arena.id(), gameTimer.toSeconds());

            // Save statistics.
            cactusPlayer.statisticsTracker().updateModeStatistics(mode.id());
            cactusPlayer.statisticsTracker().updateArenaStatistics(arena.id());
            cactusPlayer.statisticsTracker().updateAbilityStatistics();
        }

        statisticsTracker.removePlayer(player);
        LobbyUtils.sendToLobby(plugin, player);

        players.remove(player);
        plugin.abilityManager().removePlayer(player);

        // Processes leaving a game that has not started.
        if(gameState == GameState.WAITING || gameState == GameState.COUNTDOWN) {
            sendMessage("&f" + PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%") + player.getName() + " &ahas left the game! (&f"+ players.size() + "&a/&f" + mode.maxPlayerCount() + "&a)");

            // Stops the countdown if the game has too few players.
            if(players.size() < mode.minPlayerCount() && gameState == GameState.COUNTDOWN) {
                sendMessage("&cNot enough players! Countdown reset.");
                gameCountdown.cancel();
                gameCountdown = new GameCountdown(plugin, this);
                gameState = GameState.WAITING;
            }

            // If the game is empty, delete it.
            if(players.size() == 0) {
                plugin.gameManager().deleteGame(this);
            }

            return;
        }

        // If the game is running, remove the player from their team.
        if(gameState == GameState.BETWEEN_ROUND || gameState == GameState.RUNNING) {
            Team team = teamManager.getTeam(player);
            team.removePlayer(player);
            sendMessage(team.color().textColor() + player.getName() + " &adisconnected.");

            // If the team is empty, check if the game should end.
            if(team.players().size() == 0) {
                List<Team> remainingTeams = new ArrayList<>();
                for(Team remainingTeam : teamManager.teams()) {
                    if(remainingTeam.players().size() > 0) {
                        remainingTeams.add(remainingTeam);
                    }
                }

                // End the game if only 1 team remains.
                if(remainingTeams.size() == 1) {
                    endGame(remainingTeams.get(0));
                }
            }
        }
    }

    /**
     * Remove a spectator from the game.
     * @param player Spectator to remove.
     */
    public void removeSpectator(Player player) {
        spectators.remove(player);

        for(Player pl : Bukkit.getOnlinePlayers()) {
            pl.showPlayer(plugin, player);
        }

        LobbyUtils.sendToLobby(plugin, player);
    }

    /**
     * Resets the arena.
     * Clears placed blocks and replaces barriers.
     */
    private void resetArena() {
        // Reset placed blocks.
        new ArrayList<>(placedBlocks.keySet()).forEach(this::removePlacedBlock);
        placedBlocks.clear();

        for(Team team : teamManager.teams()) {
            // Reset barrier blocks:
            for(Block block : team.arenaTeam().getBarrierBlocks(world)) {
                block.setType(Material.GLASS_PANE);
            }
        }
    }

    /**
     * Gets the game's current round.
     * @return Game round number.
     */
    public int round() {
        return round;
    }

    /**
     * Sends a message to the entire game.
     * @param message Message to send to the game.
     */
    public void sendMessage(String message) {
        for(Player player : players) {
            ChatUtils.chat(player, message);
        }

        spectators.forEach(player -> ChatUtils.chat(player, message));
    }

    /**
     * Sets up the arena for the first time.
     * Replaces goal blocks and score room walls.
     */
    private void setupArena() {
        for(Team team : teamManager.teams()) {
            // Set goal blocks:
            for(Block block : team.arenaTeam().getGoalBlocks(world)) {
                block.setType(team.color().goalMaterial());
            }

            // Set score room walls:
            for(Block block : team.arenaTeam().getScoreRoom().getScoreRoomBlocks(world)) {
                block.setType(team.color().scoreRoomMaterial());
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
        plugin.abilityManager().getAbility(player).giveItem(player);
        player.getInventory().setItem(8, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lAbility Selector").build());

        // Teleport to team spawn.
        Team team = teamManager.getTeam(player);
        player.teleport(team.arenaTeam().getSpawnPoint(world));

        // Remove egg cooldown
        removeEggCooldown(player);

        if(reason == GameDeathType.NONE) {
            return;
        }

        // Updates death statistic.
        if(mode != Mode.DUEL) {
            CactusPlayer cactusPlayer = plugin.cactusPlayerManager().getPlayer(player);
            cactusPlayer.statisticsTracker().addDeath(mode.id(), arena.id(), reason);
        }
    }

    /**
     * Get all spectators of the game.
     * @return Game spectators.
     */
    public Collection<Player> spectators() {
        return spectators;
    }

    /**
     * Manually starts the game's countdown.
     * Used in duels.
     */
    public void startCountdown() {
        gameCountdown.start();
        gameCountdown.seconds(5);
        gameState = GameState.COUNTDOWN;
    }

    /**
     * Gets the game statistics tracker.
     * Tracks per-game statistics, which are displayed at the end of rounds and games.
     * @return The game's statistic tracker.
     */
    public GameStatisticsTracker statisticsTracker() {
        return statisticsTracker;
    }

    /**
     * Gets the game's team manager.
     * Handles the creation and processing of teams.
     * @return Team manager.
     */
    public TeamManager teamManager() {
        return teamManager;
    }

    /**
     * Get the UUID of the game.
     * @return Game uuid.
     */
    public UUID uuid() {
        return uuid;
    }

    /**
     * Get the world the game is taking place in.
     * @return Game world.
     */
    public World world() {
        return world;
    }

    @Nullable
    public RoundPlayer getRoundPlayer(@NotNull final UUID playerUUID, final int roundNumber) {
        if(roundNumber > this.round) {
            return null;
        }

        final Round round = rounds.get(roundNumber);
        return round.getPlayer(playerUUID);
    }

    public Round getCurrentRound() {
        return rounds.get(round);
    }
}