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
package net.jadedmc.cactusrush;

import net.jadedmc.cactusrush.commands.AbstractCommand;
import net.jadedmc.cactusrush.game.GameManager;
import net.jadedmc.cactusrush.game.abilitiy.AbilityManager;
import net.jadedmc.cactusrush.game.arena.ArenaManager;
import net.jadedmc.cactusrush.game.duel.DuelManager;
import net.jadedmc.cactusrush.game.leaderboard.LeaderboardManager;
import net.jadedmc.cactusrush.listeners.*;
import net.jadedmc.cactusrush.player.CactusPlayerManager;
import net.jadedmc.cactusrush.utils.LevelUtils;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedchat.features.channels.channel.ChatChannel;
import net.jadedmc.jadedchat.features.channels.channel.ChatChannelBuilder;
import net.jadedmc.jadedchat.features.channels.fomat.ChatFormatBuilder;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.games.Game;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class creates the Cactus Rush plugin.
 */
public class CactusRushPlugin extends JavaPlugin {
    private AbilityManager abilityManager;
    private ArenaManager arenaManager;
    private SettingsManager settingsManager;
    private GameManager gameManager;
    private CactusPlayerManager cactusPlayerManager;
    private LeaderboardManager leaderboardManager;
    private DuelManager duelManager;

    /**
     * Runs when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        // Create MySQL tables if they don't' already exist.
        loadTables();

        // Setup Chat Utils.
        ChatUtils.setAdventure(BukkitAudiences.create(this));

        settingsManager = new SettingsManager(this);
        arenaManager = new ArenaManager(this);
        arenaManager.loadArenas();
        abilityManager = new AbilityManager(this);
        gameManager = new GameManager(this);
        cactusPlayerManager = new CactusPlayerManager(this);
        leaderboardManager = new LeaderboardManager(this);
        duelManager = new DuelManager(this);

        // If PlaceholderAPI is installed, enables placeholders
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
        }
        else {
            // If not, logs a warning and disables the plugin
            Bukkit.getLogger().warning("CactusRush requires PlaceholderAPI to be installed.");
            getServer().getPluginManager().disablePlugin(this);
        }

        AbstractCommand.registerCommands(this);

        // Register listeners.
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new ChannelMessageSendListener(this), this);
        getServer().getPluginManager().registerEvents(new ChannelSwitchListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEggThrowListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileHitListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileLaunchListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldInitListener(), this);
        getServer().getPluginManager().registerEvents(new LobbyJoinListener(this), this);

        // Registers utilities.
        new LevelUtils(this);

        // Create Chat Channels
        if(!JadedChat.channelExists("GAME")) {
            ChatChannel gameChannel = new ChatChannelBuilder("GAME")
                    .setDisplayName("<green>GAME</green>")
                    .useDiscordSRV(true)
                    .addChatFormat(new ChatFormatBuilder("default")
                            .addSection("team", "%cr_chat_prefix% ")
                            .addSection("prefix", "%luckperms_prefix%")
                            .addSection("player", "<gray>%player_name%")
                            .addSection("seperator", "<dark_gray> » ")
                            .addSection("message", "<gray><message>")
                            .build())
                    .build();
            gameChannel.saveToFile("game.yml");
            JadedChat.loadChannel(gameChannel);
        }

        if(!JadedChat.channelExists("TEAM")) {
            ChatChannel gameChannel = new ChatChannelBuilder("TEAM")
                    .setDisplayName("<white>TEAM</white>")
                    .addAlias("T")
                    .addAlias("TC")
                    .addChatFormat(new ChatFormatBuilder("default")
                            .addSection("team", "<white>[TEAM] ")
                            .addSection("prefix", "%luckperms_prefix%")
                            .addSection("player", "<gray>%player_name%")
                            .addSection("seperator", "<dark_gray> » ")
                            .addSection("message", "<gray><message>")
                            .build())
                    .build();
            gameChannel.saveToFile("team.yml");
            JadedChat.loadChannel(gameChannel);
        }

        // Register achievements
        JadedAPI.getPlugin().achievementManager().createAchievement(Game.CACTUS_RUSH, "cactus_rush_1", "Genesis of a Journey", "Win a game of Cactus Rush for the first time.", 5);
        JadedAPI.getPlugin().achievementManager().createAchievement(Game.CACTUS_RUSH, "cactus_rush_2", "Take That!", "Win a game using /duel.", 5);
        JadedAPI.getPlugin().achievementManager().createAchievement(Game.CACTUS_RUSH, "cactus_rush_3", "Observer In Training", "Spectate an ongoing game.", 5);
        JadedAPI.getPlugin().achievementManager().createAchievement(Game.CACTUS_RUSH, "cactus_rush_4", "Snowball Fight!", "Hit someone with a Deathball, instantly killing them.", 5);
        JadedAPI.getPlugin().achievementManager().createAchievement(Game.CACTUS_RUSH, "cactus_rush_5", "Desert Destroyer", "Break 100 Cacti in a single game of Cactus Rush.", 10);
        JadedAPI.getPlugin().achievementManager().createAchievement(Game.CACTUS_RUSH, "cactus_rush_6", "Chicken Chucker", "Throw 200 Eggs in a single game of Cactus Rush.", 10);
        JadedAPI.getPlugin().achievementManager().createAchievement(Game.CACTUS_RUSH, "cactus_rush_7", "Persistent Planter", "Place 300 Cacti in a single game of Cactus Rush.", 10);
        JadedAPI.getPlugin().achievementManager().createAchievement(Game.CACTUS_RUSH, "cactus_rush_8", "Well-Rounded", "Play at least 1 match in every mode: 1v1, 2v2, 3v3, and 4v4.", 10);
    }

    public AbilityManager abilityManager() {
        return abilityManager;
    }

    /**
     * Gets the Arena Manager.
     * @return ArenaManager.
     */
    public ArenaManager arenaManager() {
        return arenaManager;
    }

