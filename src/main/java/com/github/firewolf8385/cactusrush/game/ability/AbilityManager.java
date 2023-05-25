package com.github.firewolf8385.cactusrush.game.ability;

import com.github.firewolf8385.cactusrush.game.ability.abilities.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AbilityManager {
    private final Map<String, Ability> abilities = new LinkedHashMap<>();
    private final Map<ItemStack, Ability> abilityItems = new HashMap<>();
    private final Map<Player, Ability> players = new HashMap<>();

    public AbilityManager(Plugin plugin) {
        addAbility(new FlashAbility(plugin));
        addAbility(new BreakerAbility(plugin));
        addAbility(new BallOfDeathAbility(plugin));
        addAbility(new BlindAbility(plugin));
        addAbility(new FreezeAbility(plugin));
    }

    private void addAbility(Ability ability) {
        abilities.put(ability.getId(), ability);
        abilityItems.put(ability.getItemStack(), ability);
    }

    public void addPlayer(Player player, Ability ability) {
        players.put(player, ability);
    }

    public Ability getAbility(Player player) {
        if(players.containsKey(player)) {
            return players.get(player);
        }

        return null;
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

    public void removePlayer(Player player) {

        if(players.containsKey(player)) {
            players.get(player).removeCooldown(player);
        }

        players.remove(player);
    }
}
