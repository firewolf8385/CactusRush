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
package net.jadedmc.cactusrush.game.leaderboard;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.jadedcore.JadedAPI;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Manages leaderboards for various statistics.
 */
public class LeaderboardManager {
    private final CactusRushPlugin plugin;

    private final Map<String, Map<String, Integer>> wins = new HashMap<>();
    private final Map<String, Integer> level = new LinkedHashMap<>();

    /**
     * Creates the manager.
     * @param plugin Instance of the plugin.
     */
    public LeaderboardManager(CactusRushPlugin plugin) {
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
        List<String> modes = new ArrayList<>();
        modes.add("overall");

        // Add all available modes.
        for(Mode mode : Mode.values()) {
            modes.add(mode.getId());
        }

        try {
            wins.clear();

            for(String mode : modes) {
                Map<String, Integer> modeLeaderboard = new LinkedHashMap<>();

                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("SELECT * FROM cactus_rush_mode_statistics WHERE mode = ? ORDER BY wins DESC LIMIT 10");
                statement.setString(1, mode);
                ResultSet resultSet = statement.executeQuery();

                while(resultSet.next()) {
                    PreparedStatement statement2 = JadedAPI.getDatabase().prepareStatement("SELECT * from player_info WHERE uuid = ? LIMIT 1");
                    statement2.setString(1, resultSet.getString(1));
                    ResultSet results2 = statement2.executeQuery();

                    if(results2.next()) {
                        modeLeaderboard.put(results2.getString(2), resultSet.getInt("wins"));
                    }
                    else {
                        modeLeaderboard.put("null", resultSet.getInt("wins"));
                    }
                }

                wins.put(mode, modeLeaderboard);
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

    public Map<String, Integer> getWinsLeaderboard(String mode) {
        return wins.get(mode);
    }

    public Map<String, Integer> getWinsLeaderboard() {
        return wins.get("overall");
    }

    public Map<String, Integer> getLevelLeaderboard() {
        return level;
    }
}