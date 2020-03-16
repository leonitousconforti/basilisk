package basilisk;

import java.util.Arrays;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import processing.core.PApplet;
// import basilisk.*;

/**
 * Basilisk is an artificial intelligence program designed to play the google snake game perfectly.
 * Its core is written algorithms are written in Java, however, it also uses Processing to create
 * an optional graphical UI to display stats to the user.
 */
public class Basilisk {
	// GUI
	private final Gui gui;
	// Screen capture for recording the screen
	private final ScreenCapture screenCapture;
	// Game element detection for finding the snake and apple
	private final GameElementDetection gameElementDetection;
	// The current frames being worked on
	private BufferedImage gameImg, elementsImage;

	// Creates a new Basilisk instance
	public Basilisk() {
		// Initialize variables
		gui = new Gui();
		gameElementDetection = new GameElementDetection();
		screenCapture = new ScreenCapture();
		init();

		// Start the AI in a new thread
		new Thread(run()).start();

		// Initialize the other half of the basilisk program with a Processing window
        // to display the graphical UI for the user.
        String[] processingArgs = { "Basilisk" };
        PApplet.runSketch(processingArgs, gui);
	}

	// Initialize method
	private void init() {
		// Use to get your screen configuration, Basilisk only runs on 1920x1080 or 1440x900
		final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		final int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		final int[][] screenModes = { {1920, 1080}, {1440, 900} };

		// Checks your current screen size against the screen sizes that it can run on
		int[] currentScreenMode = {screenWidth, screenHeight};
		if ( Arrays.equals(screenModes[0], currentScreenMode) ) {
			System.out.println("you are running 1920x1080p");
			Config.screenConfig = Config.SCREEN_1920x1080;
		} else if ( Arrays.equals(screenModes[1], currentScreenMode) ) {
			System.out.println("you are running 1440x900p");
			Config.screenConfig = Config.SCREEN_1440x900;

			// Default config when your screen is 1440x900. Does not take into account things like bookmark bar and such
			// screenCapture.setPositionFromCords( loadingScreen.setupGame() );
			screenCapture.setPositionFromCords(420, 205, 600, 600);
		} else {
			// Not running on a supported screen size
			throw new RuntimeException("your screen configuration is not supported");
		}

		// Some delay between the program starting and the AI booting up
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch(InterruptedException e) {}

		// Set the apple color based on what game mode you are playing
		gameImg = screenCapture.getFrame();
		gameElementDetection.setAppleColor(gameImg);
	}

	// Main run loop for the AI, runs in its own thread
	private Runnable run() {
		return new Runnable() {

			@Override
			public void run() {
				// Main control loop
				while (true) {
					// Start a timer for loop timing
					double startTime = System.nanoTime();

					// Process the images:
					gameImg = screenCapture.getFrame();
					elementsImage = gameElementDetection.shrinkProcess(gameImg);
					gameElementDetection.detect(elementsImage);
					gui.update(
						gameElementDetection.getApplePos(), 
						gameElementDetection.getSnakeHead(), 
						gameElementDetection.getSnakeParts(),
						gameImg
					);

					// Calculate the loop timings
					double fps = 1 / ((System.nanoTime() - startTime) / 1000000000.0);
					System.out.println("loop took: " + (System.nanoTime() - startTime) / 1000000.0 + " ms, processing at: " + fps + " frames per second");
				}
			}

		};
	}
}
