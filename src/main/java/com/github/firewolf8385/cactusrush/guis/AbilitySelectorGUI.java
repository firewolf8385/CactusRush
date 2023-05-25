package com.github.firewolf8385.cactusrush.guis;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import com.github.firewolf8385.cactusrush.game.ability.Ability;
import com.github.firewolf8385.cactusrush.utils.item.ItemBuilder;
import com.github.firewolf8385.cactusrush.utils.xseries.XMaterial;
import net.jadedmc.jadedcore.utils.gui.CustomGUI;
import org.bukkit.inventory.ItemStack;

public class AbilitySelectorGUI extends CustomGUI {

    public AbilitySelectorGUI(CactusRush plugin) {
        super(54, "Abilities");

        // Filler
        ItemStack filler = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53};
        int[] slots = {20,22,24,30,32};

        for(int i : fillers) {
            setItem(i, filler);
        }

        int i = 0;
        for(Ability ability : plugin.getAbilityManager().getAbilities()) {

            ItemBuilder builder = new ItemBuilder(ability.getItemStack());
            setItem(slots[i], builder.build(), (p,a) -> {
                p.closeInventory();

                plugin.getAbilityManager().removePlayer(p);
                plugin.getAbilityManager().addPlayer(p, ability);

                Game game = plugin.getGameManager().getGame(p);

                if(game == null) {
                    return;
                }

                if(game.getGameState() != GameState.BETWEEN_ROUND) {
                    return;
                }

                ability.giveItem(p);
            });

            i++;
        }
    }
}
