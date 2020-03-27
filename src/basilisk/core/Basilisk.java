package basilisk.core;

import java.net.URI;
import java.awt.Point;
import java.awt.Desktop;
import java.util.Arrays;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URISyntaxException;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import processing.core.PApplet;
import basilisk.algorithms.shared.*;
import basilisk.algorithms.AlgorithmBase;

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
	// The current AI algorithm running
	private final Algorithms algorithms;
	// The fps rate
	private double fps;

	// Creates a new Basilisk instance
	public Basilisk() {
		// Initialize components
		gameElementDetection = new GameElementDetection();
		screenCapture = new ScreenCapture();
		gui = new Gui(this);
		algorithms = new Algorithms();
		init();

		// Start the AI in a new thread
		new Thread( null, run(), "Basilisk" ).start();

		// Initialize the other half of the basilisk program with a Processing window
        // to display the gui for the user.
        String[] processingArgs = { "Basilisk" };
		PApplet.runSketch(processingArgs, gui);
		
		// Enable debug logs
		Config.BasiliskProgram.showAiDebugs = true;
		Config.BasiliskProgram.showAnimationDebugs = false;

		Config.GuiConfigs.loading = false;
		Config.GuiConfigs.showGameDetection = true;
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
			Config.BasiliskProgram.screenConfig = Config.BasiliskProgram.SCREEN_1920x1080;
		} else if ( Arrays.equals(screenModes[1], currentScreenMode) ) {
			System.out.println("you are running 1440x900p");
			Config.BasiliskProgram.screenConfig = Config.BasiliskProgram.SCREEN_1440x900;

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

		// Attempt to set the apple color based on what game mode you are playing
		gameImg = screenCapture.getFrame();
		gameElementDetection.setAppleColor(gameImg, new Point(12, 7));
	}

	// Main run loop for the AI, runs in its own thread
	private Runnable run() {
		return new Runnable() {

			@Override
			public void run() {
				// Main control loop
				while (true) {

					// Only run when the program is not paused!
					// TODO: Research better ways to stall thread until boolean changes?
					// see: https://stackoverflow.com/questions/44660137/how-check-boolean-value-until-it-is-true?noredirect=1&lq=1
					// see: https://stackoverflow.com/questions/19025366/wait-until-boolean-value-changes-it-state
					
					// while ((Config.paused) || (Config.loading));
					// Potentially change to this for better performance?
					while ((Config.BasiliskProgram.paused) || (Config.GuiConfigs.loading)) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							System.out.println("Something interrupted the AI thread: " + e);
							e.printStackTrace();
						}
					}

					// Start a timer for loop timing
					double startTime = System.nanoTime();

					// Process the images:
					gameImg = screenCapture.getFrame();
					elementsImage = gameElementDetection.shrinkProcess(gameImg);
					gameElementDetection.detect(elementsImage);
					
					// Update the GUI:
					// gui.update(
					// 	gameElementDetection.getApplePos(), 
					// 	gameElementDetection.getSnakeHead(), 
					// 	gameElementDetection.getSnakeParts(),
					// 	gameImg
					// );

					// Two methods, they both do the same thing. Offloads a little work to the animation tread
					gui.update();
					
					// Run the path finding algorithm
					AlgorithmBase algo = algorithms.getRunningAlgorithm();
					algorithms.run(
						algo,
						gameElementDetection.getSnakeHead(),
						gameElementDetection.getSnakeParts(),
						gameElementDetection.getApplePos()
					);

					// Get the next action from the algorithm and check it
					Action act = algorithms.getActionManager().getNextAction();
					boolean ready = algorithms.getActionManager().checkAction(act, gameElementDetection.getSnakeHead());

					// Try to find an action for where the snake is now
					if ((!ready) && (algo.supportsPathSkipping())) {
						Action actForPosition = algorithms.getActionManager().findActionForPosition( gameElementDetection.getSnakeHead() );

						if (actForPosition != null) {
							algorithms.getActionManager().go(actForPosition);
							System.out.println("dispatched action for position, snake was out of line");
						}
					}

					// If the snake is in the right position, GO!
					if (ready) {
						algorithms.getActionManager().go(act);
					}

					// Calculate the loop timings
					fps = 1 / ((System.nanoTime() - startTime) / 1000000000.0);
					double ms = (System.nanoTime() - startTime) / 1000000.0;

					// Format number to two decimal places to look nicer in console
					fps = (double) Math.round(fps * 100) / 100;
					ms = (double) Math.round(ms * 100) / 100;

					// Print Debug logs
					if (Config.BasiliskProgram.showAiDebugs) {
						System.out.println("AI loop took: " + ms + " ms, processing at: " + fps + " frames per second");
					}
				} // End loop
			} // End override run method

		}; // End runnable type
	} // End basilisk run method

	/**
	 * Open the play google snake page
	 */
	public static final void openSnakeInGoogleWindow() {
		try {
            // String[] s = new String[] {"Google Chrome", "https://www.google.com/search?q=play%20snake"};
            // Runtime.getRuntime().exec(s);
            URI uri = new URI("https://www.google.com/search?q=play%20snake");
			Desktop.getDesktop().browse( uri );
		
		// Catch any exceptions
        } catch (IOException ignore) {
            System.out.println("Could not launch chrome, error: " + ignore);
        } catch (URISyntaxException ignore) {
            System.out.println("Could not launch chrome, error: " + ignore);
        } 
	}

	/**
	 * Get the current game element detection object
	 * @return GameElementDetection
	 */
	public final GameElementDetection getGameElementDetection() {
		return this.gameElementDetection;
	}

	/**
	 * Get the current screen capture object
	 * @return ScreenCapture
	 */
	public final ScreenCapture getScreenCapture() {
		return this.screenCapture;
	}

	/**
	 * Get the current images being used by the AI, see what the AI sees
	 * @return gameImages
	 */
	public BufferedImage[] getProcessedGameImages() {
		return new BufferedImage[] { gameImg, elementsImage };
	}

	/**
	 * Get the current Algorithms manager for the AI
	 * @return algorithms
	 */
	public final Algorithms getAI_Algorithms() {
		return this.algorithms;
	}

	/**
	 * Get the actions manager
	 * @return actionManager
	 */
	public final ActionsManager getActionsManager() {
		return this.algorithms.getActionManager();
	}

	/**
	 * Get the gui element
	 * @return gui
	 */
	public final Gui getGui() {
		return this.gui;
	}

	/**
	 * Get the fps rate the AI is processing at
	 * @return fps
	 */
	public double fps() {
		return this.fps;
	}
}
