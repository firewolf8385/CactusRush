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
package net.jadedmc.cactusrush.player;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.GameDeathType;
import net.jadedmc.cactusrush.game.ability.Ability;
import net.jadedmc.cactusrush.game.team.TeamColor;
import net.jadedmc.cactusrush.utils.LevelUtils;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.player.JadedPlayer;
import net.jadedmc.jadedutils.chat.ChatUtils;
import net.jadedmc.jadedutils.player.CustomPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Manages and caches plugin data about a player.
 */
public class CactusPlayer implements CustomPlayer {
    // Object creation variables.
    private final CactusRushPlugin plugin;
    private final UUID playerUUID;

    // Data cache.
    private int coins = 0;
    private int experience = 0;
    private int level = 1;
    private String selectedAbility = "flash";
    private final List<String> unlockedAbilities = new ArrayList<>();
    private String primaryTeamColor = "NONE";
    private String secondaryTeamColor = "NONE";
    private final Collection<TeamColor> unlockedTeamColors = new HashSet<>();


    // Statistics
    // Mode Stats
    private final Map<String, Integer> modeWins = new HashMap<>();
    private final Map<String, Integer> modeLosses = new HashMap<>();
    private final Map<String, Integer> modeWinStreak = new HashMap<>();
    private final Map<String, Integer> modeLoseStreak = new HashMap<>();
    private final Map<String, Integer> modeBestWinStreak = new HashMap<>();
    private final Map<String, Integer> modeWorstLoseStreak = new HashMap<>();
    private final Map<String, Integer> modeCactiBroke = new HashMap<>();
    private final Map<String, Integer> modeCactiPlaced = new HashMap<>();
    private final Map<String, Integer> modeEggsThrown = new HashMap<>();
    private final Map<String, Integer> modeGoalsScored = new HashMap<>();
    private final Map<String, Integer> modeAbilitiesUsed = new HashMap<>();
    private final Map<String, Integer> modeGamesPlayed = new HashMap<>();
    private final Map<String, Integer> modeRoundsPlayed = new HashMap<>();
    private final Map<String, Integer> modeDeaths = new HashMap<>();
    private final Map<String, Integer> modeCactiDeaths = new HashMap<>();
    private final Map<String, Integer> modeVoidDeaths = new HashMap<>();
    private final Map<String, Integer> modeAbilityDeaths = new HashMap<>();
    private final Map<String, Integer> modePlayTime = new HashMap<>();

    // Arena Stats
    private final Map<String, Integer> arenaWins = new HashMap<>();
    private final Map<String, Integer> arenaLosses = new HashMap<>();
    private final Map<String, Integer> arenaWinStreak = new HashMap<>();
    private final Map<String, Integer> arenaLoseStreak = new HashMap<>();
    private final Map<String, Integer> arenaBestWinStreak = new HashMap<>();
    private final Map<String, Integer> arenaWorstLoseStreak = new HashMap<>();
    private final Map<String, Integer> arenaCactiBroke = new HashMap<>();
    private final Map<String, Integer> arenaCactiPlaced = new HashMap<>();
    private final Map<String, Integer> arenaEggsThrown = new HashMap<>();
    private final Map<String, Integer> arenaGoalsScored = new HashMap<>();
    private final Map<String, Integer> arenaAbilitiesUsed = new HashMap<>();
    private final Map<String, Integer> arenaGamesPlayed = new HashMap<>();
    private final Map<String, Integer> arenaRoundsPlayed = new HashMap<>();
    private final Map<String, Integer> arenaDeaths = new HashMap<>();
    private final Map<String, Integer> arenaCactiDeaths = new HashMap<>();
    private final Map<String, Integer> arenaVoidDeaths = new HashMap<>();
    private final Map<String, Integer> arenaAbilityDeaths = new HashMap<>();
    private final Map<String, Integer> arenaPlayTime = new HashMap<>();

    // Abilities
    private final Map<String, Integer> abilityUses = new HashMap<>();
    private final Map<String, Integer> abilityRoundsUsed = new HashMap<>();
    private int deathballKills = 0;

    // Coins
    private int lifetimeCoinsEarned = 0;
    private int mostCoinsAtOnce = 0;
    private int lifetimeCoinsSpent = 0;


    /**
     * Creates the Cactus Player
     * @param plugin Instance of the plugin.
     * @param playerUUID UUID of the Player to cache data for.
     */
    public CactusPlayer(@NotNull final CactusRushPlugin plugin, @NotNull final UUID playerUUID) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;

