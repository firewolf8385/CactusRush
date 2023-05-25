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
                .addLore("&eCooldown: 30 seconds");

        return builder.build();
    }

    @Override
    public void onUse(Player player, Game game) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 100, 1);
        player.addPotionEffect(speed);
        ChatUtils.chat(player, "&aYou have activated your &eFlash &eability!");
    }
}
