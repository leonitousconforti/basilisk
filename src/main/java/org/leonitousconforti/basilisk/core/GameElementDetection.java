package org.leonitousconforti.basilisk.core;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.IntStream;

import org.leonitousconforti.basilisk.Config;
import org.leonitousconforti.basilisk.detectors.Detector;

/**
 * Handles everything for looking at the game and parsing the game elements out
 * of it.
 */
public class GameElementDetection {
    // The colors used to identify snake parts
    private final ArrayList<Detector> snakeDetectors;
    private Detector selectedSnakeDetector;

    // The colors of the things the snake can eat
    private final ArrayList<Detector> thingsToEatDetectors;
    private Detector selectedThingToEatDetector;

    // Difference between the two frames (current vs previous) shows some result
    // where there is a new snake part, then we know it is the snake head and not a
    // snake part
    private ArrayList<Point> snakeParts;
    private ArrayList<Point> lastSnakeParts;

    // Save the final determined positions for each object
    private Point applePos;
    private Point snakeHead;

    // A raster buffer to write data to when performing the shrink process
    private BufferedImage gameShrinkImage;

    /**
     * Look at a screen shot of the game in any state and detect the positions of
     * the snake and apple reliably.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public GameElementDetection() {
        // Initialize arrayLists
        snakeParts = new ArrayList<Point>();
        lastSnakeParts = new ArrayList<Point>();

        // Initialize points
        applePos = new Point(-1, -1);
        snakeHead = new Point(-1, -1);

        // Initialize detectors
        snakeDetectors = new ArrayList<Detector>();
        thingsToEatDetectors = new ArrayList<Detector>();
        selectedSnakeDetector = null;
        selectedThingToEatDetector = null;

        // Setup image capture data
        gameShrinkImage = new BufferedImage(Config.NumberOfColsOnGameBoard, Config.NumberOfRowsOnGameBoard,
                BufferedImage.TYPE_INT_RGB);

        // Check the snake detectors and make sure there is a selected key
        if (selectedSnakeDetector == null) {
            addSnakeDetector("Random Detector Because None Others Existed", new Color(0, 0, 0));
            setSelectedSnakeDetector("Random Detector Because None Others Existed");
        }
        // Check the thingsToEat detectors and make sure there is a selected key
        if (selectedThingToEatDetector == null) {
            addThingToEatDetector("Random Detector Because None Others Existed", new Color(255, 255, 255));
            setSelectedThingToEatDetector("Random Detector Because None Others Existed");
        }
    }

    /**
     * Shrinks a 600x600 pixel screen shot of the game into something more useable
     * that the {@link #detect(BufferedImage) detect} method can use to find the
     * game elements. This method looks at the center pixel of every square of the
     * game grid and compiles a new image from that, making the final image just
     * 17x15 pixels which makes the process of finding the snake and apple a lot
     * faster and less computational work has to be done
     *
     * @param img the screen shot of the game window
     * @return shrunkImage
     */
    public BufferedImage shrinkProcess(BufferedImage img) {
        int xToQuadrant;
        int yToQuadrant;

        // Loop over every pixel square/quadrant on the game board. Use the
        // y-coordinates as the outer loop because that is how the image data is stored
        // in memory
        for (int y = 0; y < gameShrinkImage.getHeight(); y++) {
            for (int x = 0; x < gameShrinkImage.getWidth(); x++) {
                // Converting from grid/quadrant space to pixel space for the input image.
                // To get the middle pixel of any particular square on the game board use
                // x column or y row * 32 + (32 / 2) + borderOffsets = pixel location in screen
                // shot
                xToQuadrant = x * Config.GameBoardQuadrantSizePixels + (Config.GameBoardQuadrantSizePixels / 2)
                        + Config.GameBoardBoarderWidthPixels;
                yToQuadrant = y * Config.GameBoardQuadrantSizePixels + (Config.GameBoardQuadrantSizePixels / 2)
                        + Config.GameBoardHeaderHeightPixels;

                // Detect the color in the raster buffer
                Color colorInQuadrant = new Color(img.getRGB(xToQuadrant, yToQuadrant));

                // Set the color in the BufferedImage
                gameShrinkImage.setRGB(x, y, colorInQuadrant.getRGB());
            }
        }

        return gameShrinkImage;
    }

