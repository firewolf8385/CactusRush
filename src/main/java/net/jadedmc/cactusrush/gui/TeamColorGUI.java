package net.jadedmc.cactusrush.gui;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.team.TeamColor;
import net.jadedmc.cactusrush.player.CactusPlayer;
import net.jadedmc.jadedutils.chat.ChatUtils;
import net.jadedmc.jadedutils.gui.CustomGUI;
import net.jadedmc.jadedutils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TeamColorGUI extends CustomGUI {
    private final CactusRushPlugin plugin;

    public TeamColorGUI(CactusRushPlugin plugin, Player player) {
        super(45, "Team Colors");
        this.plugin = plugin;

        CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);

        // Filler
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44};
        for(int i : fillers) {
            setItem(i, filler);
        }

        if(cactusPlayer.hasPrimaryTeamColor()) {
            ItemBuilder builder = new ItemBuilder(cactusPlayer.getPrimaryTeamColor().goalMaterial())
                    .setDisplayName("&a&lPrimary Color");
            setItem(20, builder.build(), (p,a) -> new TeamColorGUI(plugin, p, "primary").open(p));
        }
        else {
            ItemBuilder builder = new ItemBuilder(Material.YELLOW_TERRACOTTA)
                    .setDisplayName("&a&lPrimary Color");
            setItem(20, builder.build(), (p,a) -> new TeamColorGUI(plugin, p, "primary").open(p));
        }

        if(cactusPlayer.hasSecondaryTeamColor()) {
            ItemBuilder builder = new ItemBuilder(cactusPlayer.getSecondaryTeamColor().goalMaterial())
                    .setDisplayName("&a&lSecondary Color");
            setItem(24, builder.build(), (p,a) -> new TeamColorGUI(plugin, p, "secondary").open(p));
        }
        else {
            ItemBuilder builder = new ItemBuilder(Material.PURPLE_TERRACOTTA)
                    .setDisplayName("&a&lSecondary Color");
            setItem(24, builder.build(), (p,a) -> new TeamColorGUI(plugin, p, "secondary").open(p));
        }
    }

    public TeamColorGUI(CactusRushPlugin plugin, Player player, String type) {
        super(54, "Team Colors");
        this.plugin = plugin;

        CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);

        // Filler
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        int[] fillers = {0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53};
        for(int i : fillers) {
            setItem(i, filler);
        }

        TeamColor[] colors = new TeamColor[]{TeamColor.YELLOW, TeamColor.PURPLE, TeamColor.RED, TeamColor.ORANGE, TeamColor.GREEN, TeamColor.AQUA, TeamColor.CYAN, TeamColor.WHITE, TeamColor.BLACK};
        int[] colorSlots = new int[]{18,28,20,30,22,32,24,34,26,36};

        int i = 0;
        for(TeamColor teamColor : colors) {

            if(cactusPlayer.getUnlockedTeamColors().contains(teamColor)) {
                ItemBuilder builder = new ItemBuilder(teamColor.goalMaterial())
                        .setDisplayName(teamColor.getTextColor() + teamColor.getTeamName())
                        .addLore("")
                        .addLore("&aClick to select!");
                setItem(colorSlots[i], builder.build(), (p, a) -> {
                    if(type.equalsIgnoreCase("primary")) {
                        cactusPlayer.setPrimaryTeamColor(teamColor);
                    }
                    else {
                        cactusPlayer.setSecondaryTeamColor(teamColor);
                    }

                    ChatUtils.chat(p, "&f" + teamColor.getTextColor() + teamColor.getTeamName() + " &ahas been selected!");
                    p.closeInventory();
                });
            }
            else {
                ItemBuilder builder = new ItemBuilder(teamColor.goalMaterial())
                        .setDisplayName(teamColor.getTextColor() + teamColor.getTeamName())
                        .addLore("")
                        .addLore("&6Price: " + teamColor.price() + " Coins")
                        .addLore("&cClick to purchase!");
                setItem(colorSlots[i], builder.build(), (p,a) -> {
                    if(cactusPlayer.getCoins() < teamColor.price()) {
                        ChatUtils.chat(p, "&cError &8» &cYou do not have enough coins for that!");
                        return;
                    }

                    if(type.equalsIgnoreCase("primary")) {
                        cactusPlayer.setPrimaryTeamColor(teamColor);
                    }
                    else {
                        cactusPlayer.setSecondaryTeamColor(teamColor);
                    }

                    cactusPlayer.removeCoins(teamColor.price());
                    cactusPlayer.unlockTeamColor(teamColor);

                    ChatUtils.chat(p, "&f" + teamColor.getTextColor() + teamColor.getTeamName() + " &ahas been purchased and selected!");
                    p.closeInventory();

                });
            }

            i++;
        }
    }
}