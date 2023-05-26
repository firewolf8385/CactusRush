package com.github.firewolf8385.cactusrush.leaderboard;

import com.github.firewolf8385.cactusrush.CactusRush;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.JadedCore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages leaderboards for various statistics.
 */
public class LeaderboardManager {
    private final CactusRush plugin;

    private final Map<String, Integer> wins = new LinkedHashMap<>();
    private final Map<String, Integer> level = new LinkedHashMap<>();

    /**
     * Creates the manager.
     * @param plugin Instance of the plugin.
     */
    public LeaderboardManager(CactusRush plugin) {
        this.plugin = plugin;

        // Creates a task that updates the leaderboards every 20 minutes
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::update, 20*4, 20*60*20);
    }

    /**
     * Updates the leaderboards.
     */
    public void update() {
        updateWins();
        updateLevel();
    }

    public void updateWins() {
        try {
            wins.clear();

            PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("SELECT * FROM cactus_rush_statistics ORDER BY wins DESC LIMIT 10");
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                PreparedStatement statement2 = JadedAPI.getDatabase().prepareStatement("SELECT * from player_info WHERE uuid = ? LIMIT 1");
                statement2.setString(1, resultSet.getString(1));
                ResultSet results2 = statement2.executeQuery();

                if(results2.next()) {
                    wins.put(results2.getString(2), resultSet.getInt("wins"));
                }
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void updateLevel() {
        try {
            level.clear();

            PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("SELECT * FROM cactus_rush_players ORDER BY level DESC, experience DESC LIMIT 10");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PreparedStatement statement2 = JadedAPI.getDatabase().prepareStatement("SELECT * from player_info WHERE uuid = ? LIMIT 1");
                statement2.setString(1, resultSet.getString(1));
                ResultSet results2 = statement2.executeQuery();

                if(results2.next()) {
                    level.put(results2.getString(2), resultSet.getInt("level"));
                }
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Map<String, Integer> getWinsLeaderboard() {
        return wins;
    }

    public Map<String, Integer> getLevelLeaderboard() {
        return level;
    }
}