package org.leonitousconforti.basilisk.algorithms;

import java.awt.Point;
import java.util.ArrayList;

import org.leonitousconforti.basilisk.Config;
import org.leonitousconforti.basilisk.algorithms.shared.Action;
import org.leonitousconforti.basilisk.core.ActionsManager;

/**
 * Outline for the basis of an algorithm.
 */
public abstract class AlgorithmBase implements Runnable {
    // The actions manager to handle the offloading of actions
    private ActionsManager actionsManager;

    // The name of this algorithm, used to identify it in the settings gui
    private final String name;

    // When this algorithm is selected by the user, this flag is set
    private volatile boolean selected;

    // When this algorithm is set tun just run once, this flag is set
    private boolean runOnce;

    // The delay to wait between loop runs
    private int delay;

    // References for all the data the algorithm has access to
    private Point snakeHead;
    private Point applePos;
    private ArrayList<Point> snakeParts;

    /**
     * Creates the basics of an algorithm.
     *
     * @param algorithmName the name of the algorithm
     */
    public AlgorithmBase(String algorithmName) {
        // Initialize variables
        name = algorithmName;
        selected = false;
        runOnce = false;
        delay = 0;

        // These can not be left null
        snakeHead = new Point(-1, -1);
        applePos = new Point(-1, -1);
        snakeParts = new ArrayList<Point>();
    }

    // The run method from Runnable, runs in its own thread
    @Override
    public final void run() {
        while (true) {
            // Make sure it is this algorithm's turn to run
            if ((Config.paused) || (!selected)) {
                continue;
            }

            // Runs the algorithm's code now
            calcPath();

            // Wait a little if you want
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Check the run once flag
            if (runOnce) {
                // This algorithm has completed its one loop
                selected = false;
            }
        }
    }

    /**
     * Update the information about the game. This method will always be ran before
     * the algorithms cade, making sure that the algorithm has the most recent
     * information before making computations
     *
     * @param updatedSnakeHead  the position of the snake's head
     * @param updatedSnakeParts the position of the snake's body parts
     * @param updatedApplePos   the position of the apple/thing the snake is trying
     *                          to eat
     */
    public final void update(Point updatedSnakeHead, ArrayList<Point> updatedSnakeParts, Point updatedApplePos) {
        snakeHead = updatedSnakeHead;
        snakeParts = updatedSnakeParts;
        applePos = updatedApplePos;
    }

    /**
     * Configures this algorithm with the
     * {@link org.leonitousconforti.basilisk.core.ActionsManager ActionsManager} to
     * offload the executing of actions.
     *
     * @param actions
     */
    public final void setActionsManager(ActionsManager actions) {
        actionsManager = actions;
    }

    /**
     * Set the time to wait between loop runs.
     *
     * @param millis the milliseconds to wait
     */
    public final void setDelayBetweenLoops(int millis) {
        delay = millis;
    }

    /**
     * Add an action to the action queue.
     *
     * @param a the action to add
     */
    public final void addAction(Action a) {
        actionsManager.addAction(a);
    }

    /**
     * Stalls the thread until the snake position has updated.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public final void runOnNextDataChange() {
        // Get the snake's head position at the start of the loop
        Point previousSnakeHead = getSnakeHead();

        // Compare the positions until they change
        while ((previousSnakeHead.x == getSnakeHead().x) && (previousSnakeHead.y == getSnakeHead().y)) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the algorithm's name
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the algorithm as selected by the user.
     */
    public final void setSelected() {
        selected = true;
        actionsManager.wipe();
        init();
    }

    /**
     * Another algorithm is running, so this one does not need to run anymore.
     */
    public final void unSelect() {
        selected = false;
    }

    /**
     * Tells the algorithm to just run its code one time.
     */
    public final void runOnce() {
        setSelected();
        runOnce = true;
    }

    /**
     * @return Get the position of the snake's head
     */
    public final Point getSnakeHead() {
        return snakeHead;
    }

    /**
     * @return Get the locations of the snake's body parts
     */
    public final ArrayList<Point> getSnakeParts() {
        return snakeParts;
    }

    /**
     * @return Get the position of the apple
     */
    public final Point getApplePos() {
        return applePos;
    }

    /**
     * Runs one time when the algorithm first starts up. Override this method!
     */
    public abstract void init();

    /**
     * Runs every time after the AI has finished processing the game images.
     * Override this method!
     */
    public abstract void calcPath();
}
