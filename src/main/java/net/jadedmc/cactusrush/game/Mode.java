package net.jadedmc.cactusrush.game;

/**
 * Represents a game mode for Cactus Rush.
 * ALL is used for stat tracking only.
 */
public enum Mode {
    ALL(0,0),
    ONE_V_ONE(2,1),
    TWO_V_TWO(2,2),
    THREE_V_THREE(2,3),
    FOUR_V_FOUR(2,4);

    private final int teamCount;
    private final int teamSize;
    Mode(int teamCount, int teamSize) {
        this.teamCount = teamCount;
        this.teamSize = teamSize;
    }

    /**
     * Get the number of teams the mode has.
     * @return Team count of the mode.
     */
    public int teamCount() {
        return teamCount;
    }

    /**
     * Get the side of each team for the mode.
     * @return Team size of the mode.
     */
    public int teamSize() {
        return teamSize;
    }
}