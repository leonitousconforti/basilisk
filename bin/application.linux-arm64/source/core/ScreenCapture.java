package basilisk.core;

import java.awt.Robot;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
// import java.awt.image.VolatileImage;

public class ScreenCapture {
	// Java robot handles interactions with user's screen
	private Robot screenCapture;
	// Rectangle where to record the screen
	private Rectangle screenRect;
	// BufferedImage for the screenshot of the user's screen.
	private BufferedImage captureFrame;

	/**
	 * Creates a new Screen Capture instance recording the entire screen
	 */
	public ScreenCapture() {
		// Attempt to initialize the java Robot
		try {
			screenCapture = new Robot();
			screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		} catch (java.awt.AWTException e) {
			System.out.println("critical error when initializing screen capture, exception - " + e);
			System.exit(-1);
			// Runtime.getRuntime().exit(-1);
		}
	}

	/**
	 * Gets a new frame from the user's screen in the recording area
	 * @return {BufferedImage} captureFrame
	 * 
	 * TODO: possibly change from BufferedImage to VolatileImage for better performance?
	 * @see https://stackoverflow.com/questions/11514929/current-state-of-bufferedimage-vs-volatileimage
	 */
	public BufferedImage getFrame() {
		captureFrame = screenCapture.createScreenCapture(screenRect);
		// return resizeImg(captureFrame, 300, 300);
		return captureFrame;
	}

	/**
	 * Resizes an image to the desired width and height
	 * @param ImgToResize the image to be resized
	 * @param newWidth the width you want the image to be
	 * @param newHeight the height you want the image to be
	 * @return {BufferedImage} resizedImage
	 * 
	 * @see https://stackoverflow.com/questions/9417356/bufferedimage-resize
	 * TODO: find a better way to reuse the Graphics2D g2d object
	 */
	public static BufferedImage resizeImg(BufferedImage imgToResize, int newWidth, int newHeight) {
		Image tmp = imgToResize.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = resizedImage.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
	
		return resizedImage;
	}

	/**
	 * Changes the position where the Robot is recording the screen
	 * @param x1 the x coordinate of the upper left corner
	 * @param y1 the y coordinate of the upper left corner
	 * @param newLength the width of the area to record
	 * @param newHeight the height of the area to record
	 */
	public void setPositionFromCords(int x1, int y1, int newLength, int newHeight) {
		screenRect = new Rectangle(x1, y1, newLength, newHeight);
		System.out.println("changing recording location to: x1 - > " + x1 + ", y1 -> " + y1 + ", x2 -> " + (x1 + newLength) + ", y2 -> " + (y1 + newHeight));
	}
}
