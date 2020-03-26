package basilisk;

import basilisk.core.*;
import basilisk.algorithms.AlgorithmBase;

// The main entry method for java programs
@SuppressWarnings("unused")
public class Main {
    static public void main(String[] args) {
        // Open the snake page
        // Basilisk.openSnakeInGoogleWindow();

        // Create a new basilisk program
        Basilisk basilisk = new Basilisk();

        // All the individual parts of Basilisk
        Gui gui = basilisk.getGui();
        ScreenCapture cap = basilisk.getScreenCapture();
        GameElementDetection ged = basilisk.getGameElementDetection();
        ActionsManager act = basilisk.getActionsManager();
        Algorithms algo = basilisk.getAI_Algorithms();

        // The AI algorithm running the show right now
        basilisk.getAI_Algorithms().setAlgorithm("A-star");
        // basilisk.getAI_Algorithms().setAlgorithm("Random-Movement");
        AlgorithmBase algoRunning = basilisk.getAI_Algorithms().getRunningAlgorithm();
    }
}
