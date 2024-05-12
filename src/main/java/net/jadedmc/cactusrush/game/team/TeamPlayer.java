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
package net.jadedmc.cactusrush.game.team;

import net.jadedmc.cactusrush.game.ability.Ability;
import net.jadedmc.jadedutils.player.CustomPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamPlayer implements CustomPlayer {
    private final UUID playerUUID;
    private final String playerName;
    private Ability ability;
    private int cactiBroke = 0;
    private int cactiPlaced = 0;
    private int eggsThrown = 0;
    private int goalsScored = 0;
    private int abilitiesUsed = 0;
    private int deaths = 0;
    private int cactiDeaths = 0;
    private int voidDeaths = 0;
    private int abilityDeaths = 0;

    public TeamPlayer(@NotNull final UUID playerUUID, @NotNull final String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
    }

    @Override
    public String getName() {
        return playerName;
    }

    @Override
    public UUID getUniqueId() {
        return playerUUID;
    }
}