package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedchat.features.channels.events.ChannelSwitchEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChannelSwitchListener implements Listener {
    private final CactusRushPlugin plugin;

    public ChannelSwitchListener(CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwitch(ChannelSwitchEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.gameManager().getGame(player);

        if(game == null) {
            if(event.getToChannel().name().equalsIgnoreCase("GAME") || event.getToChannel().name().equalsIgnoreCase("TEAM")) {
                event.setToChannel(JadedChat.getDefaultChannel());
            }
        }
        else {
            if(event.getToChannel().equals(JadedChat.getDefaultChannel())) {
                event.setToChannel(JadedChat.getChannel("GAME"));
            }
        }
    }
}