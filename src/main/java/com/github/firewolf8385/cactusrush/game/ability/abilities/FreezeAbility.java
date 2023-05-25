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

public class FreezeAbility extends Ability {
    public FreezeAbility(Plugin plugin) {
        super(plugin, "freeze", "&b&lFreeze", 35);
    }

    @Override
    public ItemStack getItemStack() {
        ItemBuilder builder = new ItemBuilder(XMaterial.LIGHT_BLUE_DYE)
                .setDisplayName("&b&lFreeze &7(Right Click)")
                .addLore("")
                .addLore("&7Prevents the closest opponent from")
                .addLore("&7moving for 2 seconds!")
                .addLore("")
                .addLore("&eCooldown: 35 seconds");

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

        PotionEffect jumpBoost = new PotionEffect(PotionEffectType.JUMP, 40, 249);
        PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, 40, 9);
        closestOpponent.addPotionEffect(jumpBoost);
        closestOpponent.addPotionEffect(slowness);

        ChatUtils.chat(closestOpponent, "&aYou have been frozen by &f" + player.getName() + "&a!");
        ChatUtils.chat(player, "&aYou have frozen &f" + closestOpponent.getName() + "&a!");
    }
}