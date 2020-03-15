package basilisk;

import java.awt.Point;
import java.util.Arrays;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

// import basilisk.*;
import processing.core.*;

/**
 * Basilisk is an artificial intelligence program designed to play the google snake game perfectly.
 * Its core is written algorithms are written in Java, however, it also uses Processing to create
 * an optional graphical UI to display stats to the user.
 */
public class Basilisk extends PApplet {
	// Screen capture for recording the screen
	private final ScreenCapture screenCapture;
	// Game element detection for finding the snake and apple
	private final GameElementDetection gameElementDetection;
	// The current frames being worked on
	private BufferedImage gameImg, elementsImage;

	// Creates a new Basilisk instance
	public Basilisk() {
		// Initialize variables
		gameElementDetection = new GameElementDetection();
		screenCapture = new ScreenCapture();

		init();
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

					// Calculate the loop timings
					double fps = 1 / ((System.nanoTime() - startTime) / 1000000000.0);
					System.out.println("loop took: " + (System.nanoTime() - startTime) / 1000000.0 + " ms, processing at: " + fps + " frames per second");
				}
			}

		};
	}

	// Processing settings method
	@Override
    public void settings() {
		// Create the window based on the screen size
		if (Config.screenConfig == Config.SCREEN_1920x1080) {
			size(600, 600);
		} else if (Config.screenConfig == Config.SCREEN_1440x900) {
			size(300, 300);
		}
	}

	// Processing setup method
    @Override
    public void setup() {
		// Setup the processing surface
        background(51);
		frameRate(30);
		surface.setAlwaysOnTop(true);
		surface.setLocation(100, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - height / 2);

		// Start the AI in a new thread
		new Thread(run()).start();
    }

	// Processing loop method
	@Override
    public void draw() {
		// Draw the current screen shot
		image( new PImage( ScreenCapture.resizeImg(gameImg, 300, 300) ), 0, 0 );

		// Scale the elements in the PApplet according to the screen size
		if (Config.screenConfig == Config.SCREEN_1920x1080) {
			scale( (float) 1 );
		} else if (Config.screenConfig == Config.SCREEN_1440x900) {
			scale( (float) 1/2 );
		}

		// Draw where we think the apple is
		strokeWeight(6);
		fill(255);
		stroke(200, 100, 200);
		ellipse(gameElementDetection.getApplePos().x * 32 + 28 + 16, gameElementDetection.getApplePos().y * 32 + 95 + 16, 32, 32);

		// Draw all the snake parts we found
		strokeWeight(6);
		fill(255);
		stroke(100, 100, 200);
		// TODO: fix concurrent modification exception
		// see https://stackoverflow.com/questions/8104692/how-to-avoid-java-util-concurrentmodificationexception-when-iterating-through-an
		// Fixed 03/14/20
		
		// The iterators returned by this class's iterator and listIterator methods are fail-fast: if the list is 
		// structurally modified at any time after the iterator is created, in any way except through the iterator 
		// own remove or add methods, the iterator will throw a ConcurrentModificationException.
		for (Point p : gameElementDetection.getSnakeParts()) {
			rect(p.x * 32 + 28, p.y * 32 + 95, 32, 32);
		}

		// Draw where we think the snake head is
		fill(0);
		noStroke();
		rect(28 + gameElementDetection.getSnakeHead().x * 32, 95 + gameElementDetection.getSnakeHead().y * 32, 32, 32);
	}
}
