package net.jadedmc.cactusrush.game.ability;

import net.jadedmc.cactusrush.CactusRushPlugin;
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
        abilities.put(ability.id(), ability);
        abilityItems.put(ability.itemStack(), ability);
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

    public void removePlayer(Player player) {
        getAbility(player).removeCooldown(player);
    }
}