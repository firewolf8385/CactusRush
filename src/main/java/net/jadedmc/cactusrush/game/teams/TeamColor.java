package net.jadedmc.cactusrush.game.teams;

import com.github.firewolf8385.cactusrush.utils.xseries.XMaterial;
import org.bukkit.ChatColor;

/**
 * Represents a color option for a team.
 */
public enum TeamColor {
    RED(ChatColor.RED, "Red", XMaterial.RED_TERRACOTTA, XMaterial.RED_STAINED_GLASS, "R"),
    ORANGE(ChatColor.GOLD, "Orange", XMaterial.ORANGE_TERRACOTTA, XMaterial.ORANGE_STAINED_GLASS, "O"),
    YELLOW(ChatColor.YELLOW, "Yellow", XMaterial.YELLOW_TERRACOTTA, XMaterial.YELLOW_STAINED_GLASS, "Y"),
    GREEN(ChatColor.GREEN, "Green", XMaterial.GREEN_TERRACOTTA, XMaterial.GREEN_STAINED_GLASS, "G"),
    BLUE(ChatColor.BLUE, "Blue", XMaterial.BLUE_TERRACOTTA, XMaterial.BLUE_STAINED_GLASS, "B"),
    AQUA(ChatColor.AQUA, "Aqua", XMaterial.LIGHT_BLUE_TERRACOTTA, XMaterial.LIGHT_BLUE_STAINED_GLASS, "A"),
    PURPLE(ChatColor.DARK_PURPLE, "Purple", XMaterial.PURPLE_TERRACOTTA, XMaterial.PURPLE_STAINED_GLASS, "P"),
    PINK(ChatColor.LIGHT_PURPLE, "Pink", XMaterial.PINK_TERRACOTTA, XMaterial.PINK_STAINED_GLASS, "P"),
    BLACK(ChatColor.BLACK, "Black", XMaterial.BLACK_TERRACOTTA, XMaterial.BLACK_STAINED_GLASS, "B");

    private final ChatColor chatColor;
    private final String name;
    private final XMaterial goal;
    private final XMaterial scoreRoom;
    private final String abbreviation;

    TeamColor(ChatColor chatColor, String name, XMaterial goal, XMaterial scoreRoom, String abbreviation) {
        this.chatColor = chatColor;
        this.name = name;
        this.goal = goal;
        this.scoreRoom = scoreRoom;
        this.abbreviation = abbreviation;
    }

    /**
     * Gets the abbreviation of a team.
     * @return Team's single-letter abbreviation.
     */
    public String abbreviation() {
        return abbreviation;
    }

    /**
     * Gets the chat color of a team.
     * @return Chat color of the team.
     */
    public ChatColor chatColor() {
        return chatColor;
    }

    /**
     * Gets the goal material of the team,
     * @return Goal material.
     */
    public XMaterial goalMaterial() {
        return goal;
    }

    /**
     * Get the score room material of the team
     * @return Score Room Material
     */
    public XMaterial scoreRoomMaterial() {
        return scoreRoom;
    }

    /**
     * Gets the name of the team.
     * @return Name of the team.
     */
    public String teamName() {
        return name;
    }
}