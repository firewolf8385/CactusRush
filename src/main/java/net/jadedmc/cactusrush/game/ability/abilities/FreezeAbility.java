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
package net.jadedmc.cactusrush.game.ability.abilities;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.ability.Ability;
import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.jadedutils.chat.ChatUtils;
import net.jadedmc.jadedutils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Runs the freeze ability, which temporarily freezes the nearest opponent.
 */
public class FreezeAbility extends Ability {
    /**
     * Creates the ability.
     * @param plugin Instance of the plugin.
     */
    public FreezeAbility(@NotNull final CactusRushPlugin plugin) {
        super(plugin, "freeze", "&b&lFreeze", 40, 100);
    }

    /**
     * Gets the ability's icon.
     * @return Ability icon.
     */
    @Override
    public ItemStack getItemStack() {
        final ItemBuilder builder = new ItemBuilder(Material.LIGHT_BLUE_DYE)
                .setDisplayName("&b&lFreeze &7(Right Click)")
                .addLore("")
                .addLore("&7Prevents the closest opponent from")
                .addLore("&7moving for 2 seconds!")
                .addLore("")
                .addLore("&eCooldown: " + this.cooldown() + " seconds.");

        return builder.build();
    }

    /**
     * Runs when the ability is used.
     * @param player Player who used the ability.
     * @param game Game the ability was used in.
     */
    @Override
    public boolean onUse(@NotNull final Player player, @NotNull final Game game) {
        // Get all the opposing teams of the player.
        final List<Team> opponentTeams = new ArrayList<>();
        for(final Team team : game.getTeamManager().getTeams()) {
            if(team.equals(game.getTeamManager().getTeam(player))) {
                continue;
            }

            opponentTeams.add(team);
        }

        // Find the distance each opponent is from the player.
        final Map<Player, Double> distances = new HashMap<>();
        for(final Team team : opponentTeams) {
            for(final Player opponent : team.getTeamPlayers().asBukkitPlayers()) {
                distances.put(opponent, player.getLocation().distance(opponent.getLocation()));
            }
        }

        // Figure out which player is the closest.
        Player closestOpponent = distances.keySet().iterator().next();
        for(final Player opponent : distances.keySet()) {
            if(distances.get(opponent) < distances.get(closestOpponent)) {
                closestOpponent = opponent;
            }
        }

        // If they aren't close, cancel the ability.
        if(distances.get(closestOpponent) > 20) {
            ChatUtils.chat(player, "<red>No nearby players found!");
            return false;
        }

        // Apply the effects.
        final PotionEffect jumpBoost = new PotionEffect(PotionEffectType.JUMP, 40, 249);
        final PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, 40, 9);
        closestOpponent.addPotionEffect(jumpBoost);
        closestOpponent.addPotionEffect(slowness);

        // Send chat messages.
        final Team userTeam = game.getTeamManager().getTeam(player);
        final Team opponentTeam= game.getTeamManager().getTeam(closestOpponent);
        ChatUtils.chat(closestOpponent, "&aYou have been frozen by " + userTeam.getColor().getTextColor() + player.getName() + "&a!");
        ChatUtils.chat(player, "&aYou have frozen " + opponentTeam.getColor().getTextColor() + closestOpponent.getName() + "&a!");

        // Send spectator chat messages.
        for(final UUID spectator : game.getSpectators()) {
            ChatUtils.chat(spectator, userTeam.getColor().getTextColor() + player.getName() + " &ahas frozen " + opponentTeam.getColor().getTextColor() + closestOpponent.getName() + "&a!");
        }

        return true;
    }
}