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
import net.jadedmc.cactusrush.gui.DuelGUI;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.jadedpartybukkit.JadedParty;
import net.jadedmc.jadedpartybukkit.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Runs the /duel command, which allows players to duel each other.
 */
public class DuelCMD extends AbstractCommand {
    private final CactusRushPlugin plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public DuelCMD(final CactusRushPlugin plugin) {
        super("duel", "", false);
        this.plugin = plugin;
    }

    /**
     * Executes the command.
     * @param sender The Command Sender.
     * @param args Arguments of the command.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        // Make sure the player is using the command properly.
        if(args.length < 1) {
            ChatUtils.chat(player, "&cUsage &8» &c/duel [accept|player]");
            return;
        }

        // Check for sub commands.
        switch (args[0].toLowerCase()) {

            // Processes the "accept" sub command.
            case "accept" -> {
                // Makes sure the player includes another player..
                if(args.length < 2) {
                    ChatUtils.chat(sender, "&cUsage &8» &c/duel accept [player]");
                    return;
                }

                Player opponent = Bukkit.getPlayer(args[1]);

                // Make sure the player is still online.
                if(opponent == null) {
                    ChatUtils.chat(sender, "&cError &8» &cThat player is not online.");
                    return;
                }

                // Make sure the duel request exists.
                if(!plugin.duelManager().hasDuelRequest(opponent, player)) {
                    ChatUtils.chat(sender, "&cError &8» &cThat person has not sent a duel request.");
                    return;
                }

                // Makes sure the player is not in a match already.
                if(plugin.gameManager().getGame(player) != null) {
                    ChatUtils.chat(sender, "&cError &8» &cYou are in a match already.");
                    return;
                }

                // Make sure the opponent is not already in a match.
                if(plugin.gameManager().getGame(opponent) != null) {
                    ChatUtils.chat(sender, "&cError &8» &cThey are in a match already.");
                    return;
                }

                // Makes sure the player is the party leader if they are in a party.
                Party targetParty = JadedParty.partyManager().getParty(opponent);
                if(targetParty != null && (!targetParty.getLeader().equals(opponent.getUniqueId()))) {
                    ChatUtils.chat(sender, "&cError &8» &cThat player is in a party.");
                    return;
                }

                // Accepts the Duel request.
                ChatUtils.chat(sender, "&aDuel request has been accepted.");
                ChatUtils.chat(opponent, "&f" + player.getName() + " &ahas accepted your duel request.");
                plugin.duelManager().acceptDuelRequest(opponent, player);
            }

            // If not any of the above, then they must be sending a duel request.
            default -> {
                // Make sure they don't have an active duel request already.
                if(plugin.duelManager().hasDuelRequest(player)) {
                    ChatUtils.chat(sender, "&cError &8» &cYou already have an active duel request.");
                    return;
                }

                Player opponent = Bukkit.getPlayer(args[0]);

                // Make sure the target is online.
                if(opponent == null) {
                    ChatUtils.chat(sender, "&cError &8» &cThat player is not online.");
                    return;
                }

                // Make sure the player isn't dueling themselves.
                if(opponent.equals(player)) {
                    ChatUtils.chat(sender, "&cError &8» &cYou cannot duel yourself!");
                    return;
                }

                // Make sure the player isn't in a game already.
                if(plugin.gameManager().getGame(player) != null) {
                    ChatUtils.chat(sender, "&cError &8» &cYou are in a game already.");
                    return;
                }

                // Make sure the target isn't in a game already.
                if(plugin.gameManager().getGame(opponent) != null) {
                    ChatUtils.chat(sender, "&cError &8» &cThey are in a game already.");
                    return;
                }

                // Opens the duel gui.
                new DuelGUI(plugin, player, opponent).open(player);
            }
        }
    }
}