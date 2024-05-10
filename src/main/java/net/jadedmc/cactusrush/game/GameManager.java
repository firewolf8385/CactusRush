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
package net.jadedmc.cactusrush.game;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.jadedcore.JadedAPI;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GameManager {
    private final CactusRushPlugin plugin;
    private final GameSet localGames = new GameSet();

    public GameManager(@NotNull final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public final GameSet getLocalGames() {
        return localGames;
    }

    /**
     * Retrieves a Set of all parties stored in Redis.
     * <b>Warning: Database operation. Call asynchronously.</b>
     * @return Set containing Parties grabbed from Redis.
     */
    @NotNull
    public GameSet getRemoteGames() {
        final GameSet remoteGames = new GameSet();

        final Set<String> keys = JadedAPI.getRedis().keys("games:cactusrush:*");
        for(final String key : keys) {
            final Document gameDocument = Document.parse(JadedAPI.getRedis().get(key));
            remoteGames.add(new Game(plugin, gameDocument));
        }

        return remoteGames;
    }
}