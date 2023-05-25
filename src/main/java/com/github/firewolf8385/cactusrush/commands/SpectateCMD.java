package com.github.firewolf8385.cactusrush.commands;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.guis.SpectateGUI;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCMD extends AbstractCommand {
    private final CactusRush plugin;

    public SpectateCMD(CactusRush plugin) {
        super("spectate", "", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 1) {
            new SpectateGUI(plugin).open((Player) sender);
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            ChatUtils.chat(sender, "&cError &8» &cThat player is not online.");
            return;
        }

        Game game = plugin.getGameManager().getGame(target);
        if(game == null) {
            ChatUtils.chat(sender, "&cError &8» &cThat player is not in a game.");
            return;
        }

        Player player = (Player) sender;

        if(plugin.getGameManager().getGame(player) != null) {
            ChatUtils.chat(sender, "&cError &8» &cYou are already spectating someone!");
            return;
        }

        game.addSpectator(player);
        game.sendMessage("&a" + player.getName() + " is now spectating.");

        return;
    }
}