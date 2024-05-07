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
package net.jadedmc.cactusrush.game;

import net.jadedmc.cactusrush.CactusRushPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GamePlayer {
    private final CactusRushPlugin plugin;
    private final Game game;
    private final UUID playerUUID;

    public GamePlayer(@NotNull final CactusRushPlugin plugin, @NotNull final Game game, @NotNull final UUID playerUUID) {
        this.plugin = plugin;
        this.game = game;
        this.playerUUID = playerUUID;
    }

    public final RoundPlayer getRoundPlayer() {
        return game.getCurrentRound().getPlayer(playerUUID);
    }

    public final RoundPlayer getRoundPlayer(final int round) {
        return game.getRoundPlayer(playerUUID, round);
    }

    @NotNull
    public final UUID getUniqueID() {
        return playerUUID;
    }
}