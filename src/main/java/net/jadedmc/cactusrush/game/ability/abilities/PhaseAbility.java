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
import net.jadedmc.jadedutils.chat.ChatUtils;
import net.jadedmc.jadedutils.items.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PhaseAbility extends Ability {

    public PhaseAbility(@NotNull final CactusRushPlugin plugin) {
        super(plugin, "phase", "&a&lPhase", 50, 1000);
    }

    @Override
    public ItemStack getItemStack() {
        ItemBuilder builder = new ItemBuilder(Material.LIME_DYE)
                .setDisplayName("&a&lPhase &7(Right Click)")
                .addLore("")
                .addLore("&7Teleport to the block you are looking")
                .addLore("&7at, if it is safe.")
                .addLore("")
                .addLore("&eCooldown: " + this.cooldown() + " seconds.");

        return builder.build();
    }

    @Override
    public boolean onUse(@NotNull final Player player, @NotNull final Game game) {
        final Block targetBlock = player.getTargetBlockExact(5);

        if(targetBlock != null && (targetBlock.getType() == Material.SAND || targetBlock.getType() == Material.RED_SAND) && targetBlock.getRelative(BlockFace.UP).getType() == Material.AIR) {
            final Block safeBlock = targetBlock.getRelative(BlockFace.UP);
            final Location tpLocation = new Location(safeBlock.getWorld(), safeBlock.getLocation().getX() + 0.5, safeBlock.getLocation().getY(), safeBlock.getLocation().getZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());

            player.teleport(tpLocation);

            ChatUtils.chat(player, "&aYou have activated your &a&lPhase &aability!");

            for(final UUID spectator : game.getSpectators()) {
                ChatUtils.chat(spectator, game.getTeamManager().getTeam(player).getColor().getTextColor() + player.getName() + " &ahas activated their &a&lPhase &aability!");
            }

            return true;
        }

        ChatUtils.chat(player, "&cNo valid location found!");

        return false;
    }
}