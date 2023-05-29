package com.github.firewolf8385.cactusrush.dev;

import com.github.firewolf8385.cactusrush.CactusRush;
import net.jadedmc.cactusrush.game.Mode;

public class ModeStats {
    private final CactusRush plugin;
    private final NewCactusPlayer cactusPlayer;
    private final Mode mode;

    private int bestLoseStreak;
    private int bestWinStreak;
    private int cactiBroke;
    private int cactiPlaced;
    private int currentLoseStreak;
    private int currentWinStreak;
    private int eggsHit;
    private int eggsThrown;
    private int gamesPlayed;
    private int goalsScored;
    private int losses;
    private int roundsPlayed;
    private int wins;



    public ModeStats(final CactusRush plugin, NewCactusPlayer cactusPlayer, final Mode mode) {
        this.plugin = plugin;
        this.cactusPlayer = cactusPlayer;
        this.mode = mode;
    }

    public int bestLoseStreak() {
        return bestLoseStreak;
    }

    public void bestLoseStreak(int bestLoseStreak) {
        this.bestLoseStreak = bestLoseStreak;
    }

    public int bestWinStreak() {
        return bestWinStreak;
    }

    public void bestWinStreak(int bestWinStreak) {
        this.bestWinStreak = bestWinStreak;
    }

    public int cactiBroke() {
        return cactiBroke;
    }

    public void cactiBroke(int cactiBroke) {
        this.cactiBroke = cactiBroke;
    }

    public int cactiPlaced() {
        return cactiPlaced;
    }

    public void cactiPlaced(int cactiPlaced) {
        this.cactiPlaced = cactiPlaced;
    }

    public int currentLoseStreak() {
        return currentLoseStreak;
    }

    public void currentLoseStreak(int currentLoseStreak) {
        this.currentLoseStreak = currentLoseStreak;
    }

    public int currentWinStreak() {
        return currentWinStreak;
    }

    public void currentWinStreak(int currentWinStreak) {
        this.currentWinStreak = currentWinStreak;
    }

    public int eggsHit() {
        return eggsHit;
    }

    public void eggsHit(int eggsHit) {
        this.eggsHit = eggsHit;
    }

    public int eggsThrown() {
        return  eggsThrown;
    }
    public void eggsThrown(int eggsThrown) {
        this.eggsThrown = eggsThrown;
    }

    public int gamesPlayed() {
        return gamesPlayed;
    }

    public void gamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int goalsScored() {
        return goalsScored;
    }

    public void goalsScored(int goalsScored) {
        this.goalsScored = goalsScored;
    }

    public int losses() {
        return losses;
    }

    public void losses(int losses) {
        this.losses = losses;
    }

    public Mode mode() {
        return mode;
    }

    public int roundsPlayed() {
        return roundsPlayed;
    }

    public void roundsPlayed(int roundsPlayed) {
        this.roundsPlayed = roundsPlayed;
    }

    public int wins() {
        return wins;
    }

    /**
     * Change the number of wins stored.
     * @param wins New amount of wins.
     */
    public void wins(int wins) {
        this.wins = wins;
    }
}