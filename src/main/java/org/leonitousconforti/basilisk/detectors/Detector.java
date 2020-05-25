package org.leonitousconforti.basilisk.detectors;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * A detector profile to be used by the game element detection.
 */
public class Detector {
    private final Color color;
    private final float hue;
    private final String name;

    /**
     * Creates a new Detector profile to be used by the game element detection.
     *
     * @param detectorName  name of the detector
     * @param detectorColor color this detector will process
     * @param detectorHue   hue this detector will process
     *
     * @see org.leonitousconforti.basilisk.core.GameElementDetection#addSnakeDetector(String,
     *      Color)
     * @see org.leonitousconforti.basilisk.core.GameElementDetection#addThingToEatDetector(String,
     *      Color)
     */
    public Detector(String detectorName, Color detectorColor, float detectorHue) {
        name = detectorName;
        color = detectorColor;
        hue = detectorHue;
    }

    /**
     * Creates a new Detector profile to be used by the game element detection.
     *
     * @param detectorName  name of this detector
     * @param detectorColor color this detector will process
     */
    @SuppressWarnings("checkstyle:magicNumber")
    public Detector(String detectorName, Color detectorColor) {
        name = detectorName;
        color = detectorColor;
        hue = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[]) null)[0] * 255;
    }

    /**
     * @return the name of this detector
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the color this detector will find
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * @return the hue this detector will find
     */
    public float getHue() {
        return this.hue;
    }

    /**
     * Creates a new detector profile by looking at an image of the game.
     *
     * @param detectorName   the name of the detector
     * @param shrunkImage    a screen shot of the game
     * @param sampleLocation the location of where to get the color
     * @return the new detector with the name and color of the sample location
     */
    public static Detector newDetectorFromImage(String detectorName, BufferedImage shrunkImage, Point sampleLocation) {
        // Get the color of the sample location
        Color colorToDetect = new Color(shrunkImage.getRGB(sampleLocation.x, sampleLocation.y));

        // Create the new detector
        Detector d = new Detector(detectorName, colorToDetect);
        return d;
    }
}
