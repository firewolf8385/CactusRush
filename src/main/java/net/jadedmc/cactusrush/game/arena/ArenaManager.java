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
package net.jadedmc.cactusrush.game.arena;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.jadedcore.JadedAPI;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

/**
 * This class manages the creation, loading, adn retrieval of arenas.
 */
public final class ArenaManager {
    private final CactusRushPlugin plugin;
    private final Map<String, Arena> arenas = new HashMap<>();
    private ArenaBuilder arenaBuilder;

    /**
     * Creates the ArenaManager.
     * @param plugin Instance of the plugin.
     */
    public ArenaManager(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the current ArenaBuilder.
     * @return Current ArenaBuilder.
     */
    @Nullable
    public ArenaBuilder getArenaBuilder() {
        return this.arenaBuilder;
    }

    /**
     * Gets all currently loaded arenas.
     * @return Collection of arenas.
     */
    public Collection<Arena> getArenas() {
        return this.arenas.values();
    }

    /**
     * Gets all arenas that support a given mode.
     * @param mode Mode to get arenas for.
     * @return Collection of arenas for that mode.
     */
    @NotNull
    public Collection<Arena> getArenas(final Mode mode) {
        final Collection<Arena> modeArenas = new HashSet<>();

        for(final Arena arena : this.getArenas()) {
            if(arena.getModes().contains(mode)) {
                modeArenas.add(arena);
            }
        }

        return modeArenas;
    }

    /**
     * Loads an Arena from its config file, stored in MongoDB
     * @param document Arena configuration file.
     */
    public void loadArena(@NotNull final Document document) {
        final Arena arena = new Arena(document);
        this.arenas.put(arena.getFileName(), arena);
    }

    /**
     * Loads an Arena based off its id.
     * <b>Warning: Database operation that runs on whatever thread it is called from.</b>
     * @param id Id of the arena being loaded.
     */
    public void loadArena(@NotNull final String id) {
        final Document document = JadedAPI.getMongoDB().client().getDatabase("cactusrush").getCollection("maps").find(eq("fileName", id)).first();
        this.loadArena(document);
    }

    /**
     * Loads all arenas from MongoDB.
     * Creates a new thread to do this.
     */
    public void loadArenas() {
        // Clear the existing arenas.
        this.arenas.clear();

        // Run the database operations on a different thread.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final MongoDatabase database = JadedAPI.getMongoDB().client().getDatabase("cactusrush");
            final MongoCollection<Document> collection = database.getCollection("maps");

            // Loads all arenas set up in MongoDB.
            final FindIterable<Document> documentIterator = collection.find();
            for(final Document document : documentIterator) {
                this.loadArena(document);
            }
        });
    }

    /**
     * Replace the current ArenaBuilder with a new one. Can also be null.
     * @param arenaBuilder New arena builder.
     */
    public void setArenaBuilder(@Nullable final ArenaBuilder arenaBuilder) {
        this.arenaBuilder = arenaBuilder;
    }
}