    /**
     * Parses all the important elements of the game. The detection algorithms can
     * be modified and set in real time using the
     * {@link #setSelectedSnakeDetector(String) setSelectedSnakeDetector} and the
     * {@link #setSelectedThingToEatDetector(String) setSelectedThingToEatDetector}
     * methods.
     *
     * @param img the image to process, from {@link #shrinkProcess(BufferedImage)
     *            shrinkProcess}
     *
     * @see org.leonitousconforti.basilisk.detectors.Detector
     */
    @SuppressWarnings("unchecked")
    public void detect(BufferedImage img) {
        // Clear the last snake parts list
        lastSnakeParts.clear();

        // Create a shallow copy of the snakeParts list and store it in lastSnakeParts.
        // Later, this loop iteration will write data to snakeParts again. What we end
        // up with is a backup of the previous loop iteration data in lastSnakeParts and
        // this loop iteration in snakeParts. At the end of this method, compare the two
        // lists and if there is new data in snakeParts that does not match
        // lastSnakeParts then we can conclude that that is where the snakes head is
        lastSnakeParts = (ArrayList<Point>) snakeParts.clone();
        snakeParts.clear();

        // Loop over every pixel of the input image. Use the y-coordinate as the outer
        // loop because that is how the image data is stored im memory
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                // Get the color at the current pixel and map it to a hue value.
                Color colorAtPixel = new Color(img.getRGB(x, y));
                float hueAtPixel = getHueOfColor(colorAtPixel);

                // Compare what the expected apple color to be to the color of the current pixel
                if (colorAtPixel.getRGB() == selectedThingToEatDetector.getColor().getRGB()) {
                    // If true then we found the apple
                    applePos.x = x;
                    applePos.y = y;
                } else if (Math.abs(hueAtPixel - selectedSnakeDetector.getHue()) < 2) {
                    // If true then we have a snake part.
                    snakeParts.add(new Point(x, y));
                }
            }
        }

        // If the previous snake parts does not contain one of the snake parts that we
        // just found, then we have found the snake head. If all the snake parts found
        // this iteration match the previous snake parts, then the loop was processing
        // faster than the snake game runs and the snake just hasn't moved enough since
        // the last iteration
        for (Point snakePart : snakeParts) {
            if (!lastSnakeParts.contains(snakePart)) {
                snakeHead = snakePart.getLocation();
                // we found the snake head so we can stop checking now!
                break;
            }
        }
    }

    /**
     * Converts a color into a hue value.
     *
     * @param color the color you want the hue of
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private float getHueOfColor(Color color) {
        // float[] hsbValues = new float[3];
        // hsbValues = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(),
        // hsbValues);
        // float hueAtPixel = hsbValues[0] * 255;
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[]) null)[0] * 255;
    }

    /**
     * @return the position of the snake head
     */
    public Point getSnakeHead() {
        return snakeHead;
    }

    /**
     * @return the position of the item the snake is trying to eat
     */
    public Point getApplePos() {
        return applePos;
    }

    /**
     * @return the positions of all the parts of the snake, not including the snake
     *         head
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Point> getSnakeParts() {
        // return snakeParts;
        return (ArrayList<Point>) snakeParts.clone();
    }

    /**
     * @return all of the loaded snake detector profiles
     */
    public ArrayList<Detector> getLoadedSnakeDetectors() {
        return snakeDetectors;
    }

    /**
     * @return all of the loaded things to eat detector profiles
     */
    public ArrayList<Detector> getLoadedThingsToEatDetectors() {
        return thingsToEatDetectors;
    }

    /**
     * Loads a new snake detector to be used by the {@link #detect(BufferedImage)
     * detect} method.
     *
     * @param name          the name of the detector
     * @param colorToDetect the color used to identify the snake
     */
    public void addSnakeDetector(String name, Color colorToDetect) {
        // Check Inputs
        if ((name == null) || (colorToDetect == null)) {
            return;
        }

        // Otherwise, load the new detector
        float hueToDetect = getHueOfColor(colorToDetect);
        Detector d = new Detector(name, colorToDetect, hueToDetect);
        snakeDetectors.add(d);
    }

    /**
     * Loads a new snake detector to be used by the {@link #detect(BufferedImage)
     * detect} method.
     *
     * @param detectorToAdd the detector to add
     */
    public void addSnakeDetector(Detector detectorToAdd) {
        snakeDetectors.add(detectorToAdd);
    }

    /**
     * Loads a new thing to eat detector to be used by the
     * {@link #detect(BufferedImage) detect} method.
     *
     * @param name          the name of the detector
     * @param colorToDetect the color used to identify the thing to eat
     */
    public void addThingToEatDetector(String name, Color colorToDetect) {
        // Check Inputs
        if ((name == null) || (colorToDetect == null)) {
            return;
        }

        // Otherwise, load the new detector
        float hueToDetect = getHueOfColor(colorToDetect);
        Detector d = new Detector(name, colorToDetect, hueToDetect);
        thingsToEatDetectors.add(d);
    }

    /**
     * Loads a new thing to eat detector to be used by the
     * {@link #detect(BufferedImage) detect} method.
     *
     * @param detectorToAdd the detector to add
     */
    public void addThingToEatDetector(Detector detectorToAdd) {
        thingsToEatDetectors.add(detectorToAdd);
    }

    /**
     * Sets the selected snake
     * {@link org.leonitousconforti.basilisk.detectors.Detector detector} to be used
     * by the image processing. If no detector is found with the desired name, then
     * no changes are made
     *
     * @param nameOfDetector the name of the detector to be used
     */
    public void setSelectedSnakeDetector(String nameOfDetector) {
        // Check if the desired detector is one that is loaded
        boolean containsDetector = snakeDetectors.stream().map(Detector::getName).filter(nameOfDetector::equals)
                .findFirst().isPresent();

        // If not loaded, return
        if (!containsDetector) {
            return;
        }

        // Find the index and set the selector
        int index = IntStream.range(0, snakeDetectors.size())
                .filter(i -> nameOfDetector.equals(snakeDetectors.get(i).getName())).findFirst().orElse(-1);
        selectedSnakeDetector = snakeDetectors.get(index);
    }

    /**
     * Sets the selected thing to eat
     * {@link org.leonitousconforti.basilisk.detectors.Detector detector} to be used
     * by the image processing. If no detector is found with the desired name, then
     * no changes are made
     *
     * @param nameOfDetector the name of the detector to be used
     */
    public void setSelectedThingToEatDetector(String nameOfDetector) {
        // Check if the desired detector is one that is loaded
        boolean containsDetector = thingsToEatDetectors.stream().map(Detector::getName).filter(nameOfDetector::equals)
                .findFirst().isPresent();

        // If not loaded, return
        if (!containsDetector) {
            return;
        }

        // Find the index and set the selector
        int index = IntStream.range(0, thingsToEatDetectors.size())
                .filter(i -> nameOfDetector.equals(thingsToEatDetectors.get(i).getName())).findFirst().orElse(-1);
        selectedThingToEatDetector = thingsToEatDetectors.get(index);
    }
}
