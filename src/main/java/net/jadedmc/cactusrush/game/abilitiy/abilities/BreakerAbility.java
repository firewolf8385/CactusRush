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
package net.jadedmc.cactusrush.game.abilitiy.abilities;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.abilitiy.Ability;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.cactusrush.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Runs the breaker ability, which allows a player to instantly break cactus.
 */
public class BreakerAbility extends Ability {
    /**
     * Creates the ability.
     * @param plugin Instance of the plugin.
     */
    public BreakerAbility(CactusRushPlugin plugin) {
        super(plugin, "breaker", "&6&lBreaker", 45, 500);
    }

    /**
     * Get's the ability icon.
     * @return Ability icon.
     */
    @Override
    public ItemStack itemStack() {
        ItemBuilder builder = new ItemBuilder(Material.ORANGE_DYE)
                .setDisplayName("&6&lBreaker &7(Right Click)")
                .addLore("")
                .addLore("&7Make yourself instantly break cactus")
                .addLore("&7for 3 seconds!")
                .addLore("")
                .addLore("&eCooldown: " + this.cooldown() + " seconds.");

        return builder.build();
    }

    /**
     * Runs when the ability is used.
     * @param player Player who used the ability.
     * @param game Game the ability was used in.
     */
    @Override
    public void onUse(Player player, Game game) {
        PotionEffect haste = new PotionEffect(PotionEffectType.FAST_DIGGING, 60, 254);
        player.addPotionEffect(haste);
        ChatUtils.chat(player, "&aYou have activated your &6Breaker &aability!");

        for(Player spectator : game.spectators()) {
            ChatUtils.chat(spectator, game.teamManager().getTeam(player).color().textColor() + player.getName() + " &ahas activated their &6Breaker &aability!");
        }
    }
}