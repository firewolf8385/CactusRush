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

import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class Round {
    private final int roundNumber;
    private final Game game;
    private final Collection<RoundPlayer> players = new HashSet<>();

    public Round(@NotNull final Game game, final int roundNumber) {
        this.game = game;
        this.roundNumber = roundNumber;
    }

    @Nullable
    public final RoundPlayer getPlayer(@NotNull final UUID playerUUID) {
        for(final RoundPlayer roundPlayer : players) {
            if(roundPlayer.getUniqueID().equals(playerUUID)) {
                return roundPlayer;
            }
        }

        return null;
    }

    @NotNull
    public final Document toDocument() {
        final Document document = new Document();

        final Document playersDocument = new Document();
        players.forEach(player -> playersDocument.append(player.getUniqueID().toString(), player.toDocument()));
        document.append("players", playersDocument);

        return document;
    }
}