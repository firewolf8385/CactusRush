package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import com.github.firewolf8385.cactusrush.game.team.Team;
import com.github.firewolf8385.cactusrush.game.team.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {
    private final CactusRush plugin;

    public BlockPlaceListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        arenaSetup(event);
        gamePlace(event);
    }

    private void arenaSetup(BlockPlaceEvent event) {
        // Makes sure there is an arena being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            return;
        }

        // Get the player who placed the block.
        Player player = event.getPlayer();

        // Get the item they are holding.
        ItemStack item = player.getInventory().getItemInHand();

        // Exit if the item is null.
        if(item == null) {
            return;
        }

        // Exit if item meta is null.
        if(item.getItemMeta() == null) {
            return;
        }

        // Gets the name of the item.
        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        // Exit if the item does not have a custom name.
        if(itemName == null) {
            return;
        }

        // Checks if the block placed is a special setup item.
        switch (itemName) {
            case "Goal" -> {
                // Makes sure it has lore.
                if(item.getItemMeta().getLore() == null) {
                    return;
                }

                // Makes sure the lore isn't empty.
                if(item.getItemMeta().getLore().size() == 0) {
                    return;
                }

                // Gets the team.
                String teamName = ChatColor.stripColor(item.getItemMeta().getLore().get(0));

                // Makes sure the team is valid.
                boolean valid = false;
                for(TeamColor team : TeamColor.values()) {
                    if(team.toString().equals(teamName.toUpperCase())) {
                        valid = true;
                    }
                }

                // Exists if the team is invalid.
                if(!valid) {
                    return;
                }

                // Adds the goal to the arena builder.
                TeamColor team = TeamColor.valueOf(teamName);
                plugin.getArenaManager().getArenaBuilder().addGoal(team, event.getBlock().getLocation());
            }
            case "Barrier" -> {
                // Makes sure the item is not a normal barrier.
                if(item.getType() == Material.BARRIER) {
                    return;
                }

                // Makes sure it has lore.
                if(item.getItemMeta().getLore() == null) {
                    return;
                }

                // Makes sure the lore isn't empty.
                if(item.getItemMeta().getLore().size() == 0) {
                    return;
                }

                // Gets the team.
                String teamName = ChatColor.stripColor(item.getItemMeta().getLore().get(0));

                // Makes sure the team is valid.
                boolean valid = false;
                for(TeamColor team : TeamColor.values()) {
                    if(team.toString().equals(teamName.toUpperCase())) {
                        valid = true;
                    }
                }

                // Exists if the team is invalid.
                if(!valid) {
                    return;
                }

                // Adds the barrier to the arena builder.
                TeamColor team = TeamColor.valueOf(teamName);
                plugin.getArenaManager().getArenaBuilder().addBarrier(team, event.getBlock().getLocation());
            }
        }
    }

    private void gamePlace(BlockPlaceEvent event) {
        // Get the player who placed the block.
        Player player = event.getPlayer();

        // Makes sure the player is in a game.
        if(plugin.getGameManager().getGame(player) == null) {

            // Allow creative mode players to build.
            if(player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            event.setCancelled(true);
            return;
        }

        Game game = plugin.getGameManager().getGame(player);

        // Prevent players from placing the leave bed.
        if(game.getGameState() == GameState.WAITING) {
            event.setCancelled(true);
        }

        game.getArena().addBlock(event.getBlock());
        game.addPlacedCacti(player);
    }
}