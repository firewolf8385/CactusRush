package com.github.firewolf8385.cactusrush.player;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.utils.LevelUtils;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.jadedcore.JadedAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Manages and Caches plugin data about a player.
 */
public class CactusPlayer {
    private final CactusRush plugin;
    private final UUID playerUUID;

    // Sets Default Data
    private int coins = 0;
    private int level = 1;
    private int experience = 0;
    private int wins = 0;
    private int losses = 0;
    private int gamesPlayed = 0;
    private int eggsThrown = 0;
    private int cactiPlaced = 0;
    private int winStreak = 0;
    private int cactiBroke = 0;
    private int bestWinStreak = 0;
    private int goalsScored = 0;
    private int respawns = 0;

    /**
     * Creates the CactusPlayer object and loads data from MySQL if it exists.
     * If not, creates new entries in MySQL.
     * @param plugin Instance of the plugin.
     * @param playerUUID UUID of the player representing the CactusPlayer.
     */
    public CactusPlayer(CactusRush plugin, UUID playerUUID) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;

        // Run database tasks async.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // cactus_rush_players
                {
                    PreparedStatement retrieve = JadedAPI.getDatabase().prepareStatement("SELECT * from cactus_rush_players WHERE uuid = ? LIMIT 1");
                    retrieve.setString(1, playerUUID.toString());
                    ResultSet resultSet = retrieve.executeQuery();

                    if(resultSet.next()) {
                        level = resultSet.getInt("level");
                        experience = resultSet.getInt("experience");
                        coins = resultSet.getInt("coins");
                    }
                    else {
                        PreparedStatement insert = JadedAPI.getDatabase().prepareStatement("INSERT INTO cactus_rush_players (uuid) VALUES (?)");
                        insert.setString(1, playerUUID.toString());
                        insert.executeUpdate();
                    }
                }

