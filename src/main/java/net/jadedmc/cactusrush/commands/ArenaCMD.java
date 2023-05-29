package net.jadedmc.cactusrush.commands;

import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.game.arenas.Arena;
import net.jadedmc.cactusrush.game.arenas.builder.ArenaBuilder;
import net.jadedmc.cactusrush.game.arenas.builder.ArenaBuilderTeam;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
public class ArenaCMD {
    private final CactusRushPlugin plugin;

    public ArenaCMD(final CactusRushPlugin plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            return;
        }

        Player player = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "create" -> createCMD(player, args);
            case "setname" -> setNameCMD(player, args);
            case "setvoidlevel" -> setVoidLevel(player, args);
            case "addmode" -> addMode(player, args);
            case "setwaitingarea" -> setWaitingAreaCMD(player);
            case "setspectatearea" -> setSpectateArea(player);
            case "addteam" -> addTeam(player, args);
            case "setspawnbounds" -> setSpawnBounds(player, args);
            case "setteamspawn" -> setTeamSpawn(player, args);
            case "setscorebounds" -> setScoreBounds(player, args);
            case "setscorespawn" -> setScoreSpawn(player, args);
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
        // Makes sure there is no arena builder already being set up.
        if(plugin.getArenaManager().getArenaBuilder() != null) {
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

        // Makes sure the id isn't already in use.
        for(Arena arena : plugin.getArenaManager().getArenas()) {
            if(arena.getId().equalsIgnoreCase(id)) {
                ChatUtils.chat(player, "&cError &8» &cThere is already an arena with that id.");
                return;
            }
        }

        // Starts the arena setup process.
        plugin.getArenaManager().setArenaBuilder(new ArenaBuilder(plugin));
        plugin.getArenaManager().getArenaBuilder().setId(id);

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
        if(plugin.getArenaManager().getArenaBuilder() == null) {
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
        plugin.getArenaManager().getArenaBuilder().setName(name);
        ChatUtils.chat(player, "&a&lCactusRush &8» &aArena name set to &f" + name + "&a.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the arena void level with &f/arena setvoidlevel [y-value]&a.");
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
        plugin.getArenaManager().getArenaBuilder().setVoidLevel(voidLevel);
        ChatUtils.chat(player, "&a&lCactusRush &8» &aVoid level has been set to &f" + voidLevel + "&a.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, add the applicable modes with &f/arena addmode [mode]&a.");
    }

    private void addMode(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena addmode [mode]");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "1v1" -> plugin.getArenaManager().getArenaBuilder().addMode(Mode.ONE_V_ONE);
            case "2v2" -> plugin.getArenaManager().getArenaBuilder().addMode(Mode.TWO_V_TWO);
            case "3v3" -> plugin.getArenaManager().getArenaBuilder().addMode(Mode.THREE_V_THREE);
            case "4v4" -> plugin.getArenaManager().getArenaBuilder().addMode(Mode.FOUR_V_FOUR);
            default -> {
                ChatUtils.chat(player, "&cError &8» &cThat is not a valid mode.");
                return;
            }
        }

        ChatUtils.chat(player, "&a&lCactusRush &8» &aAdded more &f" + args[1] + "&a.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the waiting area spawn with &f/arena setwaitingarea&a.");
    }

