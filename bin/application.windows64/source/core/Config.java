package basilisk.core;

// Config variables shared throughout the entire application
public class Config {
    public static int screenConfig = 0;

    public static final int GuiFrameRate = 10;
    public static final int SCREEN_1440x900 = 2;
    public static final int SCREEN_1920x1080 = 1;

    public static int loadingStep = 0;
    public static boolean loading = false;

    public static final int loadingStepDone = 0;
    public static final int loadingStepApple = 1;
    public static final int loadingStepSnake = 2;
    public static final int loadingStepCornerLeft = 3;
    public static final int loadingStepCornerRight = 4;

    public static volatile boolean paused = false;
    public static boolean showGameDetection = false;

    public static boolean showAiDebugs = false;
    public static boolean showAnimationDebugs = false;

    public static final int port = 61887;
}
