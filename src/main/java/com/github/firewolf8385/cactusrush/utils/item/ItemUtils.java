package com.github.firewolf8385.cactusrush.utils.item;

import net.jadedmc.jadedcore.features.items.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ItemUtils {
    public static void giveLobbyItems(Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);

        player.getInventory().setItem(0, CustomItem.GAME_SELECTOR.toItemStack());
        player.getInventory().setItem(1, new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("&a&lProfile").build());
        player.getInventory().setItem(2, new ItemBuilder(Material.EMERALD).setDisplayName("&a&lCosmetics").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lModes").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.PAPER).setDisplayName("&a&lStats").build());
    }
}
