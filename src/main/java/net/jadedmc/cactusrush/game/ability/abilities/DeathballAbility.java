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
package net.jadedmc.cactusrush.game.ability.abilities;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.ability.Ability;
import net.jadedmc.jadedutils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DeathballAbility extends Ability {

    /**
     * Create's the Deathball Ability, which kills a player on impact.
     * @param plugin Instance of the plugin.
     */
    public DeathballAbility(@NotNull final CactusRushPlugin plugin) {
        super(plugin, "deathball", "&f&lDeathball", 50, 750);
    }

    /**
     * Gets the ItemStack of the ability.
     * @return Ability ItemStack.
     */
    @Override
    public ItemStack getItemStack() {
        final ItemBuilder builder = new ItemBuilder(Material.SNOWBALL)
                .setDisplayName("&f&lDeathball &7(Right Click)")
                .addLore("")
                .addLore("&7Snowball that instantly kills")
                .addLore("&7an opponent upon impact!")
                .addLore("")
                .addLore("&eCooldown: " + this.cooldown() + " seconds.");

        return builder.build();
    }

    @Override
    public boolean onUse(Player player, Game game) {
        return true;
    }
}