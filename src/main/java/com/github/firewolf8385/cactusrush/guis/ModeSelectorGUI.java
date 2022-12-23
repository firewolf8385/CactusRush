package com.github.firewolf8385.cactusrush.guis;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.utils.item.ItemBuilder;
import com.github.firewolf8385.cactusrush.utils.xseries.XMaterial;
import net.jadedmc.jadedcore.utils.gui.CustomGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ModeSelectorGUI extends CustomGUI {

    public ModeSelectorGUI(CactusRush plugin) {
        super(45, "Modes");

        // Filler
        ItemStack filler = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};
        for(int i : fillers) {
            setItem(i, filler);
        }

        // Modes
        ItemBuilder ones = new ItemBuilder(Material.CACTUS).setDisplayName("&a1v1");
        setItem(19, ones.build(), (p,a) -> new ModeSelectorGUI(plugin, 2, 1).open(p));

        ItemBuilder twos = new ItemBuilder(Material.CACTUS, 2).setDisplayName("&a2v2");
        setItem(21, twos.build(), (p,a) -> new ModeSelectorGUI(plugin, 2, 2).open(p));

        ItemBuilder threes = new ItemBuilder(Material.CACTUS, 3).setDisplayName("&a3v3");
        setItem(23, threes.build(), (p,a) -> new ModeSelectorGUI(plugin, 2, 3).open(p));

        ItemBuilder fours = new ItemBuilder(Material.CACTUS, 4).setDisplayName("&a4v4");
        setItem(25, fours.build(), (p,a) -> new ModeSelectorGUI(plugin, 2, 4).open(p));
    }

    public ModeSelectorGUI(CactusRush plugin, int teams, int teamSize) {
        super(45, "Modes");

        // Filler
        ItemStack filler = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};
        for(int i : fillers) {
            setItem(i, filler);
        }

        // Options
        ItemBuilder random = new ItemBuilder(XMaterial.FIREWORK_ROCKET).setDisplayName("&aRandom Map");
        setItem(21, random.build(), (p,a) -> plugin.getGameManager().addToGame(p, teams, teamSize));

        ItemBuilder mapSelector = new ItemBuilder(XMaterial.PAPER).setDisplayName("&aMap Selector");
        setItem(23, mapSelector.build());
    }
}
