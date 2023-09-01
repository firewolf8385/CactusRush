/*
 * This file is part of JadedChat, licensed under the MIT License.
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
package com.github.firewolf8385.cactusrush.game.ability.abilities;

import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.ability.Ability;
import com.github.firewolf8385.cactusrush.game.team.Team;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import com.github.firewolf8385.cactusrush.utils.item.ItemBuilder;
import com.github.firewolf8385.cactusrush.utils.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlindAbility extends Ability {

    /**
     * Creates the Blind Ability, which blinds the nearest player when used.
     * @param plugin Instance of the plugin.
     */
    public BlindAbility(Plugin plugin) {
        super(plugin, "blind", "&8&lBlind", 45);
    }

    @Override
    public ItemStack getItemStack() {
        ItemBuilder builder = new ItemBuilder(XMaterial.COAL)
                .setDisplayName("&8&lBlind &7(Right Click)")
                .addLore("")
                .addLore("&7Gives the closest opponent blindness")
                .addLore("&7for 5 seconds!")
                .addLore("")
                .addLore("&eCooldown: " + this.cooldown() + " seconds.");

        return builder.build();
    }

    @Override
    public void onUse(Player player, Game game) {
        List<Team> opponentTeams = new ArrayList<>();
        for(Team team : game.getTeamManager().getTeams()) {
            if(team.equals(game.getTeamManager().getTeam(player))) {
                continue;
            }

            opponentTeams.add(team);
        }

        Map<Player, Double> distances = new HashMap<>();

        for(Team team : opponentTeams) {
            for(Player opponent : team.getPlayers()) {
                distances.put(opponent, player.getLocation().distance(opponent.getLocation()));
            }
        }

        Player closestOpponent = opponentTeams.get(0).getPlayers().get(0);

        for(Player opponent : distances.keySet()) {
            if(distances.get(opponent) < distances.get(closestOpponent)) {
                closestOpponent = opponent;
            }
        }

        PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 100, 0);
        closestOpponent.addPotionEffect(blindness);
        ChatUtils.chat(closestOpponent, "&aYou have been blinded by &f" + player.getName() + "&a!");
        ChatUtils.chat(player, "&aYou have blinded &f" + closestOpponent.getName() + "&a!");
    }
}