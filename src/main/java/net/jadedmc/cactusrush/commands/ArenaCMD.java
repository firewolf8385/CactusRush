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
import net.jadedmc.cactusrush.game.arena.builder.ArenaBuilder;
import net.jadedmc.cactusrush.game.arena.builder.ArenaBuilderTeam;
import net.jadedmc.cactusrush.game.arena.ArenaChunkGenerator;
import net.jadedmc.cactusrush.utils.FileUtils;
import net.jadedmc.cactusrush.utils.LocationUtils;
import net.jadedmc.cactusrush.utils.StringUtils;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;

/**
 * Manages the /arena command, which is used for setting up new arenas.
 */
public class ArenaCMD extends AbstractCommand {
    private final CactusRushPlugin plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public ArenaCMD(CactusRushPlugin plugin) {
        super("arena", "cr.admin", false);
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

        // Makes sure an argument is given.
        if(args.length == 0) {
            return;
        }

        // Process sub commands.
        switch (args[0].toLowerCase()) {
            case "create" -> createCMD(player, args);
            case "setname" -> setNameCMD(player, args);
            case "addmode" -> addMode(player, args);
            case "setvoidlevel" -> setVoidLevel(player, args);
            case "setwaitingarea" -> setWaitingAreaCMD(player);
            case "setspawn" -> setSpawnCMD(player, args);
            case "setspawnbounds" -> setSpawnBounds(player, args);
            case "setscoreroomspawn" -> setScoreRoomSpawnCMD(player, args);
            case "setscoreroombounds" -> setScoreRoomBoundsCMD(player, args);
            case "finish" -> finishCMD(player);

        }
    }

