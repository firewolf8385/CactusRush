package com.github.firewolf8385.cactusrush.game.ability;

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
    public int getSeconds() {
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
    public void setSeconds(int seconds) {
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