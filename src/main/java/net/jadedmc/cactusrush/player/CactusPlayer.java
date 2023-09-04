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
import net.jadedmc.cactusrush.utils.LevelUtils;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.jadedcore.JadedAPI;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Manages and Caches plugin data about a player.
 */
public class CactusPlayer {
    private final CactusRushPlugin plugin;
    private final UUID playerUUID;
    private final CactusPlayerStatisticsTracker statisticsTracker;

    // Data
    private int level = 1;
    private int experience = 0;
    private int coins = 0;
    private String selectedAbility = "flash";


    /**
     * Creates the Cactus Player
     * @param plugin Instance of the plugin.
     * @param player Player to cache data for.
     */
    public CactusPlayer(CactusRushPlugin plugin, Player player) {
        this.plugin = plugin;
        this.playerUUID = player.getUniqueId();
        this.statisticsTracker = new CactusPlayerStatisticsTracker(plugin, player);

        // Load misc statistics.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {

                // cactus_rush_players
                {
                    PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("SELECT * from cactus_rush_players WHERE uuid = ? LIMIT 1");
                    statement.setString(1, playerUUID.toString());
                    ResultSet results = statement.executeQuery();

                    if(results.next()) {
                        coins = results.getInt("coins");
                        level = results.getInt("level");
                        experience = results.getInt("experience");
                        selectedAbility = results.getString("selectedAbility");
                    }
                    else {
                        PreparedStatement insert = JadedAPI.getDatabase().prepareStatement("INSERT INTO cactus_rush_players (uuid) VALUES (?)");
                        insert.setString(1, playerUUID.toString());
                        insert.executeUpdate();
                    }
                }
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Gives coins to the player.
     * @param coins Number of coins the player should gain.
     */
    public void addCoins(int coins) {
        coins(coins() + coins);

        statisticsTracker.lifetimeCoinsEarned(statisticsTracker.lifetimeCoinsEarned() + coins);
        if(coins > statisticsTracker().mostCoinsAtOnce()) statisticsTracker.mostCoinsAtOnce(coins);
    }

    /**
     * Add coins to the player with a specific reason.
     * @param coins Amount of coins to add.
     * @param reason Reason for adding the coins.
     */
    public void addCoins(int coins, String reason) {
        addCoins(coins);
        ChatUtils.chat(player(), "&6+" + coins + " Cactus Rush Coins (" + reason + ")");
    }

    /**
     * Adds experience to the player.
     * @param experience Amount of experience to add.
     */
    public void addExperience(int experience) {
        experience(this.experience + experience);
        int required = LevelUtils.getRequiredExperience(level);

        // If the player has enough experience, move up to the next level.
        if(this.experience >= required) {
            level(this.level + 1);
            experience(this.experience - required);

            ChatUtils.chat(player(), "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.centeredChat(player(), "&b&lLevel Up");
            ChatUtils.chat(player(), "");
            ChatUtils.centeredChat(player(), "&3&k# &bYou are now Cactus Rush Level " + level + " &3&k#");
            ChatUtils.chat(player(), "");
            ChatUtils.chat(player(), "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }
    }

    /**
     * Get the number of coins the player has.
     * @return Player's current amount of coins.
     */
    public int coins() {
        return coins;
    }

    /**
     * Changes the stored amount of coins the player has.
     * @param coins New amount of coins the player has.
     */
    private void coins(int coins) {
        this.coins = coins;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_players SET coins = ? WHERE uuid = ?");
                statement.setInt(1, coins);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Gets the amount of experience the player has.
     * @return Amount of experience.
     */
    public int experience() {
        return experience;
    }

    /**
     * Changes the amount of stored experience the player has.
     * @param experience New amount of experience the player has.
     */
    private void experience(int experience) {
        this.experience = experience;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_players SET experience = ? WHERE uuid = ?");
                statement.setInt(1, experience);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Gets the current Cactus Rush level of the player.
     * @return Cactus Rush level.
     */
    public int level() {
        return level;
    }

    /**
     * Changes the player's current Cactus Rush level.
     * @param level New level the player should be.
     */
    public void level(int level) {
        this.level = level;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_players SET level = ? WHERE uuid = ?");
                statement.setInt(1, level);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Gets the player the CactusPlayer represents.
     * @return Player object of the Cactus Player.
     */
    public Player player() {
        return plugin.getServer().getPlayer(playerUUID);
    }

    /**
     * Get the player's currently selected ability.
     * @return Selected ability.
     */
    public String selectedAbility() {
        return selectedAbility;
    }

    /**
     * Change the player's currently selected ability.
     * @param selectedAbility Newly selected ability.
     */
    public void selectedAbility(String selectedAbility) {
        this.selectedAbility = selectedAbility;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_players SET selectedAbility = ? WHERE uuid = ?");
                statement.setString(1, selectedAbility);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Get the Player's Statistics Tracker, which stores all the game statistics.
     * @return Statistic Tracker.
     */
    public CactusPlayerStatisticsTracker statisticsTracker() {
        return statisticsTracker;
    }
}