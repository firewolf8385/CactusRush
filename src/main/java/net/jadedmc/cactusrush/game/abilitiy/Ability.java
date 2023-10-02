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
package net.jadedmc.cactusrush.game.abilitiy;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class Ability {
    private final CactusRushPlugin plugin;
    private final Map<Player, AbilityCooldown> coolDown = new HashMap<>();
    private final String id;
    private final String name;
    private final int coolDownLength;
    private final int price;

    public Ability(final CactusRushPlugin plugin, final String id, final String name, int coolDownLength, int price) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.coolDownLength = coolDownLength;
        this.price = price;
    }

    public int cooldown() {
        return coolDownLength;
    }

    public AbilityCooldown getAbilityCooldown(Player player) {
        return coolDown.get(player);
    }

    private ItemStack cooldownItem(Player player) {
        int amount = 1;

        AbilityCooldown abilityCooldown = getAbilityCooldown(player);

        if(abilityCooldown != null && abilityCooldown.seconds() > 64) {
            amount = 64;
        }
        else if(abilityCooldown != null && abilityCooldown.seconds() > 1 ) {
            amount = getAbilityCooldown(player).seconds();
        }

        ItemBuilder builder = new ItemBuilder(Material.GRAY_DYE, amount).setDisplayName("&c" + name);
        return builder.build();
    }

    public String id() {
        return id;
    }

    /**
     * Gets the ability's icon.
     * @return Ability's icon.
     */
    public abstract ItemStack itemStack();

    public String name() {
        return name;
    }

    public void giveItem(Player player) {
        if(getAbilityCooldown(player) == null) {
            player.getInventory().setItem(2, itemStack());
            return;
        }

        player.getInventory().setItem(2, cooldownItem(player));
    }

    public int price() {
        return price;
    }

    public void removeCooldown(Player player) {
        coolDown.remove(player);
    }

    public void updateAbilityItem(Player player) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(!coolDown.containsKey(player)) {
                return;
            }

            if(getAbilityCooldown(player).seconds() < 1) {
                coolDown.remove(player);
                player.getInventory().setItem(2, itemStack());
                player.setLevel(0);
                player.setExp(0);
                return;
            }
            player.getInventory().setItem(2, cooldownItem(player));
            player.setLevel(getAbilityCooldown(player).seconds());
            updateAbilityItem(player);
        }, 20);
    }

    public void useAbility(Player player, Game game) {
        boolean used = onUse(player, game);

        // Exit if the ability wasn't used.
        if(!used) {
            return;
        }

        // Statistic tracking
        if(game.mode() != Mode.DUEL) {
            plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().addAbilityUse(game.mode().id(), game.arena().id(), id);
        }

        AbilityCooldown abilityCooldown = new AbilityCooldown(plugin, coolDownLength);
        abilityCooldown.start();
        coolDown.put(player, abilityCooldown);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
            player.getInventory().setItem(2, cooldownItem(player));
            updateAbilityItem(player);
        }, 1);
    }

    /**
     * Runs when the ability is used.
     * @param player Player who used the ability.
     * @param game Game the ability was used in.
     */
    public abstract boolean onUse(Player player, Game game);
}