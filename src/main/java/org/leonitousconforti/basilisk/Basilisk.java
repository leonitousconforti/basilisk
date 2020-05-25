package org.leonitousconforti.basilisk;

import java.awt.Desktop;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.leonitousconforti.basilisk.algorithms.shared.Action;
import org.leonitousconforti.basilisk.core.*;
import org.leonitousconforti.basilisk.detectors.Detector;

import de.milchreis.uibooster.UiBooster;
import processing.core.PApplet;

/**
 * Basilisk is an artificial intelligence program designed to play the google
 * snake game. Its core is written algorithms are written in Java, however, it
 * also uses Processing to create a GUI
 */
public final class Basilisk {
    // AI elements
    private final Gui gui;
    private final ScreenCapture screenCapture;
    private final Algorithms algorithmsManager;
    private final UiSettingsForms uiSettingsForms;
    private final GameElementDetection gameElementDetection;

    // Local variables
    private BufferedImage gameImg;
    private BufferedImage elementsImg;
    private double fps;
    private double ms;

    // Constructor
    private Basilisk() {
        // Initialize components
        screenCapture = new ScreenCapture();
        algorithmsManager = new Algorithms();
        gameElementDetection = new GameElementDetection();
        uiSettingsForms = new UiSettingsForms(this);
        new UiBooster().showSplashscreen(loadResource("splash", ".png").getAbsolutePath(), Config.SplashScreenTime);

        // Sleep
        try {
            TimeUnit.MILLISECONDS.sleep(Config.SplashScreenTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Run processing gui
        gui = new Gui(this);
        String[] processingArgs = { "Basilisk" };
        PApplet.runSketch(processingArgs, gui);

        // Set recording area
        screenCapture.setPositionFromCords(1,
                Toolkit.getDefaultToolkit().getScreenSize().height - Config.GameBoardSizePixels - 1,
                Config.GameBoardSizePixels, Config.GameBoardSizePixels);

        // Start
        while (true) {
            if (!Config.paused) {
                run();
            }
            if (Config.showSettingsMenu) {
                uiSettingsForms.getSettingsForm().run();
                Config.showSettingsMenu = false;
            }
        }
    }

    /**
     * Entry point of program.
     *
     * @param args The arguments of the program
     */
    public static void main(String[] args) {
        new Basilisk();
    }

    // Main run loop for the AI
    @SuppressWarnings("checkstyle:MagicNumber")
    private void run() {
        // Start timer for loop debugging
        double startTime = System.nanoTime();

        // Process the images
        gameImg = screenCapture.getFrame();
        elementsImg = gameElementDetection.shrinkProcess(gameImg);
        gameElementDetection.detect(elementsImg);

        // Give information to the current algorithm in control
        algorithmsManager.run(gameElementDetection.getSnakeHead(), gameElementDetection.getSnakeParts(),
                gameElementDetection.getApplePos());

        // Get the next desired action from the algorithm and check if it is ready to be
        // executed
        Action act = algorithmsManager.getActionsManager().getNextAction();
        boolean ready = algorithmsManager.getActionsManager().checkAction(act, gameElementDetection.getSnakeHead());

        // If the snake is in the position desired, then execute the action
        if (ready) {
            algorithmsManager.getActionsManager().go(act);
        }

        // Calculate the loop timings
        double endTime = System.nanoTime();
        fps = 1 / ((endTime - startTime) / 1000000000.0);
        ms = (endTime - startTime) / 1000000.0;

        // Format statistics to two decimal places to look prettier
        fps = (double) Math.round(fps * 100) / 100;
        ms = (double) Math.round(ms * 100) / 100;

        // Print debug logs
        System.out.println("main control loop took: " + ms + " ms, processing at: " + fps + " frames per second");
    }

    /**
     * Open the play google snake page in a browser window that can be position the
     * same on every browser and platform.
     */
    public void openGoogleSnakeWindow() {
        // Load the resource
        URI uri = loadResource("OpenSnake", ".html").toURI();

        try {
            // Open the play snake page
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates new {@link org.leonitousconforti.basilisk.detectors.Detector
     * detectors} with the correct settings for what ever config you are playing
     * with. Detectors then have to be set with
     * {@link org.leonitousconforti.basilisk.core.GameElementDetection#setSelectedSnakeDetector(String)
     * setSelectedSnakeDetector} and
     * {@link org.leonitousconforti.basilisk.core.GameElementDetection#setSelectedThingToEatDetector(String)
     * setSelectedThingToEatDetector}
     *
     * @param snakeDetectorName the name for the snake detector
     * @param appleDetectorName the name for the apple detector
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public void createDetectorForCurrentGameConfig(String snakeDetectorName, String appleDetectorName) {
        // Get a frame and shrink it down
        BufferedImage frame = screenCapture.getFrame();
        BufferedImage shrunkFrame = gameElementDetection.shrinkProcess(frame);

        // Create the detectors
        Detector d1 = Detector.newDetectorFromImage(snakeDetectorName, shrunkFrame, new Point(2, 7));
        Detector d2 = Detector.newDetectorFromImage(appleDetectorName, shrunkFrame, new Point(12, 7));

        // Add the detectors
        gameElementDetection.addSnakeDetector(d1);
        gameElementDetection.addThingToEatDetector(d2);
    }

    /**
     * Resources located inside the jar, which is compressed, and can not be read
     * directly. Best option I found was to copy to copy the file to a temp file and
     * load that, the temp file deletes itself when the program closes
     *
     * @param prefix the name of the file to load
     * @param suffix the file type of the file
     * @return URI to the resource
     */
    public File loadResource(String prefix, String suffix) {
        // Get the class loader for loading the html file from the jar resources
        ClassLoader classLoader = getClass().getClassLoader();

        // Buffer stream for reading the contents of the resource file
        InputStream in = classLoader.getResourceAsStream(prefix.concat(suffix));

        try {
            // Create a temp html file and delete it on program exit
            File temp = File.createTempFile(prefix, suffix, null);
            temp.deleteOnExit();

            // Copy the resource file to the temp file
            Files.copy(in, temp.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            return temp.getAbsoluteFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Something went wrong when loading the resource
        return null;
    }

    /**
     * @return basilisk's final processed image
     */
    public BufferedImage getProcessedGameImage() {
        return this.gameImg;
    }

    /**
     * @return basilisk's game element detection engine
     */
    public GameElementDetection getGameElementDetection() {
        return this.gameElementDetection;
    }

    /**
     * @return basilisk's algorithm manager engine
     */
    public Algorithms getAlgorithmsManager() {
        return algorithmsManager;
    }
}
