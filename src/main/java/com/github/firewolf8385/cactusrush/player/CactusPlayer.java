package com.github.firewolf8385.cactusrush.player;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.utils.LevelUtils;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

    /**
     * Creates the CactusPlayer object and loads data from MySQL if it exists.
     * If not, creates new entries in MySQL.
     * @param plugin Instance of the plugin.
     * @param playerUUID UUID of the player representing the CactusPlayer.
     */
    public CactusPlayer(CactusRush plugin, UUID playerUUID) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;

        // TODO: Load from MySQL
    }

    /**
     * Gives coins to the player.
     * @param coins Number of coins the player should gain.
     */
    public void addCoins(int coins) {
        setCoins(getCoins() + coins);
    }

    public void addCoins(int coins, String reason) {
        addCoins(coins);
        ChatUtils.chat(getPlayer(), "&6+" + coins + " Cactus Rush Coins (" + reason + ")");
    }

    public void addExperience(int experience) {
        this.experience += experience;
        int required = LevelUtils.getRequiredExperience(level);

        if(this.experience >= required) {
            level++;
            this.experience -= required;

            ChatUtils.chat(getPlayer(), "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.centeredChat(getPlayer(), "&b&lLevel Up");
            ChatUtils.chat(getPlayer(), "");
            ChatUtils.centeredChat(getPlayer(), "&bYou are now Cactus Rush Level " + level);
            ChatUtils.chat(getPlayer(), "");
            ChatUtils.chat(getPlayer(), "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }
    }

    /**
     * Adds a win to the player.
     * Also increases the player's win streak by 1.
     */
    public void addWin() {
        setWins(getWins() + 1);
        setWinStreak(getWinStreak() + 1);
    }

    /**
     * Adds a placed cactus to the player.
     */
    private void addCactusPlaced() {
        setCactiPlaced(getCactiPlaced() + 1);
    }

    /**
     * Adds an egg thrown to the player.
     */
    private void addEggThrown() {
        setEggsThrown(getEggsThrown() + 1);
    }

    /**
     * Adds a loss to the player.
     * Also resets the player's win streak.
     */
    public void addLoss() {
        setLosses(getLosses() + 1);
        setWinStreak(0);
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
     * Changes the stored amount of Cacti that the player has placed.
     * @param cactiPlaced New amount of cacti placed.
     */
    private void setCactiPlaced(int cactiPlaced) {
        this.cactiPlaced = cactiPlaced;

        // TODO: MySQL
    }

    /**
     * Changes the stored amount of coins the player has.
     * @param coins New amount of coins the player has.
     */
    private void setCoins(int coins) {
        this.coins = coins;

        // TODO: MySQL
    }

    /**
     * Changes the stored amount of eggs the player has thrown.
     * @param eggsThrown New amount of eggs thrown.
     */
    private void setEggsThrown(int eggsThrown) {
        this.eggsThrown = eggsThrown;
    }

    /**
     * Changes the amount of stored experience the player has.
     * @param experience New amount of experience the player has.
     */
    private void setExperience(int experience) {
        this.experience = experience;

        // TODO: MySQL
        // TODO: Level up player if they can level up.
        // TODO: Call 'LevelUpEvent'
    }

    /**
     * Changes the amount of stored games that the player has played.
     * @param gamesPlayed New amount of games played.
     */
    private void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;

        // TODO: MySQL
    }

    /**
     * Changes the player's current Cactus Rush level.
     * @param level New level the player should be.
     */
    public void setLevel(int level) {
        this.level = level;

        // TODO: MySQL
    }

    /**
     * Changes the amount of stored losses the player has.
     * @param losses New amount of losses the player has.
     */
    private void setLosses(int losses) {
        this.losses = losses;

        // TODO: MySQL
    }

    /**
     * Changes the amount of stored wins the player has.
     * @param wins New amount of wins the player has.
     */
    private void setWins(int wins) {
        this.wins = wins;

        // TODO: MySQL
    }

    /**
     * Changes the stored winstreak the player has.
     * @param winStreak New winstreak the player has.
     */
    private void setWinStreak(int winStreak) {
        this.winStreak = winStreak;

        // TODO: MySQL
    }
}