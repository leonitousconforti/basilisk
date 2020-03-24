package basilisk.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import basilisk.core.Config;
import basilisk.algorithms.shared.*;
import basilisk.core.ActionsManager;

public abstract class AlgorithmBase implements Runnable {
    // Actions manager to inject keystrokes
    ActionsManager actionsManager;
    // The name of the algorithm
    private String name = "";
    // Is this thread selected and initialized?
    private volatile boolean selected;
    // The delay to wait between loop runs
    private int delay = 0;

    // Cached references for everything that the algorithm can use
    private Point snakeHead;
    private Point applePos;
    private ArrayList<Point> snakeParts;

    /**
     * Creates the basis of an algorithm
     * 
     * @param _name the name of the algorithm
     * @param _actionsManager the actionsManager to use when injecting keystrokes
     */
    public AlgorithmBase(String _name, ActionsManager _actionsManager) {
        actionsManager = _actionsManager;

        name = _name;
        selected = false;
    }

    // The run method from Runnable, runs in its own thread
    @Override
    public void run() {
        while (true) {
            // Wait until this is the selected algorithm
            while (!selected || Config.BasiliskProgram.paused) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Something interrupted the AI thread: " + e);
                    e.printStackTrace();
                }
            }

            // Run the algorithm
            if (selected) {
                calcPath();
            }

            // Wait a little if you want
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.out.println("Something interrupted the AI thread: " + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Update the information about the snake and apple locations
     * @param snakeHead the location of the head of the snake
     * @param snakeParts the locations of all the parts of the snake
     * @param applePos the position of the apple
     */
    public void update(Point snakeHead, ArrayList<Point> snakeParts, Point applePos) {
        this.snakeHead = snakeHead;
        this.snakeParts = snakeParts;
        this.applePos = applePos;
    }

    /**
     * Get the position of the snake head
     * @return snakeHead
     */
    public Point getSnakeHead() {
        return this.snakeHead;
    }

    /**
     * Get the locations of the snake parts
     * @return snakeParts
     */
    public ArrayList<Point> getSnakeParts() {
        return this.snakeParts;
    }

    /**
     * Get the position of the apple
     * @return applePos
     */
    public Point getApplePos() {
        return this.applePos;
    }

    /**
     * Get the name of the algorithm
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Is this algorithm the selected algorithm?
     * @return selected
     */
    public Boolean isSelected() {
        return selected;
    }

    /**
     * Sets this algorithm as the selected algorithm
     */
    public void setSelected() {
        selected = true;
        this.actionsManager.wipe();
        init();
    }

    /**
     * Tell this algorithm to stop running
     */
    public void unSelect() {
        selected = false;
    }

    /**
     * Add an action to the action manager
     * @param a the action to add
     */
    public void addAction(Action a) {
        this.actionsManager.addAction(a);
    }

    /**
     * Set the time to wait between loop runs
     * @param millis the milliseconds to wait
     */
    public void setDelayBetweenLoops(int millis) {
        delay = millis;
    }

    /**
     * Runs one time when the algorithm first starts up.
     * Override this method!
     */
    public abstract void init();

    /**
     * Runs every time after the AI has finished processing the game images.
     * Override this method!
     */
    public abstract void calcPath();

    /**
     * Get the set of node that are node being looked at by the algorithm
     * @return a list of nodes
     */
    public List<GameTile> getClosedSet() {
        return null;
    }
    
    /**
     * Get the set of nodes that are still being considered by the algorithm
     * @return a list of nodes
     */
    public List<GameTile> getOpenSet() {
        return null;
    }

    /**
     * Get the GameTile grid being used by the algorithm if there is one
     */
    public GameTile[][] getGrid() {
        return null;
    }

    /**
     * Get the current calculated path
     */
    public List<GameTile> getPath() {
        return null;
    }
}
