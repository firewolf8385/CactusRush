package com.github.firewolf8385.cactusrush.commands;

import com.github.firewolf8385.cactusrush.CactusRush;
import com.github.firewolf8385.cactusrush.utils.LocationUtils;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class runs the /cactusrush command (/cr). Which displays various plugin information.
 */
public class CactusRushCMD extends AbstractCommand {
    private final CactusRush plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public CactusRushCMD(CactusRush plugin) {
        super("cactusrush", "cr.admin", true);
        this.plugin = plugin;
    }

    /**
     * Runs when the command is executed.
     * @param sender The Command Sender.
     * @param args Arguments of the command.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            helpCMD(sender);
            return;
        }

        switch (args[0]) {
            case "info":
                infoCMD(sender);
                break;
            case "setspawn":
                setSpawn(sender);
                break;
            default:
                helpCMD(sender);
                break;
        }
    }

    /**
     * Runs the /cr info command, which displays plugin info.
     * @param sender CommandSender who sent the command.
     */
    private void infoCMD(CommandSender sender) {
        ChatUtils.chat(sender, "&8&m+-----------------------***-----------------------+");
        ChatUtils.centeredChat(sender, "&a&lCactus Rush");
        ChatUtils.chat(sender, "");
        ChatUtils.chat(sender, "  &8» &aAuthor: &f" + plugin.getDescription().getAuthors().get(0));
        ChatUtils.chat(sender, "  &8» &aVersion: &f" + plugin.getDescription().getVersion());
        ChatUtils.chat(sender, "  &8» &aGitHub: &fhttps://github.com/firewolf8385/WoolWars");
        ChatUtils.chat(sender, "&8&m+-----------------------***-----------------------+");
    }

    /**
     * Runs the /cr help command, which displays plugin commands.
     * @param sender CommandSender who sent the command.
     */
    private void helpCMD(CommandSender sender) {
        ChatUtils.chat(sender, "&8&m+-----------------------***-----------------------+");
        ChatUtils.centeredChat(sender, "&a%l&lCactus Rush");
        ChatUtils.chat(sender, "  &8» &a/cr info");
        ChatUtils.chat(sender, "&8&m+-----------------------***-----------------------+");
    }

    /**
     * Runs the /cr setspawn command, which sets the lobby spawn.
     * @param sender CommandSender who sent the command.
     */
    private void setSpawn(CommandSender sender) {
        // Makes sure the sender is a player.
        if(!(sender instanceof Player)) {
            ChatUtils.chat(sender, "&cError &8» &cOnly players can set the spawn location.");
            return;
        }

        // Sets the lobby spawn.
        Player player = (Player) sender;
        LocationUtils.setSpawn(plugin, player.getLocation());
        ChatUtils.chat(player, "&a&lCactusRush &8» &aSpawn location has been set.");
    }
}