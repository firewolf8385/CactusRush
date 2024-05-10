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
import net.jadedmc.cactusrush.game.arena.ArenaManager;
import net.jadedmc.cactusrush.listeners.RedisMessageListener;
import net.jadedmc.cactusrush.utils.LevelUtils;
import net.jadedmc.jadedcore.JadedAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class CactusRushPlugin extends JavaPlugin {
    private ArenaManager arenaManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Load config files.
        configManager = new ConfigManager(this);

        // Set up utilities
        new LevelUtils(this);

        // Load arenas.
        arenaManager = new ArenaManager(this);
        arenaManager.loadArenas();

        // Register plugin commands.
        AbstractCommand.registerCommands(this);

        // Register listeners.
        getServer().getPluginManager().registerEvents(new RedisMessageListener(this), this);

        // Registers the game creation channel.
        JadedAPI.getRedis().subscribe("cactusrush");
    }

    /**
     * Gets the Arena Manager.
     * @return ArenaManager.
     */
    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    /**
     * Gets the Config Manager.
     * @return ConfigManager.
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
}