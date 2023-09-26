/*
 * This file is part of JadedChat, licensed under the MIT License.
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
package net.jadedmc.cactusrush.game.arena.file;

import org.bukkit.Bukkit;

import java.io.File;

/**
 * Manages the loading of Arena world files.
 */
public class ArenaFileManager {

    /**
     * Loads an Arena World File object.
     * Returns null if invalid.
     * @param name Name of the arena.
     * @return ArenaFile for the arena.
     */
    public ArenaFile loadArenaFile(String name) {
        System.out.println("LOADING ARENA FILE " + name);
        File serverFolder = Bukkit.getWorlds().get(0).getWorldFolder().getParentFile();
        File mapsFolder = new File(serverFolder, "maps");

        // Makes sure the map folder exists.
        if(!mapsFolder.exists()) {
            mapsFolder.mkdir();
        }

        File[] files = mapsFolder.listFiles();

        // Loop through all world folders in the "maps" folder to look for the world.
        for(File file : files) {
            if(file.getName().equals(name)) {
                // Creates the ArenaFile if it is found.
                System.out.println("ARENA FILE FOUND");
                return new ArenaFile(file);
            }
        }

        // Returns null if the arena is invalid.
        System.out.println("ERROR: NULL ARENA MAP: " + name);
        return null;
    }

}