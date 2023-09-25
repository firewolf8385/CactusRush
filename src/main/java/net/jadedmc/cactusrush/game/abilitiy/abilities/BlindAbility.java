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
package net.jadedmc.cactusrush.game.abilitiy.abilities;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.abilitiy.Ability;
import net.jadedmc.cactusrush.game.teams.Team;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.cactusrush.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Runs the Blind ability, which blinds the nearest player.
 */
public class BlindAbility extends Ability {

    /**
     * Creates the ability.
     * @param plugin Instance of the plugin.
     */
    public BlindAbility(CactusRushPlugin plugin) {
        super(plugin, "blind", "&8&lBlind", 45, 250);
    }

    /**
     * Gets the Ability's icon.
     * @return Ability icon.
     */
    @Override
    public ItemStack itemStack() {
        ItemBuilder builder = new ItemBuilder(Material.COAL)
                .setDisplayName("&8&lBlind &7(Right Click)")
                .addLore("")
                .addLore("&7Gives the closest opponent blindness")
                .addLore("&7for 5 seconds!")
                .addLore("")
                .addLore("&eCooldown: " + this.cooldown() + " seconds.");

        return builder.build();
    }

    /**
     * Runs when the ability is used.
     * @param player Player using the ability.
     * @param game Game the ability is used in.
     */
    @Override
    public boolean onUse(Player player, Game game) {
        List<Team> opponentTeams = new ArrayList<>();
        for(Team team : game.teamManager().teams()) {
            if(team.equals(game.teamManager().getTeam(player))) {
                continue;
            }

            opponentTeams.add(team);
        }

        Map<Player, Double> distances = new HashMap<>();

        for(Team team : opponentTeams) {
            for(Player opponent : team.players()) {
                distances.put(opponent, player.getLocation().distance(opponent.getLocation()));
            }
        }

        Player closestOpponent = opponentTeams.get(0).players().iterator().next();

        for(Player opponent : distances.keySet()) {
            if(distances.get(opponent) < distances.get(closestOpponent)) {
                closestOpponent = opponent;
            }
        }

        if(distances.get(closestOpponent) > 20) {
            ChatUtils.chat(player, "&cNo nearby players founds!");
            return false;
        }

        PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 100, 0);
        closestOpponent.addPotionEffect(blindness);
        ChatUtils.chat(closestOpponent, "&aYou have been blinded by " + game.teamManager().getTeam(player).color().textColor() + player.getName() + "&a!");
        ChatUtils.chat(player, "&aYou have blinded " + game.teamManager().getTeam(closestOpponent).color().textColor() + closestOpponent.getName() + "&a!");

        for(Player spectator : game.spectators()) {
            ChatUtils.chat(spectator, game.teamManager().getTeam(player).color().textColor() + player.getName() + " &ahas blinded " + game.teamManager().getTeam(closestOpponent).color().textColor() + closestOpponent.getName() + "&a!");
        }

        return true;
    }
}