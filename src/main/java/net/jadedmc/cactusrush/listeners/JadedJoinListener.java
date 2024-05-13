package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.events.JadedJoinEvent;
import net.jadedmc.jadedcore.minigames.Minigame;
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class JadedJoinListener implements Listener {
    private final CactusRushPlugin plugin;

    public JadedJoinListener(final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJadedJoin(JadedJoinEvent event) {
        // TODO: Load DuelPlayer object.
        final Player player = event.getJadedPlayer().getPlayer();

        for(final Game game : plugin.getGameManager().getLocalGames()) {
            if(!game.getPlayers().contains(player.getUniqueId())) {
                continue;
            }

            game.addPlayer(player);
            return;
        }

        if(!JadedAPI.getPlugin().lobbyManager().isLobbyWorld(player.getWorld())) {
            ChatUtils.chat(player, "<red>Game not found! Sending you back to the lobby.");
            JadedAPI.sendToLobby(player, Minigame.CACTUS_RUSH);
        }
    }
}