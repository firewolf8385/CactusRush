package net.jadedmc.cactusrush.gui;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.arena.Arena;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.cactusrush.utils.item.ItemBuilder;
import net.jadedmc.jadedcore.utils.gui.CustomGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Manages the DuelGUI, which allows a player to duel another.
 */
public class DuelGUI extends CustomGUI {

    /**
     * Creates the main duel GUI.
     * @param plugin Instance of the plugin.
     * @param player Player sending the duel request.
     * @param target Target of the duel request.
     */
    public DuelGUI(final CactusRushPlugin plugin, final Player player, final Player target) {
        super(45, "Duel " + target.getName());

        // Filler
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};
        for(int i : fillers) {
            setItem(i, filler);
        }

        // Options
        ItemBuilder random = new ItemBuilder(Material.FIREWORK_ROCKET).setDisplayName("&aRandom Map");
        setItem(21, random.build(), (p,a) -> new DuelRandomMapGUI(plugin, player, target).open(p));

        ItemBuilder mapSelector = new ItemBuilder(Material.PAPER).setDisplayName("&aMap Selector");
        setItem(23, mapSelector.build(), (p,a) -> new DuelMapSelectorGUI(plugin, player, target).open(p));
    }

    private class DuelRandomMapGUI extends CustomGUI {

        public DuelRandomMapGUI(final CactusRushPlugin plugin, final Player player, final Player target) {
            super(45, "Duel " + target.getName());

            ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
            int[] fillers = {0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};
            for(int i : fillers) {
                setItem(i, filler);
            }

            ItemBuilder solo = new ItemBuilder(Material.PAPER).setDisplayName("&a1v1 Maps").addLore("&7Click to select a random 1v1 map.");
            setItem(20, solo.build(), (p,a) -> processDuelRequest(plugin, player, target, "1v1"));

            ItemBuilder duos = new ItemBuilder(Material.PAPER).setDisplayName("&a2v2 Maps").addLore("&7Click to select a random 2v2 map.");
            setItem(21, duos.build(), (p,a) -> processDuelRequest(plugin, player, target, "2v2"));

            ItemBuilder threes = new ItemBuilder(Material.PAPER).setDisplayName("&a3v3 Maps").addLore("&7Click to select a random 3v3 map.");
            setItem(22, threes.build(), (p, a) -> processDuelRequest(plugin, player, target, "3v3"));

            ItemBuilder fours = new ItemBuilder(Material.PAPER).setDisplayName("&a4v4 Maps").addLore("&7Click to select a random 4v4 map.");
            setItem(23, fours.build(), (p,a) -> processDuelRequest(plugin, player, target, "4v4"));

            ItemBuilder comp = new ItemBuilder(Material.PAPER).setDisplayName("&aCompetitive Maps").addLore("&7Click to select a random competitive map.");
            setItem(24, comp.build(), (p,a) -> processDuelRequest(plugin, player, target, "comp"));

            ItemBuilder any = new ItemBuilder(Material.FIREWORK_ROCKET).setDisplayName("&aAny Map").addLore("&7Click to select any random map.");
            setItem(31, any.build(), (p,a) -> processDuelRequest(plugin, player, target, "any"));
        }

        /**
         * Processes a duel request.
         * @param plugin Instance of the plugin.
         * @param sender Request sender
         * @param receiver Request receiver
         * @param map Map the duel will be on.
         */
        private void processDuelRequest(CactusRushPlugin plugin, Player sender, Player receiver, String map) {
            if(receiver == null) {
                ChatUtils.chat(sender, "&cError &8» &cThat player is not online!");
                sender.closeInventory();
                return;
            }

            if(plugin.gameManager().getGame(receiver) != null) {
                ChatUtils.chat(sender, "&cError &8» &c/That player is currently in a game!");
                sender.closeInventory();
                return;
            }

            plugin.duelManager().addDuelRequest(sender, receiver, map);
            sender.closeInventory();
        }
    }

    private class DuelMapSelectorGUI extends CustomGUI {
        public DuelMapSelectorGUI(final CactusRushPlugin plugin, final Player player, final Player target) {
            super(45, "Duel " + target.getName());

            ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
            int[] fillers = {0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};
            for(int i : fillers) {
                setItem(i, filler);
            }

            int slot = 9;
            for(Arena arena : plugin.arenaManager().getArenas()) {
                ItemBuilder builder = new ItemBuilder(Material.PAPER).setDisplayName("&a" + arena.name());
                setItem(slot, builder.build(), (p,a) -> {
                    if(target == null) {
                        ChatUtils.chat(player, "&cError &8» &cThat player is not online!");
                        player.closeInventory();
                        return;
                    }

                    if(plugin.gameManager().getGame(target) != null) {
                        ChatUtils.chat(player, "&cError &8» &c/That player is currently in a game!");
                        player.closeInventory();
                        return;
                    }

                    plugin.duelManager().addDuelRequest(player, target, arena.id());
                    player.closeInventory();
                });
                slot++;
            }
        }
    }
}
