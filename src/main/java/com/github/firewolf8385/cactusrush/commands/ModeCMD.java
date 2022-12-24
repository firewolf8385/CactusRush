package com.github.firewolf8385.cactusrush.commands;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.guis.ModeSelectorGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModeCMD extends AbstractCommand {
    private final CactusRush plugin;

    public ModeCMD(CactusRush plugin) {
        super("mode", "cactusrush.mode", true);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        int teams = Integer.parseInt(args[1]);
        int teamSize = Integer.parseInt(args[2]);

        new ModeSelectorGUI(plugin, teams, teamSize).open(player);
    }
}
