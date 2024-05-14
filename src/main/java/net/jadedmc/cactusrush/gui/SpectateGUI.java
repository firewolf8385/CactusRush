package net.jadedmc.cactusrush.gui;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameSet;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.cactusrush.game.team.TeamPlayer;
import net.jadedmc.jadedutils.chat.ChatUtils;
import net.jadedmc.jadedutils.gui.CustomGUI;
import net.jadedmc.jadedutils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.util.Iterator;

public class SpectateGUI extends CustomGUI {

    public SpectateGUI(CactusRushPlugin plugin) {
        super(54, "Current Games");

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final GameSet games = plugin.getGameManager().getRemoteGames();
            final Iterator<Game> iterator = games.iterator();

            for(int i = 0; i < games.size(); i++) {
                final Game game = iterator.next();

                // Skip the game if it is not running.
                if(game.getGameState() != GameState.BETWEEN_ROUND && game.getGameState() != GameState.RUNNING) {
                    continue;
                }

                ItemBuilder item = new ItemBuilder(Material.CACTUS)
                        .setDisplayName("&a" + game.getMode().getId() + ": &f" + game.getArena().getName())
                        .addFlag(ItemFlag.HIDE_ATTRIBUTES);

                for(Team team : game.getTeamManager().getTeams()) {
                    item.addLore(" ");
                    item.addLore(team.getColor().getTextColor() + team.getColor().getTeamName() + ":");

                    for(TeamPlayer player : team.getTeamPlayers()) {
                        item.addLore("  &7" + player.getName());
                    }
                }

                item.addLore(" ");
                item.addLore(game.getFormattedGameScores());

                setItem(i, item.build(), (p, a) -> {
                    p.closeInventory();
                    if(game.getGameState() != GameState.RUNNING && game.getGameState() != GameState.BETWEEN_ROUND) {
                        ChatUtils.chat(p, "&cError &8» &cThat match has ended.");
                        return;
                    }

                    // TODO: Spectating
                    //game.addSpectator(p);
                    game.sendMessage("&a" + p.getName() + " is now spectating.");
                });
            }
        });
    }
}