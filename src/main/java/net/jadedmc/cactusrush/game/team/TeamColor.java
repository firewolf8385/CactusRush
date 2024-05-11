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
package net.jadedmc.cactusrush.game.team;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Represents the color of a team. Used to differentiate 2 or more teams.
 */
public enum TeamColor {
    RED(Material.RED_TERRACOTTA, Material.RED_STAINED_GLASS, ChatColor.RED, "Red", "R", 2500),
    ORANGE(Material.ORANGE_TERRACOTTA, Material.ORANGE_STAINED_GLASS, ChatColor.GOLD, "Orange", "O", 5000),
    YELLOW(Material.YELLOW_TERRACOTTA, Material.YELLOW_STAINED_GLASS, ChatColor.YELLOW, "Yellow", "Y", 0),
    GREEN(Material.LIME_TERRACOTTA, Material.LIME_STAINED_GLASS, ChatColor.GREEN, "Green", "G", 10000),
    BLUE(Material.BLUE_TERRACOTTA, Material.BLUE_STAINED_GLASS, ChatColor.BLUE, "Blue", "B", 999999),
    AQUA(Material.LIGHT_BLUE_TERRACOTTA, Material.LIGHT_BLUE_STAINED_GLASS, ChatColor.AQUA, "Aqua", "A", 15000),
    CYAN(Material.CYAN_TERRACOTTA, Material.CYAN_STAINED_GLASS, ChatColor.DARK_AQUA, "Cyan", "C", 25000),
    PURPLE(Material.PURPLE_TERRACOTTA, Material.PURPLE_STAINED_GLASS, ChatColor.DARK_PURPLE, "Purple", "P", 0),
    PINK(Material.PINK_TERRACOTTA, Material.PINK_STAINED_GLASS, ChatColor.LIGHT_PURPLE, "Pink", "P", 999999),
    BLACK(Material.BLACK_TERRACOTTA, Material.BLACK_STAINED_GLASS, ChatColor.BLACK, "Black", "B", 100000),
    WHITE(Material.WHITE_TERRACOTTA, Material.WHITE_STAINED_GLASS, ChatColor.WHITE, "White", "W", 50000);

    private final Material goalMaterial;
    private final Material scoreRoomMaterial;
    private final ChatColor textColor;
    private final String teamName;
    private final String abbreviation;
    private final int price;

    /**
     * Creates the team color.
     * @param goalMaterial Material the goal should be made of.
     * @param scoreRoomMaterial Material the score room should be made of.
     * @param textColor Color of all text associated with the team.
     * @param teamName Full name of the team.
     * @param abbreviation Abbreviation for the team.
     * @param price Price of the team color.
     */
    TeamColor(final Material goalMaterial, final Material scoreRoomMaterial, final ChatColor textColor, final String teamName, final String abbreviation, final int price) {
        this.goalMaterial = goalMaterial;
        this.scoreRoomMaterial = scoreRoomMaterial;
        this.textColor = textColor;
        this.teamName = teamName;
        this.abbreviation = abbreviation;
        this.price = price;
    }

    /**
     * Gets the team's abbreviation.
     * @return Team abbreviation.
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Gets the material the team's goal should be made of.
     * @return Goal material.
     */
    public Material goalMaterial() {
        return goalMaterial;
    }

    /**
     * Get the price of the color.
     * @return Price color.
     */
    public int price() {
        return price;
    }

    /**
     * Gets the material the team's score room should be made of.
     * @return Score room material.
     */
    public Material getScoreRoomMaterial() {
        return scoreRoomMaterial;
    }

    /**
     * Gets the full name of the team.
     * @return Team's full name.
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Gets the ChatColor enum associated with the team.
     * @return ChatColor of the team.
     */
    public ChatColor getTextColor() {
        return textColor;
    }
}