                // cactus_rush_statistics
                {
                    PreparedStatement retrieve = JadedAPI.getDatabase().prepareStatement("SELECT * from cactus_rush_statistics WHERE uuid = ? LIMIT 1");
                    retrieve.setString(1, playerUUID.toString());
                    ResultSet resultSet = retrieve.executeQuery();

                    if(resultSet.next()) {
                        wins = resultSet.getInt("wins");
                        losses = resultSet.getInt("losses");
                        winStreak = resultSet.getInt("winStreak");
                        bestWinStreak = resultSet.getInt("bestWinStreak");
                        cactiPlaced = resultSet.getInt("cactiPlaced");
                        cactiBroke = resultSet.getInt("cactiBroke");
                        eggsThrown = resultSet.getInt("eggsThrown");
                        goalsScored = resultSet.getInt("goals");
                        gamesPlayed = resultSet.getInt("gamesPlayed");
                        respawns = resultSet.getInt("respawns");
                    }
                    else {
                        PreparedStatement insert = JadedAPI.getDatabase().prepareStatement("INSERT INTO cactus_rush_statistics (uuid) VALUES (?)");
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
        setCoins(getCoins() + coins);
    }

    /**
     * Add coins to the player with a specific reason.
     * @param coins Amount of coins to add.
     * @param reason Reason for adding the coins.
     */
    public void addCoins(int coins, String reason) {
        addCoins(coins);
        ChatUtils.chat(getPlayer(), "&6+" + coins + " Cactus Rush Coins (" + reason + ")");
    }

    /**
     * Adds experience to the player.
     * @param experience Amount of experience to add.
     */
    public void addExperience(int experience) {
        setExperience(this.experience + experience);
        int required = LevelUtils.getRequiredExperience(level);

        if(this.experience >= required) {
            setLevel(level + 1);
            setExperience(this.experience - required);

            ChatUtils.chat(getPlayer(), "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.centeredChat(getPlayer(), "&b&lLevel Up");
            ChatUtils.chat(getPlayer(), "");
            ChatUtils.centeredChat(getPlayer(), "&3&k# &bYou are now Cactus Rush Level " + level + " &3&k#");
            ChatUtils.chat(getPlayer(), "");
            ChatUtils.chat(getPlayer(), "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }
    }

    /**
     * Add to the goals score counter.
     * @param goals Number of goals to add.
     */
    public void addGoalsScored(int goals) {
        setGoalsScored(goalsScored + goals);
    }

    /**
     * Adds a win to the player.
     * Also increases the player's win streak by 1.
     */
    public void addWin() {
        setWins(getWins() + 1);
        setWinStreak(getWinStreak() + 1);
        setGamesPlayed(gamesPlayed + 1);
    }

    /**
     * Adds to the number of cacti broke.
     * @param cacti The number of cacti to add.
     */
    public void addCactiBroke(int cacti) {
        setCactiBroke(cactiBroke + cacti);
    }

    /**
     * Adds a placed cactus to the player.
     * @param cacti The number of cacti placed.
     */
    public void addCactiPlaced(int cacti) {
        setCactiPlaced(getCactiPlaced() + cacti);
    }

    /**
     * Adds an egg thrown to the player.
     * @param eggs The number of eggs to add.
     */
    public void addEggsThrown(int eggs) {
        setEggsThrown(getEggsThrown() + eggs);
    }

    /**
     * Adds a loss to the player.
     * Also resets the player's win streak.
     */
    public void addLoss() {
        setLosses(getLosses() + 1);
        setWinStreak(0);
        setGamesPlayed(gamesPlayed + 1);
    }

    /**
     * Gets the number of cacti the player placed.
     * @return The number of placed cacti.
     */
    public int getCactiPlaced() {
        return cactiPlaced;
    }

    /**
     * Gets the amount of coins the player has.
     * @return Number of coins the player has.
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Get the number of eggs thrown by the player.
     * @return Amount of eggs thrown.
     */
    public int getEggsThrown() {
        return eggsThrown;
    }

    /**
     * Gets the amount of experience the player has.
     * @return Amount of experience.
     */
    public int getExperience() {
        return experience;
    }

    /**
     * Get the amount of games the player has played.
     * @return Number of games the player has played.
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Gets the current Cactus Rush level of the player.
     * @return Cactus Rush level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the number of losses the player has.
     * @return Amount of losses the player has.
     */
    public int getLosses() {
        return losses;
    }

    /**
     * Gets the player object the CactusPlayer represents.
     * @return Player object with the matching uuid.
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    /**
     * Gets the number of wins the player has.
     * @return amount of wins the player has.
     */
    public int getWins() {
        return wins;
    }

    /**
     * Gets the player's current win streak.
     * @return Player's current win streak.
     */
    public int getWinStreak() {
        return winStreak;
    }

    /**
     * Removes a number of coins from the player.
     * @param coins Amount of coins removed from the player.
     */
    public void removeCoins(int coins) {
        setCoins(getCoins() - coins);
    }

    /**
     * Changes the player's best win streak.
     * @param bestWinStreak New best win streak.
     */
    private void setBestWinStreak(int bestWinStreak) {
        this.bestWinStreak = bestWinStreak;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_statistics SET bestWinStreak = ? WHERE uuid = ?");
                statement.setInt(1, bestWinStreak);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the number of cacti the player has broken.
     * @param cactiBroke New total number of cacti broke.
     */
    private void setCactiBroke(int cactiBroke) {
        this.cactiBroke = cactiBroke;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_statistics SET cactiBroke = ? WHERE uuid = ?");
                statement.setInt(1, cactiBroke);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the stored amount of Cacti that the player has placed.
     * @param cactiPlaced New amount of cacti placed.
     */
    private void setCactiPlaced(int cactiPlaced) {
        this.cactiPlaced = cactiPlaced;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_statistics SET cactiPlaced = ? WHERE uuid = ?");
                statement.setInt(1, cactiPlaced);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the stored amount of coins the player has.
     * @param coins New amount of coins the player has.
     */
    private void setCoins(int coins) {
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
     * Changes the stored amount of eggs the player has thrown.
     * @param eggsThrown New amount of eggs thrown.
     */
    private void setEggsThrown(int eggsThrown) {
        this.eggsThrown = eggsThrown;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_statistics SET eggsThrown = ? WHERE uuid = ?");
                statement.setInt(1, eggsThrown);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the amount of stored experience the player has.
     * @param experience New amount of experience the player has.
     */
    private void setExperience(int experience) {
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
     * Changes the amount of stored games that the player has played.
     * @param gamesPlayed New amount of games played.
     */
    private void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_statistics SET gamesPlayed = ? WHERE uuid = ?");
                statement.setInt(1, gamesPlayed);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the number of goals the player has scored.
     * @param goalsScored New number of goals scored.
     */
    private void setGoalsScored(int goalsScored) {
        this.goalsScored = goalsScored;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_statistics SET goals = ? WHERE uuid = ?");
                statement.setInt(1, goalsScored);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the player's current Cactus Rush level.
     * @param level New level the player should be.
     */
    public void setLevel(int level) {
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
     * Changes the amount of stored losses the player has.
     * @param losses New amount of losses the player has.
     */
    private void setLosses(int losses) {
        this.losses = losses;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_statistics SET losses = ? WHERE uuid = ?");
                statement.setInt(1, losses);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the amount of stored wins the player has.
     * @param wins New amount of wins the player has.
     */
    private void setWins(int wins) {
        this.wins = wins;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_statistics SET wins = ? WHERE uuid = ?");
                statement.setInt(1, wins);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Changes the stored winstreak the player has.
     * @param winStreak New winstreak the player has.
     */
    private void setWinStreak(int winStreak) {
        this.winStreak = winStreak;

        if(winStreak > bestWinStreak) {
            setBestWinStreak(winStreak);
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = JadedAPI.getDatabase().prepareStatement("UPDATE cactus_rush_statistics SET winStreak = ? WHERE uuid = ?");
                statement.setInt(1, winStreak);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}