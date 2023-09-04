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
package net.jadedmc.cactusrush.game.arena;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.game.arena.builder.ArenaBuilder;
import net.jadedmc.cactusrush.game.arena.file.ArenaFileManager;

import java.io.File;
import java.util.*;

/**
 * This class manages the creation and loading of arenas.
 */
public class ArenaManager {
    private final CactusRushPlugin plugin;
    private final Map<String, Arena> arenas = new HashMap<>();
    private ArenaBuilder arenaBuilder;
    private final ArenaFileManager arenaFileManager = new ArenaFileManager();

    /**
     * Creates the ArenaManager.
     * @param plugin Instance of the plugin.
     */
    public ArenaManager(final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the Arena File Manager, which manages arena worlds.
     * @return Arena File Manager.
     */
    public ArenaFileManager arenaFileManager() {
        return arenaFileManager;
    }

    /**
     * Get an arena from its id.
     * Null if invalid.
     * @param id ID of the arena.
     * @return Associated Arena object,
     */
    public Arena getArena(String id) {
        if(arenas.containsKey(id)) {
            return arenas.get(id);
        }

        return null;
    }

    /**
     * Gets the current arena builder.
     * Returns null if there is none.
     * @return Active arena builder.
     */
    public ArenaBuilder getArenaBuilder() {
        return arenaBuilder;
    }

    /**
     * Gets all currently loaded arenas.
     * @return Collection of arenas.
     */
    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    /**
     * Gets all arenas that support a given mode.
     * @param mode Mode to get arenas for.
     * @return Collection of arenas for that mode.
     */
    public Collection<Arena> getArenas(Mode mode) {
        Collection<Arena> modeArenas = new HashSet<>();

        for(Arena arena : getArenas()) {
            if(arena.modes().contains(mode)) {
                modeArenas.add(arena);
            }
        }

        return modeArenas;
    }

    /**
     * Loads all arenas from
     */
    public void loadArenas() {
        arenas.clear();
        File arenasFolder = new File(plugin.getDataFolder(), "arenas");

        if(!arenasFolder.exists()) {
            arenasFolder.mkdir();
        }

        for(File file : Objects.requireNonNull(arenasFolder.listFiles())) {
            loadArena(file);
        }
    }

    /**
     * Loads an Arena from its config file.
     * @param file Arena configuration file.
     */
    public void loadArena(File file) {
        System.out.println("LOADING ARENA");
        arenas.put(file.getName(), new Arena(plugin, file));
    }

    /**
     * Replaces the current arena builder.
     * Used when canceling arena building.
     * @param arenaBuilder New arena builder. Can be null.
     */
    public void setArenaBuilder(ArenaBuilder arenaBuilder) {
        this.arenaBuilder = arenaBuilder;
    }
}