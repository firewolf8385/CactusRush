package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import com.github.firewolf8385.cactusrush.game.ability.Ability;
import com.github.firewolf8385.cactusrush.guis.AbilitySelectorGUI;
import com.github.firewolf8385.cactusrush.guis.ModeSelectorGUI;
import com.github.firewolf8385.cactusrush.utils.LocationUtils;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import com.github.firewolf8385.cactusrush.utils.item.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    private final CactusRush plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerInteractListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerInteractEvent.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Exit if the item is null.
        if (event.getItem() == null)
            return;

        if(event.getItem().getType() == Material.EGG) {
            Game game = plugin.getGameManager().getGame(player);
            if(game == null) {
                return;
            }

            if(game.getGameState() == GameState.BETWEEN_ROUND) {
                event.setCancelled(true);
            }
        }

        // Exit if item meta is null.
        if (event.getItem().getItemMeta() == null)
            return;

        Ability ability = plugin.getAbilityManager().getAbility(event.getItem());
        if(ability != null) {

            if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            Game game = plugin.getGameManager().getGame(player);

            if(game.getGameState() == GameState.RUNNING) {
                ability.useAbility(player, game);
            }

            return;
        }

        String item = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
        switch (item) {
            case "Respawn" -> {
                Game game = plugin.getGameManager().getGame(player);

                if (game == null) {
                    return;
                }

                if (game.getGameState() != GameState.RUNNING) {
                    return;
                }

                game.getTeamManager().getTeam(player).unscorePlayer(player);
                game.spawnPlayer(player);
            }

            case "Leave" -> {
                Game game = plugin.getGameManager().getGame(player);

                if (game == null) {
                    return;
                }

                game.removePlayer(player);
                player.teleport(LocationUtils.getSpawn(plugin));
                ItemUtils.giveLobbyItems(player);
            }

            case "Modes" -> {
                new ModeSelectorGUI(plugin).open(player);
            }

            case "Profile", "Cosmetics", "Stats" -> {
                ChatUtils.chat(player, "&cThis feature is coming soon!");
            }

            case "Ability Selector" -> {
                Game game = plugin.getGameManager().getGame(player);

                if (game == null) {
                    return;
                }

                if(game.getGameState() == GameState.RUNNING || game.getGameState() == GameState.END) {
                    ChatUtils.chat(player, "&cYou can only use that before the round begins!");
                    return;
                }

                new AbilitySelectorGUI(plugin).open(player);
            }
        }
    }
}