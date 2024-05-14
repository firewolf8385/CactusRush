package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.ability.Ability;
import net.jadedmc.cactusrush.gui.AbilitySelectorGUI;
import net.jadedmc.cactusrush.gui.ModeSelectorGUI;
import net.jadedmc.cactusrush.gui.ShopGUI;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.minigames.Minigame;
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listens for when a player interacts with an item.
 * Used to process clickable items in the lobby.
 */
public class PlayerInteractListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerInteractListener(CactusRushPlugin plugin) {
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

        // Prevent eggs from being thrown when they aren't supposed to.
        if(event.getItem().getType() == Material.EGG || event.getItem().getType() == Material.SNOWBALL) {
            Game game = plugin.getGameManager().getLocalGames().getGame(player);
            if(game == null) {
                return;
            }

            // Prevent throwing eggs if the round hasn't started.
            if(game.getGameState() == GameState.BETWEEN_ROUND) {
                event.setCancelled(true);
            }
        }

        // Exit if item meta is null.
        if (event.getItem().getItemMeta() == null)
            return;

        // Process abilities.
        Ability ability = plugin.getAbilityManager().getAbility(event.getItem());
        if(ability != null) {

            // Exit if the click wasn't a right click.
            if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            Game game = plugin.getGameManager().getLocalGames().getGame(player);

            // Cancel interaction if the cooldown isn't finished.
            if(ability.getAbilityCooldown(player) != null && ability.getAbilityCooldown(player).getSeconds() > 0) {
                event.setCancelled(true);
                return;
            }

            // Only use the ability if a round is currently running.
            if(game.getGameState() == GameState.RUNNING) {
                ability.useAbility(player, game);
            }

            return;
        }

        String item = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
        switch (item) {
            case "Leave" -> {
                Game game = plugin.getGameManager().getLocalGames().getGame(player);

                if (game == null) {
                    return;
                }

                //game.removePlayer(player);
                //player.teleport(LocationUtils.getSpawn(plugin));
                JadedAPI.sendToLobby(player, Minigame.CACTUS_RUSH);
            }

            case "Play Again" -> {
                Game game = plugin.getGameManager().getLocalGames().getGame(player);

                if(game == null) {
                    return;
                }

                // TODO: Fix this to pick random arena. I'm just lazy right now.
                plugin.getGameManager().addToGame(player, game.getArena(), game.getMode());
            }

            case "Leave Match" -> {
                Game game = plugin.getGameManager().getLocalGames().getGame(player);

                if(game == null) {
                    return;
                }

                if(game.getSpectators().contains(player.getUniqueId())) {
                    JadedAPI.sendToLobby(player, Minigame.CACTUS_RUSH);
                }
            }

            case "Modes" -> {
                new ModeSelectorGUI(plugin).open(player);
            }

            case "Shop" -> {
                new ShopGUI(plugin).open(player);
            }


            case "Stats" -> ChatUtils.chat(player, "&cThis feature is coming soon!");

            // Runs the Ability Selector Item.
            case "Ability Selector" -> {
                Game game = plugin.getGameManager().getLocalGames().getGame(player);

                // Exit if the player is not in a game.
                if (game == null) {
                    return;
                }

                // Only allow the player to change their ability before the round begins.
                if(game.getGameState() == GameState.RUNNING || game.getGameState() == GameState.END) {
                    ChatUtils.chat(player, "&cYou can only use that before the round begins!");
                    return;
                }

                new AbilitySelectorGUI(plugin, player).open(player);
            }
        }
    }
}