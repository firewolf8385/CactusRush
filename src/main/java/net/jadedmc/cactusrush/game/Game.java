package net.jadedmc.cactusrush.game;

import me.clip.placeholderapi.PlaceholderAPI;
import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.arenas.Arena;
import net.jadedmc.cactusrush.game.teams.TeamColor;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedpartybukkit.JadedParty;
import net.jadedmc.jadedpartybukkit.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Game {
    private final CactusRushPlugin plugin;
    private final Arena arena;
    private final Collection<Player> players = new HashSet<>();
    private final Map<Player, Integer> eggCooldown = new HashMap<>();
    private GameState gameState;
    private int round;
    private GameCountdown gameCountdown;
    private TeamManager teamManager;
    private Timer gameTimer;
    private int teamSize;
    private final Collection<Player> spectators = new HashSet<>();

    private final Map<Player, Integer> gameCactiBroken = new HashMap<>();
    private final Map<Player, Integer> gameCactiPlaced = new HashMap<>();
    private final Map<Player, Integer> gameEggsThrown = new HashMap<>();
    private final Map<Player, Integer> gameGoalsScored = new HashMap<>();
    private final Map<Player, Integer> roundCactiBroken = new HashMap<>();
    private final Map<Player, Integer> roundCactiPlaced = new HashMap<>();
    private final Map<Player, Integer> roundEggsThrown = new HashMap<>();
    private final Map<Player, Integer> roundGoalsScored = new HashMap<>();

    public Game(CactusRushPlugin plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;

        gameState = GameState.WAITING;
        round = 0;
        gameCountdown = new GameCountdown(plugin, this);
        teamManager = new TeamManager();
        gameTimer = new Timer(plugin);
        teamSize = 0;
    }

    // ----------------------------------------------------------------------------------------------------------------
    public void startGame() {
        // Shuffle all players before the start of the game.
        List<Player> allPlayers = new ArrayList<>(players);
        Collections.shuffle(allPlayers);

        List<ArrayList<Player>> teams = new ArrayList<>();
        List<Party> parties = new ArrayList<>();
        List<Player> soloPlayers = new ArrayList<>();

        // Load the teams for the game.
        for(TeamColor teamColor : arena.getSpawns().keySet()) {
            teams.add(new ArrayList<>());
        }

        // Loops through all players looking for parties.
        for(Player player : players) {
            Party party = JadedParty.partyManager().getParty(player);

            // Makes sure the player has a party.
            if(party == null) {
                // If they don't, add them to the solo players list.
                soloPlayers.add(player);
                continue;
            }

            // Makes sure the party isn't already listed.
            if(parties.contains(party)) {
                continue;
            }

            parties.add(party);
        }

        // Loop through parties to assign them to teams.
        for(Party party : parties) {
            // If the party is too big, add the members as solo players.
            if(party.getPlayers().size() > teamSize) {
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
            if(smallestTeam.size() + party.getPlayers().size() <= teamSize) {
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

        // Loop through all players to add default stats.
        for(Player player : allPlayers) {
            gameCactiBroken.put(player, 0);
            gameCactiPlaced.put(player, 0);
            gameEggsThrown.put(player, 0);
            gameGoalsScored.put(player, 0);
        }

        // Adds the sorted players to each team.
        int count = 0;
        List<TeamColor> colors = new ArrayList<>(arena.getSpawns().keySet());
        for(List<Player> team : teams) {
            teamManager.createTeam(team, colors.get(count));
            count++;
        }

        players.forEach(player -> JadedChat.setChannel(player, JadedChat.getChannel("GAME")));

        // Starts the first round.
        gameTimer.start();
        startRound();
    }

    public void startRound() {
        gameState = GameState.BETWEEN_ROUND;
        round++;

        eggCooldown.clear();

        for(Player player : players) {
            new GameScoreboard(plugin, player, this).update(player);
            roundCactiBroken.put(player, 0);
            roundCactiPlaced.put(player, 0);
            roundEggsThrown.put(player, 0);
            roundGoalsScored.put(player, 0);
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
        gameState = GameState.RUNNING;

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
            ChatUtils.centeredChat(player, "&aCacti Placed: &f" + roundCactiPlaced.get(player));
            ChatUtils.centeredChat(player, "&aCacti Broken: &f" + roundCactiBroken.get(player));
            ChatUtils.centeredChat(player, "&aEggs Thrown: &f" + roundEggsThrown.get(player));
            ChatUtils.centeredChat(player, "&aGoals: &f" + roundGoalsScored.get(player));
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            plugin.getAbilityManager().getAbility(player).removeCooldown(player);
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
        gameTimer.stop();
        sendMessage("&aGame Over");
        sendMessage("&aWinner: " + winner.getColor().getChatColor() + winner.getColor().getName());

        for(Team team : teamManager.getTeams()) {
            if(team.equals(winner)) {
                team.getPlayers().forEach(player -> {
                    plugin.getCactusPlayerManager().getPlayer(player).addWin();
                    Titles.sendTitle(player, 10,60,10, ChatUtils.translate("&a&lVICTORY!"), ChatUtils.translate(winner + " &ahas won the game!"));
                    plugin.getCactusPlayerManager().getPlayer(player).addCoins(100, "Win");
                });
            }
            else {
                team.getPlayers().forEach(player -> {
                    plugin.getCactusPlayerManager().getPlayer(player).addLoss();
                    Titles.sendTitle(player, 10,60,10, ChatUtils.translate("&c&lGAME OVER!"), ChatUtils.translate(winner + " &ahas won the game!"));
                });
            }
        }

        for(Player player : getPlayers()) {
            int cactiPlaced = gameCactiPlaced.get(player);
            int cactiBroken = gameCactiBroken.get(player);
            int eggsThrown = gameEggsThrown.get(player);
            int goals = gameGoalsScored.get(player);

            player.removePotionEffect(PotionEffectType.JUMP);

            CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);
            cactusPlayer.addCactiPlaced(cactiPlaced);
            cactusPlayer.addCactiBroke(cactiBroken);
            cactusPlayer.addEggsThrown(eggsThrown);
            cactusPlayer.addGoalsScored(goals);

            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.centeredChat(player, "&a&lGame Stats");
            ChatUtils.centeredChat(player, "&aCacti Placed: &f" + cactiPlaced);
            ChatUtils.centeredChat(player, "&aCacti Broken: &f" + cactiBroken);
            ChatUtils.centeredChat(player, "&aEggs Thrown: &f" + eggsThrown);
            ChatUtils.centeredChat(player, "&aGoals: &f" + goals);
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(Team team : teamManager.getTeams()) {
                team.getPlayers().forEach(player -> {
                    int coinsReward = 0;

                    coinsReward += (gameGoalsScored.get(player) * 30);

                    if(team.equals(winner)) {
                        coinsReward += 100;
                    }

                    int timeReward = (int) (gameTimer.toMinutes() * 12.0);
                    plugin.getCactusPlayerManager().getPlayer(player).addCoins(timeReward);
                    coinsReward += timeReward;

                    int xpReward = 0;

                    xpReward += (gameGoalsScored.get(player) * 50);
                    xpReward += (int) (gameTimer.toMinutes() * 25.0);

                    if(team.equals(winner)) {
                        xpReward += 150;
                    }

                    ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    ChatUtils.centeredChat(player, "&a&lReward Summary");
                    ChatUtils.chat(player, "");
                    ChatUtils.chat(player, "  &7You Earned:");
                    ChatUtils.chat(player, "    &f• &6" + coinsReward + " Cactus Rush Coins");
                    ChatUtils.chat(player, "    &f• &b" + xpReward + " Cactus Rush Experience");
                    ChatUtils.chat(player, "");
                    ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

                    int finalXpReward = xpReward;
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getCactusPlayerManager().getPlayer(player).addExperience(finalXpReward), 20);
                });
            }
        }, 3*20);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            getPlayers().forEach(player -> {
                player.teleport(LocationUtils.getSpawn(plugin));
                new LobbyScoreboard(plugin, player).update(player);
                ItemUtils.giveLobbyItems(player);
                plugin.getAbilityManager().removePlayer(player);
                JadedChat.setChannel(player, JadedChat.getDefaultChannel());
            });

            spectators.forEach(this::removeSpectator);

            teamManager = new TeamManager();
            round = 0;
            gameCountdown = new GameCountdown(plugin, this);

            arena.reset();

            players.clear();
            gameTimer = new Timer(plugin);
            gameState = GameState.WAITING;
            teamSize = 0;
        }, 5*20);
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void addEggCooldown(Player player) {
        int id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(eggCooldown.containsKey(player)) {
                if(gameState == GameState.RUNNING && !teamManager.getTeam(player).getScoredPlayers().contains(player)) {
                    player.getInventory().addItem(new ItemStack(Material.EGG));
                }

                eggCooldown.remove(player);
            }
        }, 30);
        eggCooldown.put(player, id);
    }

    /**
     * Adds a player to the game.
     * If they are in a party, adds all party members.
     * @param player Player to add.
     */
    public void addPlayer(Player player) {
        // Checks if the player is in a party.
        if(JadedParty.partyManager().getParty(player) != null) {
            // If so, adds all their party members.
            Party party = JadedParty.partyManager().getParty(player);

            for(UUID memberUUID : party.getPlayers()) {
                Player member = Bukkit.getPlayer(memberUUID);

                if(member == null) {
                    continue;
                }

                // Removes the player from their current game if they are in one.
                com.github.firewolf8385.cactusrush.game.Game game = plugin.getGameManager().getGame(member);
                if(game != null) {
                    game.removePlayer(member);
                }

                // TODO: Allow selecting own ability.
                plugin.getAbilityManager().addPlayer(member, plugin.getAbilityManager().getAbility("flash"));

                players.add(member);
                member.getInventory().clear();
                member.teleport(arena.getWaitingArea());
                sendMessage("&f" + PlaceholderAPI.setPlaceholders(member, "%luckperms_suffix%") + member.getName() + " &ahas joined the game! (&f"+ players.size() + "&a/&f" + arena.getMaxPlayers(teamSize) + "&a)");
                new GameScoreboard(plugin, member, this).update(member);
                member.getInventory().setItem(8, new ItemBuilder(XMaterial.RED_BED).setDisplayName("&c&lLeave").build());
                member.getInventory().setItem(4, new ItemBuilder(XMaterial.NETHER_STAR).setDisplayName("&a&lAbility Selector").build());
            }
        }
        else {
            // If not, just adds themselves.
            players.add(player);

            // TODO: Allow selecting own ability.
            plugin.getAbilityManager().addPlayer(player, plugin.getAbilityManager().getAbility("flash"));

            player.getInventory().clear();
            player.teleport(arena.getWaitingArea());
            sendMessage("&f" + PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%") + player.getName() + " &ahas joined the game! (&f"+ players.size() + "&a/&f" + arena.getMaxPlayers(teamSize) + "&a)");
            new GameScoreboard(plugin, player, this).update(player);
            player.getInventory().setItem(8, new ItemBuilder(XMaterial.RED_BED).setDisplayName("&c&lLeave").build());
            player.getInventory().setItem(4, new ItemBuilder(XMaterial.NETHER_STAR).setDisplayName("&a&lAbility Selector").build());
        }

        // Checks if the game is at least 75% full.
        if(getPlayers().size() >= arena.getMinPlayers(teamSize) && gameCountdown.getSeconds() == 30) {
            // If so, starts the countdown.
            gameCountdown.start();
            gameState = GameState.COUNTDOWN;
        }

        // Checks if the game is 100% full.
        if(getPlayers().size() == arena.getMaxPlayers(teamSize) && gameCountdown.getSeconds() > 5) {
            // If so, shortens the countdown to 5 seconds.
            gameCountdown.setSeconds(5);
        }
    }

    /**
     * Add a spectator to the game.
     * @param player Spectator to add.
     */
    public void addSpectator(Player player) {
        spectators.add(player);

        player.teleport(arena.getWaitingArea());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setMaxHealth(20.0);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);
        new GameScoreboard(plugin, player, this).addPlayer(player);
        JadedChat.setChannel(player, JadedChat.getChannel("GAME"));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), false);

        // Prevents player from interfering.
        player.spigot().setCollidesWithEntities(false);

        ItemStack leave = new ItemBuilder(XMaterial.RED_BED)
                .setDisplayName("&cLeave Match")
                .build();
        player.getInventory().setItem(8, leave);

        // Delayed to prevent TeleportFix from making visible again.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for(Player pl : getPlayers()) {
                pl.hidePlayer(player);
            }

            for(Player spectator : spectators) {
                spectator.hidePlayer(player);
            }
        }, 2);
    }

    public void addBrokenCacti(Player player) {
        gameCactiBroken.put(player, gameCactiBroken.get(player) + 1);
        roundCactiBroken.put(player, roundCactiBroken.get(player) + 1);
    }

    public void addPlacedCacti(Player player) {
        gameCactiPlaced.put(player, gameCactiPlaced.get(player) + 1);
        roundCactiPlaced.put(player, roundCactiPlaced.get(player) + 1);
    }

    public void addEggThrown(Player player) {
        gameEggsThrown.put(player, gameEggsThrown.get(player) + 1);
        roundEggsThrown.put(player, roundEggsThrown.get(player) + 1);
    }

    public void addGoalScored(Player player) {
        gameGoalsScored.put(player, gameGoalsScored.get(player) + 1);
        roundGoalsScored.put(player, roundGoalsScored.get(player) + 1);
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

    public Collection<Player> getPlayers() {
        return players;
    }

    public int getRound() {
        return round;
    }

    /**
     * Get all current spectators.
     * @return All current spectators.
     */
    public Collection<Player> getSpectators() {
        return spectators;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public boolean hasEggCooldown(Player player) {
        return eggCooldown.containsKey(player);
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
                team.setPlayerLeft(true);
            }
        }
    }

    public void removeEggCooldown(Player player) {
        if(eggCooldown.containsKey(player)) {
            plugin.getServer().getScheduler().cancelTask(eggCooldown.get(player));
            eggCooldown.remove(player);
        }
    }

    /**
     * Remove a spectator from the game.
     * @param player Spectator to remove.
     */
    public void removeSpectator(Player player) {
        spectators.remove(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setMaxHealth(20.0);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.teleport(LocationUtils.getSpawn(plugin));
        player.spigot().setCollidesWithEntities(true);
        JadedChat.setChannel(player, JadedChat.getDefaultChannel());
        ItemUtils.giveLobbyItems(player);

        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        // Clears arrows from the player. Requires craftbukkit.
        //((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);

        new LobbyScoreboard(plugin, player);

        for(Player pl : Bukkit.getOnlinePlayers()) {
            pl.showPlayer(player);
        }
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
        plugin.getAbilityManager().getAbility(player).giveItem(player);
        player.getInventory().setItem(8, new ItemBuilder(XMaterial.NETHER_STAR).setDisplayName("&a&lAbility Selector").build());

        removeEggCooldown(player);
    }

    public void playerScored(Player player) {
        // Prevents stuff from breaking if the round is already over.
        if(gameState == GameState.END || gameState == GameState.BETWEEN_ROUND) {
            return;
        }

        Team team = teamManager.getTeam(player);
        sendMessage(team.getColor().getChatColor() + player.getName() + " &ascored!");
        team.scorePlayer(player);
        addGoalScored(player);
        team.getPlayers().forEach(teamMember -> teamMember.playSound(teamMember.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 2));
        plugin.getCactusPlayerManager().getPlayer(player).addCoins(30, "Goal Scored");

        player.getInventory().clear();
        player.teleport(arena.getScoreRooms().get(team.getColor()));
        endRound(team);
    }

    public void removePlayer(Player player) {
        new LobbyScoreboard(plugin, player).update(player);
        players.remove(player);
        ItemUtils.giveLobbyItems(player);
        plugin.getAbilityManager().removePlayer(player);
        JadedChat.setChannel(player, JadedChat.getDefaultChannel());

        if(gameState == GameState.WAITING || gameState == GameState.COUNTDOWN) {
            sendMessage("&f" + PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%") + player.getName() + " &ahas left the game! (&f"+ players.size() + "&a/&f" + arena.getMaxPlayers(teamSize) + "&a)");

            if(getPlayers().size() < arena.getMinPlayers() && gameState == GameState.COUNTDOWN) {
                sendMessage("&cNot enough players! Countdown reset.");
                gameCountdown.cancel();
                gameCountdown = new GameCountdown(plugin, this);
                gameState = GameState.WAITING;
            }

            if(getPlayers().size() == 0) {
                teamSize = 0;
            }
        }
        else {
            teamManager.getTeam(player).removePlayer(player);
        }
    }

    public void sendMessage(String message) {
        for(Player player : players) {
            ChatUtils.chat(player, message);
        }
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }
}