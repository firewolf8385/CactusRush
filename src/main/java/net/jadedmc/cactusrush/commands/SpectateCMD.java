/*
 * This file is part of Cactus Rush, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.commands;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.gui.SpectateGUI;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents the /spectate command, which allows the player to spectate a game.
 */
public class SpectateCMD extends AbstractCommand {
    private final CactusRushPlugin plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public SpectateCMD(CactusRushPlugin plugin) {
        super("spectate", "", false);
        this.plugin = plugin;
    }

    /**
     * Runs when the command is executed.
     * @param sender The Command Sender.
     * @param args Arguments of the command.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        // Prevent being able to spectate when already in a game.
        if(plugin.gameManager().getGame(player) != null) {
            ChatUtils.chat(sender, "&cError &8» &cYou can't spectate while in a game!");
            return;
        }

        // If no arguments given, show all available games.
        if(args.length != 1) {
            new SpectateGUI(plugin).open((Player) sender);
            return;
        }

        // Make sure the target is online.
        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            ChatUtils.chat(sender, "&cError &8» &cThat player is not online.");
            return;
        }

        // Check if the target is in a game.
        Game game = plugin.gameManager().getGame(target);
        if(game == null) {
            ChatUtils.chat(sender, "&cError &8» &cThat player is not in a game.");
            return;
        }

        // Makes sure the player isn't already spectating a game.
        if(plugin.gameManager().getGame(player) != null) {
            ChatUtils.chat(sender, "&cError &8» &cYou are already spectating someone!");
            return;
        }

        // Adds them to the game.
        game.addSpectator(player);
        game.sendMessage("&a" + player.getName() + " is now spectating.");
    }
}