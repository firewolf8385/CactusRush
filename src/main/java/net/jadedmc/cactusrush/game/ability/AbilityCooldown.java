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
package net.jadedmc.cactusrush.game.ability;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Represents how long a player must wait before using an ability again.
 */
public class AbilityCooldown {
    private final Plugin plugin;
    private final BukkitRunnable task;
    private int seconds;

    /**
     * Creates an Ability Cooldown.
     * @param plugin Instance of the plugin.
     * @param cooldown How long the cool down should last, in seconds.
     */
    public AbilityCooldown(final Plugin plugin, final int cooldown) {
        this.plugin = plugin;
        seconds = cooldown;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if(seconds == 0) {
                    stopCooldown();
                }

                seconds--;
            }
        };
    }

    /**
     * Cancels the cooldown timer.
     */
    public void cancelCooldown() {
        if(seconds == 30) {
            return;
        }

        task.cancel();
    }

    /**
     * Get the seconds left in the countdown.
     * @return Seconds left in the countdown.
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Set the amount of seconds on the countdown.
     * @param seconds Seconds to set.
     */
    public void setSeconds(final int seconds) {
        this.seconds = seconds;
    }

    /**
     * Start the timer.
     */
    public void startCooldown() {
        task.runTaskTimer(plugin, 0, 20);
    }

    /**
     * Stop the timer.
     */
    public void stopCooldown() {
        task.cancel();
    }
}