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
package net.jadedmc.cactusrush.player;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.GameDeathType;
import net.jadedmc.cactusrush.game.abilitiy.Ability;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.jadedcore.JadedAPI;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks player gameplay statistics.
 * In a separate class because.... well look at the size of it.
 */
public class CactusPlayerStatisticsTracker {
    private final CactusRushPlugin plugin;
    private final UUID playerUUID;

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
     * Creates the statistic tracker.
     * @param plugin Instance of the plugin.
     * @param player The player statistics are being tracked for.
     */
    public CactusPlayerStatisticsTracker(CactusRushPlugin plugin, Player player) {
        this.plugin = plugin;
        this.playerUUID = player.getUniqueId();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Load mode statistics.
                {
                    PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("SELECT * from cactus_rush_mode_statistics WHERE uuid = ?");
                    statement.setString(1, playerUUID.toString());
                    ResultSet results = statement.executeQuery();

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
                    PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("SELECT * from cactus_rush_arena_statistics WHERE uuid = ?");
                    statement.setString(1, playerUUID.toString());
                    ResultSet results = statement.executeQuery();

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
                    PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("SELECT * from cactus_rush_misc_statistics WHERE uuid = ? LIMIT 1");
                    statement.setString(1, playerUUID.toString());
                    ResultSet results = statement.executeQuery();

                    if(results.next()) {
                        lifetimeCoinsEarned = results.getInt("lifetimeCoinsEarned");
                        lifetimeCoinsSpent = results.getInt("lifetimeCoinsSpent");
                        mostCoinsAtOnce = results.getInt("mostCoinsAtOnce");
                    }
                    else {
                        PreparedStatement insert = JadedAPI.getDatabase().prepareStatement("INSERT INTO cactus_rush_misc_statistics (uuid) VALUES (?)");
                        insert.setString(1, playerUUID.toString());
                        insert.executeUpdate();
                    }
                }
            }
            catch (SQLException exception) {
                ChatUtils.chat(player, "&cSomething went wrong loading your data! Relog to avoid potential issues.");
                exception.printStackTrace();
            }
        });
    }

    /**
     * Get the number of rounds the player has used a given ability.
     * @param ability Ability to get number of rounds used.
     * @return Number of rounds the player has used that ability.
     */
    public int abilityRoundsUsed(String ability) {
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
    private void abilityRoundsUsed(String ability, int roundsUsed) {
        abilityRoundsUsed.put(ability, roundsUsed);
    }

    /**
     * Get the number of times the player has used a given ability.
     * @param ability Ability to get the uses of.
     * @return Number of times the player has used that ability.
     */
    public int abilityUses(String ability) {
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
    private void abilityUses(String ability, int uses) {
        abilityUses.put(ability, uses);
    }

    /**
     * Adds to the ability used counters for an ability.
     * @param mode Mode ability was used in.
     * @param arena Arena ability was used in.
     * @param ability Ability that was used.
     */
    public void addAbilityUse(String mode, String arena, String ability) {
        modeAbilitiesUsed(mode, modeAbilitiesUsed(mode) + 1);
        modeAbilitiesUsed("overall", modeAbilitiesUsed("overall") + 1);
        arenaAbilitiesUsed(arena, arenaAbilitiesUsed(ability) + 1);
        abilityUses(ability, abilityUses(ability) + 1);
    }

    /**
     * Add to the cacti broke counter.
     * @param mode Mode to add to the counter in.
     * @param arena Arena to add to the counter in.
     */
    public void addCactiBroke(String mode, String arena) {
        arenaCactiBroke(arena, arenaCactiBroke(arena) + 1);
        modeCactiBroke(mode, modeCactiBroke(mode) + 1);
        modeCactiBroke("overall", modeCactiBroke("overall") + 1);
    }

    /**
     * Add to the cacti placed counter.
     * @param mode Mode to add to the counter in.
     * @param arena Arena to add to the counter in.
     */
    public void addCactiPlaced(String mode, String arena) {
        arenaCactiPlaced(arena, arenaCactiPlaced(arena) + 1);
        modeCactiPlaced(mode, modeCactiPlaced(mode) + 1);
        modeCactiPlaced("overall", modeCactiPlaced("overall") + 1);
    }

    /**
     * Adds a death to the player.
     * @param mode Mode to add the death in.
     * @param arena Arena to add the death in.
     * @param reason Reason for the death.
     */
    public void addDeath(String mode, String arena, GameDeathType reason) {
        modeDeaths(mode, modeDeaths(mode) + 1);
        modeDeaths("overall", modeDeaths("overall") + 1);
        arenaDeaths(arena, arenaDeaths(arena) + 1);

        switch (reason) {
            case ABILITY -> {
                arenaAbilityDeaths(arena, arenaAbilityDeaths(arena) + 1);
                modeAbilityDeaths(mode, modeAbilityDeaths(mode) + 1);
                modeAbilityDeaths("overall", modeAbilityDeaths("overall") + 1);
            }

            case CACTUS -> {
                arenaCactiDeaths(arena, arenaCactiDeaths(arena) + 1);
                modeCactiDeaths(mode, modeCactiDeaths(mode) + 1);
                modeCactiDeaths("overall", modeCactiDeaths("overall") + 1);
            }

            case VOID -> {
                arenaVoidDeaths(arena, arenaVoidDeaths(arena) + 1);
                modeVoidDeaths(mode, modeVoidDeaths(mode) + 1);
                modeVoidDeaths("overall", modeVoidDeaths("overall") + 1);
            }
        }
    }

    /**
     * Add a deathball kill to the player.
     */
    public void addDeathballKill() {
        deathballKills(deathballKills() + 1);
    }

    /**
     * Add to the player's eggs thrown counters.
     * @param mode Mode to add to the counter in.
     * @param arena Arena to add to the counter in.
     */
    public void addEggsThrown(String mode, String arena) {
        arenaEggsThrown(arena, arenaEggsThrown(arena) + 1);
        modeEggsThrown(mode, modeEggsThrown(mode) + 1);
        modeEggsThrown("overall", modeEggsThrown("overall") + 1);
    }

    /**
     * Add to the games played counter.
     * @param mode Mode the game was played in.
     * @param arena Arena the game was played in.
     */
    public void addGamePlayed(String mode, String arena) {
        modeGamesPlayed(mode, modeGamesPlayed(mode) + 1);
        modeGamesPlayed("overall", modeGamesPlayed("overall") + 1);
        arenaGamesPlayed(arena, arenaGamesPlayed(arena) + 1);
    }

    /**
     * Add to the player's goals scored counters.
     * @param mode Mode to add to the counter in.
     * @param arena Arena to add to the counter in.
     */
    public void addGoalsScored(String mode, String arena) {
        arenaGoalsScored(arena, arenaGoalsScored(arena) + 1);
        modeGoalsScored(mode, modeGoalsScored(mode) + 1);
        modeGoalsScored("overall", modeGoalsScored("mode") + 1);
    }

    /**
     * Adds a loss to the player.
     * Automatically processes win streaks and lose streaks.
     * @param mode Mode the loss was earned in.
     * @param arena Arena the loss was earned in.
     */
    public void addLoss(String mode, String arena) {
        // Losses
        modeLosses(mode, modeLosses(mode) + 1);
        modeLosses("overall", modeLosses("overall") + 1);
        arenaLosses(arena, arenaLosses(arena) + 1);

        // Lose streaks
        modeLoseStreak(mode, modeLoseStreak(mode) + 1);
        modeLoseStreak("overall", modeLoseStreak("overall") + 1);
        arenaLoseStreak(arena, arenaLoseStreak(arena) + 1);

        if(modeLoseStreak(mode) > modeWorstLoseStreak(mode)) modeWorstLoseStreak(mode, modeLoseStreak(mode));
        if(modeLoseStreak("overall") > modeWorstLoseStreak("overall")) modeWorstLoseStreak("overall", modeLoseStreak("overall"));
        if(arenaLoseStreak(arena) > arenaWorstLoseStreak(arena)) arenaWorstLoseStreak(arena, arenaLoseStreak(arena));

        // Win streaks
        modeWinStreak(mode, 0);
        modeWinStreak("overall", 0);
        arenaWinStreak(arena, 0);
    }

    /**
     * Adds to the play time trackers.
     * @param mode Mode the play time is in.
     * @param arena Arena the play time is in.
     * @param playTime Additional play time.
     */
    public void addPlayTime(String mode, String arena, int playTime) {
        arenaPlayTime(arena, arenaPlayTime(arena) + playTime);
        modePlayTime(mode, modePlayTime(mode) + playTime);
    }

    /**
     * Add to the rounds played counter.
     * @param mode Mode the round was played in.
     * @param arena Arena the round was played in.
     * @param ability The ability used in the round.
     */
    public void addRoundPlayed(String mode, String arena, String ability) {
        modeRoundsPlayed(mode, modeRoundsPlayed(mode) + 1);
        modeRoundsPlayed("overall", modeRoundsPlayed("overall") + 1);
        arenaRoundsPlayed(arena, arenaRoundsPlayed(arena) + 1);
        abilityRoundsUsed(ability, abilityRoundsUsed(ability) + 1);
    }

    /**
     * Adds a win to the player.
     * Automatically processes win streaks and lose streaks.
     * @param mode Mode the win was earned in.
     * @param arena Arena the win was earned in.
     */
    public void addWin(String mode, String arena) {
        // Wins
        modeWins(mode, modeWins(mode) + 1);
        modeWins("overall", modeWins("overall") + 1);
        arenaWins(arena, arenaWins(arena) + 1);

        // Win streaks
        modeWinStreak(mode, modeWinStreak(mode) + 1);
        modeWinStreak("overall", modeWinStreak("overall") + 1);
        arenaWinStreak(arena, arenaWinStreak(arena) + 1);

        if(modeWinStreak(mode) > modeBestWinStreak(mode)) modeBestWinStreak(mode, modeWinStreak(mode));
        if(modeWinStreak("overall") > modeBestWinStreak("overall")) modeBestWinStreak("overall", modeWinStreak("overall"));
        if(arenaWinStreak(arena) > arenaBestWinStreak(arena)) arenaBestWinStreak(arena, arenaWinStreak(arena));

        // Lose streaks
        modeLoseStreak(mode, 0);
        modeLoseStreak("overall", 0);
        arenaLoseStreak(arena, 0);
    }

    /**
     * Get the number of times the player has died to an ability in a given arena.
     * @param arena Arena to get the number of ability deaths in.
     * @return New number of times the player has died to an ability in that arena.
     */
    public int arenaAbilityDeaths(String arena) {
        if(arenaAbilityDeaths.containsKey(arena)) {
            return arenaAbilityDeaths.get(arena);
        }

        return 0;
    }

    /**
     * Upadte the number of times the player has died to an ability in a given arena.
     * @param arena Arena to update the number of ability deaths in.
     * @param abilityDeaths New number of times the player has died to an ability in that arena.
     */
    private void arenaAbilityDeaths(String arena, int abilityDeaths) {
        arenaAbilityDeaths.put(arena, abilityDeaths);
    }

    /**
     * Get the number of abilities the player has used in a given arena.
     * @param arena Arena to get number of abilities used in.
     * @return Number of abilities the player has used in that arena.
     */
    public int arenaAbilitiesUsed(String arena) {
        if(arenaAbilitiesUsed.containsKey(arena)) {
            return arenaAbilitiesUsed.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of abilities the player has used in a given arena.
     * @param arena Arena to update the number of abilities used in.
     * @param abilitiesUsed New number of abilities the player has used in that arena.
     */
    private void arenaAbilitiesUsed(String arena, int abilitiesUsed) {
        arenaAbilitiesUsed.put(arena, abilitiesUsed);
    }

    /**
     * Get the player's best win streak in a given arena.
     * @param arena Arena to get best win streak in.
     * @return Player's best win streak in that arena.
     */
    public int arenaBestWinStreak(String arena) {
        if(arenaBestWinStreak.containsKey(arena)) {
            return arenaBestWinStreak.get(arena);
        }

        return 0;
    }

    /**
     * Update the player's best win streak in a given arena.
     * @param arena Arena to update best win streak in.
     * @param bestWinStreak Player's new best win streak in that arena.
     */
    private void arenaBestWinStreak(String arena, int bestWinStreak) {
        arenaBestWinStreak.put(arena, bestWinStreak);
    }

    /**
     * Get the number of cacti the player has broken in a specific arena.
     * @param arena Arena to get number of cacti broken in.
     * @return The number of cacti the player has broken in that arena.
     */
    public int arenaCactiBroke(String arena) {
        if(arenaCactiBroke.containsKey(arena)) {
            return arenaCactiBroke.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of cacti the player has broken in a specific arena.
     * @param arena Arena to update the number of cacti broken in.
     * @param cactiBroke The new number of cacti the player has broken in that arena.
     */
    private void arenaCactiBroke(String arena, int cactiBroke) {
        arenaCactiBroke.put(arena, cactiBroke);
    }

    /**
     * Get the number of times the player has died to a cactus in a given arena.
     * @param arena Arena to get the number of cactus deaths in.
     * @return Number of times the player has died to a cactus in that arena.
     */
    public int arenaCactiDeaths(String arena) {
        if(arenaCactiDeaths.containsKey(arena)) {
            return arenaCactiDeaths.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of times the player has died to a cactus in a given arena.
     * @param arena Arena to update the number of cactus deaths in.
     * @param cactiDeaths New number of times the player has died to a cactus in that arena.
     */
    private void arenaCactiDeaths(String arena, int cactiDeaths) {
        arenaCactiDeaths.put(arena, cactiDeaths);
    }

    /**
     * Get the number of cacti the player has placed in a given arena.
     * @param arena Arena to get number of cacti placed in.
     * @return Number of cacti the player has placed in that arena.
     */
    public int arenaCactiPlaced(String arena) {
        if(arenaCactiPlaced.containsKey(arena)) {
            return arenaCactiPlaced.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of cacti placed the player has placed in a given arena.
     * @param arena Arena to update the number of cacti placed in.
     * @param cactiPlaced New number of cacti the player has placed in the given arena.
     */
    private void arenaCactiPlaced(String arena, int cactiPlaced) {
        arenaCactiPlaced.put(arena, cactiPlaced);
    }

    /**
     * Get the number of deaths the player has had in a given arena.
     * @param arena Arena to get the number of deaths in.
     * @return Number of times the player has died in that arena.
     */
    public int arenaDeaths(String arena) {
        if(arenaDeaths.containsKey(arena)) {
            return arenaDeaths.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of deaths the player has had in a given arena.
     * @param arena Arena to update the number of deaths in.
     * @param deaths New number of times the player has died in that arena.
     */
    private void arenaDeaths(String arena, int deaths) {
        arenaDeaths.put(arena, deaths);
    }

    /**
     * Get the number of eggs the player has thrown in a given arena.
     * @param arena Arena to get the number of eggs thrown in.
     * @return Number of eggs the player has thrown in the arena.
     */
    public int arenaEggsThrown(String arena) {
        if(arenaEggsThrown.containsKey(arena)) {
            return arenaEggsThrown.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of eggs the player has thrown in a given arena.
     * @param arena Arena to update the number of eggs thrown in.
     * @param eggsThrown New number of eggs the player has thrown in a given arena.
     */
    private void arenaEggsThrown(String arena, int eggsThrown) {
        arenaEggsThrown.put(arena, eggsThrown);
    }

    /**
     * Get the number of games the player has played in a given arena.
     * @param arena Arena to get the number of games played in.
     * @return The number of games the player has played in that arena.
     */
    public int arenaGamesPlayed(String arena) {
        if(arenaGamesPlayed.containsKey(arena)) {
            return arenaGamesPlayed.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of games the player has played in a given arena.
     * @param arena Arena to update the number of games played in.
     * @param gamesPlayed New number of games the player has played in that arena.
     */
    private void arenaGamesPlayed(String arena, int gamesPlayed) {
        arenaGamesPlayed.put(arena, gamesPlayed);
    }

    /**
     * Get the number of goals the player has scored in a given arena.
     * @param arena Arena to get the number of goals scored in.
     * @return Number of goals the player has scored in the given arena.
     */
    public int arenaGoalsScored(String arena) {
        if(arenaGoalsScored.containsKey(arena)) {
            return arenaGoalsScored.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of goals the player has scored in a given arena.
     * @param arena Arena to update the number of goals scored in.
     * @param goalsScored New number of goals the player has scored in the given arena.
     */
    private void arenaGoalsScored(String arena, int goalsScored) {
        arenaGoalsScored.put(arena, goalsScored);
    }

    /**
     * Get the player's current lose streak in a given arena.
     * @param arena Arena to get lose streak in.
     * @return Player's lose streak in that arena.
     */
    public int arenaLoseStreak(String arena) {
        if(arenaLoseStreak.containsKey(arena)) {
            return arenaLoseStreak.get(arena);
        }

        return 0;
    }

    /**
     * Update the player's current lose streak in a given arena.
     * @param arena Arena to update lose streak in.
     * @param loseStreak Player's new lose streak.
     */
    private void arenaLoseStreak(String arena, int loseStreak) {
        arenaLoseStreak.put(arena, loseStreak);
    }

    /**
     * Get the number of losses the player has in a specific arena.
     * @param arena Arena to get losses in.
     * @return The number of losses they have.
     */
    public int arenaLosses(String arena) {
        if(arenaLosses.containsKey(arena)) {
            return arenaLosses.get(arena);
        }

        return 0;
    }

    /**
     * Update the player's losses in a specific arena.
     * @param arena Arena to update losses in.
     * @param losses New number of losses.
     */
    private void arenaLosses(String arena, int losses) {
        arenaLosses.put(arena, losses);
    }

    /**
     * Get the amount of play time the player has in a given arena.
     * @param arena Arena to get the play time of.
     * @return Amount of play time the player has in that arena.
     */
    public int arenaPlayTime(String arena) {
        if(arenaPlayTime.containsKey(arena)) {
            return arenaPlayTime.get(arena);
        }

        return 0;
    }

    /**
     * Update the amount of play time the player has in a given arena.
     * @param arena Arena to update the play time of.
     * @param playTime New amount of play time the player has in that arena.
     */
    private void arenaPlayTime(String arena, int playTime) {
        arenaPlayTime.put(arena, playTime);
    }

    /**
     * Get the number of rounds the player has played in a given arena.
     * @param arena Arena to get the number of rounds played in.
     * @return Number of rounds the player has played in that arena.
     */
    public int arenaRoundsPlayed(String arena) {
        if(arenaRoundsPlayed.containsKey(arena)) {
            return arenaRoundsPlayed.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of rounds the player has played in a given arena.
     * @param arena Arena to update the number of rounds played in.
     * @param roundsPlayed New number of rounds the player has played in that arena.     */
    private void arenaRoundsPlayed(String arena, int roundsPlayed) {
        arenaRoundsPlayed.put(arena, roundsPlayed);
    }

    /**
     * Get the number of times the player has died to the void in a given arena.
     * @param arena Arena to get the number of void deaths in.
     * @return Number of times the player has died to the void in that arena.
     */
    public int arenaVoidDeaths(String arena) {
        if(arenaVoidDeaths.containsKey(arena)) {
            return arenaVoidDeaths.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of times the player has died to the void in a given arena.
     * @param arena Arena to update the number of void deaths in.
     * @param voidDeaths New number of times the player has died to the void in that arena.
     */
    private void arenaVoidDeaths(String arena, int voidDeaths) {
        arenaVoidDeaths.put(arena, voidDeaths);
    }

    /**
     * Get the player's current win streak in a given arena.
     * @param arena Arena to get win streak in.
     * @return Player's current win streak in that arena.
     */
    public int arenaWinStreak(String arena) {
        if(arenaWinStreak.containsKey(arena)) {
            return arenaWinStreak.get(arena);
        }

        return 0;
    }

    /**
     * Update the player's current win streak in a given arena.
     * @param arena Arena to update win streak in.
     * @param winStreak Player's new win streak in that arena.
     */
    private void arenaWinStreak(String arena, int winStreak) {
        arenaWinStreak.put(arena, winStreak);
    }

    /**
     * Get the number of wins the player has in a given arena.
     * @param arena Arena to get wins in.
     * @return Number of wins the player has.
     */
    public int arenaWins(String arena) {
        if(arenaWins.containsKey(arena)) {
            return arenaWins.get(arena);
        }

        return 0;
    }

    /**
     * Update the number of wins the player has in a given arena.
     * @param arena Arena to set wins in.
     * @param wins New number of wins.
     */
    private void arenaWins(String arena, int wins) {
        arenaWins.put(arena, wins);
    }

    /**
     * Get the player's worst lose streak in a given arena.
     * @param arena Arena to get worst lose streak of.
     * @return Player's worst lose streak in that arena.
     */
    public int arenaWorstLoseStreak(String arena) {
        if(arenaWorstLoseStreak.containsKey(arena)) {
            return arenaWorstLoseStreak.get(arena);
        }

        return 0;
    }

    /**
     * Update the player's worst lose streak in a given arena.
     * @param arena Arena to update worst lose streak in.
     * @param loseStreak Player's new worst lose streak in that mode.
     */
    private void arenaWorstLoseStreak(String arena, int loseStreak) {
        arenaWorstLoseStreak.put(arena, loseStreak);
    }

    /**
     * Get the number of times the player has gotten a kill with the deathball ability.
     * @return Number of kills obtained with the deathball ability.
     */
    public int deathballKills() {
        return deathballKills;
    }

    /**
     * Update the number of times the player has gottena kill with the deathball ability.
     * @param deathballKills New number of kills obtained with the deathball ability.
     */
    private void deathballKills(int deathballKills) {
        this.deathballKills = deathballKills;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_misc_statistics SET deathballKills = ? WHERE uuid = ?");
                statement.setInt(1, deathballKills);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Get the number of coins the player has ever earned.
     * @return Number of coins earned in the player's lifetime.
     */
    public int lifetimeCoinsEarned() {
        return lifetimeCoinsEarned;
    }

    /**
     * Update the number of coins the player has ever earned.
     * @param lifetimeCoinsEarned New number of coins earned in the player's lifetime.
     */
    void lifetimeCoinsEarned(int lifetimeCoinsEarned) {
        this.lifetimeCoinsEarned = lifetimeCoinsEarned;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_misc_statistics SET lifetimeCoinsEarned = ? WHERE uuid = ?");
                statement.setInt(1, lifetimeCoinsEarned);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Get the number of coins the player has spent in total.
     * @return Number of coins the player has spent.
     */
    public int lifetimeCoinsSpent() {
        return lifetimeCoinsSpent;
    }

    /**
     * Update the number of coins the player has spent in total.
     * @param lifetimeCoinsSpent New number of coins the player has spent.
     */
    private void lifetimeCoinsSpent(int lifetimeCoinsSpent) {
        this.lifetimeCoinsSpent = lifetimeCoinsSpent;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_misc_statistics SET lifetimeCoinsSpent = ? WHERE uuid = ?");
                statement.setInt(1, lifetimeCoinsSpent);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Get the number of times the player has died to an ability in a given mode.
     * @param mode Mode to get the number of ability deaths in.
     * @return Number of times the player has died to an ability in that mode.
     */
    public int modeAbilityDeaths(String mode) {
        if(modeAbilityDeaths.containsKey(mode)) {
            return modeAbilityDeaths.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of times the player has died to an ability in a given mode.
     * @param mode Mode to update the number of ability deaths in.
     * @param abilityDeaths New number of times the player has died to an ability in that mode.
     */
    private void modeAbilityDeaths(String mode, int abilityDeaths) {
        modeAbilityDeaths.put(mode, abilityDeaths);
    }

    /**
     * Get the number of abilities the player has used in a given mode.
     * @param mode Mode to get number of abilities used in.
     * @return Number of abilities the player has used in that mode.
     */
    public int modeAbilitiesUsed(String mode) {
        if(modeAbilitiesUsed.containsKey(mode)) {
            return modeAbilitiesUsed.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of abilities the player has used in a given mode.
     * @param mode Mode to update the number of abilities used in.
     * @param abilitiesUsed New number of abilities the player has used in that mode.
     */
    private void modeAbilitiesUsed(String mode, int abilitiesUsed) {
        modeAbilitiesUsed.put(mode, abilitiesUsed);
    }

    /**
     * Get the player's best win streak in a given mode.
     * @param mode Mode to get best win streak of.
     * @return Player's best win streak in that mode.
     */
    public int modeBestWinStreak(String mode) {
        if(modeBestWinStreak.containsKey(mode)) {
            return modeBestWinStreak.get(mode);
        }

        return 0;
    }

    /**
     * Update the player's best win streak in a given mode.
     * @param mode Mode to update best win streak in.
     * @param bestWinStreak New best win streak in that mode.
     */
    private void modeBestWinStreak(String mode, int bestWinStreak) {
        modeBestWinStreak.put(mode, bestWinStreak);
    }

    /**
     * Get the number of cacti the player has broken in a given mode.
     * @param mode Mode to get the number of cacti broken in.
     * @return The amount of cacti the player has broken in that mode.
     */
    public int modeCactiBroke(String mode) {
        if(modeCactiBroke.containsKey(mode)) {
            return modeCactiBroke.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of cacti the player has broken in a given mode.
     * @param mode Mode to update the number of cacti broken in.
     * @param cactiBroke The new number of cacti the player has broken in that mode.
     */
    private void modeCactiBroke(String mode, int cactiBroke) {
        modeCactiBroke.put(mode, cactiBroke);
    }

    /**
     * Get the number of times the player has died to a cactus in a given mode.
     * @param mode Mode to get the number of cactus deaths in.
     * @return Number of times the player has died to a cactus in that mode.
     */
    public int modeCactiDeaths(String mode) {
        if(modeCactiDeaths.containsKey(mode)) {
            return modeCactiDeaths.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of times the player has died to a cactus in a given mode.
     * @param mode Mode to update the number of cactus deaths in.
     * @param deaths New number of times the player has died to a cactus in that mode.
     */
    private void modeCactiDeaths(String mode, int deaths) {
        modeCactiDeaths.put(mode, deaths);
    }

    /**
     * Get the number of cacti the player has placed in a specific mode.
     * @param mode Mode to get the number of cacti placed in.
     * @return Number of cacti the player has placed in that mode.
     */
    public int modeCactiPlaced(String mode) {
        if(modeCactiPlaced.containsKey(mode)) {
            return modeCactiPlaced.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of cacti the player has placed in a specific mode.
     * @param mode Mode to update the number of cacti placed in.
     * @param cactiPlaced New number of cacti the player has placed in that mode.
     */
    private void modeCactiPlaced(String mode, int cactiPlaced) {
        modeCactiPlaced.put(mode, cactiPlaced);
    }

    /**
     * Get the number of deaths the player has had in a given mode.
     * @param mode Mode to get the number of deaths in.
     * @return Number of times the player has died in that mode.
     */
    public int modeDeaths(String mode) {
        if(modeDeaths.containsKey(mode)) {
            return modeDeaths.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of deaths player has had in a given mode.
     * @param mode Mode to update the number of deaths in.
     * @param deaths New number of times the player has died in that mode.
     */
    private void modeDeaths(String mode, int deaths) {
        modeDeaths.put(mode, deaths);
    }

    /**
     * Get the number of eggs the player has thrown in a given mode.
     * @param mode Mode to get the number of eggs thrown in.
     * @return The number of eggs the player has thrown in that mode.
     */
    public int modeEggsThrown(String mode) {
        if(modeEggsThrown.containsKey(mode)) {
            return modeEggsThrown.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of eggs the player has thrown in a given mode.
     * @param mode Mode to update the number of eggs thrown in.
     * @param eggsThrown New number of eggs the player has thrown in a given mode.
     */
    private void modeEggsThrown(String mode, int eggsThrown) {
        modeEggsThrown.put(mode, eggsThrown);
    }

    /**
     * Gets the number of games the player has played in a given mode.
     * @param mode Mode to get the number of games played in.
     * @return Number of games the player has played that mode.
     */
    public int modeGamesPlayed(String mode) {
        if(modeGamesPlayed.containsKey(mode)) {
            return modeGamesPlayed.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of games the player has played in a given mode.
     * @param mode Mode to update the number of games played in.
     * @param gamesPlayed New number of games the player has played in that mode.
     */
    private void modeGamesPlayed(String mode, int gamesPlayed) {
        modeGamesPlayed.put(mode, gamesPlayed);
    }

    /**
     * Get the number of goals the player has scored in a given mode.
     * @param mode Mode to get the number of goals scored in.
     * @return The number of goals the player has scored in that mode.
     */
    public int modeGoalsScored(String mode) {
        if(modeGoalsScored.containsKey(mode)) {
            return modeGoalsScored.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of goals the player has scored in a given mode.
     * @param mode The mode to update the number of goals scored in.
     * @param goalsScored New number of goals the player has scored in that mode.
     */
    private void modeGoalsScored(String mode, int goalsScored) {
        modeGoalsScored.put(mode, goalsScored);
    }

    /**
     * Get the player's current lose streak in a given mode.
     * @param mode Mode to get lose streak in.
     * @return Player's lose streak in that mode.
     */
    public int modeLoseStreak(String mode) {
        if(modeLoseStreak.containsKey(mode)) {
            return modeLoseStreak.get(mode);
        }

        return 0;
    }

    /**
     * Update the player's current lose streak in a given mode.
     * @param mode Mode to update lose streak in.
     * @param loseStreak New lose streak in that mode.
     */
    private void modeLoseStreak(String mode, int loseStreak) {
        modeLoseStreak.put(mode, loseStreak);
    }

    /**
     * Get the number of losses the player has in a given mode.
     * @param mode Mode to get losses in.
     * @return Number of losses in that mode.
     */
    public int modeLosses(String mode) {
        if(modeLosses.containsKey(mode)) {
            return modeLosses.get(mode);
        }

        return 0;
    }

    /**
     * Update the amount of losses the player has in a mode.
     * @param mode Mode to update the losses of.
     * @param losses New number of losses.
     */
    private void modeLosses(String mode, int losses) {
        modeLosses.put(mode, losses);
    }

    /**
     * Get the amount of play time the player has in a given mode.
     * @param mode Mode to get the play time of.
     * @return The amount of play time the player has in that mode.
     */
    public int modePlayTime(String mode) {
        if(modePlayTime.containsKey(mode)) {
            return modePlayTime.get(mode);
        }

        return 0;
    }

    /**
     * Update the amount of play time the player has in a given mode.
     * @param mode Mode to update the amount of play time in.
     * @param playTime New amount of play time the player has in that mode.
     */
    private void modePlayTime(String mode, int playTime) {
        modePlayTime.put(mode, playTime);
    }

    /**
     * Get the number of rounds the player has played in a given mode.
     * @param mode Mode to get the number of rounds played in.
     * @return Number of rounds the player has played in that mode.
     */
    public int modeRoundsPlayed(String mode) {
        if(modeRoundsPlayed.containsKey(mode)) {
            return modeRoundsPlayed.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of rounds the player has played in a given mode.
     * @param mode Mode to update the number of rounds played in.
     * @param roundsPlayed New number of rounds the player has played in that mode.
     */
    private void modeRoundsPlayed(String mode, int roundsPlayed) {
        modeRoundsPlayed.put(mode, roundsPlayed);
    }

    /**
     * Get the number of times the player has died to the void in a given mode.
     * @param mode Mode to get the number of void deaths in.
     * @return Number of times the player has died to the void in that mode.
     */
    public int modeVoidDeaths(String mode) {
        if(modeVoidDeaths.containsKey(mode)) {
            return modeVoidDeaths.get(mode);
        }

        return 0;
    }

    /**
     * Update the number of times the player has died to the void in a given mode.
     * @param mode Mode to update the number of void deaths in.
     * @param voidDeaths New number of times the player has died to the void in that mode.
     */
    private void modeVoidDeaths(String mode, int voidDeaths) {
        modeVoidDeaths.put(mode, voidDeaths);
    }

    /**
     * Get the number of wins the player has in a given mode.
     * @param mode Mode to get wins of.
     * @return Number of wins the player has.
     */
    public int modeWins(String mode) {
        if(modeWins.containsKey(mode)) {
            return modeWins.get(mode);
        }

        return 0;
    }

    /**
     * Update the amount of wins the player has in a mode.
     * @param mode Mode to update the wins of.
     * @param wins New number of wins.
     */
    private void modeWins(String mode, int wins) {
        modeWins.put(mode, wins);
    }

    /**
     * Get the player's current win streak in a given mode.
     * @param mode Mode to get win streak in.
     * @return Player's win streak in that mode.
     */
    public int modeWinStreak(String mode) {
        if(modeWinStreak.containsKey(mode)) {
            return modeWinStreak.get(mode);
        }

        return 0;
    }

    /**
     * Update the player's current win streak in a specific mode.
     * @param mode Mode win streak is in.
     * @param winStreak New win streak.
     */
    private void modeWinStreak(String mode, int winStreak) {
        modeWinStreak.put(mode, winStreak);
    }

    /**
     * Get the player's worst lose streak in a given mode.
     * @param mode Mode to get worst lose streak in.
     * @return Player's worst lose streak in that mode.
     */
    public int modeWorstLoseStreak(String mode) {
        if(modeWorstLoseStreak.containsKey(mode)) {
            return modeWorstLoseStreak.get(mode);
        }

        return 0;
    }

    /**
     * Update the player's worst lose streak in a given mode.
     * @param mode Mode to update worst lose streak in.
     * @param loseStreak Player's new worst lose streak in that mode.
     */
    private void modeWorstLoseStreak(String mode, int loseStreak) {
        modeWorstLoseStreak.put(mode, loseStreak);
    }

    /**
     * Get the most coins the player has had at one time.
     * @return Most coins the player has had at once.
     */
    public int mostCoinsAtOnce() {
        return mostCoinsAtOnce;
    }

    /**
     * Update the number of coins that player has had at most at one time.
     * @param mostCoinsAtOnce New number of coins that player has had at once.
     */
    void mostCoinsAtOnce(int mostCoinsAtOnce) {
        this.mostCoinsAtOnce = mostCoinsAtOnce;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_misc_statistics SET mostCoinsAtOnce = ? WHERE uuid = ?");
                statement.setInt(1, mostCoinsAtOnce);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Stores updated ability statistics to the database.
     * Stores all abilities at the same time.
     */
    public void updateAbilityStatistics() {
        for(Ability ability : plugin.abilityManager().getAbilities()) {
            updateAbilityStatistics(ability.id());
        }
    }

    /**
     * Store updated ability statistics to the database.
     * @param ability Ability to store statistics for.
     */
    public void updateAbilityStatistics(String ability) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("REPLACE INTO cactus_rush_ability_statistics (uuid,ability,timesUsed,roundsUsed) VALUES (?,?,?,?)");
                statement.setString(1, playerUUID.toString());
                statement.setString(2, ability);
                statement.setInt(3, abilityUses(ability));
                statement.setInt(4, abilityRoundsUsed(ability));
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Store updated arena statistics to the database.
     * @param arena Arena to store statistics for.
     */
    public void updateArenaStatistics(String arena) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("REPLACE INTO cactus_rush_arena_statistics (uuid,arena,wins,losses,winStreak,loseStreak,bestWinStreak,worstLoseStreak,cactiBroke,cactiPlaced,eggsThrown,goalsScored,abilitiesUsed,gamesPlayed,roundsPlayed,deaths,cactiDeaths,voidDeaths,abilityDeaths,playTime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                statement.setString(1, playerUUID.toString());
                statement.setString(2, arena);
                statement.setInt(3, arenaWins(arena));
                statement.setInt(4, arenaLosses(arena));
                statement.setInt(5, arenaWinStreak(arena));
                statement.setInt(6, arenaLoseStreak(arena));
                statement.setInt(7, arenaBestWinStreak(arena));
                statement.setInt(8, arenaWorstLoseStreak(arena));
                statement.setInt(9, arenaCactiBroke(arena));
                statement.setInt(10, arenaCactiPlaced(arena));
                statement.setInt(11, arenaEggsThrown(arena));
                statement.setInt(12, arenaGoalsScored(arena));
                statement.setInt(13, arenaAbilitiesUsed(arena));
                statement.setInt(14, arenaGamesPlayed(arena));
                statement.setInt(15, arenaRoundsPlayed(arena));
                statement.setInt(16, arenaDeaths(arena));
                statement.setInt(17, arenaCactiDeaths(arena));
                statement.setInt(18, arenaVoidDeaths(arena));
                statement.setInt(19, arenaAbilityDeaths(arena));
                statement.setInt(20, arenaPlayTime(arena));
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Store updated mode statistics to the database.
     * @param mode Arena to store statistics for.
     */
    public void updateModeStatistics(String mode) {

        // Also update overall statistics so that I don't forget.
        if(!mode.equalsIgnoreCase("overall")) {
            updateModeStatistics("overall");
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("REPLACE INTO cactus_rush_mode_statistics (uuid,mode,wins,losses,winStreak,loseStreak,bestWinStreak,worstLoseStreak,cactiBroke,cactiPlaced,eggsThrown,goalsScored,abilitiesUsed,gamesPlayed,roundsPlayed,deaths,cactiDeaths,voidDeaths,abilityDeaths,playTime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                statement.setString(1, playerUUID.toString());
                statement.setString(2, mode);
                statement.setInt(3, modeWins(mode));
                statement.setInt(4, modeLosses(mode));
                statement.setInt(5, modeWinStreak(mode));
                statement.setInt(6, modeLoseStreak(mode));
                statement.setInt(7, modeBestWinStreak(mode));
                statement.setInt(8, modeWorstLoseStreak(mode));
                statement.setInt(9, modeCactiBroke(mode));
                statement.setInt(10, modeCactiPlaced(mode));
                statement.setInt(11, modeEggsThrown(mode));
                statement.setInt(12, modeGoalsScored(mode));
                statement.setInt(13, modeAbilitiesUsed(mode));
                statement.setInt(14, modeGamesPlayed(mode));
                statement.setInt(15, modeRoundsPlayed(mode));
                statement.setInt(16, modeDeaths(mode));
                statement.setInt(17, modeCactiDeaths(mode));
                statement.setInt(18, modeVoidDeaths(mode));
                statement.setInt(19, modeAbilityDeaths(mode));
                statement.setInt(20, modePlayTime(mode));
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}