package net.jadedmc.cactusrush.listeners;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.teams.Team;
import net.jadedmc.jadedchat.features.channels.events.ChannelMessageSendEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ChannelMessageSendListener implements Listener {
    private final CactusRushPlugin plugin;

    public ChannelMessageSendListener(CactusRushPlugin plugin) {
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
        Game game = plugin.gameManager().getGame(player);

        if(game == null) {
            event.setCancelled(true);
            return;
        }

        List<Player> viewers = new ArrayList<>(game.players());
        viewers.addAll(game.spectators());

        event.setViewers(viewers);
    }

    private void teamChannel(ChannelMessageSendEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.gameManager().getGame(player);

        if(game == null) {
            event.setCancelled(true);
            return;
        }

        Team team = game.teamManager().getTeam(player);
        if(team == null) {
            event.setCancelled(true);
            return;
        }

        List<Player> viewers = new ArrayList<>(team.players());

        event.setViewers(viewers);
    }
}