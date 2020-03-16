package basilisk;

import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import processing.core.*;

public class Gui extends PApplet {
    private ArrayList<Point> snakeParts;
    private Point snakeHead;
    private Point applePos;
    private BufferedImage gameImg;

    public Gui() {
        snakeParts = new ArrayList<Point>();
        snakeHead = new Point(-1, -1);
        applePos = new Point(-1, 1);
    }

    /**
     * Updates the elements on the screen to their new positions
     * @param _applePos the position of the apple
     * @param _snakeHead the position of the snake head
     * @param _snakeParts an arraylist of all the snake parts
     * @param _gameImg the frame of the game image
     */
    public void update(Point _applePos, Point _snakeHead, ArrayList<Point> _snakeParts, BufferedImage _gameImg) {
        applePos = _applePos;
        snakeHead = _snakeHead;
        snakeParts = _snakeParts;
        gameImg = _gameImg;
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
		ellipse(applePos.x * 32 + 28 + 16, applePos.y * 32 + 95 + 16, 32, 32);

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
		for (Point p : snakeParts) {
			rect(p.x * 32 + 28, p.y * 32 + 95, 32, 32);
		}

		// Draw where we think the snake head is
		fill(0);
		noStroke();
		rect(28 + snakeHead.x * 32, 95 + snakeHead.y * 32, 32, 32);
	}
}
