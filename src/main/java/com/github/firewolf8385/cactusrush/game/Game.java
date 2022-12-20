package com.github.firewolf8385.cactusrush.game;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.LobbyScoreboard;
import com.github.firewolf8385.cactusrush.game.arena.Arena;
import com.github.firewolf8385.cactusrush.game.team.Team;
import com.github.firewolf8385.cactusrush.game.team.TeamColor;
import com.github.firewolf8385.cactusrush.game.team.TeamManager;
import com.github.firewolf8385.cactusrush.utils.LocationUtils;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import com.github.firewolf8385.cactusrush.utils.xseries.Titles;
import com.github.firewolf8385.cactusrush.utils.xseries.XSound;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Game {
    private final CactusRush plugin;
    private final Arena arena;
    private final Collection<Player> players = new HashSet<>();
    private final Collection<Player> spectators = new HashSet<>();
    private final Collection<Player> eggCooldown = new HashSet<>();
    private GameState gameState;
    private int round;
    private GameCountdown gameCountdown;
    private TeamManager teamManager;

    public Game(CactusRush plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;

        gameState = GameState.WAITING;
        round = 0;
        gameCountdown = new GameCountdown(plugin, this);
        teamManager = new TeamManager();
    }

    // ----------------------------------------------------------------------------------------------------------------
    public void startGame() {
        List<ArrayList<Player>> teams = new ArrayList<>();

        for(TeamColor teamColor : arena.getSpawns().keySet()) {
            teams.add(new ArrayList<>());
        }

        int count = 0;
        for(Player player : players) {
            if(count == teams.size()) {
                count = 0;
            }

            teams.get(count).add(player);
            count++;
        }

        count = 0;
        List<TeamColor> colors = new ArrayList<>(arena.getSpawns().keySet());
        for(List<Player> team : teams) {
            teamManager.createTeam(team, colors.get(count));
            count++;
        }

        startRound();
    }

    public void startRound() {
        gameState = GameState.RUNNING;
        round++;

        eggCooldown.clear();
        new ArrayList<>(spectators).forEach(this::removeSpectator);

        for(Player player : players) {
            new GameScoreboard(plugin, player, this).update(player);
        }

        // Resets the arena.
        arena.reset();

        for(Team team : teamManager.getTeams()) {
            team.getPlayers().forEach(this::spawnPlayer);
            team.reset();
        }

        for(Player player : getPlayers()) {
            Titles.sendTitle(player, ChatUtils.translate("&e&lPRE ROUND"), ChatUtils.translate("&bGet ready to fight!"));
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
                    for (Player player : getPlayers()) {
                        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_HAT.parseSound(), 1, 1);
                    }
                }
                if(counter == 0) {
                    for(Player player : getPlayers()) {
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

    /**
     * Runs the game countdown.
     */
    private void runRound() {
        for(Player player : getPlayers()) {
            Titles.sendTitle(player, ChatUtils.translate("&a&lROUND START"), ChatUtils.translate("&bRound " + round));
            player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(Player player : getPlayers()) {
                player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);
            }
        }, 3);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(Player player : getPlayers()) {
                player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);
            }
        }, 6);

        // Removes the barriers
        for(TeamColor teamColor : arena.getBarriers().keySet()) {
            for(Location location : arena.getBarriers().get(teamColor)) {
                location.getWorld().getBlockAt(location).setType(Material.AIR);
            }
        }
    }

    /**
     * Ends the round.
     * @param winner Winner of the round.
     */
    public void endRound(Team winner) {
        gameState = GameState.BETWEEN_ROUND;

        // Gives the winning team a point.
        winner.addPoint();

        // Checks if there is only one team left.
        if(teamManager.getTeams().size() == 1) {
            endGame(winner);
            return;
        }

        for(Team team : teamManager.getTeams()) {
            if(team.equals(winner)) {
                team.getPlayers().forEach(player -> {
                    Titles.sendTitle(player, 10,60,10, getFormattedGameScores(), ChatUtils.translate("&a&lROUND WON!"));
                    player.playSound(player.getLocation(), XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.parseSound(), 1, (float) 0.8);
                });
            }
            else {
                team.getPlayers().forEach(player -> {
                    Titles.sendTitle(player, getFormattedGameScores(), ChatUtils.translate("&c&lROUND LOST!"));
                    player.playSound(player.getLocation(), XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.parseSound(), 1, (float) 0.5);
                });
            }
        }

        // Display round stats.
        for(Player player : getPlayers()) {
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.centeredChat(player, "&a&lRound #" + round + " Stats");
            ChatUtils.centeredChat(player, "&aCacti Placed: &f0");
            ChatUtils.centeredChat(player, "&aCacti Broken: &f0");
            ChatUtils.centeredChat(player, "&aEggs Thrown: &f0");
            ChatUtils.centeredChat(player, "&aDeaths: &f0");
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }

        // Checks if a team has enough points to win.
        if(winner.getScore() >= 3) {
            endGame(winner);
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::startRound, 5*20);
    }

    /**
     * Ends the game.
     * @param winner Winner of the game.
     */
    private void endGame(Team winner) {
        gameState = GameState.END;
        sendMessage("&aGame Over");
        sendMessage("&aWinner: " + winner.getColor().getChatColor() + winner.getColor().getName());

        for(Team team : teamManager.getTeams()) {
            if(team.equals(winner)) {
                team.getPlayers().forEach(player -> {
                    plugin.getCactusPlayerManager().getPlayer(player).addWin();
                });
            }
            else {
                team.getPlayers().forEach(player -> {
                    plugin.getCactusPlayerManager().getPlayer(player).addLoss();
                });
            }
        }

        teamManager = new TeamManager();
        round = 0;
        gameCountdown = new GameCountdown(plugin, this);

        arena.reset();

        spectators.forEach(this::removeSpectator);

        for(Player player : getPlayers()) {
            player.teleport(LocationUtils.getSpawn(plugin));
            new LobbyScoreboard(plugin, player).update(player);

            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.centeredChat(player, "&a&lGame Stats");
            ChatUtils.centeredChat(player, "&aCacti Placed: &f0");
            ChatUtils.centeredChat(player, "&aCacti Broken: &f0");
            ChatUtils.centeredChat(player, "&aEggs Thrown: &f0");
            ChatUtils.centeredChat(player, "&aDeaths: &f0");
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }

        players.clear();
        gameState = GameState.WAITING;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void addEggCooldown(Player player) {
        eggCooldown.add(player);
    }

    /**
     * Adds a player to the game.
     * If they are in a party, adds all party members.
     * @param player Player to add.
     */
    public void addPlayer(Player player) {
        // If not, just adds themselves.
        players.add(player);
        player.teleport(arena.getWaitingArea());
        sendMessage("&f" + PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%") + player.getName() + " &ahas joined the game! (&f"+ players.size() + "&a/&f" + arena.getMaxPlayers() + "&a)");
        new GameScoreboard(plugin, player, this).update(player);

        // Checks if the game is at least 75% full.
        if(getPlayers().size() >= arena.getMinPlayers() && gameCountdown.getSeconds() == 30) {
            // If so, starts the countdown.
            gameCountdown.start();
            gameState = GameState.COUNTDOWN;
        }

        // Checks if the game is 100% full.
        if(getPlayers().size() == arena.getMaxPlayers() && gameCountdown.getSeconds() > 5) {
            // If so, shortens the countdown to 5 seconds.
            gameCountdown.setSeconds(5);
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setHealth(20.0);
        player.setFoodLevel(20);

        for(Player pl : getPlayers()) {
            pl.hidePlayer(player);
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

    public String getFormattedGameScores() {
         int divisions = teamManager.getTeams().size() - 1;

         String scores = "";

         int count = 0;
         for(Team team : teamManager.getTeams()) {
             scores += "" + team.getColor().getChatColor() + team.getScore();

             if(count < divisions) {
                 count++;
                 scores += " &8- ";
             }
         }

        return ChatUtils.translate(scores);
    }

    public String getFormattedTeamSCore(Team team) {
        String formattedScore = team.getColor().getChatColor() + "[" + team.getColor().getAbbreviation() + "] ";

        int count = 0;
        for(int i = 0; i < team.getScore(); i++) {
            formattedScore += "⬤";
            count++;
        }

        formattedScore += "&7";
        for(int i = count; i < 3; i++) {
            formattedScore += "⬤";
        }

        formattedScore += " &8(" + team.getScore() + "/3)";
        return formattedScore;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public int getRound() {
        return round;
    }

    public Collection<Player> getSpectators() {
        return spectators;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public boolean hasEggCooldown(Player player) {
        return eggCooldown.contains(player);
    }

    public void playerDisconnect(Player player) {
        removePlayer(player);

        if(gameState == GameState.RUNNING) {
            sendMessage("&a" + player.getName() + " disconnected");

            Team team = teamManager.getTeam(player);
            team.removePlayer(player);

            if(team.getPlayers().size() == 0) {
                teamManager.removeTeam(team);
            }

            if(team.getRemainingPlayers().size() == 0) {
                endRound(team);
            }
        }
    }

    public void removeEggCooldown(Player player) {
        eggCooldown.remove(player);
    }

    public void spawnPlayer(Player player) {
        player.teleport(arena.getSpawns().get(teamManager.getTeam(player).getColor()));
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(19);
        player.setGameMode(GameMode.SURVIVAL);

        player.getInventory().clear();
        player.getInventory().setItem(0, new ItemStack(Material.CACTUS, 64));
        player.getInventory().setItem(1, new ItemStack(Material.EGG));

        eggCooldown.remove(player);
    }

    public void playerScored(Player player) {
        // Prevents stuff from breaking if the game is already over.
        if(gameState == GameState.END) {
            return;
        }

        Team team = teamManager.getTeam(player);
        sendMessage(team.getColor().getChatColor() + player.getName() + " &ascored!");
        team.scorePlayer(player);
        team.getPlayers().forEach(teamMember -> teamMember.playSound(teamMember.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 2));
        addSpectator(player);
        removeSpectator(player);

        if(team.getRemainingPlayers().size() == 0) {
            endRound(team);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);

        if(gameState == GameState.WAITING || gameState == GameState.COUNTDOWN) {
            sendMessage("&f" + player.getName() + " &ahas left the game! (&f"+ players.size() + "&a/&f" + arena.getMaxPlayers() + "&a)");

            if(getPlayers().size() < ((arena.getMaxPlayers()/4) * 3)) {
                sendMessage("&cNot enough players! Countdown reset.");
                gameCountdown = new GameCountdown(plugin, this);
            }
        }
    }

    public void removeSpectator(Player player) {
        spectators.remove(player);

        // Stop the player from flying.
        player.setFlying(false);
        player.setAllowFlight(false);

        for(Player pl : Bukkit.getOnlinePlayers()) {
            pl.showPlayer(player);
        }
    }

    public void sendMessage(String message) {
        for(Player player : players) {
            ChatUtils.chat(player, message);
        }
    }
}