        try {
            // cactus_rush_players
            {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("SELECT * from cactus_rush_players WHERE uuid = ? LIMIT 1");
                statement.setString(1, playerUUID.toString());
                final ResultSet results = statement.executeQuery();

                if(results.next()) {
                    coins = results.getInt("coins");
                    level = results.getInt("level");
                    experience = results.getInt("experience");
                    selectedAbility = results.getString("selectedAbility");
                }
                else {
                    final PreparedStatement insert = JadedAPI.getMySQL().getConnection().prepareStatement("INSERT INTO cactus_rush_players (uuid) VALUES (?)");
                    insert.setString(1, playerUUID.toString());
                    insert.executeUpdate();
                }
            }

            // cactus_rush_abilities
            {
                final PreparedStatement retrieve = JadedAPI.getMySQL().getConnection().prepareStatement("SELECT * from cactus_rush_abilities WHERE uuid = ?");
                retrieve.setString(1, playerUUID.toString());
                final ResultSet resultSet = retrieve.executeQuery();

                while(resultSet.next()) {
                    unlockedAbilities.add(resultSet.getString(2));
                }
            }

            // Cosmetics
            {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("SELECT * from cactus_rush_cosmetics WHERE uuid = ? LIMIT 1");
                statement.setString(1, playerUUID.toString());
                final ResultSet results = statement.executeQuery();

                if(results.next()) {
                    primaryTeamColor = results.getString("primaryTeamColor");
                    secondaryTeamColor = results.getString("secondaryTeamColor");
                }
                else {
                    final PreparedStatement insert = JadedAPI.getMySQL().getConnection().prepareStatement("INSERT INTO cactus_rush_cosmetics (uuid) VALUES (?)");
                    insert.setString(1, playerUUID.toString());
                    insert.executeUpdate();
                }
            }

            // cactus_rush_team_colors
            {
                final PreparedStatement retrieve = JadedAPI.getMySQL().getConnection().prepareStatement("SELECT * from cactus_rush_team_colors WHERE uuid = ?");
                retrieve.setString(1, playerUUID.toString());
                final ResultSet resultSet = retrieve.executeQuery();

                while(resultSet.next()) {
                    unlockedTeamColors.add(TeamColor.valueOf(resultSet.getString(2)));
                }
            }

            // Load mode statistics.
            {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("SELECT * from cactus_rush_mode_statistics WHERE uuid = ?");
                statement.setString(1, playerUUID.toString());
                final ResultSet results = statement.executeQuery();

                while (results.next()) {
                    String mode = results.getString("mode");
                    modeWins.put(mode, results.getInt("wins"));
                    modeLosses.put(mode, results.getInt("losses"));
                    modeWinStreak.put(mode, results.getInt("winStreak"));
                    modeLoseStreak.put(mode, results.getInt("loseStreak"));
                    modeBestWinStreak.put(mode, results.getInt("bestWinStreak"));
                    modeWorstLoseStreak.put(mode, results.getInt("worstLoseStreak"));
                    modeCactiBroke.put(mode, results.getInt("cactiBroke"));
                    modeCactiPlaced.put(mode, results.getInt("cactiPlaced"));
                    modeEggsThrown.put(mode, results.getInt("eggsThrown"));
                    modeGoalsScored.put(mode, results.getInt("goalsScored"));
                    modeAbilitiesUsed.put(mode, results.getInt("abilitiesUsed"));
                    modeGamesPlayed.put(mode, results.getInt("gamesPlayed"));
                    modeRoundsPlayed.put(mode, results.getInt("roundsPlayed"));
                    modeDeaths.put(mode, results.getInt("deaths"));
                    modeCactiDeaths.put(mode, results.getInt("cactiDeaths"));
                    modeVoidDeaths.put(mode, results.getInt("voidDeaths"));
                    modeAbilityDeaths.put(mode, results.getInt("abilityDeaths"));
                    modePlayTime.put(mode, results.getInt("playTime"));
                }
            }

            // Load arena statistics.
            {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("SELECT * from cactus_rush_arena_statistics WHERE uuid = ?");
                statement.setString(1, playerUUID.toString());
                final ResultSet results = statement.executeQuery();

                while (results.next()) {
                    String arena = results.getString("arena");
                    arenaWins.put(arena, results.getInt("wins"));
                    arenaLosses.put(arena, results.getInt("losses"));
                    arenaWinStreak.put(arena, results.getInt("winStreak"));
                    arenaLoseStreak.put(arena, results.getInt("loseStreak"));
                    arenaBestWinStreak.put(arena, results.getInt("bestWinStreak"));
                    arenaWorstLoseStreak.put(arena, results.getInt("worstLoseStreak"));
                    arenaCactiBroke.put(arena, results.getInt("cactiBroke"));
                    arenaCactiPlaced.put(arena, results.getInt("cactiPlaced"));
                    arenaEggsThrown.put(arena, results.getInt("eggsThrown"));
                    arenaGoalsScored.put(arena, results.getInt("goalsScored"));
                    arenaAbilitiesUsed.put(arena, results.getInt("abilitiesUsed"));
                    arenaGamesPlayed.put(arena, results.getInt("gamesPlayed"));
                    arenaRoundsPlayed.put(arena, results.getInt("roundsPlayed"));
                    arenaDeaths.put(arena, results.getInt("deaths"));
                    arenaCactiDeaths.put(arena, results.getInt("cactiDeaths"));
                    arenaVoidDeaths.put(arena, results.getInt("voidDeaths"));
                    arenaAbilityDeaths.put(arena, results.getInt("abilityDeaths"));
                    arenaPlayTime.put(arena, results.getInt("playTime"));
                }
            }

            // Load misc statistics.
            {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("SELECT * from cactus_rush_misc_statistics WHERE uuid = ? LIMIT 1");
                statement.setString(1, playerUUID.toString());
                final ResultSet results = statement.executeQuery();

                if(results.next()) {
                    lifetimeCoinsEarned = results.getInt("lifetimeCoinsEarned");
                    lifetimeCoinsSpent = results.getInt("lifetimeCoinsSpent");
                    mostCoinsAtOnce = results.getInt("mostCoinsAtOnce");
                }
                else {
                    final PreparedStatement insert = JadedAPI.getMySQL().getConnection().prepareStatement("INSERT INTO cactus_rush_misc_statistics (uuid) VALUES (?)");
                    insert.setString(1, playerUUID.toString());
                    insert.executeUpdate();
                }
            }
        }
        catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Adds to the ability used counters for an ability.
     * @param mode Mode ability was used in.
     * @param arena Arena ability was used in.
     * @param ability Ability that was used.
     */
    public void addAbilityUse(final String mode, final String arena, final String ability) {
        setModeAbilitiesUsed(mode, getModeAbilitiesUsed(mode) + 1);
        setModeAbilitiesUsed("overall", getModeAbilitiesUsed("overall") + 1);
        setArenaAbilitiesUsed(arena, getArenaAbilitiesUsed(ability) + 1);
        setAbilityUses(ability, getAbilityUses(ability) + 1);
    }

    /**
     * Add to the cacti broke counter.
     * @param mode Mode to add to the counter in.
     * @param arena Arena to add to the counter in.
     */
    public void addCactiBroke(final String mode, final String arena) {
        setArenaCactiBroke(arena, getArenaCactiBroke(arena) + 1);
        setModeCactiBroke(mode, getModeCactiBroke(mode) + 1);
        setModeCactiBroke("overall", getModeCactiBroke("overall") + 1);
    }

    /**
     * Add to the cacti placed counter.
     * @param mode Mode to add to the counter in.
     * @param arena Arena to add to the counter in.
     */
    public void addCactiPlaced(final String mode, final String arena) {
        setArenaCactiPlaced(arena, getArenaCactiPlaced(arena) + 1);
        setModeCactiPlaced(mode, getModeCactiPlaced(mode) + 1);
        setModeCactiPlaced("overall", getModeCactiPlaced("overall") + 1);
    }

    /**
     * Gives coins to the player.
     * @param coins Number of coins the player should gain.
     */
    public void addCoins(final int coins) {
        setCoins(getCoins() + coins);

        setLifetimeCoinsEarned(getLifetimeCoinsEarned() + coins);
        if(coins > getMostCoinsAtOnce()) setMostCoinsAtOnce(coins);
    }

    /**
     * Add coins to the player with a specific reason.
     * @param coins Amount of coins to add.
     * @param reason Reason for adding the coins.
     */
    public void addCoins(final int coins, final String reason) {
        addCoins(coins);
        ChatUtils.chat(getPlayer(), "<gold>+" + coins + " Cactus Rush Coins (" + reason + ")");
    }

    /**
     * Adds a death to the player.
     * @param mode Mode to add the death in.
     * @param arena Arena to add the death in.
     * @param reason Reason for the death.
     */
    public void addDeath(final String mode, final String arena, final GameDeathType reason) {
        setModeDeaths(mode, getModeDeaths(mode) + 1);
        setModeDeaths("overall", getModeDeaths("overall") + 1);
        setArenaDeaths(arena, getArenaDeaths(arena) + 1);

        switch (reason) {
            case ABILITY -> {
                setArenaAbilityDeaths(arena, getArenaAbilityDeaths(arena) + 1);
                setModeAbilityDeaths(mode, getModeAbilityDeaths(mode) + 1);
                setModeAbilityDeaths("overall", getModeAbilityDeaths("overall") + 1);
            }

            case CACTUS -> {
                setArenaCactiDeaths(arena, getArenaCactiDeaths(arena) + 1);
                setModeCactiDeaths(mode, getModeCactiDeaths(mode) + 1);
                setModeCactiDeaths("overall", getModeCactiDeaths("overall") + 1);
            }

            case VOID -> {
                setArenaVoidDeaths(arena, getArenaVoidDeaths(arena) + 1);
                setModeVoidDeaths(mode, getModeVoidDeaths(mode) + 1);
                setModeVoidDeaths("overall", getModeVoidDeaths("overall") + 1);
            }
        }
    }

