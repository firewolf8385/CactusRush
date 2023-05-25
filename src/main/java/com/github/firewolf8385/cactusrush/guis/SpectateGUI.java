package com.github.firewolf8385.cactusrush.guis;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import com.github.firewolf8385.cactusrush.utils.item.ItemBuilder;
import net.jadedmc.jadedcore.utils.gui.CustomGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class SpectateGUI extends CustomGUI {

    public SpectateGUI(CactusRush plugin) {
        super(54, "Current Games");

        for(int i = 0; i < plugin.getGameManager().getActiveGames().size(); i++) {
            Game game = plugin.getGameManager().getActiveGames().get(i);


            ItemBuilder item = new ItemBuilder(Material.BOW)
                    .setDisplayName("&a" + game.getArena().getName())
                    .addFlag(ItemFlag.HIDE_ATTRIBUTES);

            for(Player p : game.getPlayers()) {
                item.addLore("&7  - " + p.getName());
            }

            setItem(i, item.build(), (p, a) -> {
                p.closeInventory();
                if(game.getGameState() != GameState.RUNNING && game.getGameState() != GameState.BETWEEN_ROUND) {
                    ChatUtils.chat(p, "&cError &8» &cThat match has ended.");
                    return;
                }

                game.addSpectator(p);
                game.sendMessage("&a" + p.getName() + " is now spectating.");
            });
        }
    }
}