package org.leonitousconforti.basilisk.core;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * Handles everything about recording the screen and resizing images.
 */
public class ScreenCapture {
    // Java robot handles interactions with user's screen
    private Robot screenCapture;

    // Rectangle where to record the screen
    private Rectangle screenRect;

    // BufferedImage for the screenshot of the user's screen.
    private BufferedImage captureFrame;

    /**
     * Captures the screen.
     */
    public ScreenCapture() {
        // Attempt to initialize the java Robot
        try {
            screenCapture = new Robot();
            screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        } catch (java.awt.AWTException e) {
            System.out.println("critical error when initializing screen capture");
            e.printStackTrace();
        }
    }

    /**
     * Gets a new frame from the user's screen in the recording area. Use the
     * {@link #setPositionFromCords(int, int, int, int) setPositionFromCords} method
     * to set where to record the screen at
     *
     * @return captureFrame
     *
     * @see https://stackoverflow.com/questions/11514929/current-state-of-bufferedimage-vs-volatileimage
     */
    public BufferedImage getFrame() {
        captureFrame = screenCapture.createScreenCapture(screenRect);
        return captureFrame;
    }

    /**
     * Resizes an image to the desired width and height.
     *
     * @param imgToResize the image to be resized
     * @param newWidth    desired width of final image
     * @param newHeight   desired height of final image
     *
     * @see https://stackoverflow.com/questions/9417356/bufferedimage-resize
     */
    public static BufferedImage resizeImg(BufferedImage imgToResize, int newWidth, int newHeight) {
        Image tmp = imgToResize.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        // Imprint the image to a new size
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    /**
     * Changes the position where the program is recording the screen.
     *
     * @param x1        the x coordinate of the upper left corner
     * @param y1        the y coordinate of the upper left corner
     * @param newLength the width of the area to record
     * @param newHeight the height of the area to record
     */
    public void setPositionFromCords(int x1, int y1, int newLength, int newHeight) {
        screenRect = new Rectangle(x1, y1, newLength, newHeight);
        System.out.println("changing screen recording location to: x1 - > " + x1 + ", y1 -> " + y1 + ", x2 -> "
                + (x1 + newLength) + ", y2 -> " + (y1 + newHeight));
    }
}
