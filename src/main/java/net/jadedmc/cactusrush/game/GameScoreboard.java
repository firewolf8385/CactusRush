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
import net.jadedmc.cactusrush.game.round.RoundPlayer;
import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedutils.DateUtils;
import net.jadedmc.jadedutils.scoreboard.CustomScoreboard;
import net.jadedmc.jadedutils.scoreboard.ScoreHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GameScoreboard extends CustomScoreboard {
    private final Game game;
    private final CactusRushPlugin plugin;

    public GameScoreboard(@NotNull final CactusRushPlugin plugin, @NotNull final Player player, @NotNull final Game game) {
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

        switch (game.getGameState()) {
            case WAITING, COUNTDOWN -> {
                helper.setTitle("&a&lCACTUS RUSH");
                helper.setSlot(10, "&7" + DateUtils.currentDateToString() + " &8" + JadedAPI.getCurrentInstance().getName());
                helper.setSlot(9, "");
                helper.setSlot(8, "&fMap: &a" + game.getArena().getName());

                if(game.getMode() != Mode.DUEL) {
                    helper.setSlot(7, "&fPlayers: &a" + game.getPlayers().size() + "&f/&a" + game.getMode().getMaxPlayerCount());
                }
                else {
                    helper.setSlot(7, "&fPlayers: &a" + game.getPlayers().size());
                }

                helper.setSlot(6, "");
                helper.setSlot(5, "&fAbility: " + plugin.getAbilityManager().getAbility(player).name());
                helper.setSlot(4, "");

                if(game.getGameState() == GameState.COUNTDOWN) {
                    helper.setSlot(3, "&fStarting in &a" + game.getGameCountdown().seconds() +  "s");
                }
                else {
                    helper.setSlot(3, "&fWaiting for players");
                }

                helper.setSlot(2, " ");
                helper.removeSlot(1);
                helper.setSlot(1, "&aplay.jadedmc.net");
            }

            case RUNNING, BETWEEN_ROUND, END -> {
                helper.setTitle("&a&lCACTUS RUSH");
                helper.setSlot(15, "&7" + DateUtils.currentDateToString() + " &8" + JadedAPI.getCurrentInstance().getName());
                helper.setSlot(14, "");
                helper.setSlot(13, "&fRound: &a" + game.getRoundManager().getCurrentRoundNumber());
                helper.setSlot(12, "&fMap: &a" + game.getArena().getName());
                helper.setSlot(11, "");

                int slot = 10;
                for(final Team team : game.getTeamManager().getTeams()) {
                    helper.setSlot(slot, team.getFormattedScore());
                    slot--;
                }

                helper.setSlot(8, " ");

                if(!game.getSpectators().contains(player.getUniqueId())) {
                    final RoundPlayer roundPlayer = this.game.getRoundManager().getCurrentRound().getPlayers().getPlayer(player);

                    if(roundPlayer == null) {
                        return;
                    }

                    helper.setSlot(7, "Cacti Placed: &a" + roundPlayer.getCactiPlaced());
                    helper.setSlot(6, "Eggs Thrown: &a" + roundPlayer.getEggsThrown());
                    helper.setSlot(5, " ");
                    helper.setSlot(4, "&fAbility: " + plugin.getAbilityManager().getAbility(player).name());
                    helper.setSlot(3, "");
                }
                else {
                    helper.removeSlot(7);
                    helper.removeSlot(6);
                    helper.removeSlot(5);
                    helper.removeSlot(4);
                    helper.removeSlot(3);
                }

                helper.removeSlot(2);
                helper.setSlot(1, "&aplay.jadedmc.net");

            }
        }
    }

}