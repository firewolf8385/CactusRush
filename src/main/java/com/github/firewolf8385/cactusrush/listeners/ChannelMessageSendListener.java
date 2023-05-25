package com.github.firewolf8385.cactusrush.listeners;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.team.Team;
import net.jadedmc.jadedchat.features.channels.events.ChannelMessageSendEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ChannelMessageSendListener implements Listener {
    private final CactusRush plugin;

    public ChannelMessageSendListener(CactusRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMessage(ChannelMessageSendEvent event) {

        switch (event.getChannel().name().toUpperCase()) {
            case "GAME" -> {
                gameChannel(event);
            }

            case "TEAM" -> {
                teamChannel(event);
            }
        }
    }

    private void gameChannel(ChannelMessageSendEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            event.setCancelled(true);
            return;
        }

        List<Player> viewers = new ArrayList<>(game.getPlayers());
        viewers.addAll(game.getSpectators());

        event.setViewers(viewers);
    }

    private void teamChannel(ChannelMessageSendEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getGame(player);

        if(game == null) {
            event.setCancelled(true);
            return;
        }

        Team team = game.getTeamManager().getTeam(player);
        if(team == null) {
            event.setCancelled(true);
            return;
        }

        List<Player> viewers = new ArrayList<>(team.getPlayers());

        event.setViewers(viewers);
    }
}