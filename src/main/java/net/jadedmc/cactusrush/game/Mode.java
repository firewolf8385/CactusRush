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
package net.jadedmc.cactusrush.game;

/**
 * Represents a game mode for Cactus Rush.
 * ALL is used for stat tracking only.
 */
public enum Mode {
    ONE_V_ONE(2,1),
    TWO_V_TWO(2,2),
    THREE_V_THREE(2,3),
    FOUR_V_FOUR(2,4);

    private final int teamCount;
    private final int teamSize;
    Mode(int teamCount, int teamSize) {
        this.teamCount = teamCount;
        this.teamSize = teamSize;
    }

    /**
     * Get the number of teams the mode has.
     * @return Team count of the mode.
     */
    public int teamCount() {
        return teamCount;
    }

    /**
     * Get the side of each team for the mode.
     * @return Team size of the mode.
     */
    public int teamSize() {
        return teamSize;
    }
}