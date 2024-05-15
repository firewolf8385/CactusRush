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
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.game.round;

import net.jadedmc.cactusrush.CactusRushPlugin;
import net.jadedmc.cactusrush.game.GameDeathType;
import net.jadedmc.cactusrush.game.ability.Ability;
import net.jadedmc.jadedutils.player.CustomPlayer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RoundPlayer implements CustomPlayer {
    private final UUID playerUUID;
    private final String playerName;
    private final Ability ability;
    private int cactiBroke = 0;
    private int cactiPlaced = 0;
    private int eggsThrown = 0;
    private int goalsScored = 0;
    private int abilitiesUsed = 0;
    private int deaths = 0;
    private int cactiDeaths = 0;
    private int voidDeaths = 0;
    private int abilityDeaths = 0;

    public RoundPlayer(@NotNull final UUID playerUUID, @NotNull final String playerName, @NotNull final Ability ability) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.ability = ability;
    }

    public RoundPlayer(@NotNull final CactusRushPlugin plugin, @NotNull final Document document) {
        this.playerUUID = UUID.fromString(document.getString("uuid"));
        this.playerName = document.getString("name");
        this.ability = plugin.getAbilityManager().getAbility(document.getString("ability"));

        final Document stats = document.get("stats", Document.class);
        this.cactiBroke = document.getInteger("cactiBroke");
        this.cactiPlaced = document.getInteger("cactiPlaced");
        this.eggsThrown = document.getInteger("eggsThrown");
        this.goalsScored = document.getInteger("goalsScored");
        this.abilitiesUsed = document.getInteger("abilitiesUsed");
        this.deaths = document.getInteger("deaths");
        this.cactiDeaths = document.getInteger("cactiDeaths");
        this.voidDeaths = document.getInteger("voidDeaths");
        this.abilityDeaths = document.getInteger("abilityDeaths");
    }

    public void addAbilityUsed() {
        abilitiesUsed++;
    }

    public void addCactiBroken() {
        cactiBroke++;
    }

    public void addCactiPlaced() {
        cactiPlaced++;
    }

    public void addDeath(final GameDeathType deathType) {
        switch (deathType) {
            case ABILITY -> abilityDeaths++;
            case CACTUS -> cactiDeaths++;
            case VOID -> voidDeaths++;
        }

        deaths++;
    }

    public void addEggThrown() {
        eggsThrown++;
    }

    public void addGoalScored() {
        goalsScored++;
    }

    public final int getAbilitiesUsed() {
        return abilitiesUsed;
    }

    public final int getCactiBroken() {
        return cactiBroke;
    }

    public final int getCactiPlaced() {
        return cactiPlaced;
    }

    public final int getDeaths() {
        return deaths;
    }

    public final int getDeaths(final GameDeathType deathType) {
        switch (deathType) {
            case ABILITY -> {
                return abilityDeaths;
            }

            case CACTUS -> {
                return cactiDeaths;
            }

            case VOID -> {
                return voidDeaths;
            }

            default -> {
                return deaths;
            }
        }
    }

    public final int getEggsThrown() {
        return eggsThrown;
    }

    public final int getGoalsScored() {
        return goalsScored;
    }

    public final String getName() {
        return playerName;
    }

    @Override
    public final UUID getUniqueId() {
        return playerUUID;
    }

    @NotNull
    public final Document toDocument() {
        final Document document = new Document();

        if(this.ability == null) {
            document.append("ability", "NULL");
        }
        else {
            document.append("ability", this.ability.getId());
        }

        document.append("cactiBroke", cactiBroke)
                .append("cactiPlaced", cactiPlaced)
                .append("eggsThrown", eggsThrown)
                .append("goalsScored", goalsScored)
                .append("abilitiesUsed", abilitiesUsed)
                .append("deaths", deaths)
                .append("cactiDeaths", cactiDeaths)
                .append("voidDeaths", voidDeaths)
                .append("abilityDeaths", abilityDeaths);

        return document;
    }
}