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

public class BreakerAbility extends Ability {
    public BreakerAbility(Plugin plugin) {
        super(plugin, "breaker", "&6&lBreaker", 45);
    }

    @Override
    public ItemStack getItemStack() {
        ItemBuilder builder = new ItemBuilder(XMaterial.ORANGE_DYE)
                .setDisplayName("&6&lBreaker &7(Right Click)")
                .addLore("")
                .addLore("&7Make yourself instantly break cactus")
                .addLore("&7for 3 seconds!")
                .addLore("")
                .addLore("&eCooldown: 45 seconds");

        return builder.build();
    }

    @Override
    public void onUse(Player player, Game game) {
        PotionEffect haste = new PotionEffect(PotionEffectType.FAST_DIGGING, 60, 254);
        player.addPotionEffect(haste);
        ChatUtils.chat(player, "&aYou have activated your &6Breaker &aability!");
    }
}