    /**
     * Runs the /arena create command.
     * This command starts the arena creation process.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void createCMD(Player player, String[] args) {
        if(plugin.arenaManager().getArenaBuilder() != null) {
            ChatUtils.chat(player, "&cError &8» &cThere is already an arena being set up.");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena create [id]");
            return;
        }

        // Gets the arena id.
        String id = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");

        // Starts the arena setup process.
        plugin.arenaManager().setArenaBuilder(new ArenaBuilder(plugin));
        plugin.arenaManager().getArenaBuilder().setId(id);

        // Creates the arena world.
        WorldCreator worldCreator = new WorldCreator(id);
        worldCreator.generator(new ArenaChunkGenerator());
        World world = Bukkit.createWorld(worldCreator);

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

        player.teleport(world.getSpawnLocation());
        player.setFlying(true);

        ChatUtils.chat(player, "&a&lCactusRush &8» &aCreated an arena with the id &f" + id + "&a.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the arena name with &f/arena setname [name]&a.");
    }

    /**
     * Runs the /arena setname command.
     * This command sets the name of the arena.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void setNameCMD(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.arenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setname [name]");
            return;
        }

        // Gets the arena name.
        String name = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");

        // Sets the arena name.
        plugin.arenaManager().getArenaBuilder().setName(name);
        ChatUtils.chat(player, "&a&lCactusRush &8» &aArena name set to &f" + name + "&a.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, add all allowable modes with &f/arena addmode [mode]&a.");
    }

    /**
     * Runs the /arena addmode command.
     * This command adds to the list of modes that the arena is allowed to be used in.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void addMode(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.arenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena addmode [mode]");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "1v1" -> plugin.arenaManager().getArenaBuilder().addMode(Mode.ONE_V_ONE);
            case "2v2" -> plugin.arenaManager().getArenaBuilder().addMode(Mode.TWO_V_TWO);
            case "3v3" -> plugin.arenaManager().getArenaBuilder().addMode(Mode.THREE_V_THREE);
            case "4v4" -> plugin.arenaManager().getArenaBuilder().addMode(Mode.FOUR_V_FOUR);
            case "competitive", "comp" -> plugin.arenaManager().getArenaBuilder().addMode(Mode.COMPETITIVE);
            default -> {
                ChatUtils.chat(player, "&cError &8» &cValid modes are: 1v1, 2v2, 3v3, 4v4, competitive");
                return;
            }
        }

        ChatUtils.chat(player, "&a&lCactusRush &8» &aAdded &f" + args[1] + "&a as a valid mode.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aWhen you are ready, set the void level with /arena setvoidlevel [level]");
    }

    /**
     * Runs the /arena setvoidlevel command.
     * This command sets the y level in which players should die and respawn.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void setVoidLevel(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.arenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setvoidlevel [y-level]");
            return;
        }

        // Gets the team size from the command.
        int voidLevel = Integer.parseInt(args[1]);

        // Sets the team size.
        plugin.arenaManager().getArenaBuilder().setVoidLevel(voidLevel);
        ChatUtils.chat(player, "&a&lCactusRush &8» &aVoid level has been set to &f" + voidLevel + "&a.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the waiting area spawn with &f/arena setwaitingarea&a.");
    }

    /**
     * Runs the /arena setwaitingarea command.
     * This command sets the waiting area spawn for the new arena.
     * @param player Player running the command.
     */
    private void setWaitingAreaCMD(Player player) {
        // Makes sure there an arena is being set up.
        if(plugin.arenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Sets the waiting area spawn.
        plugin.arenaManager().getArenaBuilder().setWaitingArea(player.getLocation());
        ChatUtils.chat(player, "&a&lCactusRush &8» &aYou have set the waiting area spawn to your location.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the first team's spawn with &f/arena setspawn 1.");
    }

    /**
     * Runs the /arena setspawn command.
     * This command sets the spawn point of a team in the arena.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void setSpawnCMD(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.arenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setspawn [team]");
            return;
        }

        // Count the number of teams required.
        int maxTeams = 0;
        for(Mode mode : plugin.arenaManager().getArenaBuilder().getModes()) {
            if(mode.teamCount() > maxTeams) {
                maxTeams = mode.teamCount();
            }
        }

        // Get the team number to modify.
        int teamNumber = Integer.parseInt(args[1]);

        // Make sure it is a valid team number.
        if(teamNumber > maxTeams || teamNumber < 1) {
            ChatUtils.chat(player, "&cError &8» &cInvalid team number, must be between 1 and " + maxTeams + ".");
            return;
        }

        // Create the team if it doesn't exist.
        if(plugin.arenaManager().getArenaBuilder().getTeam(args[1]) == null) {
            plugin.arenaManager().getArenaBuilder().addTeam(args[1]);

        }

        // Set the team's spawn point.
        ArenaBuilderTeam team = plugin.arenaManager().getArenaBuilder().getTeam(args[1]);
        team.setSpawnPoint(player.getLocation());

        ChatUtils.chat(player, "&a&lCactusRush &8» &aSpawn point of team &f" + teamNumber + " &aset to your location.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the spawn area boundaries with /arena setspawnbounds " + teamNumber + " [1/2].");
    }

    /**
     * Runs the /arena setspawnbounds command.
     * This command sets the spawn boundary area for a team.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void setSpawnBounds(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.arenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length != 3) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setspawnbounds [team] [bound#]");
            return;
        }

        // Count the number of teams required.
        int maxTeams = 0;
        for(Mode mode : plugin.arenaManager().getArenaBuilder().getModes()) {
            if(mode.teamCount() > maxTeams) {
                maxTeams = mode.teamCount();
            }
        }

        // Get the team number to modify.
        int teamNumber = Integer.parseInt(args[1]);

        // Make sure it is a valid team number.
        if(teamNumber > maxTeams || teamNumber < 1) {
            ChatUtils.chat(player, "&cError &8» &cInvalid team number, must be between 1 and " + maxTeams + ".");
            return;
        }

        // Makes sure the team exists.
        if(plugin.arenaManager().getArenaBuilder().getTeam(args[1]) == null) {
            ChatUtils.chat(player, "&cError &8» &cTeam " + teamNumber + " does not exist! Set a spawn first with /arena setspawn " + teamNumber + ".");
            return;
        }

        // Gets the boundary position number.
        int boundsPos = Integer.parseInt(args[2]);

        // Makes sure it's a valid boundary.
        if(boundsPos < 1 || boundsPos > 2) {
            ChatUtils.chat(player, "&cError &8» &cInvalid boundary position. Must be between 1 and 2!");
            return;
        }

        ChatUtils.chat(player, "&a&lCactusRush &8» &aBoundary &f" + boundsPos + " &aset to your location.");

        // Set the team's boundary.
        ArenaBuilderTeam team = plugin.arenaManager().getArenaBuilder().getTeam(args[1]);
        if(boundsPos == 1) {
            team.setBounds1(player.getLocation());
            ChatUtils.chat(player, "&a&lCactusRush &8» &aNow set the next one with /arena setspawnbounds " + teamNumber + " 2.");
        }
        else {
            team.setBounds2(player.getLocation());
            ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the score room spawn point with /arena setscoreroomspawn " + teamNumber + ".");
        }
    }

    /**
     * Runs the /arena setscoreroomspawn command.
     * This command sets the score room spawn point for a team.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void setScoreRoomSpawnCMD(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.arenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length != 2) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setscoreroomspawn [team]");
            return;
        }

        // Count the number of teams required.
        int maxTeams = 0;
        for(Mode mode : plugin.arenaManager().getArenaBuilder().getModes()) {
            if(mode.teamCount() > maxTeams) {
                maxTeams = mode.teamCount();
            }
        }

        // Get the team number to modify.
        int teamNumber = Integer.parseInt(args[1]);

        // Make sure it is a valid team number.
        if(teamNumber > maxTeams || teamNumber < 1) {
            ChatUtils.chat(player, "&cError &8» &cInvalid team number, must be between 1 and " + maxTeams + ".");
            return;
        }

        // Makes sure the team exists.
        if(plugin.arenaManager().getArenaBuilder().getTeam(args[1]) == null) {
            ChatUtils.chat(player, "&cError &8» &cTeam " + teamNumber + " does not exist! Set a spawn first with /arena setspawn " + teamNumber + ".");
            return;
        }

        // Set the team's score room spawn.
        ArenaBuilderTeam team = plugin.arenaManager().getArenaBuilder().getTeam(args[1]);
        team.getScoreRoom().setSpawnPoint(player.getLocation());

        ChatUtils.chat(player, "&a&lCactusRush &8» &aScore room spawn point of team &f" + teamNumber + " &aset to your location.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the spawn area boundaries with /arena setscoreroombounds " + teamNumber + " [1/2].");
    }

    /**
     * Runs the /arena setscoreroombounds command.
     * This command sets the score room boundary area for a team.
     * @param player Player running the command.
     * @param args Command arguments.
     */
    private void setScoreRoomBoundsCMD(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.arenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length != 3) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setscoreroombounds [team] [bound#]");
            return;
        }

        // Count the number of teams required.
        int maxTeams = 0;
        for(Mode mode : plugin.arenaManager().getArenaBuilder().getModes()) {
            if(mode.teamCount() > maxTeams) {
                maxTeams = mode.teamCount();
            }
        }

        // Get the team number to modify.
        int teamNumber = Integer.parseInt(args[1]);

        // Make sure it is a valid team number.
        if(teamNumber > maxTeams || teamNumber < 1) {
            ChatUtils.chat(player, "&cError &8» &cInvalid team number, must be between 1 and " + maxTeams + ".");
            return;
        }

        // Makes sure the team exists.
        if(plugin.arenaManager().getArenaBuilder().getTeam(args[1]) == null) {
            ChatUtils.chat(player, "&cError &8» &cTeam " + teamNumber + " does not exist! Set a spawn first with /arena setspawn " + teamNumber + ".");
            return;
        }

        // Gets the boundary position number.
        int boundsPos = Integer.parseInt(args[2]);

        // Makes sure it's a valid boundary.
        if(boundsPos < 1 || boundsPos > 2) {
            ChatUtils.chat(player, "&cError &8» &cInvalid boundary position. Must be between 1 and 2!");
            return;
        }

        ChatUtils.chat(player, "&a&lCactusRush &8» &aBoundary &f" + boundsPos + " &aset to your location.");

        // Set the team's boundary.
        ArenaBuilderTeam team = plugin.arenaManager().getArenaBuilder().getTeam(args[1]);
        if(boundsPos == 1) {
            team.getScoreRoom().setBounds1(player.getLocation());
            ChatUtils.chat(player, "&a&lCactusRush &8» &aNow set the next one with /arena setscoreroombounds " + teamNumber + " 2.");
        }
        else {
            team.getScoreRoom().setBounds2(player.getLocation());

            if(teamNumber == maxTeams) {
                ChatUtils.chat(player, "&a&lCactusRush &8» &aMake sure goals and barriers are all set. When you're done, finish the setup with /arena finish.");
            }
            else {
                ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the next team's spawn with /arena setspawn " + (teamNumber + 1) + ".");
            }
        }
    }

