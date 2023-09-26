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
import net.jadedmc.cactusrush.game.abilitiy.Ability;
import net.jadedmc.cactusrush.player.CactusPlayer;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.cactusrush.utils.item.ItemBuilder;
import net.jadedmc.jadedcore.utils.gui.CustomGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AbilitySelectorGUI extends CustomGUI {

    public AbilitySelectorGUI(CactusRushPlugin plugin, Player player) {
        super(54, "Abilities");

        // Filler
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53};
        //int[] slots = {20,22,24,30,32};
        int[] slots = {21,22,23,30,31,32};

        for(int i : fillers) {
            setItem(i, filler);
        }

        int i = 0;
        for(Ability ability : plugin.abilityManager().getAbilities()) {

            CactusPlayer cactusPlayer = plugin.cactusPlayerManager().getPlayer(player);

            if(cactusPlayer.unlockedAbilities().contains(ability.id()) || ability.price() == 0) {
                ItemBuilder builder = new ItemBuilder(ability.itemStack());
                setItem(slots[i], builder.build(), (p,a) -> {
                    p.closeInventory();

                    Game game = plugin.gameManager().getGame(p);

                    // Exit if the player isn't in a game.
                    if(game == null) {
                        cactusPlayer.selectedAbility(ability.id());
                        ChatUtils.chat(p, "&f" + ability.name() + " &ahas been selected!.");
                        p.closeInventory();
                        return;
                    }

                    // Disable if a round is running.
                    if(game.gameState() != GameState.BETWEEN_ROUND && game.gameState() != GameState.WAITING && game.gameState() != GameState.COUNTDOWN) {
                        return;
                    }

                    // Replace the player's ability.
                    plugin.abilityManager().removePlayer(p);
                    plugin.cactusPlayerManager().getPlayer(p).selectedAbility(ability.id());

                    // Gives them the ability item if they aren't in the waiting area.
                    if(game.gameState() == GameState.BETWEEN_ROUND) {
                        ability.giveItem(p);
                    }
                });
            }
            else {
                ItemBuilder builder = new ItemBuilder(ability.itemStack())
                        .setMaterial(Material.GRAY_DYE)
                        .addLore("")
                        .addLore("&6Price: " + ability.price())
                        .addLore("&cClick to purchase!");
                setItem(slots[i], builder.build(), (p,a) -> {
                    if(cactusPlayer.coins() < ability.price()) {
                        ChatUtils.chat(p, "&cError &8» &cYou do not have enough coins for that!");
                        return;
                    }

                    cactusPlayer.removeCoins(ability.price());
                    cactusPlayer.unlockAbility(ability);

                    Game game = plugin.gameManager().getGame(p);

                    // Exit if the player isn't in a game.
                    if(game == null) {
                        cactusPlayer.selectedAbility(ability.id());
                        ChatUtils.chat(p, "&f" + ability.name() + " &ahas been purchased and selected!.");
                        return;
                    }

                    // Disable if a round is running.
                    if(game.gameState() != GameState.BETWEEN_ROUND && game.gameState() != GameState.WAITING && game.gameState() != GameState.COUNTDOWN) {
                        return;
                    }

                    // Replace the player's ability.
                    plugin.abilityManager().removePlayer(p);
                    plugin.cactusPlayerManager().getPlayer(p).selectedAbility(ability.id());

                    ChatUtils.chat(p, "&f" + ability.name() + " &ahas been purchased and selected!.");
                    p.closeInventory();

                    // Gives them the ability item if they aren't in the waiting area.
                    if(game.gameState() == GameState.BETWEEN_ROUND) {
                        ability.giveItem(p);
                    }
                });
            }

            i++;
        }
    }
}