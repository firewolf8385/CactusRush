package net.jadedmc.cactusrush.gui;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.jadedutils.gui.CustomGUI;
import net.jadedmc.jadedutils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopGUI extends CustomGUI {

    public ShopGUI(final CactusRushPlugin plugin) {
        super(45, "Shop");

        // Filler
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};
        for(int i : fillers) {
            setItem(i, filler);
        }

        ItemBuilder abilities = new ItemBuilder(Material.YELLOW_DYE)
                .setDisplayName("&aAbility Shop");
        setItem(20, abilities.build(), (p,a) -> new AbilitySelectorGUI(plugin, p).open(p));
        ItemBuilder cosmetics = new ItemBuilder(Material.PURPLE_TERRACOTTA)
                .setDisplayName("&aTeam Color Shop");
        setItem(24, cosmetics.build(), (p,a) -> new TeamColorGUI(plugin, p).open(p));
    }
}