    /**
     * Runs the /arena finish command.
     * This command checks if the arena is done and saves it if so.
     * @param player Player running the command.
     */
    private void finishCMD(Player player) {
        // Makes sure there an arena is being set up.
        if(plugin.arenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Warn the player if setup is not complete.
        if(!plugin.arenaManager().getArenaBuilder().isSet()) {
            ChatUtils.chat(player, "&cError &8» &cSetup not complete!");
            return;
        }

        ChatUtils.chat(player, "&a&lCactusRush &8» &aArena has been saved.");

        // Saves the arena.
        plugin.arenaManager().getArenaBuilder().save();
        plugin.arenaManager().setArenaBuilder(null);

        // Remove all players from the world.
        World world = player.getWorld();
        String worldID = world.getName();
        File worldFolder = world.getWorldFolder();
        for(Player worldPlayer : world.getPlayers()) {
            worldPlayer.teleport(LocationUtils.getSpawn(plugin));
        }

        Bukkit.unloadWorld(world,true);

        // Saves the world where it belongs.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Load applicable folders.
            File mapsFolder = new File(worldFolder.getParentFile(), "maps");
            File savedWorldFolder = new File(mapsFolder, worldID);

            // Copies the world to the maps folder.
            FileUtils.copyFileStructure(worldFolder, savedWorldFolder);

            // Deletes the previous world.
            FileUtils.deleteDirectory(worldFolder);
        });
    }
}