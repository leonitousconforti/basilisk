package basilisk;

import java.awt.Color;
import java.awt.Point;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
// import java.util.Collections;
// import java.awt.image.Raster;

public class GameElementDetection {
	// TODO: move variable initializations to class constructor

	// The color used to identify snake parts
	private Color rgbSnakeColor = new Color(65, 100, 240);

	// private float snakeColorHue1 = Color.RGBtoHSB((rgbSnakeColor >> 16) & 0xff, (rgbSnakeColor >> 8) & 0xff, rgbSnakeColor & 0xff, null);
	private float snakeColorHue = Color.RGBtoHSB(rgbSnakeColor.getRed(), rgbSnakeColor.getGreen(), rgbSnakeColor.getBlue(), null)[0] * 255;
	
	// The color used to identify the apples
	private Color rgbAppleColor = new Color(223, 48, 24);
	
	// The previous apple position is used to tell weather or not a new a-star path needs to be calculated.
	// Only when the apple has changed positions (ie. eaten) or the snake is not on track then a new a-star path needs to be calculated.
	private Point previousApplePos = new Point(-1, -1);
	
	// The snake parts from the most recently processed frame are save to differentiate where the head is.
	// the basic detection relies on colors and since it only detect colors, it is impossible to identify what part
	// is part of the snake body or if it is the snake head. Thus, if the difference between the two frames (current vs previous)
	// shows some result where there is a new snake part, then we know it is the snake head and not a snake part; everything else would be a snake part.
	private ArrayList<Point> snakeParts = new ArrayList<Point>();
	private ArrayList<Point> lastSnakeParts = new ArrayList<Point>();

	// Save the final determined positions for each object
	private Point applePos = new Point(0, 0);
	private Point snakeHead = new Point(0, 0);

	// Rectangle where to capture the game data
	private final Rectangle gameDataRasterRectangle = new Rectangle(28, 95, 544, 480);

	// Rectangle where to capture the apple color
	private final Point appleColorRasterRectangle = new Point(39, 39);
	
	// A raster buffer to write data to when performing the shrink process
	private BufferedImage gameRasterImage = new BufferedImage(17, 15, BufferedImage.TYPE_INT_RGB);

	public GameElementDetection() {
	}

	/**
	 * Sets the apple color using the object in the upper left hand corner
	 * @param img the screen show of the game window
	 */
	public void setAppleColor(BufferedImage img) {
		// Convert from quadrant space to pixel space for the input image
		int x = appleColorRasterRectangle.x;
		int y = appleColorRasterRectangle.y;

		// Get the color and then set the color
		Color appleColorInWindow = new Color( img.getRGB(x, y) );
		rgbAppleColor = appleColorInWindow;
		System.out.println(rgbAppleColor);
	}

	/**
	 * Shrinks a screen shot of the game into something more useable that the
	 * detect method can use. This method looks at the center pixel of every square
	 * of the game grid and compiles a single image from that, making the final image
	 * just 17x15 pixels which makes the process of finding the snake and apple a lot faster.
	 * @param img the screen shot of the game window
	 * @return processedImage
	 */
	public BufferedImage shrinkProcess(BufferedImage img) {
		// A raster buffer to write data to when performing the shrink process
		// Raster rasterBuffer = img.getData(gameDataRasterRectangle);

		int xToQuadrant, yToQuadrant;
		// Supposedly looping over image columns and then rows is faster; have not tested though
		// @see https://stackoverflow.com/questions/7749895/java-loop-through-pixels-in-an-image
		for (int y = 0; y < gameRasterImage.getHeight(); y++) {
			for (int x = 0; x < gameRasterImage.getWidth(); x++) {
				// Convert from quadrant space to pixel space for the input image
				xToQuadrant = x * 32 + 16 + gameDataRasterRectangle.x;
				yToQuadrant = y * 32 + 16 + gameDataRasterRectangle.y;
				
				// Detect the color in the raster buffer
				// int[] pixelData = rasterBuffer.getPixel( xToQuadrant, yToQuadrant, (int[]) null );
				// Color colorInQuadrant = new Color( pixelData[0], pixelData[1], pixelData[2] );
				Color colorInQuadrant = new Color( img.getRGB(xToQuadrant, yToQuadrant) );

				// Set the color in the BufferedImage
				gameRasterImage.setRGB(x, y, colorInQuadrant.getRGB());
			}
		}

		return gameRasterImage;
	}

	/**
	 * Parses all the important parts of the game taking in a simplified screen shot
	 * @param img the image to process 17x15 pixels
	 */
	@SuppressWarnings ("unchecked") // Specifically for the cast from "snakeParts.clone()" back to ArrayList<Point>
	public void detect(BufferedImage img) {
		// clear the last snake parts
		lastSnakeParts.clear();
		// TODO: fix unchecked cast from Object to ArrayList
		// clone the current snake parts to the previous snake part before we clear it
		lastSnakeParts = (ArrayList<Point>) snakeParts.clone();
		// Collections.copy(lastSnakeParts, snakeParts);
		snakeParts.clear();
		// set the previous apple position.
		previousApplePos = applePos.getLocation();

		// Supposedly looping over image columns and then rows is faster; have not tested though
		// @see https://stackoverflow.com/questions/7749895/java-loop-through-pixels-in-an-image
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				// Get the color at the current pixel and map it to a HSC value.
				Color colorAtPixel = new Color( img.getRGB(x, y) );
				// Array to store HSB values
				float[] hsbValues = new float[3];
				hsbValues = Color.RGBtoHSB(colorAtPixel.getRed(), colorAtPixel.getGreen(), colorAtPixel.getBlue(), hsbValues);
				// Get the hue at the current pixel
				float hueAtPixel = hsbValues[0] * 255;

				// Compare what the expected apple color to be to the color of the current pixel
				if (colorAtPixel.getRGB() == rgbAppleColor.getRGB()) {
					// If true then we found the apple
					applePos.x = x;
					applePos.y = y;
				} else if (Math.abs(hueAtPixel - snakeColorHue) < 2) {
					// If true then we have a snake part.
					// Note: it is impossible to tell right now weather or not this is the snake head
					// we must compare to the previous snake parts to be certain
					snakeParts.add(new Point(x, y));
				}
			}
		}
		
		// If the previous snake parts does not contain one of the snake parts that we just found,
		// then we have found the snake head. If all the snake parts found this iteration match the
		// previous snake parts, then the loop was processing faster than the snake game runs and
		// the snake just hasn't moved enough since the last iteration.
		for (Point snakePart : snakeParts) {
			if (!lastSnakeParts.contains( snakePart )) {
				snakeHead = snakePart.getLocation();
				// we found the snake head so we can stop checking now!
				break;
			}
		}
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
	 * Gets the current position of the snake head
	 * @return {Point} snakeHead
	 */
	public Point getSnakeHead() {
		return snakeHead;
	}

	/**
	 * Gets the current position of the apple
	 * @return {Point} applePos
	 */
	public Point getApplePos() {
		return applePos;
	}

	/**
	 * Gets the previous apple position
	 * @return {Point} previousApplePos
	 */
	public Point getPreviousApplePos() {
		return previousApplePos;
	}

	/**
	 * Gets all the parts of the snake
	 * @return {ArrayList<Point>} snakeParts
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Point> getSnakeParts() {
		// return snakeParts;
		return (ArrayList<Point>) snakeParts.clone();
	}
}
