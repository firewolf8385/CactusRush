package com.github.firewolf8385.cactusrush.game.team;

import com.github.firewolf8385.cactusrush.utils.xseries.XMaterial;
import org.bukkit.ChatColor;

/**
 * Represents a color option for a team.
 */
public enum TeamColor {
    RED(ChatColor.RED, "RED", XMaterial.RED_WOOL, "R"),
    ORANGE(ChatColor.GOLD, "ORANGE", XMaterial.ORANGE_WOOL, "O"),
    YELLOW(ChatColor.YELLOW, "YELLOW", XMaterial.YELLOW_WOOL, "Y"),
    GREEN(ChatColor.GREEN, "GREEN", XMaterial.LIME_WOOL, "G"),
    BLUE(ChatColor.BLUE, "BLUE", XMaterial.BLUE_WOOL, "B"),
    AQUA(ChatColor.AQUA, "AQUA", XMaterial.LIGHT_BLUE_WOOL, "A"),
    PURPLE(ChatColor.DARK_PURPLE, "PURPLE", XMaterial.PURPLE_WOOL, "P"),
    PINK(ChatColor.LIGHT_PURPLE, "PINK", XMaterial.PINK_WOOL, "P"),
    BLACK(ChatColor.BLACK, "BLACK", XMaterial.BLACK_WOOL, "B");

    private final ChatColor chatColor;
    private final String name;
    private final XMaterial goal;
    private final String abbreviation;

    TeamColor(ChatColor chatColor, String name, XMaterial goal, String abbreviation) {
        this.chatColor = chatColor;
        this.name = name;
        this.goal = goal;
        this.abbreviation = abbreviation;
    }

    /**
     * Gets the abbreviation of a team.
     * @return Team's single-letter abbreviation.
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Gets the chat color of a team.
     * @return Chat color of the team.
     */
    public ChatColor getChatColor() {
        return chatColor;
    }

    /**
     * Gets the goal color of the team,
     * @return Wool color.
     */
    public XMaterial getGoal() {
        return goal;
    }

    /**
     * Gets the name of the team.
     * @return Name of the team.
     */
    public String getName() {
        return name;
    }
}