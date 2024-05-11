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
package net.jadedmc.cactusrush.game.ability;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.ability.abilities.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AbilityManager {
    private final CactusRushPlugin plugin;
    private final Map<String, Ability> abilities = new LinkedHashMap<>();
    private final Map<ItemStack, Ability> abilityItems = new HashMap<>();

    public AbilityManager(final CactusRushPlugin plugin) {
        this.plugin = plugin;
        addAbility(new FlashAbility(plugin));
        addAbility(new BreakerAbility(plugin));
        addAbility(new DeathballAbility(plugin));
        addAbility(new BlindAbility(plugin));
        addAbility(new FreezeAbility(plugin));
        addAbility(new PhaseAbility(plugin));
    }

    private void addAbility(final Ability ability) {
        abilities.put(ability.getId(), ability);
        abilityItems.put(ability.getItemStack(), ability);
    }

    public Ability getAbility(final Player player) {
        return getAbility(plugin.getCactusPlayerManager().getPlayer(player).getSelectedAbility());
    }

    public Ability getAbility(ItemStack itemStack) {
        if(abilityItems.containsKey(itemStack)) {
            return abilityItems.get(itemStack);
        }

        return null;
    }

    public Ability getAbility(String id) {
        if(abilities.containsKey(id)) {
            return abilities.get(id);
        }

        return null;
    }

    public Collection<Ability> getAbilities() {
        return abilities.values();
    }

    public void removePlayer(final Player player) {
        getAbility(player).removeCooldown(player);
    }
}