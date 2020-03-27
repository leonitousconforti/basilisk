package basilisk.core;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import processing.core.*;
import basilisk.algorithms.shared.*;
import basilisk.algorithms.AlgorithmBase;

// Gui for the basilisk program
public class Gui extends PApplet {
    // A reference to the AI section of the program, needed to interact with other components
    private Basilisk AI;

    // Cached references for everything that is going to be drawn on the screen
    private Point snakeHead;
    private Point applePos;
    private ArrayList<Point> snakeParts;
    private BufferedImage gameImg;

    // Flags for the loading process
    private boolean loadingFlag1;
    private boolean loadingFlag2;

    public Gui(Basilisk basilisk) {
        snakeParts = new ArrayList<Point>();
        snakeHead = new Point(-1, -1);
        applePos = new Point(-1, 1);

        loadingFlag1 = false;
        loadingFlag2 = false;

        AI = basilisk;
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

    /**
     * Updates the elements on the screen to their new positions
     */
    public void update() {
        update(
            AI.getGameElementDetection().getApplePos(),
            AI.getGameElementDetection().getSnakeHead(),
            AI.getGameElementDetection().getSnakeParts(),
            AI.getProcessedGameImages()[0]
        );
    }

    // Processing settings method
	@Override
    public void settings() {
		// Create the window based on the screen size
		if (Config.BasiliskProgram.screenConfig == Config.BasiliskProgram.SCREEN_1920x1080) {
            size(600, 600);
            Config.GuiConfigs.scale = 1;
		} else if (Config.BasiliskProgram.screenConfig == Config.BasiliskProgram.SCREEN_1440x900) {
            size(300, 300);
            Config.GuiConfigs.scale = 1/2;
        }
	}

	// Processing setup method
    @Override
    public void setup() {
		// Setup the processing surface
        background(51);
		frameRate(Config.GuiConfigs.GuiFrameRate);
		surface.setAlwaysOnTop(true);
        surface.setLocation(100, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - height / 2);
        surface.setTitle("Basilisk AI");
        // PImage icon = loadImage("logo.png");
        // surface.setIcon( icon );
    }

	// Processing loop method
	@Override
    public void draw() {
        // Start a timer for loop timing
        double startTime = System.nanoTime();

		// Scale the elements in the PApplet according to the screen size
        scale( (float) 1/2 );

        // Run the appropriate method desired through key presses
        if (Config.GuiConfigs.loading) {
            loading();
        }
        if (Config.GuiConfigs.showGameDetection) {
            showGameDetection();
        }

        // Calculate the loop timings
        double fps = 1 / ((System.nanoTime() - startTime) / 1000000000.0);
        double ms = (System.nanoTime() - startTime) / 1000000.0;

        // Format number to two decimal places to look nicer in console
        fps = (double) Math.round(fps * 100) / 100;
        ms = (double) Math.round(ms * 100) / 100;

        // Print Debug logs
        if (Config.BasiliskProgram.showAnimationDebugs) {
            System.out.println("Animation loop took: " + ms + " ms, processing at: " + fps + " frames per second");
        }
    }

    // Processing keyboard input method
    @Override
    public void keyPressed() {
        // Exit on escape
        if (key == ESC) {
            System.out.println("Goodbye!");
            System.exit(-1);
        }
        // Pause on p
        if (key == 'p') {
            Config.BasiliskProgram.paused = !Config.BasiliskProgram.paused;

            if (!Config.BasiliskProgram.paused) {
                loop();
            } else if (Config.BasiliskProgram.paused) {
                noLoop();
            }
        }
        // Reloading setup process
        if (key == 'r') {
            Config.GuiConfigs.showGameDetection = false;
            Config.GuiConfigs.loading = true;

            // Initialize some flags for later
            loadingFlag1 = false;
            loadingFlag2 = false;

            System.out.println("Reloading capture area...");
        }
        // Toggle debug messages to console
        if (key == 'd') {
            Config.BasiliskProgram.showAiDebugs = !Config.BasiliskProgram.showAiDebugs;
            Config.BasiliskProgram.showAnimationDebugs = !Config.BasiliskProgram.showAnimationDebugs;
            System.out.println("Printing debug messages: " + Config.BasiliskProgram.showAiDebugs);
        }
    }

    // Processing mouse input method
    @Override
    public void mousePressed() {
        if (Config.GuiConfigs.loadingStep == Config.GuiConfigs.loadingStepDone) {
            System.out.println("You do not need to click for this!");
            return;
        }

        // To detect the color of the apple
        if (Config.GuiConfigs.loadingStep == Config.GuiConfigs.loadingStepApple) {
            AI.getGameElementDetection().setAppleColor( gameImg, new Point( mouseX * 2, mouseY * 2 ) );
            loadingFlag1 = true;

            System.out.println("Done setting apple color when reloading setup");
        }

        // To detect the color of the snake
        else if (Config.GuiConfigs.loadingStep == Config.GuiConfigs.loadingStepSnake) {
            AI.getGameElementDetection().setSnakeColor( new Color( gameImg.getRGB(mouseX * 2, mouseY * 2) ) );
            loadingFlag2 = true;
            System.out.println("Done setting snake color when reloading setup");

            Config.GuiConfigs.loadingStep = Config.GuiConfigs.loadingStepDone;
            Config.GuiConfigs.loading = false;
            Config.GuiConfigs.showGameDetection = true;
        }
    }

    // Show everything the AI sees in the animation window
    private void showGameDetection() {
		// Draw the current screen shot
		image( new PImage( gameImg ), 0, 0 );

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
		// *Fixed 03/14/20
		
		// "The iterators returned by this class's iterator and listIterator methods are fail-fast: if the list is 
		// structurally modified at any time after the iterator is created, in any way except through the iterator 
		// own remove or add methods, the iterator will throw a ConcurrentModificationException.""
		for (Point p : snakeParts) {
			rect(p.x * 32 + 28, p.y * 32 + 95, 32, 32);
		}

		// Draw where we think the snake head is
		fill(0);
		noStroke();
        rect(28 + snakeHead.x * 32, 95 + snakeHead.y * 32, 32, 32);

        // Draw the snake AI path finding
        AlgorithmBase algo = AI.getAI_Algorithms().getRunningAlgorithm();

        if (algo.getName() == "A* search") {
            // Draw nodes
            for (int x = 0; x < 17; x++) {
                for (int y = 0; y < 15; y++) {
                    GameTile g = algo.getGrid()[x][y];

                    if (g.wall) {
                        fill(0);
                        ellipse(g.x * 32 + 28 + 16, g.y * 32 + 95 + 16, 7, 7);
                    } else {
                        fill(255);
                        ellipse(g.x * 32 + 28 + 16, g.y * 32 + 95 + 16, 7, 7);
                    }
                }
            }

            // Draw the open set
            for (GameTile g : algo.getOpenSet()) {
                fill(50, 205, 50);
                ellipse(g.x * 32 + 28 + 16, g.y * 32 + 95 + 16, 10, 10);
            }

            // Draw the closed set
            for (GameTile g : algo.getClosedSet()) {
                fill(210, 33, 45);
                ellipse(g.x * 32 + 28 + 16, g.y * 32 + 95 + 16, 10, 10);
            }

            // Draw the path being worked on
            for (GameTile g : algo.getPath()) {
                fill(200, 100, 200);
                ellipse(g.x * 32 + 28 + 16, g.y * 32 + 95 + 16, 10, 10);
            }
            noFill();
            stroke(255, 0, 200);
            strokeWeight(6);
            beginShape();
            for (int i = 0; i < algo.getPath().size(); i++) {
              vertex(algo.getPath().get(i).x * 32 + 32 / 2 + 28, algo.getPath().get(i).y * 32 + 32 / 2 + 95);
            }
            endShape();
        }

        // Draw anywhere there are actions to take place
        // fill(50, 140, 180);
        // List<Action> actions = AI.getAI_Algorithms().getActionManager().getActions();
        // for (Action a : actions) {
        //     rect(a.executePoint.x * 32 + 28 + 6, a.executePoint.y * 32 + 95 + 6, 20, 20);
        // }
    }

    // Show the setup process in the animation window
    private void loading() {
        // Make things look pretty
        background(255);
        textAlign(CENTER);
        textSize(20);
        fill(0);

        // If the user has not hit the apple nor the snake yet...
        if ((!loadingFlag1) && (!loadingFlag2)) {
            // Update the config variable
            Config.GuiConfigs.loadingStep = Config.GuiConfigs.loadingStepApple;

            // Draw a screenshot for the user
            image( new PImage(gameImg), 0, 0 );

            // Prompt the user
            text("click on the apple", width / 2, height / 2);
            text("you see in THIS window", width / 2, height / 2 + 20);
        } 

        // We have the apple, now time for the snake...
        else if ((loadingFlag1) && (!loadingFlag2)) {
            // Update the config variable
            Config.GuiConfigs.loadingStep = Config.GuiConfigs.loadingStepSnake;

            // Draw a screenshot for the user
            image( new PImage(gameImg), 0, 0 );

            // Prompt the user
            text("click anywhere on the snake", width / 2, height / 2);
            text("you see in THIS window", width / 2, height / 2 + 20);
        }
    }
}
