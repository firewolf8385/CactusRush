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
package net.jadedmc.cactusrush.game.abilitiy;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityCooldown {
    private final Plugin plugin;
    private final BukkitRunnable task;
    private int seconds;

    public AbilityCooldown(Plugin plugin, int cooldown) {
        this.plugin = plugin;
        seconds = cooldown;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if(seconds == 0) {
                    stop();
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
    }

    public void cancel() {
        if(seconds == 30) {
            return;
        }

        task.cancel();
    }
}