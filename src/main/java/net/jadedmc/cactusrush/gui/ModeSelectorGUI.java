package net.jadedmc.cactusrush.gui;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.game.arena.Arena;
import net.jadedmc.jadedutils.gui.CustomGUI;
import net.jadedmc.jadedutils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ModeSelectorGUI extends CustomGUI {

    public ModeSelectorGUI(CactusRushPlugin plugin) {
        super(45, "Modes");

        // Filler
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};
        for(int i : fillers) {
            setItem(i, filler);
        }

        // Modes
        ItemBuilder ones = new ItemBuilder(Material.CACTUS).setDisplayName("&a1v1");
        setItem(19, ones.build(), (p,a) -> new ModeSelectorGUI(plugin, Mode.ONE_V_ONE).open(p));

        ItemBuilder twos = new ItemBuilder(Material.CACTUS, 2).setDisplayName("&a2v2");
        setItem(21, twos.build(), (p,a) -> new ModeSelectorGUI(plugin, Mode.TWO_V_TWO).open(p));

        ItemBuilder threes = new ItemBuilder(Material.CACTUS, 3).setDisplayName("&a3v3");
        setItem(23, threes.build(), (p,a) -> new ModeSelectorGUI(plugin, Mode.THREE_V_THREE).open(p));

        ItemBuilder fours = new ItemBuilder(Material.CACTUS, 4).setDisplayName("&a4v4");
        setItem(25, fours.build(), (p,a) -> new ModeSelectorGUI(plugin, Mode.FOUR_V_FOUR).open(p));

        ItemBuilder spectate = new ItemBuilder(Material.ENDER_EYE).setDisplayName("&aSpectate");
        setItem(31, spectate.build(), (p,a) -> new SpectateGUI(plugin).open(p));
    }

    public ModeSelectorGUI(CactusRushPlugin plugin, Mode mode) {
        super(45, "Modes");

        // Filler
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};
        for(int i : fillers) {
            setItem(i, filler);
        }

        // Options
        ItemBuilder random = new ItemBuilder(Material.FIREWORK_ROCKET).setDisplayName("&aRandom Map");
        setItem(21, random.build(), (p,a) -> plugin.getGameManager().addToGame(p, mode));

        ItemBuilder mapSelector = new ItemBuilder(Material.PAPER).setDisplayName("&aMap Selector");
        setItem(23, mapSelector.build(), (p,a) -> new ArenaSelectorGUI(plugin, mode).open(p));
    }

    private class ArenaSelectorGUI extends CustomGUI {
        public ArenaSelectorGUI(CactusRushPlugin plugin, Mode mode) {
            super(45, "Map Selector");

            // Filler
            ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
            int[] fillers = {0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};
            for(int i : fillers) {
                setItem(i, filler);
            }

            int slot = 9;
            for(Arena arena : plugin.getArenaManager().getArenas(mode)) {
                ItemBuilder builder = new ItemBuilder(Material.PAPER).setDisplayName("&a" + arena.getName());
                setItem(slot, builder.build(), (p,a) -> plugin.getGameManager().addToGame(p, arena, mode));
                slot++;
            }
        }
    }
}