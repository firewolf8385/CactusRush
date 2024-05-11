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

/**
 * Runs the freeze ability, which temporarily freezes the nearest opponent.
 */
public class FreezeAbility extends Ability {
    /**
     * Creates the ability.
     * @param plugin Instance of the plugin.
     */
    public FreezeAbility(CactusRushPlugin plugin) {
        super(plugin, "freeze", "&b&lFreeze", 40, 100);
    }

    /**
     * Gets the ability's icon.
     * @return Ability icon.
     */
    @Override
    public ItemStack itemStack() {
        ItemBuilder builder = new ItemBuilder(Material.LIGHT_BLUE_DYE)
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
            ChatUtils.chat(player, "&cNo nearby players found!");
            return false;
        }

        PotionEffect jumpBoost = new PotionEffect(PotionEffectType.JUMP, 40, 249);
        PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, 40, 9);
        closestOpponent.addPotionEffect(jumpBoost);
        closestOpponent.addPotionEffect(slowness);

        ChatUtils.chat(closestOpponent, "&aYou have been frozen by " + game.teamManager().getTeam(player).color().textColor() + player.getName() + "&a!");
        ChatUtils.chat(player, "&aYou have frozen " + game.teamManager().getTeam(closestOpponent).color().textColor() + closestOpponent.getName() + "&a!");

        for(Player spectator : game.spectators()) {
            ChatUtils.chat(spectator, game.teamManager().getTeam(player).color().textColor() + player.getName() + " &ahas frozen " + game.teamManager().getTeam(closestOpponent).color().textColor() + closestOpponent.getName() + "&a!");
        }

        return true;
    }
}