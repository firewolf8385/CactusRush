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
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.gui.ModeSelectorGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class runs the /modes command, which opens the mode selector gui.
 */
public class ModesCMD extends AbstractCommand {
    private final CactusRushPlugin plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public ModesCMD(CactusRushPlugin plugin) {
        super("modes", "", true);
        this.plugin = plugin;
    }

    /**
     * Executes the command.
     * @param sender The Command Sender.
     * @param args Arguments of the command.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {

        // Opens the empty menu if no arguments.
        if(args.length == 0) {
            // Opens the GUI.
            Player player = (Player) sender;
            new ModeSelectorGUI(plugin).open(player);
            return;
        }

        if(args.length == 1) {
            Player player = (Player) sender;

            // Process arguments.
            switch (args[0]) {
                case "1v1" -> new ModeSelectorGUI(plugin, Mode.ONE_V_ONE).open(player);
                case "2v2" -> new ModeSelectorGUI(plugin, Mode.TWO_V_TWO).open(player);
                case "3v3" -> new ModeSelectorGUI(plugin, Mode.THREE_V_THREE).open(player);
                case "4v4" -> new ModeSelectorGUI(plugin, Mode.FOUR_V_FOUR).open(player);
                default -> new ModeSelectorGUI(plugin).open(player);
            }

            return;
        }

        if(args.length == 2) {
            Player target = Bukkit.getPlayer(args[0]);

            // Process arguments.
            switch (args[1]) {
                case "1v1" -> new ModeSelectorGUI(plugin, Mode.ONE_V_ONE).open(target);
                case "2v2" -> new ModeSelectorGUI(plugin, Mode.TWO_V_TWO).open(target);
                case "3v3" -> new ModeSelectorGUI(plugin, Mode.THREE_V_THREE).open(target);
                case "4v4" -> new ModeSelectorGUI(plugin, Mode.FOUR_V_FOUR).open(target);
                default -> new ModeSelectorGUI(plugin).open(target);
            }
        }
    }
}