    public GameManager gameManager() {
        return gameManager;
    }

    /**
     * Gets the Settings Manager.
     * @return SettingsManager.
     */
    public SettingsManager settingsManager() {
        return settingsManager;
    }

    public CactusPlayerManager cactusPlayerManager() {
        return cactusPlayerManager;
    }

    public LeaderboardManager leaderboardManager() {
        return leaderboardManager;
    }

    public DuelManager duelManager() {
        return duelManager;
    }

    private void loadTables() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {

                // cactus_rush_players
                {
                    PreparedStatement cactus_rush_players = JadedAPI.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS cactus_rush_players (" +
                            "uuid VARCHAR(36)," +
                            "level INT DEFAULT 1," +
                            "experience INT DEFAULT 0," +
                            "coins INT DEFAULT 0," +
                            "selectedAbility VARCHAR(24) DEFAULT \"flash\"," +
                            "PRIMARY KEY (uuid)" +
                            ");");
                    cactus_rush_players.execute();
                }

                // cactus_rush_mode_statistics
                {
                    PreparedStatement cactus_rush_mode_statistics = JadedAPI.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS cactus_rush_mode_statistics (" +
                            "uuid VARCHAR(36)," +
                            "mode VARCHAR(24)," +
                            "wins INT DEFAULT 0," +
                            "losses INT DEFAULT 0," +
                            "winStreak INT DEFAULT 0," +
                            "loseStreak INT DEFAULT 0," +
                            "bestWinStreak INT DEFAULT 0," +
                            "worstLoseStreak INT DEFAULT 0," +
                            "cactiBroke INT DEFAULT 0," +
                            "cactiPlaced INT DEFAULT 0," +
                            "eggsThrown INT DEFAULT 0," +
                            "goalsScored INT DEFAULT 0," +
                            "abilitiesUsed INT DEFAULT 0," +
                            "gamesPlayed INT DEFAULT 0," +
                            "roundsPlayed INT DEFAULT 0," +
                            "deaths INT DEFAULT 0," +
                            "cactiDeaths INT DEFAULT 0," +
                            "voidDeaths INT DEFAULT 0," +
                            "abilityDeaths INT DEFAULT 0," +
                            "playTime INT DEFAULT 0," +
                            "PRIMARY KEY (uuid,mode)" +
                            ");");
                    cactus_rush_mode_statistics.execute();
                }

                // cactus_rush_arena_statistics
                {
                    PreparedStatement cactus_rush_mode_statistics = JadedAPI.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS cactus_rush_arena_statistics (" +
                            "uuid VARCHAR(36)," +
                            "arena VARCHAR(24)," +
                            "wins INT DEFAULT 0," +
                            "losses INT DEFAULT 0," +
                            "winStreak INT DEFAULT 0," +
                            "loseStreak INT DEFAULT 0," +
                            "bestWinStreak INT DEFAULT 0," +
                            "worstLoseStreak INT DEFAULT 0," +
                            "cactiBroke INT DEFAULT 0," +
                            "cactiPlaced INT DEFAULT 0," +
                            "eggsThrown INT DEFAULT 0," +
                            "goalsScored INT DEFAULT 0," +
                            "abilitiesUsed INT DEFAULT 0," +
                            "gamesPlayed INT DEFAULT 0," +
                            "roundsPlayed INT DEFAULT 0," +
                            "deaths INT DEFAULT 0," +
                            "cactiDeaths INT DEFAULT 0," +
                            "voidDeaths INT DEFAULT 0," +
                            "abilityDeaths INT DEFAULT 0," +
                            "playTime INT DEFAULT 0," +
                            "PRIMARY KEY (uuid,arena)" +
                            ");");
                    cactus_rush_mode_statistics.execute();
                }

                // cactus_rush_ability_statistics
                {
                    PreparedStatement cactus_rush_ability_statistics = JadedAPI.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS cactus_rush_ability_statistics (" +
                            "uuid VARCHAR(36)," +
                            "ability VARCHAR(24)," +
                            "timesUsed INT DEFAULT 0," +
                            "roundsUsed INT DEFAULT 0," +
                            "PRIMARY KEY (uuid,ability)" +
                            ");");
                    cactus_rush_ability_statistics.execute();
                }

                // cactus_rush_misc_statistics
                {
                    PreparedStatement cactus_rush_mode_statistics = JadedAPI.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS cactus_rush_misc_statistics (" +
                            "uuid VARCHAR(36)," +
                            "lifetimeCoinsEarned INT DEFAULT 0," +
                            "lifetimeCoinsSpent INT DEFAULT 0," +
                            "mostCoinsAtOnce INT DEFAULT 0," +
                            "deathballKills INT DEFAULT 0," +
                            "PRIMARY KEY (uuid)" +
                            ");");
                    cactus_rush_mode_statistics.execute();
                }

                // Abilities
                {
                    PreparedStatement cactus_rush_abilities = JadedAPI.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS cactus_rush_abilities (" +
                            "uuid VARCHAR(36)," +
                            "ability VARCHAR(24)," +
                            "PRIMARY KEY (uuid, ability)" +
                            ");");
                    cactus_rush_abilities.execute();
                }

                // Cosmetics
                {
                    PreparedStatement cactus_rush_cosmetics = JadedAPI.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS cactus_rush_cosmetics (" +
                            "uuid VARCHAR(36)," +
                            "primaryTeamColor VARCHAR(16) DEFAULT 'NONE'," +
                            "secondaryTeamColor VARCHAR(16) DEFAULT 'NONE'," +
                            "PRIMARY KEY (uuid)" +
                            ");");
                    cactus_rush_cosmetics.execute();
                }

                // Unlocked Team Colors
                {
                    PreparedStatement cactus_rush_cosmetics = JadedAPI.getDatabase().prepareStatement("CREATE TABLE IF NOT EXISTS cactus_rush_team_colors (" +
                            "uuid VARCHAR(36)," +
                            "teamColor VARCHAR(16)," +
                            "PRIMARY KEY (uuid,teamColor)" +
                            ");");
                    cactus_rush_cosmetics.execute();
                }

            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}