package net.jadedmc.cactusrush.game.abilitiy.abilities;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.abilitiy.Ability;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.cactusrush.utils.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PhaseAbility extends Ability {

    public PhaseAbility(final CactusRushPlugin plugin) {
        super(plugin, "phase", "&a&lPhase", 50, 1000);
    }

    @Override
    public ItemStack itemStack() {
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
    public boolean onUse(Player player, Game game) {
        Block targetBlock = player.getTargetBlockExact(5);

        if(targetBlock != null && (targetBlock.getType() == Material.SAND || targetBlock.getType() == Material.RED_SAND) && targetBlock.getRelative(BlockFace.UP).getType() == Material.AIR) {
            Block safeBlock = targetBlock.getRelative(BlockFace.UP);
            Location tpLocation = new Location(safeBlock.getWorld(), safeBlock.getLocation().getX() + 0.5, safeBlock.getLocation().getY(), safeBlock.getLocation().getZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());

            player.teleport(tpLocation);

            ChatUtils.chat(player, "&aYou have activated your &a&lPhase &aability!");

            for(Player spectator : game.spectators()) {
                ChatUtils.chat(spectator, game.teamManager().getTeam(player).color().textColor() + player.getName() + " &ahas activated their &a&lPhase &aability!");
            }

            return true;
        }

        ChatUtils.chat(player, "&cNo valid location found!");

        return false;
    }
}