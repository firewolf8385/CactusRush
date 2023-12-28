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
package net.jadedmc.cactusrush.game.arena.file;

import net.jadedmc.jadedutils.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a world not loaded by the server, where an arena is stored.
 */
public class ArenaFile {
    private final File file;
    private final String id;

    /**
     * Creates the arena file.
     * @param file File to create arena file object for.
     */
    public ArenaFile(File file) {
        this.file = file;
        this.id = file.getName();
    }

    /**
     * Gets the world folder.
     * @return World folder.
     */
    public File file() {
        return file;
    }

    /**
     * Gets the id of the ArenaFile.
     * This should be the same as the arena id, for simplicity.
     * @return ArenaFile id.
     */
    public String id() {
        return id;
    }

    /**
     * Creates a copy of the arena world with a specific name.
     * @param name Name of the arena.
     * @return new world folder.
     */
    public CompletableFuture<File> createCopy(String name) {
        return CompletableFuture.supplyAsync(() -> {
            File serverFolder = Bukkit.getWorlds().get(0).getWorldFolder().getParentFile();
            File worldFolder = new File(serverFolder, name);
            FileUtils.copyFileStructure(file, worldFolder);
            return worldFolder;
        });
    }
}