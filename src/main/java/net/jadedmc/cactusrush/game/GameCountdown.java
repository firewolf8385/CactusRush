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
package net.jadedmc.cactusrush.game;

import com.cryptomorin.xseries.XSound;
import net.jadedmc.cactusrush.CactusRushPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GameCountdown {
    private final CactusRushPlugin plugin;
    private final BukkitRunnable task;
    private int seconds;
    private final Game game;

    public GameCountdown(@NotNull final CactusRushPlugin plugin, @NotNull final Game game) {
        this.plugin = plugin;
        this.game = game;

        seconds = 30;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if(seconds == 0) {
                    stop();
                }

                if(seconds <= 5 && seconds > 0) {
                    game.sendMessage("&aGame is starting in &f" + seconds + "s&a.");

                    for (final UUID playerUUID : game.getPlayers()) {
                        final Player player = plugin.getServer().getPlayer(playerUUID);
                        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_HAT.parseSound(), 1, 1);
                    }
                }

                seconds--;
            }
        };
    }

    /**
     * Get the seconds left in the countdown.
     * @return Seconds left in the countdown.
     */
    public int seconds() {
        return seconds;
    }

    /**
     * Start the timer.
     */
    public void start() {
        task.runTaskTimer(plugin, 0, 20);
    }

    /**
     * Set the amount of seconds on the countdown.
     * @param seconds Seconds to set.
     */
    public void seconds(int seconds) {
        this.seconds = seconds;
    }

    /**
     * Stop the timer.
     */
    public void stop() {
        task.cancel();
        game.startGame();
    }

    public void cancel() {
        if(seconds == 30) {
            return;
        }

        task.cancel();
    }
}