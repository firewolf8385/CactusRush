/*
 * This file is part of JadedChat, licensed under the MIT License.
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
package com.github.firewolf8385.cactusrush.game.ability.abilities;

import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.ability.Ability;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import com.github.firewolf8385.cactusrush.utils.item.ItemBuilder;
import com.github.firewolf8385.cactusrush.utils.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlashAbility extends Ability {
    public FlashAbility(Plugin plugin) {
        super(plugin, "flash", "&e&lFlash", 30);
    }

    @Override
    public ItemStack getItemStack() {
        ItemBuilder builder = new ItemBuilder(XMaterial.YELLOW_DYE)
                .setDisplayName("&e&lFlash &7(Right Click)")
                .addLore("")
                .addLore("&7Give yourself Speed II for 5 seconds!")
                .addLore("")
                .addLore("&eCooldown: " + this.cooldown() + " seconds.");

        return builder.build();
    }

    @Override
    public void onUse(Player player, Game game) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 100, 1);
        player.addPotionEffect(speed);
        ChatUtils.chat(player, "&aYou have activated your &eFlash &eability!");
    }
}
