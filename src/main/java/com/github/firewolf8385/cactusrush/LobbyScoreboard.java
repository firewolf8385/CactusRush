package com.github.firewolf8385.cactusrush;

import com.github.firewolf8385.cactusrush.player.CactusPlayer;
import com.github.firewolf8385.cactusrush.utils.DateUtils;
import com.github.firewolf8385.cactusrush.utils.LevelUtils;
import com.github.firewolf8385.cactusrush.utils.scoreboard.CustomScoreboard;
import com.github.firewolf8385.cactusrush.utils.scoreboard.ScoreHelper;
import org.bukkit.entity.Player;

/**
 * This class creates and displays the lobby scoreboard.
 */
public class LobbyScoreboard extends CustomScoreboard {
    private final CactusRush plugin;

    /**
     * Links the player with the scoreboard.
     * @param plugin Instance of the plugin.
     * @param player Player to create scoreboard for.
     */
    public LobbyScoreboard(CactusRush plugin, Player player) {
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
        CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);

        // Sets up the scoreboard.
        helper.setTitle("&a&lCACTUS RUSH");
        helper.setSlot(11, "&7" + DateUtils.currentDateToString());
        helper.setSlot(10, "");
        helper.setSlot(9, "&aLevel: " + LevelUtils.getFormattedLevel(cactusPlayer.getLevel()));
        helper.setSlot(8, "");
        helper.setSlot(7, "&aProgress: &7" + LevelUtils.getFormattedExperience(cactusPlayer.getExperience()) + "&7/&a" + LevelUtils.getFormattedRequiredExperience(cactusPlayer.getLevel()));
        helper.setSlot(6, " " + LevelUtils.getSmallLevelBar(cactusPlayer.getExperience(), cactusPlayer.getLevel()));
        helper.setSlot(5, "");
        helper.setSlot(4, "&aCoins: " + "&6" + cactusPlayer.getCoins());
        helper.setSlot(3, "&aWins: &f" + cactusPlayer.getWins());
        helper.setSlot(2, "");
        helper.setSlot(1, "&ajadedmc.net");
    }
}