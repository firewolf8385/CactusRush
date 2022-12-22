package com.github.firewolf8385.cactusrush.game.team;

import com.github.firewolf8385.cactusrush.utils.xseries.XMaterial;
import org.bukkit.ChatColor;

/**
 * Represents a color option for a team.
 */
public enum TeamColor {
    RED(ChatColor.RED, "Red", XMaterial.RED_TERRACOTTA, "R"),
    ORANGE(ChatColor.GOLD, "Orange", XMaterial.ORANGE_TERRACOTTA, "O"),
    YELLOW(ChatColor.YELLOW, "Yellow", XMaterial.YELLOW_TERRACOTTA, "Y"),
    GREEN(ChatColor.GREEN, "Green", XMaterial.GREEN_TERRACOTTA, "G"),
    BLUE(ChatColor.BLUE, "Blue", XMaterial.BLUE_TERRACOTTA, "B"),
    AQUA(ChatColor.AQUA, "Aqua", XMaterial.LIGHT_BLUE_TERRACOTTA, "A"),
    PURPLE(ChatColor.DARK_PURPLE, "Purple", XMaterial.PURPLE_TERRACOTTA, "P"),
    PINK(ChatColor.LIGHT_PURPLE, "Pink", XMaterial.PINK_TERRACOTTA, "P"),
    BLACK(ChatColor.BLACK, "Black", XMaterial.BLACK_TERRACOTTA, "B");

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