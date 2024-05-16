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

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameDeathType;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.game.ability.Ability;
import net.jadedmc.cactusrush.game.round.RoundPlayer;
import net.jadedmc.cactusrush.player.CactusPlayer;
import net.jadedmc.jadedcore.player.Rank;
import net.jadedmc.jadedutils.player.PluginPlayer;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TeamPlayer extends PluginPlayer {
    private final CactusRushPlugin plugin;
    private final Rank rank;
    private Ability ability;
    private int cactiBroke = 0;
    private int cactiPlaced = 0;
    private int eggsThrown = 0;
    private int goalsScored = 0;
    private int abilitiesUsed = 0;
    private int deaths = 0;
    private int cactiDeaths = 0;
    private int voidDeaths = 0;
    private int abilityDeaths = 0;
    private final Game game;
    private final CactusPlayer cactusPlayer;
    private int eggCooldownTaskID = -1;

    public TeamPlayer(@NotNull final CactusRushPlugin plugin, @NotNull final UUID playerUUID, @NotNull final String playerName, final Rank rank, @NotNull final Game game) {
        super(playerUUID, playerName);
        this.plugin = plugin;
        this.rank = rank;
        this.game = game;
        this.cactusPlayer = plugin.getCactusPlayerManager().getPlayer(playerUUID);
    }

    public TeamPlayer(@NotNull final Document document, @NotNull final Game game) {
        super(UUID.fromString(document.getString("uuid")), document.getString("name"));

        this.rank = Rank.valueOf(document.getString("rank"));

        final Document statsDocument = document.get("stats", Document.class);
        this.cactiBroke = statsDocument.getInteger("cactiBroke");
        this.cactiPlaced = statsDocument.getInteger("cactiPlaced");
        this.eggsThrown = statsDocument.getInteger("eggsThrown");
        this.goalsScored = statsDocument.getInteger("goalsScored");
        this.abilitiesUsed = statsDocument.getInteger("abilitiesUsed");
        this.deaths = statsDocument.getInteger("deaths");
        this.cactiDeaths = statsDocument.getInteger("cactiDeaths");
        this.voidDeaths = statsDocument.getInteger("voidDeaths");
        this.abilityDeaths = statsDocument.getInteger("abilityDeaths");

        this.game = game;
        this.cactusPlayer = null;
        this.plugin = null;
    }

    public void addAbilityUsed() {
        this.abilitiesUsed++;
        this.getRoundPlayer().addAbilityUsed();

        if(this.game.getMode() != Mode.DUEL) {
            this.cactusPlayer.addAbilityUse(this.game.getMode().getId(), this.game.getArena().getFileName(), this.plugin.getAbilityManager().getAbility(this.getUniqueId()).getId());
        }
    }

    public void addCactiBroken() {
        this.cactiBroke++;
        this.getRoundPlayer().addCactiBroken();

        if(this.game.getMode() != Mode.DUEL) {
            this.cactusPlayer.addCactiBroke(this.game.getMode().getId(), this.game.getArena().getFileName());
        }
    }

    public void addCactiPlaced() {
        this.cactiPlaced++;
        this.getRoundPlayer().addCactiPlaced();

        if(this.game.getMode() != Mode.DUEL) {
            this.cactusPlayer.addCactiPlaced(this.game.getMode().getId(), this.game.getArena().getFileName());
        }
    }

    public void addDeath(final GameDeathType deathType) {
        switch (deathType) {
            case ABILITY -> this.abilityDeaths++;
            case CACTUS -> cactiDeaths++;
            case VOID -> voidDeaths++;
        }

        deaths++;
        this.getRoundPlayer().addDeath(deathType);

        if(this.game.getMode() != Mode.DUEL) {
            this.cactusPlayer.addDeath(this.game.getMode().getId(), this.game.getArena().getFileName(), deathType);
        }
    }

    public void addEggCooldown() {
        if(hasEggCooldown()) {
            return;
        }

        eggCooldownTaskID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(hasEggCooldown()) {
                if(game.getGameState() == GameState.RUNNING) {
                    Player player = plugin.getServer().getPlayer(this.getUniqueId());

                    if(player != null) {
                        player.getInventory().addItem(new ItemStack(Material.EGG));
                    }
                }

                eggCooldownTaskID = -1;
            }
        }, 30);
    }

    public void addEggThrown() {
        eggsThrown++;
        this.getRoundPlayer().addEggThrown();

        if(this.game.getMode() != Mode.DUEL) {
            this.cactusPlayer.addEggsThrown(this.game.getMode().getId(), this.game.getArena().getFileName());
        }
    }

    public void addGoalScored() {
        goalsScored++;
        this.getRoundPlayer().addGoalScored();

        if(this.game.getMode() != Mode.DUEL) {
            this.cactusPlayer.addGoalsScored(this.game.getMode().getId(), this.game.getArena().getFileName());
        }
    }

    public int getAbilityDeaths() {
        return abilityDeaths;
    }

    public int getAbilitiesUsed() {
        return abilitiesUsed;
    }

    /**
     * Gets the Bukkit Player being represented.
     * Returns null if not found.
     * @return Bukkit Player object.
     */
    @Nullable
    public Player getBukkitPlayer() {
        return plugin.getServer().getPlayer(this.getUniqueId());
    }

    public int getCactiBroke() {
        return cactiBroke;
    }

    public int getCactiDeaths() {
        return cactiDeaths;
    }

    public int getCactiPlaced() {
        return cactiPlaced;
    }

    public CactusPlayer getCactusPlayer() {
        return cactusPlayer;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getEggsThrown() {
        return eggsThrown;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public RoundPlayer getRoundPlayer() {
        if(this.game.getRoundManager().getCurrentRound() == null) {
            return null;
        }

        return this.game.getRoundManager().getCurrentRound().getPlayers().getPlayer(this.getUniqueId());
    }

    public int getVoidDeaths() {
        return voidDeaths;
    }

    public boolean hasEggCooldown() {
        return eggCooldownTaskID != -1;
    }

    /**
     * Plays a given sound for the player with a given volume and pitch if they are online.
     * Skips them if they are not online.
     * @param sound Sound to play.
     * @param volume Volume to play sound at.
     * @param pitch Pitch to play sound with.
     */
    public void playSound(final Sound sound, final float volume, final float pitch) {
        final Player player = this.getBukkitPlayer();

        // If the player is not online, ignore them.
        if(player == null) {
            return;
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void removeEggCooldown() {
        plugin.getServer().getScheduler().cancelTask(this.eggCooldownTaskID);
        eggCooldownTaskID = -1;
    }

    public Document toDocument() {
        final Document document = new Document()
                .append("uuid", this.getUniqueId().toString())
                .append("name", this.getName())
                .append("rank", rank.toString());

        final Document statsDocument = new Document()
                .append("cactiBroke", cactiBroke)
                .append("cactiPlaced", cactiPlaced)
                .append("eggsThrown", eggsThrown)
                .append("goalsScored", goalsScored)
                .append("abilitiesUsed", abilitiesUsed)
                .append("deaths", deaths)
                .append("cactiDeaths", cactiDeaths)
                .append("voidDeaths", voidDeaths)
                .append("abilityDeaths", abilityDeaths);
        document.append("stats", statsDocument);

        return document;
    }
}