package com.github.firewolf8385.cactusrush.game.ability;

import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.utils.item.ItemBuilder;
import com.github.firewolf8385.cactusrush.utils.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public abstract class Ability {
    private final Plugin plugin;
    private final Map<Player, AbilityCooldown> coolDown = new HashMap<>();
    private final String id;
    private final String name;
    private final int coolDownLength;

    public Ability(final Plugin plugin, final String id, final String name, int coolDownLength) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.coolDownLength = coolDownLength;
    }

    public AbilityCooldown getAbilityCooldown(Player player) {
        return coolDown.get(player);
    }

    private ItemStack getCooldownItem(Player player) {
        int amount = 1;

        AbilityCooldown abilityCooldown = getAbilityCooldown(player);

        if(abilityCooldown != null && abilityCooldown.getSeconds() > 64) {
            amount = 64;
        }
        else if(abilityCooldown != null && abilityCooldown.getSeconds() > 1 ) {
            amount = getAbilityCooldown(player).getSeconds();
        }

        ItemBuilder builder = new ItemBuilder(XMaterial.GRAY_DYE, amount).setDisplayName("&c" + name);
        return builder.build();
    }

    public String getId() {
        return id;
    }

    public abstract ItemStack getItemStack();

    public String getName() {
        return name;
    }

    public void giveItem(Player player) {
        if(getAbilityCooldown(player) == null) {
            player.getInventory().setItem(2, getItemStack());
            return;
        }

        player.getInventory().setItem(2, getCooldownItem(player));
    }

    public void removeCooldown(Player player) {
        coolDown.remove(player);
    }

    public void updateAbilityItem(Player player) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(!coolDown.containsKey(player)) {
                return;
            }

            if(getAbilityCooldown(player).getSeconds() < 1) {
                coolDown.remove(player);
                player.getInventory().setItem(2, getItemStack());
                return;
            }
            player.getInventory().setItem(2, getCooldownItem(player));
            updateAbilityItem(player);
        }, 20);
    }

    public void useAbility(Player player, Game game) {
        onUse(player, game);
        AbilityCooldown abilityCooldown = new AbilityCooldown(plugin, coolDownLength);
        abilityCooldown.start();
        coolDown.put(player, abilityCooldown);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
            player.getInventory().setItem(2, getCooldownItem(player));
            updateAbilityItem(player);
        }, 1);
    }

    public abstract void onUse(Player player, Game game);
}