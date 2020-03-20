package basilisk;

import basilisk.core.*;
import basilisk.algorithms.AlgorithmBase;

// The main entry method for java programs
@SuppressWarnings("unused")
public class Main {
    static public void main(String[] args) {
        // Create a new basilisk program
        Basilisk basilisk = new Basilisk();

        ScreenCapture cap = basilisk.getScreenCapture();
        GameElementDetection ged = basilisk.getGameElementDetection();
        ActionsManager act = basilisk.getActionsManager();
        Algorithms algo = basilisk.getAI_Algorithms();

        AlgorithmBase algoRunning = basilisk.getAI_Algorithms().getRunningAlgorithm();
    }
}
