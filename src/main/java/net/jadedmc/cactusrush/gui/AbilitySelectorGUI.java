package net.jadedmc.cactusrush.gui;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.ability.Ability;
import net.jadedmc.cactusrush.player.CactusPlayer;
import net.jadedmc.jadedutils.chat.ChatUtils;
import net.jadedmc.jadedutils.gui.CustomGUI;
import net.jadedmc.jadedutils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AbilitySelectorGUI extends CustomGUI {

    public AbilitySelectorGUI(CactusRushPlugin plugin, Player player) {
        super(54, "Abilities");

        // Filler
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53};
        //int[] slots = {20,22,24,30,32};
        int[] slots = {21,22,23,30,31,32};

        for(int i : fillers) {
            setItem(i, filler);
        }

        int i = 0;
        for(Ability ability : plugin.getAbilityManager().getAbilities()) {

            CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);

            if(cactusPlayer.getUnlockedAbilities().contains(ability.getId()) || ability.price() == 0) {
                ItemBuilder builder = new ItemBuilder(ability.getItemStack());
                setItem(slots[i], builder.build(), (p,a) -> {
                    p.closeInventory();

                    Game game = plugin.getGameManager().getLocalGames().getGame(p);

                    // Exit if the player isn't in a game.
                    if(game == null) {
                        cactusPlayer.setSelectedAbility(ability.getId());
                        ChatUtils.chat(p, "&f" + ability.name() + " &ahas been selected!");
                        p.closeInventory();
                        return;
                    }

                    // Disable if a round is running.
                    if(game.getGameState() != GameState.BETWEEN_ROUND && game.getGameState() != GameState.WAITING && game.getGameState() != GameState.COUNTDOWN) {
                        return;
                    }

                    // Replace the player's ability.
                    plugin.getAbilityManager().removePlayer(p);
                    plugin.getCactusPlayerManager().getPlayer(p).setSelectedAbility(ability.getId());

                    // Gives them the ability item if they aren't in the waiting area.
                    if(game.getGameState() == GameState.BETWEEN_ROUND) {
                        ability.giveItem(p);
                    }
                });
            }
            else {
                ItemBuilder builder = new ItemBuilder(ability.getItemStack())
                        .setMaterial(Material.GRAY_DYE)
                        .addLore("")
                        .addLore("&6Price: " + ability.price())
                        .addLore("&cClick to purchase!");
                setItem(slots[i], builder.build(), (p,a) -> {
                    if(cactusPlayer.getCoins() < ability.price()) {
                        ChatUtils.chat(p, "&cError &8» &cYou do not have enough coins for that!");
                        return;
                    }

                    cactusPlayer.removeCoins(ability.price());
                    cactusPlayer.unlockAbility(ability);

                    Game game = plugin.getGameManager().getLocalGames().getGame(p);

                    // Exit if the player isn't in a game.
                    if(game == null) {
                        cactusPlayer.setSelectedAbility(ability.getId());
                        ChatUtils.chat(p, "&f" + ability.name() + " &ahas been purchased and selected!");
                        return;
                    }

                    // Disable if a round is running.
                    if(game.getGameState() != GameState.BETWEEN_ROUND && game.getGameState() != GameState.WAITING && game.getGameState() != GameState.COUNTDOWN) {
                        return;
                    }

                    // Replace the player's ability.
                    plugin.getAbilityManager().removePlayer(p);
                    plugin.getCactusPlayerManager().getPlayer(p).setSelectedAbility(ability.getId());

                    ChatUtils.chat(p, "&f" + ability.name() + " &ahas been purchased and selected!");
                    p.closeInventory();

                    // Gives them the ability item if they aren't in the waiting area.
                    if(game.getGameState() == GameState.BETWEEN_ROUND) {
                        ability.giveItem(p);
                    }
                });
            }

            i++;
        }
    }
}