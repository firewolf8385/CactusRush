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
import net.jadedmc.cactusrush.game.teams.Team;
import net.jadedmc.cactusrush.utils.DateUtils;
import net.jadedmc.cactusrush.utils.scoreboard.CustomScoreboard;
import net.jadedmc.cactusrush.utils.scoreboard.ScoreHelper;
import org.bukkit.entity.Player;

public class GameScoreboard extends CustomScoreboard {
    private final Game game;
    private final CactusRushPlugin plugin;

    public GameScoreboard(CactusRushPlugin plugin, Player player, Game game) {
        super(player);
        this.plugin = plugin;
        this.game = game;

        CustomScoreboard.getPlayers().put(player.getUniqueId(), this);
        update(player);
    }

    public void update(Player player) {
        ScoreHelper helper;

        if(ScoreHelper.hasScore(player)) {
            helper = ScoreHelper.getByPlayer(player);
        }
        else {
            helper = ScoreHelper.createScore(player);
        }

        switch (game.gameState()) {
            case WAITING, COUNTDOWN -> {
                helper.setTitle("&a&lCACTUS RUSH");
                helper.setSlot(10, "&7" + DateUtils.currentDateToString());
                helper.setSlot(9, "");
                helper.setSlot(8, "&fMap: &a" + game.arena().name());
                helper.setSlot(7, "&fPlayers: &a" + game.players().size() + "&f/&a" + game.mode().maxPlayerCount());
                helper.setSlot(6, "");
                helper.setSlot(5, "&fAbility: " + plugin.abilityManager().getAbility(player).name());
                helper.setSlot(4, "");
                helper.removeSlot(3);
                helper.removeSlot(2);
                helper.removeSlot(1);
                helper.setSlot(1, "&ajadedmc.net");
            }

            case RUNNING, BETWEEN_ROUND, END -> {
                helper.setTitle("&a&lCACTUS RUSH");
                helper.setSlot(15, "&7" + DateUtils.currentDateToString());
                helper.setSlot(14, "");
                helper.setSlot(13, "&fRound: &a" + game.round());
                helper.setSlot(12, "&fMap: &a" + game.arena().name());
                helper.setSlot(11, "");

                int slot = 10;
                for(Team team : game.teamManager().teams()) {
                    helper.setSlot(slot, team.formattedScore());
                    slot--;
                }

                helper.setSlot(slot, "");
                slot--;

                for(Team team : game.teamManager().teams()) {
                    String line = team.color().textColor() + team.color().teamName() + " Left: &f" + team.players().size();

                    if(team.players().contains(player)) {
                        line += " &7(You)";
                    }

                    helper.setSlot(slot, line);
                    slot--;
                }

                helper.setSlot(4, "");


                if(!game.spectators().contains(player)) {
                    helper.setSlot(3, "&fAbility: " + plugin.abilityManager().getAbility(player).name());
                    helper.setSlot(2, "");
                }
                else {
                    helper.removeSlot(3);
                    helper.removeSlot(2);
                }

                helper.setSlot(1, "&ajadedmc.net");
            }
        }
    }
}