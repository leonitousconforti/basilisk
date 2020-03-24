package basilisk.core;

// Config variables shared throughout the entire application
public class Config {
    public static final class BasiliskProgram {
        // Is the program paused
        public static volatile boolean paused = false;

        // What logs should be shown
        public static boolean showAiDebugs = false;
        public static boolean showAnimationDebugs = false;

        // The screen config the AI is going to have to run in
        public static int screenConfig = 0;
        public static final int SCREEN_1920x1080 = 1;
        public static final int SCREEN_1440x900 = 2;
    }

    public static final class GuiConfigs {
        // Gui framerate
        public static final int GuiFrameRate = 10;

        // For scaling elements when drawing to the windows
        public static int scale = 1;
    
        // Is this is loading processing and what step are we on?
        public static int loadingStep = 0;
        public static boolean loading = false;

        // Define the loading steps
        public static final int loadingStepDone = 0;
        public static final int loadingStepApple = 1;
        public static final int loadingStepSnake = 2;
        public static final int loadingStepCornerLeft = 3;
        public static final int loadingStepCornerRight = 4;

        // Show what the AI sees on the gui
        public static boolean showGameDetection = false;
    }

    public static final class ActionsManagerConfigs {
        // Where the websocket is going to listen for requests
        public static final int websocketConnectionPort = 61888;

        public static boolean keyerCanInjectKeys = false;
        public static boolean websocketCanInjectKeys = false;
    }
}
