package org.leonitousconforti.basilisk;

/**
 * Config variables shared throughout the entire application.
 */
@SuppressWarnings({ "checkstyle:ConstantName", "checkstyle:ExplicitInitialization", "checkstyle:VisibilityModifier",
        "checkstyle:JavadocVariable" })
public final class Config {
    /**
     * The number of columns there are on the snake game board.
     */
    public static final int NumberOfColsOnGameBoard = 17;

    /**
     * The number of rows there are on the snake game board.
     */
    public static final int NumberOfRowsOnGameBoard = 15;

    /**
     * The width of the board around the game in pixels.
     */
    public static final int GameBoardBoarderWidthPixels = 28;

    /**
     * THe height in pixels of the header board above the game board.
     */
    public static final int GameBoardHeaderHeightPixels = 95;

    /**
     * The total size of the game window, in pixels.
     */
    public static final int GameBoardSizePixels = 600;

    /**
     * The size of each of the squares/quadrants on the game board that make up the
     * grid in pixels.
     */
    public static final int GameBoardQuadrantSizePixels = 32;

    /**
     * How long the splash screen should be shown before automatically closing, in
     * milliseconds.
     */
    public static final int SplashScreenTime = 5 * 1000;

    public static volatile boolean paused = false;
    public static volatile boolean showSettingsMenu = false;
    public static volatile int websocketServerPort = 61888;

    private Config() {
    }
}
