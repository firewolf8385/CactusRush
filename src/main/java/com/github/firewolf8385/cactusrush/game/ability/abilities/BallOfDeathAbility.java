package com.github.firewolf8385.cactusrush.game.ability.abilities;

import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.ability.Ability;
import com.github.firewolf8385.cactusrush.utils.item.ItemBuilder;
import com.github.firewolf8385.cactusrush.utils.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class BallOfDeathAbility extends Ability {
    public BallOfDeathAbility(Plugin plugin) {
        super(plugin, "deathball", "&f&lDeathball", 9999);
    }

    @Override
    public ItemStack getItemStack() {
        ItemBuilder builder = new ItemBuilder(XMaterial.SNOWBALL)
                .setDisplayName("&f&lDeathball &7(Right Click)")
                .addLore("")
                .addLore("&7Snowball that instantly kills")
                .addLore("&7an opponent upon impact!")
                .addLore("")
                .addLore("&eCooldown: 1 per round");

        return builder.build();
    }

    @Override
    public void onUse(Player player, Game game) {}
}