package com.github.firewolf8385.cactusrush.game;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.team.Team;
import com.github.firewolf8385.cactusrush.utils.DateUtils;
import com.github.firewolf8385.cactusrush.utils.scoreboard.CustomScoreboard;
import com.github.firewolf8385.cactusrush.utils.scoreboard.ScoreHelper;
import org.bukkit.entity.Player;

public class GameScoreboard extends CustomScoreboard {
    private final Game game;
    private final CactusRush plugin;

    public GameScoreboard(CactusRush plugin, Player player, Game game) {
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
            case WAITING:
            case COUNTDOWN:
                helper.setTitle("&a&lCACTUS RUSH");
                helper.setSlot(10, "&7" + DateUtils.currentDateToString());
                helper.setSlot(9, "");
                helper.setSlot(8, "&fMap: &a" + game.getArena().getName());
                helper.setSlot(7, "&fPlayers: &a" + game.getPlayers().size() + "&f/&a" + game.getArena().getMaxPlayers(game.getTeamSize()));
                helper.setSlot(6, "");
                helper.setSlot(5, "&fAbility: &a" + plugin.getAbilityManager().getAbility(player).getName());
                helper.setSlot(4, "");

                if(game.getGameState() == GameState.COUNTDOWN) {
                    helper.setSlot(3, "&fStarting in &a" + game.getGameCountdown().getSeconds() +  "s");
                }
                else {
                    helper.setSlot(3, "&fWaiting for players");
                }

                helper.setSlot(2, "");
                helper.setSlot(1, "&ajadedmc.net");
                break;
            case RUNNING:
            case BETWEEN_ROUND:
            case END:
                helper.setTitle("&a&lCACTUS RUSH");
                helper.setSlot(15, "&7" + DateUtils.currentDateToString());
                helper.setSlot(14, "");
                helper.setSlot(13, "&fRound: &a" + game.getRound());
                helper.setSlot(12, "&fMap: &a" + game.getArena().getName());
                helper.setSlot(11, "");

                int slot = 10;
                for(Team team : game.getTeamManager().getTeams()) {
                    helper.setSlot(slot, team.getFormattedScore());
                    slot--;
                }

                helper.setSlot(slot, "");
                slot--;

                for(Team team : game.getTeamManager().getTeams()) {
                    String line = team.getColor().getChatColor() + team.getColor().getName() + " Left: &f" + team.getRemainingPlayers().size();

                    if(team.getPlayers().contains(player)) {
                        line += " &7(You)";
                    }

                    helper.setSlot(slot, line);
                    slot--;
                }

                helper.setSlot(4, "");
                helper.setSlot(3, "&fAbility: &a" + plugin.getAbilityManager().getAbility(player).getName());
                helper.setSlot(2, "");
                helper.setSlot(1, "&ajadedmc.net");
                break;
            default:
                helper.setTitle("&a&lCACTUS RUSH");
                helper.setSlot(2, "");
                helper.setSlot(1, "&ajadedmc.net");
                break;
        }
    }
}