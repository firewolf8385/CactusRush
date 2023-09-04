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
package net.jadedmc.cactusrush.gui;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.cactusrush.utils.item.ItemBuilder;
import net.jadedmc.jadedcore.utils.gui.CustomGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Iterator;

public class SpectateGUI extends CustomGUI {

    public SpectateGUI(CactusRushPlugin plugin) {
        super(54, "Current Games");

        Iterator<Game> activeGames = plugin.gameManager().activeGames().iterator();

        for(int i = 0; i < plugin.gameManager().activeGames().size(); i++) {
            Game game = activeGames.next();


            ItemBuilder item = new ItemBuilder(Material.CACTUS)
                    .setDisplayName("&a" + game.arena().name())
                    .addFlag(ItemFlag.HIDE_ATTRIBUTES);

            for(Player p : game.players()) {
                item.addLore("&7  - " + p.getName());
            }

            setItem(i, item.build(), (p, a) -> {
                p.closeInventory();
                if(game.gameState() != GameState.RUNNING && game.gameState() != GameState.BETWEEN_ROUND) {
                    ChatUtils.chat(p, "&cError &8» &cThat match has ended.");
                    return;
                }

                game.addSpectator(p);
                game.sendMessage("&a" + p.getName() + " is now spectating.");
            });
        }
    }
}