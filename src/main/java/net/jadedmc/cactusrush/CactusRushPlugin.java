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
package net.jadedmc.cactusrush;

import net.jadedmc.cactusrush.commands.AbstractCommand;
import net.jadedmc.cactusrush.game.GameManager;
import net.jadedmc.cactusrush.game.ability.AbilityManager;
import net.jadedmc.cactusrush.game.arena.ArenaManager;
import net.jadedmc.cactusrush.listeners.*;
import net.jadedmc.cactusrush.player.CactusPlayerManager;
import net.jadedmc.cactusrush.utils.LevelUtils;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedchat.features.channels.channel.ChatChannel;
import net.jadedmc.jadedchat.features.channels.channel.ChatChannelBuilder;
import net.jadedmc.jadedchat.features.channels.fomat.ChatFormatBuilder;
import net.jadedmc.jadedcore.JadedAPI;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class creates the CactusRush plugin.
 */
public class CactusRushPlugin extends JavaPlugin {
    private AbilityManager abilityManager;
    private ArenaManager arenaManager;
    private CactusPlayerManager cactusPlayerManager;
    private ConfigManager configManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        // Load config files.
        configManager = new ConfigManager(this);

        // Set up utilities
        new LevelUtils(this);

        // Load arenas.
        arenaManager = new ArenaManager(this);
        arenaManager.loadArenas();

        abilityManager = new AbilityManager(this);
        cactusPlayerManager = new CactusPlayerManager(this);
        gameManager = new GameManager(this);

        // Register plugin commands.
        AbstractCommand.registerCommands(this);

        // Register listeners.
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new JadedJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new LobbyJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEggThrowListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileLaunchListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileLaunchListener(this), this);
        getServer().getPluginManager().registerEvents(new RedisMessageListener(this), this);

        // Registers the game creation channel.
        JadedAPI.getRedis().subscribe("cactusrush");

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
    }

    /**
     * Gets the Ability Manager.
     * @return AbilityManager.
     */
    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    /**
     * Gets the Arena Manager.
     * @return ArenaManager.
     */
    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    /**
     * Gets the Cactus Player Manager
     * @return CactusPlayerManager.
     */
    public CactusPlayerManager getCactusPlayerManager() {
        return cactusPlayerManager;
    }

    /**
     * Gets the Config Manager.
     * @return ConfigManager.
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Gets the game manager.
     * @return GameManager.
     */
    public GameManager getGameManager() {
        return gameManager;
    }
}