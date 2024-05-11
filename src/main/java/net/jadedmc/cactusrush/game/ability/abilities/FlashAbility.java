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
 * Runs the Flash ability, which gives the player a temporary speed boost.
 */
public class FlashAbility extends Ability {

    /**
     * Creates the ability.
     * @param plugin Instance of the plugin.
     */
    public FlashAbility(CactusRushPlugin plugin) {
        super(plugin, "flash", "&e&lFlash", 30, 0);
    }

    /**
     * Gets the ability's icon.
     * @return Ability's icon.
     */
    @Override
    public ItemStack itemStack() {
        ItemBuilder builder = new ItemBuilder(Material.YELLOW_DYE)
                .setDisplayName("&e&lFlash &7(Right Click)")
                .addLore("")
                .addLore("&7Give yourself Speed II for 5 seconds!")
                .addLore("")
                .addLore("&eCooldown: " + this.cooldown() + " seconds.");

        return builder.build();
    }

    /**
     * Runs when the ability is activated.
     * @param player Player who used the ability.
     * @param game Game the ability was used in.
     */
    @Override
    public boolean onUse(Player player, Game game) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 100, 1);
        player.addPotionEffect(speed);
        ChatUtils.chat(player, "&aYou have activated your &eFlash &eability!");

        for(Player spectator : game.spectators()) {
            ChatUtils.chat(spectator, game.teamManager().getTeam(player).color().textColor() + player.getName() + " &ahas activated their &eFlash &aability!");
        }

        return true;
    }
}