    /**
     * Add a deathball kill to the player.
     */
    public void addDeathballKill() {
        setDeathballKills(getDeathballKills() + 1);
    }

    /**
     * Add to the player's eggs thrown counters.
     * @param mode Mode to add to the counter in.
     * @param arena Arena to add to the counter in.
     */
    public void addEggsThrown(final String mode, final String arena) {
        setArenaEggsThrown(arena, getArenaEggsThrown(arena) + 1);
        setModeEggsThrown(mode, getModeEggsThrown(mode) + 1);
        setModeEggsThrown("overall", getModeEggsThrown("overall") + 1);
    }

    /**
     * Adds experience to the player.
     * @param experience Amount of experience to add.
     */
    public void addExperience(final int experience) {
        setExperience(getExperience() + experience);
        final int required = LevelUtils.getRequiredExperience(level);

        // If the player has enough experience, move up to the next level.
        if(getExperience() >= required) {
            setLevel(getLevel() + 1);
            setExperience(getExperience() - required);

            final Player player = getPlayer();

            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.chat(player, ChatUtils.centerText("&b&lLevel Up"));
            ChatUtils.chat(player, "");
            ChatUtils.chat(player, ChatUtils.centerText("&3&k# &bYou are now Cactus Rush Level " + level + " &3&k#"));
            ChatUtils.chat(player, "");
            ChatUtils.chat(player, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }
    }

    /**
     * Add to the games played counter.
     * @param mode Mode the game was played in.
     * @param arena Arena the game was played in.
     */
    public void addGamePlayed(final String mode, final String arena) {
        setModeGamesPlayed(mode, getModeGamesPlayed(mode) + 1);
        setModeGamesPlayed("overall", getModeGamesPlayed("overall") + 1);
        setArenaGamesPlayed(arena, getArenaGamesPlayed(arena) + 1);

        // Check for teh "Well-Rounded" achievement.
        if(getModeGamesPlayed("1v1") > 0 && getModeGamesPlayed("2v2") > 0 && getModeGamesPlayed("3v3") > 0 && getModeGamesPlayed("4v4") > 0) {
            JadedAPI.getPlugin().achievementManager().getAchievement("cactus_rush_8").unlock(plugin.getServer().getPlayer(playerUUID));
        }
    }

    /**
     * Add to the player's goals scored counters.
     * @param mode Mode to add to the counter in.
     * @param arena Arena to add to the counter in.
     */
    public void addGoalsScored(final String mode, final String arena) {
        setArenaGoalsScored(arena, getArenaGoalsScored(arena) + 1);
        setModeGoalsScored(mode, getModeGoalsScored(mode) + 1);
        setModeGoalsScored("overall", getModeGoalsScored("overall") + 1);
    }

    /**
     * Adds a loss to the player.
     * Automatically processes win streaks and lose streaks.
     * @param mode Mode the loss was earned in.
     * @param arena Arena the loss was earned in.
     */
    public void addLoss(final String mode, final String arena) {
        // Losses
        setModeLosses(mode, getModeLosses(mode) + 1);
        setModeLosses("overall", getModeLosses("overall") + 1);
        setArenaLosses(arena, getArenaLosses(arena) + 1);

        // Lose streaks
        setModeLoseStreak(mode, getModeLoseStreak(mode) + 1);
        setModeLoseStreak("overall", getModeLoseStreak("overall") + 1);
        setArenaLoseStreak(arena, getArenaLoseStreak(arena) + 1);

        if(getModeLoseStreak(mode) > getModeWorstLoseStreak(mode)) setModeWorstLoseStreak(mode, getModeLoseStreak(mode));
        if(getModeLoseStreak("overall") > getModeWorstLoseStreak("overall")) setModeWorstLoseStreak("overall", getModeLoseStreak("overall"));
        if(getArenaLoseStreak(arena) > getArenaWorstLoseStreak(arena)) setArenaWorstLoseStreak(arena, getArenaLoseStreak(arena));

        // Win streaks
        setModeWinStreak(mode, 0);
        setModeWinStreak("overall", 0);
        setArenaWinStreak(arena, 0);
    }

    /**
     * Adds to the play time trackers.
     * @param mode Mode the play time is in.
     * @param arena Arena the play time is in.
     * @param playTime Additional play time.
     */
    public void addPlayTime(final String mode, final String arena, final int playTime) {
        setArenaPlayTime(arena, getArenaPlayTime(arena) + playTime);
        setModePlayTime(mode, getModePlayTime(mode) + playTime);
        setModePlayTime("overall", getModePlayTime("overall") + playTime);
    }

    /**
     * Add to the rounds played counter.
     * @param mode Mode the round was played in.
     * @param arena Arena the round was played in.
     * @param ability The ability used in the round.
     */
    public void addRoundPlayed(final String mode, final String arena, final String ability) {
        setModeRoundsPlayed(mode, getModeRoundsPlayed(mode) + 1);
        setModeRoundsPlayed("overall", getModeRoundsPlayed("overall") + 1);
        setArenaRoundsPlayed(arena, getArenaRoundsPlayed(arena) + 1);
        setAbilityRoundsUsed(ability, getAbilityRoundsUsed(ability) + 1);
    }

    /**
     * Adds a win to the player.
     * Automatically processes win streaks and lose streaks.
     * @param mode Mode the win was earned in.
     * @param arena Arena the win was earned in.
     */
    public void addWin(final String mode, final String arena) {
        // Wins
        setModeWins(mode, getModeWins(mode) + 1);
        setModeWins("overall", getModeWins("overall") + 1);
        setArenaWins(arena, getArenaWins(arena) + 1);

        // Win streaks
        setModeWinStreak(mode, getModeWinStreak(mode) + 1);
        setModeWinStreak("overall", getModeWinStreak("overall") + 1);
        setArenaWinStreak(arena, getArenaWinStreak(arena) + 1);

        if(getModeWinStreak(mode) > getModeBestWinStreak(mode)) setModeBestWinStreak(mode, getModeWinStreak(mode));
        if(getModeWinStreak("overall") > getModeBestWinStreak("overall")) setModeBestWinStreak("overall", getModeWinStreak("overall"));
        if(getArenaWinStreak(arena) > getArenaBestWinStreak(arena)) setArenaBestWinStreak(arena, getArenaWinStreak(arena));

        // Lose streaks
        setModeLoseStreak(mode, 0);
        setModeLoseStreak("overall", 0);
        setArenaLoseStreak(arena, 0);
    }

    /**
     * Get the number of rounds the player has used a given ability.
     * @param ability Ability to get number of rounds used.
     * @return Number of rounds the player has used that ability.
     */
    public int getAbilityRoundsUsed(final String ability) {
        if(abilityRoundsUsed.containsKey(ability)) {
            return abilityRoundsUsed.get(ability);
        }

        return 0;
    }

    /**
     * Update the number of rounds the player has used a given ability.
     * @param ability Ability to update number of rounds used.
     * @param roundsUsed New number of rounds the player has used that ability.
     */
    private void getAbilityRoundsUsed(final String ability, final int roundsUsed) {
        abilityRoundsUsed.put(ability, roundsUsed);
    }

    /**
     * Get the number of times the player has used a given ability.
     * @param ability Ability to get the uses of.
     * @return Number of times the player has used that ability.
     */
    public int getAbilityUses(final String ability) {
        if(abilityUses.containsKey(ability)) {
            return abilityUses.get(ability);
        }

        return 0;
    }

    /**
     * Update the number of times the player has used a given ability.
     * @param ability Ability to update the number of uses for.
     * @param uses New number of times the player has used that ability.
     */
    private void getAbilityUses(final String ability, final int uses) {
        abilityUses.put(ability, uses);
    }

    /**
     * Get the number of times the player has died to an ability in a given arena.
     * @param arena Arena to get the number of ability deaths in.
     * @return New number of times the player has died to an ability in that arena.
     */
    public int getArenaAbilityDeaths(final String arena) {
        if(arenaAbilityDeaths.containsKey(arena)) {
            return arenaAbilityDeaths.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of abilities the player has used in a given arena.
     * @param arena Arena to get number of abilities used in.
     * @return Number of abilities the player has used in that arena.
     */
    public int getArenaAbilitiesUsed(final String arena) {
        if(arenaAbilitiesUsed.containsKey(arena)) {
            return arenaAbilitiesUsed.get(arena);
        }

        return 0;
    }

    /**
     * Get the player's best win streak in a given arena.
     * @param arena Arena to get best win streak in.
     * @return Player's best win streak in that arena.
     */
    public int getArenaBestWinStreak(final String arena) {
        if(arenaBestWinStreak.containsKey(arena)) {
            return arenaBestWinStreak.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of cacti the player has broken in a specific arena.
     * @param arena Arena to get number of cacti broken in.
     * @return The number of cacti the player has broken in that arena.
     */
    public int getArenaCactiBroke(final String arena) {
        if(arenaCactiBroke.containsKey(arena)) {
            return arenaCactiBroke.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of times the player has died to a cactus in a given arena.
     * @param arena Arena to get the number of cactus deaths in.
     * @return Number of times the player has died to a cactus in that arena.
     */
    public int getArenaCactiDeaths(final String arena) {
        if(arenaCactiDeaths.containsKey(arena)) {
            return arenaCactiDeaths.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of cacti the player has placed in a given arena.
     * @param arena Arena to get number of cacti placed in.
     * @return Number of cacti the player has placed in that arena.
     */
    public int getArenaCactiPlaced(final String arena) {
        if(arenaCactiPlaced.containsKey(arena)) {
            return arenaCactiPlaced.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of deaths the player has had in a given arena.
     * @param arena Arena to get the number of deaths in.
     * @return Number of times the player has died in that arena.
     */
    public int getArenaDeaths(final String arena) {
        if(arenaDeaths.containsKey(arena)) {
            return arenaDeaths.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of eggs the player has thrown in a given arena.
     * @param arena Arena to get the number of eggs thrown in.
     * @return Number of eggs the player has thrown in the arena.
     */
    public int getArenaEggsThrown(final String arena) {
        if(arenaEggsThrown.containsKey(arena)) {
            return arenaEggsThrown.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of games the player has played in a given arena.
     * @param arena Arena to get the number of games played in.
     * @return The number of games the player has played in that arena.
     */
    public int getArenaGamesPlayed(final String arena) {
        if(arenaGamesPlayed.containsKey(arena)) {
            return arenaGamesPlayed.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of goals the player has scored in a given arena.
     * @param arena Arena to get the number of goals scored in.
     * @return Number of goals the player has scored in the given arena.
     */
    public int getArenaGoalsScored(final String arena) {
        if(arenaGoalsScored.containsKey(arena)) {
            return arenaGoalsScored.get(arena);
        }

        return 0;
    }

    /**
     * Get the player's current lose streak in a given arena.
     * @param arena Arena to get lose streak in.
     * @return Player's lose streak in that arena.
     */
    public int getArenaLoseStreak(final String arena) {
        if(arenaLoseStreak.containsKey(arena)) {
            return arenaLoseStreak.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of losses the player has in a specific arena.
     * @param arena Arena to get losses in.
     * @return The number of losses they have.
     */
    public int getArenaLosses(final String arena) {
        if(arenaLosses.containsKey(arena)) {
            return arenaLosses.get(arena);
        }

        return 0;
    }

    /**
     * Get the amount of play time the player has in a given arena.
     * @param arena Arena to get the play time of.
     * @return Amount of play time the player has in that arena.
     */
    public int getArenaPlayTime(final String arena) {
        if(arenaPlayTime.containsKey(arena)) {
            return arenaPlayTime.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of rounds the player has played in a given arena.
     * @param arena Arena to get the number of rounds played in.
     * @return Number of rounds the player has played in that arena.
     */
    public int getArenaRoundsPlayed(final String arena) {
        if(arenaRoundsPlayed.containsKey(arena)) {
            return arenaRoundsPlayed.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of times the player has died to the void in a given arena.
     * @param arena Arena to get the number of void deaths in.
     * @return Number of times the player has died to the void in that arena.
     */
    public int getArenaVoidDeaths(final String arena) {
        if(arenaVoidDeaths.containsKey(arena)) {
            return arenaVoidDeaths.get(arena);
        }

        return 0;
    }

    /**
     * Get the player's current win streak in a given arena.
     * @param arena Arena to get win streak in.
     * @return Player's current win streak in that arena.
     */
    public int getArenaWinStreak(final String arena) {
        if(arenaWinStreak.containsKey(arena)) {
            return arenaWinStreak.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of wins the player has in a given arena.
     * @param arena Arena to get wins in.
     * @return Number of wins the player has.
     */
    public int getArenaWins(final String arena) {
        if(arenaWins.containsKey(arena)) {
            return arenaWins.get(arena);
        }

        return 0;
    }

    /**
     * Get the player's worst lose streak in a given arena.
     * @param arena Arena to get worst lose streak of.
     * @return Player's worst lose streak in that arena.
     */
    public int getArenaWorstLoseStreak(final String arena) {
        if(arenaWorstLoseStreak.containsKey(arena)) {
            return arenaWorstLoseStreak.get(arena);
        }

        return 0;
    }

    /**
     * Get the number of coins the player has.
     * @return Player's current amount of coins.
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Get the number of times the player has gotten a kill with the deathball ability.
     * @return Number of kills obtained with the deathball ability.
     */
    public int getDeathballKills() {
        return deathballKills;
    }

    /**
     * Gets the amount of Cactus Rush Experience the player has.
     * @return Amount of experience.
     */
    public int getExperience() {
        return experience;
    }

    /**
     * Get the JadedPlayer of the player.
     * @return Player's JadedPlayer.
     */
    public JadedPlayer getJadedPlayer() {
        return JadedAPI.getJadedPlayer(getPlayer());
    }

    /**
     * Gets the current Cactus Rush level of the player.
     * @return Cactus Rush level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get the number of coins the player has ever earned.
     * @return Number of coins earned in the player's lifetime.
     */
    public int getLifetimeCoinsEarned() {
        return lifetimeCoinsEarned;
    }

    /**
     * Get the number of coins the player has spent in total.
     * @return Number of coins the player has spent.
     */
    public int getLifetimeCoinsSpent() {
        return lifetimeCoinsSpent;
    }

    /**
     * Get the number of times the player has died to an ability in a given mode.
     * @param mode Mode to get the number of ability deaths in.
     * @return Number of times the player has died to an ability in that mode.
     */
    public int getModeAbilityDeaths(final String mode) {
        if(modeAbilityDeaths.containsKey(mode)) {
            return modeAbilityDeaths.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of abilities the player has used in a given mode.
     * @param mode Mode to get number of abilities used in.
     * @return Number of abilities the player has used in that mode.
     */
    public int getModeAbilitiesUsed(final String mode) {
        if(modeAbilitiesUsed.containsKey(mode)) {
            return modeAbilitiesUsed.get(mode);
        }

        return 0;
    }

    /**
     * Get the player's best win streak in a given mode.
     * @param mode Mode to get best win streak of.
     * @return Player's best win streak in that mode.
     */
    public int getModeBestWinStreak(final String mode) {
        if(modeBestWinStreak.containsKey(mode)) {
            return modeBestWinStreak.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of cacti the player has broken in a given mode.
     * @param mode Mode to get the number of cacti broken in.
     * @return The amount of cacti the player has broken in that mode.
     */
    public int getModeCactiBroke(final String mode) {
        if(modeCactiBroke.containsKey(mode)) {
            return modeCactiBroke.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of times the player has died to a cactus in a given mode.
     * @param mode Mode to get the number of cactus deaths in.
     * @return Number of times the player has died to a cactus in that mode.
     */
    public int getModeCactiDeaths(final String mode) {
        if(modeCactiDeaths.containsKey(mode)) {
            return modeCactiDeaths.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of cacti the player has placed in a specific mode.
     * @param mode Mode to get the number of cacti placed in.
     * @return Number of cacti the player has placed in that mode.
     */
    public int getModeCactiPlaced(final String mode) {
        if(modeCactiPlaced.containsKey(mode)) {
            return modeCactiPlaced.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of deaths the player has had in a given mode.
     * @param mode Mode to get the number of deaths in.
     * @return Number of times the player has died in that mode.
     */
    public int getModeDeaths(final String mode) {
        if(modeDeaths.containsKey(mode)) {
            return modeDeaths.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of eggs the player has thrown in a given mode.
     * @param mode Mode to get the number of eggs thrown in.
     * @return The number of eggs the player has thrown in that mode.
     */
    public int getModeEggsThrown(final String mode) {
        if(modeEggsThrown.containsKey(mode)) {
            return modeEggsThrown.get(mode);
        }

        return 0;
    }

    /**
     * Gets the number of games the player has played in a given mode.
     * @param mode Mode to get the number of games played in.
     * @return Number of games the player has played that mode.
     */
    public int getModeGamesPlayed(final String mode) {
        if(modeGamesPlayed.containsKey(mode)) {
            return modeGamesPlayed.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of goals the player has scored in a given mode.
     * @param mode Mode to get the number of goals scored in.
     * @return The number of goals the player has scored in that mode.
     */
    public int getModeGoalsScored(final String mode) {
        if(modeGoalsScored.containsKey(mode)) {
            return modeGoalsScored.get(mode);
        }

        return 0;
    }

    /**
     * Get the player's current lose streak in a given mode.
     * @param mode Mode to get lose streak in.
     * @return Player's lose streak in that mode.
     */
    public int getModeLoseStreak(final String mode) {
        if(modeLoseStreak.containsKey(mode)) {
            return modeLoseStreak.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of losses the player has in a given mode.
     * @param mode Mode to get losses in.
     * @return Number of losses in that mode.
     */
    public int getModeLosses(final String mode) {
        if(modeLosses.containsKey(mode)) {
            return modeLosses.get(mode);
        }

        return 0;
    }

    /**
     * Get the amount of play time the player has in a given mode.
     * @param mode Mode to get the play time of.
     * @return The amount of play time the player has in that mode.
     */
    public int getModePlayTime(final String mode) {
        if(modePlayTime.containsKey(mode)) {
            return modePlayTime.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of rounds the player has played in a given mode.
     * @param mode Mode to get the number of rounds played in.
     * @return Number of rounds the player has played in that mode.
     */
    public int getModeRoundsPlayed(final String mode) {
        if(modeRoundsPlayed.containsKey(mode)) {
            return modeRoundsPlayed.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of times the player has died to the void in a given mode.
     * @param mode Mode to get the number of void deaths in.
     * @return Number of times the player has died to the void in that mode.
     */
    public int getModeVoidDeaths(final String mode) {
        if(modeVoidDeaths.containsKey(mode)) {
            return modeVoidDeaths.get(mode);
        }

        return 0;
    }

    /**
     * Get the number of wins the player has in a given mode.
     * @param mode Mode to get wins of.
     * @return Number of wins the player has.
     */
    public int getModeWins(final String mode) {
        if(modeWins.containsKey(mode)) {
            return modeWins.get(mode);
        }

        return 0;
    }

    /**
     * Get the player's current win streak in a given mode.
     * @param mode Mode to get win streak in.
     * @return Player's win streak in that mode.
     */
    public int getModeWinStreak(final String mode) {
        if(modeWinStreak.containsKey(mode)) {
            return modeWinStreak.get(mode);
        }

        return 0;
    }

    /**
     * Get the player's worst lose streak in a given mode.
     * @param mode Mode to get worst lose streak in.
     * @return Player's worst lose streak in that mode.
     */
    public int getModeWorstLoseStreak(final String mode) {
        if(modeWorstLoseStreak.containsKey(mode)) {
            return modeWorstLoseStreak.get(mode);
        }

        return 0;
    }

    /**
     * Get the most coins the player has had at one time.
     * @return Most coins the player has had at once.
     */
    public int getMostCoinsAtOnce() {
        return mostCoinsAtOnce;
    }

    /**
     * Gets the name of the player.
     * @return Player name.
     */
    public String getName() {
        return Objects.requireNonNull(plugin.getServer().getPlayer(playerUUID)).getName();
    }

    /**
     * Get the player object being wrapped.
     * @return Player object.
     */
    public Player getPlayer() {
        return plugin.getServer().getPlayer(playerUUID);
    }

    /**
     * Gets the player's current primary TeamColor.
     * @return Player's primary TeamColor.
     */
    public TeamColor getPrimaryTeamColor() {
        return TeamColor.valueOf(primaryTeamColor);
    }

    /**
     * Get the player's secondary team color.
     * @return Player's secondary team color.
     */
    public TeamColor getSecondaryTeamColor() {
        return TeamColor.valueOf(secondaryTeamColor);
    }

    /**
     * Get the player's currently selected ability.
     * @return Selected ability.
     */
    public String getSelectedAbility() {
        return selectedAbility;
    }

    /**
     * Get the player's UUID.
     * @return Player's UUID.
     */
    public UUID getUniqueId() {
        return playerUUID;
    }

    /**
     * Gets a list of the abilities the player has unlocked.
     * @return List of the ids of abilities unlocked.
     */
    public List<String> getUnlockedAbilities() {
        return unlockedAbilities;
    }

    /**
     * Get the player's unlocked team colors.
     * @return Collection of unlocked team colors.
     */
    public Collection<TeamColor> getUnlockedTeamColors() {
        return unlockedTeamColors;
    }

    /**
     * Check if a player has selected a primary team color.
     * @return True if a primary team color has been selected, false if not.
     */
    public boolean hasPrimaryTeamColor() {
        return !primaryTeamColor.equalsIgnoreCase("NONE");
    }

    /**
     * Check if ap layer has selected a secondary team color.
     * @return True if a secondary team color has been selected, false if not.
     */
    public boolean hasSecondaryTeamColor() {
        return !secondaryTeamColor.equalsIgnoreCase("NONE");
    }

    /**
     * Remove coins from the player.
     * @param coins Number of coins to remove.
     */
    public void removeCoins(final int coins) {
        setCoins(getCoins() - coins);
    }

    /**
     * Update the number of rounds the player has used a given ability.
     * @param ability Ability to update number of rounds used.
     * @param roundsUsed New number of rounds the player has used that ability.
     */
    private void setAbilityRoundsUsed(final String ability, final int roundsUsed) {
        abilityRoundsUsed.put(ability, roundsUsed);
    }

    /**
     * Update the number of times the player has used a given ability.
     * @param ability Ability to update the number of uses for.
     * @param uses New number of times the player has used that ability.
     */
    private void setAbilityUses(final String ability, final int uses) {
        abilityUses.put(ability, uses);
    }

    /**
     * Update the number of times the player has died to an ability in a given arena.
     * @param arena Arena to update the number of ability deaths in.
     * @param abilityDeaths New number of times the player has died to an ability in that arena.
     */
    private void setArenaAbilityDeaths(final String arena, final int abilityDeaths) {
        arenaAbilityDeaths.put(arena, abilityDeaths);
    }

    /**
     * Update the number of abilities the player has used in a given arena.
     * @param arena Arena to update the number of abilities used in.
     * @param abilitiesUsed New number of abilities the player has used in that arena.
     */
    private void setArenaAbilitiesUsed(final String arena, final int abilitiesUsed) {
        arenaAbilitiesUsed.put(arena, abilitiesUsed);
    }

    /**
     * Update the player's best win streak in a given arena.
     * @param arena Arena to update best win streak in.
     * @param bestWinStreak Player's new best win streak in that arena.
     */
    private void setArenaBestWinStreak(final String arena, final int bestWinStreak) {
        arenaBestWinStreak.put(arena, bestWinStreak);
    }

    /**
     * Update the number of cacti the player has broken in a specific arena.
     * @param arena Arena to update the number of cacti broken in.
     * @param cactiBroke The new number of cacti the player has broken in that arena.
     */
    private void setArenaCactiBroke(final String arena, final int cactiBroke) {
        arenaCactiBroke.put(arena, cactiBroke);
    }

    /**
     * Update the number of times the player has died to a cactus in a given arena.
     * @param arena Arena to update the number of cactus deaths in.
     * @param cactiDeaths New number of times the player has died to a cactus in that arena.
     */
    private void setArenaCactiDeaths(final String arena, final int cactiDeaths) {
        arenaCactiDeaths.put(arena, cactiDeaths);
    }

    /**
     * Update the number of cacti placed the player has placed in a given arena.
     * @param arena Arena to update the number of cacti placed in.
     * @param cactiPlaced New number of cacti the player has placed in the given arena.
     */
    private void setArenaCactiPlaced(final String arena, final int cactiPlaced) {
        arenaCactiPlaced.put(arena, cactiPlaced);
    }

    /**
     * Update the number of deaths the player has had in a given arena.
     * @param arena Arena to update the number of deaths in.
     * @param deaths New number of times the player has died in that arena.
     */
    private void setArenaDeaths(final String arena, final int deaths) {
        arenaDeaths.put(arena, deaths);
    }

    /**
     * Update the number of eggs the player has thrown in a given arena.
     * @param arena Arena to update the number of eggs thrown in.
     * @param eggsThrown New number of eggs the player has thrown in a given arena.
     */
    private void setArenaEggsThrown(final String arena, final int eggsThrown) {
        arenaEggsThrown.put(arena, eggsThrown);
    }

    /**
     * Update the number of games the player has played in a given arena.
     * @param arena Arena to update the number of games played in.
     * @param gamesPlayed New number of games the player has played in that arena.
     */
    private void setArenaGamesPlayed(final String arena, final int gamesPlayed) {
        arenaGamesPlayed.put(arena, gamesPlayed);
    }

    /**
     * Update the number of goals the player has scored in a given arena.
     * @param arena Arena to update the number of goals scored in.
     * @param goalsScored New number of goals the player has scored in the given arena.
     */
    private void setArenaGoalsScored(final String arena, final int goalsScored) {
        arenaGoalsScored.put(arena, goalsScored);
    }

    /**
     * Update the player's current lose streak in a given arena.
     * @param arena Arena to update lose streak in.
     * @param loseStreak Player's new lose streak.
     */
    private void setArenaLoseStreak(final String arena, final int loseStreak) {
        arenaLoseStreak.put(arena, loseStreak);
    }

    /**
     * Update the player's losses in a specific arena.
     * @param arena Arena to update losses in.
     * @param losses New number of losses.
     */
    private void setArenaLosses(final String arena, final int losses) {
        arenaLosses.put(arena, losses);
    }

    /**
     * Update the amount of play time the player has in a given arena.
     * @param arena Arena to update the play time of.
     * @param playTime New amount of play time the player has in that arena.
     */
    private void setArenaPlayTime(final String arena, final int playTime) {
        arenaPlayTime.put(arena, playTime);
    }

    /**
     * Update the number of rounds the player has played in a given arena.
     * @param arena Arena to update the number of rounds played in.
     * @param roundsPlayed New number of rounds the player has played in that arena.
     */
    private void setArenaRoundsPlayed(final String arena, final int roundsPlayed) {
        arenaRoundsPlayed.put(arena, roundsPlayed);
    }

    /**
     * Update the number of times the player has died to the void in a given arena.
     * @param arena Arena to update the number of void deaths in.
     * @param voidDeaths New number of times the player has died to the void in that arena.
     */
    private void setArenaVoidDeaths(final String arena, final int voidDeaths) {
        arenaVoidDeaths.put(arena, voidDeaths);
    }

    /**
     * Update the player's current win streak in a given arena.
     * @param arena Arena to update win streak in.
     * @param winStreak Player's new win streak in that arena.
     */
    private void setArenaWinStreak(final String arena, final int winStreak) {
        arenaWinStreak.put(arena, winStreak);
    }

    /**
     * Update the number of wins the player has in a given arena.
     * @param arena Arena to set wins in.
     * @param wins New number of wins.
     */
    private void setArenaWins(final String arena, final int wins) {
        arenaWins.put(arena, wins);
    }

    /**
     * Update the player's worst lose streak in a given arena.
     * @param arena Arena to update worst lose streak in.
     * @param loseStreak Player's new worst lose streak in that mode.
     */
    private void setArenaWorstLoseStreak(final String arena, final int loseStreak) {
        arenaWorstLoseStreak.put(arena, loseStreak);
    }

    /**
     * Changes the stored amount of coins the player has.
     * @param coins New amount of coins the player has.
     */
    private void setCoins(final int coins) {
        this.coins = coins;

        // Update MySQL.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("UPDATE cactus_rush_players SET coins = ? WHERE uuid = ?");
                statement.setInt(1, coins);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Update the number of times the player has gottena kill with the deathball ability.
     * @param deathballKills New number of kills obtained with the deathball ability.
     */
    private void setDeathballKills(final int deathballKills) {
        this.deathballKills = deathballKills;

        // Update MySQL
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("UPDATE cactus_rush_misc_statistics SET deathballKills = ? WHERE uuid = ?");
                statement.setInt(1, deathballKills);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the amount of stored experience the player has.
     * @param experience New amount of experience the player has.
     */
    private void setExperience(final int experience) {
        this.experience = experience;

        // Update MySQL.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("UPDATE cactus_rush_players SET experience = ? WHERE uuid = ?");
                statement.setInt(1, experience);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the player's current Cactus Rush level.
     * @param level New level the player should be.
     */
    public void setLevel(final int level) {
        this.level = level;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_players SET level = ? WHERE uuid = ?");
                statement.setInt(1, level);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Update the number of coins the player has ever earned.
     * @param lifetimeCoinsEarned New number of coins earned in the player's lifetime.
     */
    public void setLifetimeCoinsEarned(final int lifetimeCoinsEarned) {
        this.lifetimeCoinsEarned = lifetimeCoinsEarned;

        // Update MySQL
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("UPDATE cactus_rush_misc_statistics SET lifetimeCoinsEarned = ? WHERE uuid = ?");
                statement.setInt(1, lifetimeCoinsEarned);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Update the number of coins the player has spent in total.
     * @param lifetimeCoinsSpent New number of coins the player has spent.
     */
    private void setLifetimeCoinsSpent(final int lifetimeCoinsSpent) {
        this.lifetimeCoinsSpent = lifetimeCoinsSpent;

        // Update MySQL
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("UPDATE cactus_rush_misc_statistics SET lifetimeCoinsSpent = ? WHERE uuid = ?");
                statement.setInt(1, lifetimeCoinsSpent);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Update the number of times the player has died to an ability in a given mode.
     * @param mode Mode to update the number of ability deaths in.
     * @param abilityDeaths New number of times the player has died to an ability in that mode.
     */
    private void setModeAbilityDeaths(final String mode, final int abilityDeaths) {
        modeAbilityDeaths.put(mode, abilityDeaths);
    }

    /**
     * Update the number of abilities the player has used in a given mode.
     * @param mode Mode to update the number of abilities used in.
     * @param abilitiesUsed New number of abilities the player has used in that mode.
     */
    private void setModeAbilitiesUsed(final String mode, final int abilitiesUsed) {
        modeAbilitiesUsed.put(mode, abilitiesUsed);
    }

    /**
     * Update the player's best win streak in a given mode.
     * @param mode Mode to update best win streak in.
     * @param bestWinStreak New best win streak in that mode.
     */
    private void setModeBestWinStreak(final String mode, final int bestWinStreak) {
        modeBestWinStreak.put(mode, bestWinStreak);
    }

    /**
     * Update the number of cacti the player has broken in a given mode.
     * @param mode Mode to update the number of cacti broken in.
     * @param cactiBroke The new number of cacti the player has broken in that mode.
     */
    private void setModeCactiBroke(final String mode, final int cactiBroke) {
        modeCactiBroke.put(mode, cactiBroke);
    }

    /**
     * Update the number of times the player has died to a cactus in a given mode.
     * @param mode Mode to update the number of cactus deaths in.
     * @param deaths New number of times the player has died to a cactus in that mode.
     */
    private void setModeCactiDeaths(final String mode, final int deaths) {
        modeCactiDeaths.put(mode, deaths);
    }

    /**
     * Update the number of cacti the player has placed in a specific mode.
     * @param mode Mode to update the number of cacti placed in.
     * @param cactiPlaced New number of cacti the player has placed in that mode.
     */
    private void setModeCactiPlaced(final String mode, final int cactiPlaced) {
        modeCactiPlaced.put(mode, cactiPlaced);
    }

    /**
     * Update the number of deaths player has had in a given mode.
     * @param mode Mode to update the number of deaths in.
     * @param deaths New number of times the player has died in that mode.
     */
    private void setModeDeaths(final String mode, final int deaths) {
        modeDeaths.put(mode, deaths);
    }

    /**
     * Update the number of eggs the player has thrown in a given mode.
     * @param mode Mode to update the number of eggs thrown in.
     * @param eggsThrown New number of eggs the player has thrown in a given mode.
     */
    private void setModeEggsThrown(final String mode, final int eggsThrown) {
        modeEggsThrown.put(mode, eggsThrown);
    }

    /**
     * Update the number of games the player has played in a given mode.
     * @param mode Mode to update the number of games played in.
     * @param gamesPlayed New number of games the player has played in that mode.
     */
    private void setModeGamesPlayed(final String mode, final int gamesPlayed) {
        modeGamesPlayed.put(mode, gamesPlayed);
    }

    /**
     * Update the number of goals the player has scored in a given mode.
     * @param mode The mode to update the number of goals scored in.
     * @param goalsScored New number of goals the player has scored in that mode.
     */
    private void setModeGoalsScored(final String mode, final int goalsScored) {
        modeGoalsScored.put(mode, goalsScored);
    }

    /**
     * Update the player's current lose streak in a given mode.
     * @param mode Mode to update lose streak in.
     * @param loseStreak New lose streak in that mode.
     */
    private void setModeLoseStreak(final String mode, final int loseStreak) {
        modeLoseStreak.put(mode, loseStreak);
    }

    /**
     * Update the amount of losses the player has in a mode.
     * @param mode Mode to update the losses of.
     * @param losses New number of losses.
     */
    private void setModeLosses(final String mode, final int losses) {
        modeLosses.put(mode, losses);
    }

    /**
     * Update the amount of play time the player has in a given mode.
     * @param mode Mode to update the amount of play time in.
     * @param playTime New amount of play time the player has in that mode.
     */
    private void setModePlayTime(final String mode, final int playTime) {
        modePlayTime.put(mode, playTime);
    }

    /**
     * Update the number of rounds the player has played in a given mode.
     * @param mode Mode to update the number of rounds played in.
     * @param roundsPlayed New number of rounds the player has played in that mode.
     */
    private void setModeRoundsPlayed(final String mode, final int roundsPlayed) {
        modeRoundsPlayed.put(mode, roundsPlayed);
    }

    /**
     * Update the number of times the player has died to the void in a given mode.
     * @param mode Mode to update the number of void deaths in.
     * @param voidDeaths New number of times the player has died to the void in that mode.
     */
    private void setModeVoidDeaths(final String mode, final int voidDeaths) {
        modeVoidDeaths.put(mode, voidDeaths);
    }

    /**
     * Update the amount of wins the player has in a mode.
     * @param mode Mode to update the wins of.
     * @param wins New number of wins.
     */
    private void setModeWins(final String mode, final int wins) {
        modeWins.put(mode, wins);
    }

    /**
     * Update the player's worst lose streak in a given mode.
     * @param mode Mode to update worst lose streak in.
     * @param loseStreak Player's new worst lose streak in that mode.
     */
    private void setModeWorstLoseStreak(final String mode, final int loseStreak) {
        modeWorstLoseStreak.put(mode, loseStreak);
    }

    /**
     * Update the player's current win streak in a specific mode.
     * @param mode Mode win streak is in.
     * @param winStreak New win streak.
     */
    private void setModeWinStreak(final String mode, final int winStreak) {
        modeWinStreak.put(mode, winStreak);
    }

    /**
     * Update the number of coins that player has had at most at one time.
     * @param mostCoinsAtOnce New number of coins that player has had at once.
     */
    private void setMostCoinsAtOnce(final int mostCoinsAtOnce) {
        this.mostCoinsAtOnce = mostCoinsAtOnce;

        // Update MySQL
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("UPDATE cactus_rush_misc_statistics SET mostCoinsAtOnce = ? WHERE uuid = ?");
                statement.setInt(1, mostCoinsAtOnce);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the player's primary team color.
     * @param teamColor New primary team color.
     */
    public void setPrimaryTeamColor(final TeamColor teamColor) {
        this.primaryTeamColor = teamColor.toString();

        // Update MySQL
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("UPDATE cactus_rush_cosmetics SET primaryTeamColor = ? WHERE uuid = ?");
                statement.setString(1, primaryTeamColor);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the player's secondary team color.
     * @param teamColor New secondary team color.
     */
    public void setSecondaryTeamColor(final TeamColor teamColor) {
        this.secondaryTeamColor = teamColor.toString();

        // Update MySQL
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("UPDATE cactus_rush_cosmetics SET secondaryTeamColor = ? WHERE uuid = ?");
                statement.setString(1, secondaryTeamColor);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Change the player's currently selected ability.
     * @param selectedAbility Newly selected ability.
     */
    public void setSelectedAbility(final String selectedAbility) {
        this.selectedAbility = selectedAbility;

        // Update MySQL
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_players SET selectedAbility = ? WHERE uuid = ?");
                statement.setString(1, selectedAbility);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Allows a player to use an ability.
     * @param ability Ability to unlock.
     */
    public void unlockAbility(final Ability ability) {
        unlockedAbilities.add(ability.getId());

        // Update MySQL
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("INSERT INTO cactus_rush_abilities (uuid,ability) VALUES (?,?)");
                statement.setString(1, playerUUID.toString());
                statement.setString(2, ability.getId());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Unlocks a team color for the player.
     * @param teamColor Team color to unlock
     */
    public void unlockTeamColor(final TeamColor teamColor) {
        this.unlockedTeamColors.add(teamColor);

        // Update MySQL
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("INSERT INTO cactus_rush_team_colors (uuid,teamColor) VALUES (?,?)");
                statement.setString(1, playerUUID.toString());
                statement.setString(2, teamColor.toString());
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Stores updated ability statistics to the database.
     * Stores all abilities at the same time.
     */
    public void updateAbilityStatistics() {
        for(final Ability ability : plugin.abilityManager().getAbilities()) {
            updateAbilityStatistics(ability.getId());
        }
    }

    /**
     * Store updated ability statistics to the database.
     * @param ability Ability to store statistics for.
     */
    public void updateAbilityStatistics(final String ability) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("REPLACE INTO cactus_rush_ability_statistics (uuid,ability,timesUsed,roundsUsed) VALUES (?,?,?,?)");
                statement.setString(1, playerUUID.toString());
                statement.setString(2, ability);
                statement.setInt(3, getAbilityUses(ability));
                statement.setInt(4, getAbilityRoundsUsed(ability));
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Store updated arena statistics to the database.
     * @param arena Arena to store statistics for.
     */
    public void updateArenaStatistics(final String arena) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("REPLACE INTO cactus_rush_arena_statistics (uuid,arena,wins,losses,winStreak,loseStreak,bestWinStreak,worstLoseStreak,cactiBroke,cactiPlaced,eggsThrown,goalsScored,abilitiesUsed,gamesPlayed,roundsPlayed,deaths,cactiDeaths,voidDeaths,abilityDeaths,playTime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                statement.setString(1, playerUUID.toString());
                statement.setString(2, arena);
                statement.setInt(3, getArenaWins(arena));
                statement.setInt(4, getArenaLosses(arena));
                statement.setInt(5, getArenaWinStreak(arena));
                statement.setInt(6, getArenaLoseStreak(arena));
                statement.setInt(7, getArenaBestWinStreak(arena));
                statement.setInt(8, getArenaWorstLoseStreak(arena));
                statement.setInt(9, getArenaCactiBroke(arena));
                statement.setInt(10, getArenaCactiPlaced(arena));
                statement.setInt(11, getArenaEggsThrown(arena));
                statement.setInt(12, getArenaGoalsScored(arena));
                statement.setInt(13, getArenaAbilitiesUsed(arena));
                statement.setInt(14, getArenaGamesPlayed(arena));
                statement.setInt(15, getArenaRoundsPlayed(arena));
                statement.setInt(16, getArenaDeaths(arena));
                statement.setInt(17, getArenaCactiDeaths(arena));
                statement.setInt(18, getArenaVoidDeaths(arena));
                statement.setInt(19, getArenaAbilityDeaths(arena));
                statement.setInt(20, getArenaPlayTime(arena));
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Store updated mode statistics to the database.
     * @param mode Arena to store statistics for.
     */
    public void updateModeStatistics(final String mode) {

        // Also update overall statistics so that I don't forget.
        if(!mode.equalsIgnoreCase("overall")) {
            updateModeStatistics("overall");
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getMySQL().getConnection().prepareStatement("REPLACE INTO cactus_rush_mode_statistics (uuid,mode,wins,losses,winStreak,loseStreak,bestWinStreak,worstLoseStreak,cactiBroke,cactiPlaced,eggsThrown,goalsScored,abilitiesUsed,gamesPlayed,roundsPlayed,deaths,cactiDeaths,voidDeaths,abilityDeaths,playTime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                statement.setString(1, playerUUID.toString());
                statement.setString(2, mode);
                statement.setInt(3, getModeWins(mode));
                statement.setInt(4, getModeLosses(mode));
                statement.setInt(5, getModeWinStreak(mode));
                statement.setInt(6, getModeLoseStreak(mode));
                statement.setInt(7, getModeBestWinStreak(mode));
                statement.setInt(8, getModeWorstLoseStreak(mode));
                statement.setInt(9, getModeCactiBroke(mode));
                statement.setInt(10, getModeCactiPlaced(mode));
                statement.setInt(11, getModeEggsThrown(mode));
                statement.setInt(12, getModeGoalsScored(mode));
                statement.setInt(13, getModeAbilitiesUsed(mode));
                statement.setInt(14, getModeGamesPlayed(mode));
                statement.setInt(15, getModeRoundsPlayed(mode));
                statement.setInt(16, getModeDeaths(mode));
                statement.setInt(17, getModeCactiDeaths(mode));
                statement.setInt(18, getModeVoidDeaths(mode));
                statement.setInt(19, getModeAbilityDeaths(mode));
                statement.setInt(20, getModePlayTime(mode));
                statement.executeUpdate();
            }
            catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}