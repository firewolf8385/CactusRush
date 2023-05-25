package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.jadedchat.features.channels.events.ChannelSwitchEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChannelSwitchListener implements Listener {
    private final CactusRush plugin;

    public ChannelSwitchListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwitch(ChannelSwitchEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player);

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