    /**
     * Runs the /arena setwaitingarea command.
     * This command sets the waiting area spawn for the new arena.
     * @param player Player running the command.
     */
    private void setWaitingAreaCMD(Player player) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Sets the waiting area spawn.
        plugin.getArenaManager().getArenaBuilder().setWaitingArea(player.getLocation());
        ChatUtils.chat(player, "&a&lCactusRush &8» &aYou have set the waiting area spawn to your location.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, spectate area with &f/arena setspectatearea.");
    }

    /**
     * Runs the /arena setspectatearea command.
     * This command sets the waiting area spawn for the new arena.
     * @param player Player running the command.
     */
    private void setSpectateArea(Player player) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Sets the waiting area spawn.
        plugin.getArenaManager().getArenaBuilder().setSpectatorArea(player.getLocation());
        ChatUtils.chat(player, "&a&lCactusRush &8» &aYou have set the spectate area to your location.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, add the first team with &f/arena addteam [team number].");
    }

    private void addTeam(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length == 1) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena addteam [id]");
            return;
        }

        // Gets the team to add.
        String id = args[1];

        // Checks if it already exists.
        if(plugin.getArenaManager().getArenaBuilder().hasTeam(id)) {
            ChatUtils.chat(player, "&cError &8» &cThat team already exists!");
            return;
        }

        plugin.getArenaManager().getArenaBuilder().addTeam(id);
        ChatUtils.chat(player, "&a&lCactusRush &8» &aCreated team &f" + id + "&a!");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNow, set the team spawn bounds with &f/arena setspawnbounds " + id + " [1/2]&a.");
    }

    private void setSpawnBounds(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length < 3) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setspawnbounds [team] [1/2]");
            return;
        }

        // Gets the team to add.
        ArenaBuilderTeam team = plugin.getArenaManager().getArenaBuilder().getTeam(args[1]);

        // Make sure the team exists.
        if(team == null) {
            ChatUtils.chat(player, "&cError &8» &cThat team does not exist! Add them with &f/arena addteam " + args[1] + "&c.");
            return;
        }

        int boundsNumber = Integer.parseInt(args[2]);

        if(boundsNumber == 1) {
            team.setBounds1(player.getTargetBlock(null, 5).getLocation());
            ChatUtils.chat(player, "&a&lCactusRush &8» &aBounds 1 set.");
        }
        else if(boundsNumber == 2) {
            team.setBounds2(player.getTargetBlock(null, 5).getLocation());
            ChatUtils.chat(player, "&a&lCactusRush &8» &aBounds 2 set.");
            ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the team spawn point with &f/arena setteamspawn [team]");
        }
        else {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setspawnbounds [team] [1/2]");
            return;
        }
    }

    private void setTeamSpawn(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length < 2) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setteamspawn [team]");
            return;
        }

        // Gets the team to add.
        ArenaBuilderTeam team = plugin.getArenaManager().getArenaBuilder().getTeam(args[1]);

        // Make sure the team exists.
        if(team == null) {
            ChatUtils.chat(player, "&cError &8» &cThat team does not exist! Add them with &f/arena addteam " + args[1] + "&c.");
            return;
        }

        team.setSpawnPoint(player.getLocation());
        ChatUtils.chat(player, "&a&lCactusRush &8» &aSpawn point set.");
        ChatUtils.chat(player, "&a&lCactusRush &8» &aNext, set the score room bounds with &f/arena setscorebounds [team]");
    }

    private void setScoreBounds(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length < 3) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setscorebounds [team] [1/2]");
            return;
        }

        // Gets the team to add.
        ArenaBuilderTeam team = plugin.getArenaManager().getArenaBuilder().getTeam(args[1]);

        // Make sure the team exists.
        if(team == null) {
            ChatUtils.chat(player, "&cError &8» &cThat team does not exist! Add them with &f/arena addteam " + args[1] + "&c.");
            return;
        }

        int boundsNumber = Integer.parseInt(args[2]);

        if(boundsNumber == 1) {
            team.getScoreRoom().setBounds1(player.getTargetBlock(null, 5).getLocation());
            ChatUtils.chat(player, "&a&lCactusRush &8» &aBounds 1 set.");
        }
        else if(boundsNumber == 2) {
            team.getScoreRoom().setBounds2(player.getTargetBlock(null, 5).getLocation());
            ChatUtils.chat(player, "&a&lCactusRush &8» &aBounds 2 set.");
            ChatUtils.chat(player, "&a&lCactusRush &8» &aThen, set the score room spawn point with &f/arena setscorespawn [team]");
        }
    }

    private void setScoreSpawn(Player player, String[] args) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the command is being used properly.
        if(args.length < 2) {
            ChatUtils.chat(player, "&cUsage &8» &c/arena setscorespawn [team]");
            return;
        }

        // Gets the team to add.
        ArenaBuilderTeam team = plugin.getArenaManager().getArenaBuilder().getTeam(args[1]);

        // Make sure the team exists.
        if(team == null) {
            ChatUtils.chat(player, "&cError &8» &cThat team does not exist! Add them with &f/arena addteam " + args[1] + "&c.");
            return;
        }

        team.getScoreRoom().setSpawnPoint(player.getLocation());
        ChatUtils.chat(player, "&a&lCactusRush &8» &aScore room spawn point set.");

        if(plugin.getArenaManager().getArenaBuilder().isSet()) {
            ChatUtils.chat(player, "&a&lCactusRush &8» &aScore room spawn point set.");
            ChatUtils.chat(player, "&a&lCactusRush &8» &aFinish the setup with &f/arena finish&a.");
        }
        else {
            ChatUtils.chat(player, "&a&lCactusRush &8» &aNow set up the next team with &f./arena addteam [team]&a.");
        }
    }

    /**
     * Runs the /arena finish command.
     * This command checks if the arena is done and saves it if so.
     * @param player Player running the command.
     */
    private void finishCMD(Player player) {
        // Makes sure there an arena is being set up.
        if(plugin.getArenaManager().getArenaBuilder() == null) {
            ChatUtils.chat(player, "&cError &8» &cYou need to create an arena first! /arena create");
            return;
        }

        // Makes sure the arena has a name.
        if(!plugin.getArenaManager().getArenaBuilder().isSet()) {
            ChatUtils.chat(player, "&cError &8» &cThe arena isn't finished yet!");
            return;
        }

        // Saves the arena.
        ChatUtils.chat(player, "&a&lCactusRush &8» &aArena has been saved.");
        plugin.getArenaManager().getArenaBuilder().save();
    }
}