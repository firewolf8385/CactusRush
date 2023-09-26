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
package net.jadedmc.cactusrush.game.lobby;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.player.CactusPlayer;
import net.jadedmc.cactusrush.utils.DateUtils;
import net.jadedmc.cactusrush.utils.LevelUtils;
import net.jadedmc.cactusrush.utils.scoreboard.CustomScoreboard;
import net.jadedmc.cactusrush.utils.scoreboard.ScoreHelper;
import org.bukkit.entity.Player;

/**
 * This class creates and displays the lobby scoreboard.
 */
public class LobbyScoreboard extends CustomScoreboard {
    private final CactusRushPlugin plugin;

    /**
     * Links the player with the scoreboard.
     * @param plugin Instance of the plugin.
     * @param player Player to create scoreboard for.
     */
    public LobbyScoreboard(CactusRushPlugin plugin, Player player) {
        super(player);
        this.plugin = plugin;

        CustomScoreboard.getPlayers().put(player.getUniqueId(), this);
        update(player);
    }

    /**
     * Updates the scoreboard for a specific player.
     * @param player Player to update scoreboard for.
     */
    public void update(Player player) {
        ScoreHelper helper;

        if(ScoreHelper.hasScore(player)) {
            helper = ScoreHelper.getByPlayer(player);
        }
        else {
            helper = ScoreHelper.createScore(player);
        }

        // Gets the
        CactusPlayer cactusPlayer = plugin.cactusPlayerManager().getPlayer(player);

        // Sets up the scoreboard.
        helper.setTitle("&a&lCACTUS RUSH");
        helper.setSlot(11, "&7" + DateUtils.currentDateToString());
        helper.setSlot(10, "");
        helper.setSlot(9, "&aLevel: " + LevelUtils.getFormattedLevel(cactusPlayer.level()));
        helper.setSlot(8, "");
        helper.setSlot(7, "&aProgress: &7" + LevelUtils.getFormattedExperience(cactusPlayer.experience()) + "&7/&a" + LevelUtils.getFormattedRequiredExperience(cactusPlayer.level()));
        helper.setSlot(6, " " + LevelUtils.getSmallLevelBar(cactusPlayer.experience(), cactusPlayer.level()));
        helper.setSlot(5, "");
        helper.setSlot(4, "&aCoins: " + "&6" + cactusPlayer.coins());
        helper.setSlot(3, "&aWins: &f" + cactusPlayer.statisticsTracker().modeWins("overall"));
        helper.setSlot(2, "");
        helper.setSlot(1, "&aplay.jadedmc.net");
    }
}