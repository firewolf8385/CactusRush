package net.jadedmc.cactusrush.game.arenas;

import net.jadedmc.cactusrush.game.arenas.builder.ArenaBuilder;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArenaManager {
    private final Map<String, Arena> arenas = new HashMap<>();
    private ArenaBuilder arenaBuilder;

    public ArenaManager(final Plugin plugin) {
        for(File file : Objects.requireNonNull(new File(plugin.getDataFolder(), "arenas").listFiles())) {
            loadArena(file);
        }
    }

    public void loadArena(File file) {
        arenas.put(file.getName(), new Arena(file));
    }

    public Arena getArena(String id) {
        if(arenas.containsKey(id)) {
            return arenas.get(id);
        }

        return null;
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    public ArenaBuilder getArenaBuilder() {
        return arenaBuilder;
    }

    public void setArenaBuilder(ArenaBuilder arenaBuilder) {
        this.arenaBuilder = arenaBuilder;
    }
}