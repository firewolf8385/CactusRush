package com.github.firewolf8385.cactusrush.dev;

import com.github.firewolf8385.cactusrush.CactusRush;
import net.jadedmc.cactusrush.game.Mode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewCactusPlayer {
    private final CactusRush plugin;
    private final UUID uuid;
    private final Map<Mode, ModeStats> gameStats = new HashMap<>();
    private int level;
    private int experience;

    public NewCactusPlayer(CactusRush plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;


    }

    public ModeStats gameStates(Mode mode) {
        if(gameStats.containsKey(mode)) {
            return gameStats.get(mode);
        }

        return null;
    }

    public void addExperience() {

    }

    public int experience() {
        return experience;
    }

    public int level() {
        return level;
    }
}