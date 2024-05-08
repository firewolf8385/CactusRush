/*
 * This file is part of CactusRush, licensed under the MIT License.
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
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.commands;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.arena.Arena;
import net.jadedmc.cactusrush.game.arena.ArenaBuilder;
import net.jadedmc.cactusrush.game.arena.CactusRushGenerator;
import net.jadedmc.jadedchat.utils.StringUtils;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedutils.chat.ChatUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Runs the /arena command, which is used when setting up or editing arenas.
 */
public class ArenaCMD extends AbstractCommand {
    private final CactusRushPlugin plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public ArenaCMD(final CactusRushPlugin plugin) {
        super("arena", "cactus.admin", false);
        this.plugin = plugin;
    }

    /**
     * Executes the command.
     * @param sender The Command Sender.
     * @param args Arguments of the command.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        final Player player = (Player) sender;

        // Makes sure an argument is given.
        if(args.length == 0) {
            return;
        }

        switch (args[0].toLowerCase()) {
            case "addmode" -> addMode(player, args);
            case "create" -> createCMD(player, args);
            case "edit" -> editCMD(player, args);
            case "finish" -> finishCMD(player);
            case "setname" -> setNameCMD(player, args);
            case "setvoidlevel" -> setVoidLevel(player, args);
        }
    }

    /**
     * Runs the /arena create command.
     * This command starts the arena creation process.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void createCMD(Player player, String[] args) {
        if(plugin.getArenaManager().getArenaBuilder() != null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>There is already an arena being set up.");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/arena create [id]");
            return;
        }

        // Gets the arena id.
        final String id = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");

        // Creates the arena world.
        final WorldCreator worldCreator = new WorldCreator(id).type(WorldType.FLAT);
        worldCreator.generator(new CactusRushGenerator());
        final World world = Bukkit.createWorld(worldCreator);

        // Sets world settings.
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setClearWeatherDuration(Integer.MAX_VALUE);
        world.setTime(6000);
        world.getWorldBorder().setCenter(world.getSpawnLocation());
        world.getWorldBorder().setSize(210);
        world.setKeepSpawnInMemory(true);

        player.setGameMode(GameMode.CREATIVE);
        player.teleport(world.getSpawnLocation());
        player.setFlying(true);

        // Starts the arena setup process.
        plugin.getArenaManager().setArenaBuilder(new ArenaBuilder(plugin, world));
        plugin.getArenaManager().getArenaBuilder().setID(id);

        ChatUtils.chat(player, "<green><bold>CactusRush</bold> <dark_gray>» <green>Created an arena with the id <white>" + id + "<green>.");
        ChatUtils.chat(player, "<green><bold>CactusRush</bold> <dark_gray>» <green>Next, set the arena name with <white>/arena setname [name]<green>.");
    }

    /**
     * Runs the /arena setname command.
     * This command sets the name of the arena.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void setNameCMD(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/arena setname [name]");
            return;
        }

        // Gets the arena name.
        final String name = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");

        // Sets the arena name.
        plugin.getArenaManager().getArenaBuilder().setName(name);
        ChatUtils.chat(player, "<green><bold>CactusRush</bold> <dark_gray>» <green>Arena name set to <white>" + name + "<green>.");
        ChatUtils.chat(player, "<green><bold>CactusRush</bold> <dark_gray>» <green>Next, add all allowable modes with <white>/arena addmode [mode]<green>.");
    }

    /**
     * Runs the /arena addMode command.
     * This command adds to the list of modes that the arena is allowed to be used in.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void addMode(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/arena addkit [kit]");
            return;
        }

        plugin.getArenaManager().getArenaBuilder().addMode(args[1]);

        ChatUtils.chat(player, "<green><bold>CactusRush</bold> <dark_gray>» <green>Added <white>" + args[1] + "<green> as a valid kit.");
        ChatUtils.chat(player, "<green><bold>CactusRush</bold> <dark_gray>» <green>When you are done, finish the arena with <white>/arena finish<green>");
    }

    /**
     * Runs the /arena setvoidlevel command.
     * This command sets the y level in which players should die and respawn.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void setVoidLevel(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>/arena setvoidlevel [y-level]");
            return;
        }

        // Gets the team size from the command.
        final int voidLevel = Integer.parseInt(args[1]);

        // Sets the team size.
        plugin.getArenaManager().getArenaBuilder().setVoidLevel(voidLevel);
        ChatUtils.chat(player, "<green><bold>CactusRush</bold> <dark_gray>» <green>Void level has been set to <white>" + voidLevel + "<green>.");
    }

    /**
     * Runs the /arena finish command.
     * This command checks if the arena is done and saves it if so.
     * @param player Player running the command.
     */
    private void finishCMD(Player player) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You need to create an arena first! /arena create");
            return;
        }

        // Warn the player if setup is not complete.
        if(!plugin.getArenaManager().getArenaBuilder().isReady()) {
            return;
        }

        ChatUtils.chat(player, "<green><bold>CactusRush</bold> <dark_gray>» <green>Arena has been saved.");

        // Saves the arena.
        plugin.getArenaManager().getArenaBuilder().save();
        plugin.getArenaManager().setArenaBuilder(null);
    }

    /**
     * Runs the /arena edit command.
     * This command edits an existing arena.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void editCMD(Player player, String[] args) {
        if(plugin.getArenaManager().getArenaBuilder() != null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>There is already an arena being set up.");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/arena edit [id]");
            return;
        }

        // Gets the arena id.
        final String id = args[1];

        // Makes sure the arena exists.
        if(plugin.getArenaManager().getArena(id) == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That arena does not exist!");
            return;
        }

        final Arena arena = plugin.getArenaManager().getArena(id);

        JadedAPI.getPlugin().worldManager().loadWorld(id).thenAccept(world -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.setGameMode(GameMode.CREATIVE);
                player.teleport(world.getSpawnLocation());
                player.setFlying(true);
                plugin.getArenaManager().setArenaBuilder(new ArenaBuilder(plugin, arena, world));

                ChatUtils.chat(player, "<green><bold>CactusRush</bold> <dark_gray>» <green>You are now editing <white>" + arena.getName() + "<green>.");
                ChatUtils.chat(player, "<green><bold>CactusRush</bold> <dark_gray>» <green>When you are done, finish the arena with <white>/arena finish<green>.");
            });
        });
    }
}