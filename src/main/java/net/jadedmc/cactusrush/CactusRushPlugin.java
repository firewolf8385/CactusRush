package net.jadedmc.cactusrush;

import net.jadedmc.cactusrush.game.arenas.ArenaManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CactusRushPlugin extends JavaPlugin {
    private ArenaManager arenaManager;

    public void onEnable() {
        arenaManager = new ArenaManager(this);
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }
}