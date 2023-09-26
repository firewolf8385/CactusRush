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
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks player statistics on a per-game basis.
 */
public class GameStatisticsTracker {
    private final CactusRushPlugin plugin;
    private final Game game;
    private final Map<Player, Integer> gameCactiBroken = new HashMap<>();
    private final Map<Player, Integer> gameCactiPlaced = new HashMap<>();
    private final Map<Player, Integer> gameEggsThrown = new HashMap<>();
    private final Map<Player, Integer> gameGoalsScored = new HashMap<>();
    private final Map<Player, Integer> roundCactiBroken = new HashMap<>();
    private final Map<Player, Integer> roundCactiPlaced = new HashMap<>();
    private final Map<Player, Integer> roundEggsThrown = new HashMap<>();
    private final Map<Player, Integer> roundGoalsScored = new HashMap<>();

    /**
     * Creates the tracker.
     * @param plugin Instance of the plugin.
     * @param game Game being tracked.
     */
    public GameStatisticsTracker(final CactusRushPlugin plugin, final Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    /**
     * Adds a player to the tracker.
     * @param player Player to add.
     */
    public void addPlayer(Player player) {
        gameCactiBroken.put(player, 0);
        gameCactiPlaced.put(player, 0);
        gameEggsThrown.put(player, 0);
        gameGoalsScored.put(player, 0);
        roundCactiBroken.put(player, 0);
        roundCactiPlaced.put(player, 0);
        roundEggsThrown.put(player, 0);
        roundGoalsScored.put(player, 0);
    }

    /**
     * Increase cacti broken counter.
     * @param player Player who broke the cactus.
     */
    public void addBrokenCacti(Player player) {
        gameCactiBroken.put(player, gameCactiBroken.get(player) + 1);
        roundCactiBroken.put(player, roundCactiBroken.get(player) + 1);
        plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addCactiBroke(game.mode().id(), game.arena().id());
    }

    /**
     * Increase cacti placed counter.
     * @param player Player who placed the cactus.
     */
    public void addPlacedCacti(Player player) {
        gameCactiPlaced.put(player, gameCactiPlaced.get(player) + 1);
        roundCactiPlaced.put(player, roundCactiPlaced.get(player) + 1);
        plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addCactiPlaced(game.mode().id(), game.arena().id());
    }

    /**
     * Increase the eggs thrown counter.
     * @param player Player who threw the egg.
     */
    public void addEggThrown(Player player) {
        gameEggsThrown.put(player, gameEggsThrown.get(player) + 1);
        roundEggsThrown.put(player, roundEggsThrown.get(player) + 1);
        plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addEggsThrown(game.mode().id(), game.arena().id());
    }

    /**
     * Increase the goals scored counter.
     * @param player Player who scored the goal.
     */
    public void addGoalScored(Player player) {
        gameGoalsScored.put(player, gameGoalsScored.get(player) + 1);
        roundGoalsScored.put(player, roundGoalsScored.get(player) + 1);
        plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addGoalsScored(game.mode().id(), game.arena().id());
    }

    /**
     * Get the number of cacti a player has brokenin the game.
     * @param player Player to get cacti broken count of.
     * @return Number of cacti they have broken.
     */
    public int getGameCactiBroken(Player player) {
        return gameCactiBroken.get(player);
    }

    /**
     * Get the number of cacti a player has placed in the game.
     * @param player Player to get cacti placed count of.
     * @return Number of cacti they have placed.
     */
    public int getGameCactiPlaced(Player player) {
        return gameCactiPlaced.get(player);
    }

    /**
     * Get the number of eggs a player has thrown in the game.
     * @param player Player to get eggs thrown count of.
     * @return The number of eggs they have thrown.
     */
    public int getGameEggsThrown(Player player) {
        return gameEggsThrown.get(player);
    }

    /**
     * Get the number of goals a player has scored in the game.
     * @param player Player to get goals scored count of.
     * @return The number of goals they have scored.
     */
    public int getGameGoalsScored(Player player) {
        return gameGoalsScored.get(player);
    }

    /**
     * Get the number of cacti a player has broken in the current round.
     * @param player The player to get the count of.
     * @return The number of cacti they have broken.
     */
    public int getRoundCactiBroken(Player player) {
        return roundCactiBroken.get(player);
    }

    /**
     * Get the number of cacti a player has placed in the current round.
     * @param player Player to get the count of.
     * @return The number of cacti they have placed.
     */
    public int getRoundCactiPlaced(Player player) {
        return roundCactiPlaced.get(player);
    }

    /**
     * Get the number of eggs a player has thrown in the current round.
     * @param player Player to get count of.
     * @return Number of eggs they have thrown.
     */
    public int getRoundEggsThrown(Player player) {
        return roundEggsThrown.get(player);
    }

    /**
     * Get the number of goals a player has scored in the current round.
     * @param player Player to get count of.
     * @return Number of eggs they have thrown.
     */
    public int getRoundGoalsScored(Player player) {
        return roundGoalsScored.get(player);
    }

    /**
     * Removes a player from the tracker.
     * Used if they leave the game.
     * @param player Player to remove.
     */
    public void removePlayer(Player player) {
        gameCactiBroken.remove(player);
        gameCactiPlaced.remove(player);
        gameEggsThrown.remove(player);
        gameGoalsScored.remove(player);
        roundCactiBroken.remove(player);
        roundCactiPlaced.remove(player);
        roundEggsThrown.remove(player);
        roundGoalsScored.remove(player);
    }

    /**
     * Resets the round statistics.
     * Used when starting a new round.
     */
    public void resetRound() {
        resetMap(roundCactiBroken);
        resetMap(roundCactiPlaced);
        resetMap(roundEggsThrown);
        resetMap(roundGoalsScored);
    }

    /**
     * Set all players in a map to 0.
     * @param map Map to reset.
     */
    private void resetMap(Map<Player, Integer> map) {
        for(Player player : map.keySet()) {
            map.put(player, 0);
        }
    }
}