package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This class runs a listener that is called whenever a player joins.
 * This teleports the player to spawn, reads and caches data from MySQL, and other tasks.
 */
public class PlayerJoinListener implements Listener {
    private final CactusRushPlugin plugin;

    /**
     * Creates the Listener.
     *
     * @param plugin Instance of the plugin.
     */
    public PlayerJoinListener(CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     *
     * @param event PlayerJoinEvent
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getCactusPlayerManager().addPlayer(player);

        // Send the player to the lobby.
        //LobbyUtils.sendToLobby(plugin, player);

        // TODO: Get rid of this. Serve no purpose and may be wrong.
        // Send message is the game is empty.
        if (Bukkit.getOnlinePlayers().size() == 1) {
            ChatUtils.chat(player, "&3▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            ChatUtils.chat(player, ChatUtils.centerText("&3&lWelcome, &f&l" + player.getName() + "&3&l!"));
            ChatUtils.chat(player, "");
            ChatUtils.chat(player, "&3Looks like the server is empty right now. This game is much better with friends. Consider joining our Discord Server to see when other people are online! &f<click:open_url:'http://discord.gg/YWGFeNA'>http://discord.gg/YWGFeNA</click>");
            ChatUtils.chat(player, "");
            ChatUtils.chat(player, "&3▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }
    }
}