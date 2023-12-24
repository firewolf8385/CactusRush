package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.lobby.LobbyScoreboard;
import net.jadedmc.cactusrush.utils.item.ItemBuilder;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedlobby.events.LobbyJoinEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LobbyJoinListener implements Listener {
    private final CactusRushPlugin plugin;

    public LobbyJoinListener(final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLobbyJoin(LobbyJoinEvent event) {
        Player player = event.getPlayer();

        player.getInventory().setItem(2, new ItemBuilder(Material.EMERALD).setDisplayName("&a&lShop").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lModes").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.PAPER).setDisplayName("&a&lStats").build());

        new LobbyScoreboard(plugin, player).update(player);

        if(JadedChat.getChannel(player).name().equalsIgnoreCase("GAME") || JadedChat.getChannel(player).name().equalsIgnoreCase("TEAM")) {
            JadedChat.setChannel(player, JadedChat.getDefaultChannel());
        }
    }

}
