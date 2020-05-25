package org.leonitousconforti.basilisk.algorithms.shared;

import java.awt.Point;

/**
 * Defines an action that the snake can perform.
 */
public final class Action {
    // What direction to travel in
    private final String dir;

    // When to execute this action
    private final Point executionPoint;

    private boolean removeOnExecution;

    /**
     * Creates a new action that the snake can take.
     *
     * @param d the direction [left, right, up down] the snake should travel in
     *          relative to the board
     * @param p the point where the snake should perform this action. If the point
     *          is (-1, -1) the action will be executed instantaneously
     */
    public Action(String d, Point p) {
        dir = d;
        executionPoint = p;
        removeOnExecution = true;
    }

    /**
     * Determines where the snake would end up after performing this action.
     *
     * @param snakePos the position of the snake's head now
     */
    public Point getEndingLocation(Point snakePos) {
        Point delta = new Point(0, 0);

        delta.x = "left".equals(dir) ? -1 : delta.x;
        delta.x = "right".equals(dir) ? 1 : delta.x;
        delta.y = "up".equals(dir) ? -1 : delta.y;
        delta.y = "down".equals(dir) ? 1 : delta.y;

        return new Point(snakePos.x + delta.x, snakePos.y + delta.y);
    }

    /**
     * Determines where the snake would end up after performing this action.
     */
    public Point getEndingLocation() {
        // If this action does not have an execution point, then determining the ending
        // location is not possible because it doesn't know where to start
        if ((executionPoint.x <= -1) || (executionPoint.y <= -1)) {
            return new Point(-1, -1);
        }

        return getEndingLocation(executionPoint);
    }

    /**
     * Get the direction for this action.
     *
     * @return direction
     */
    public String getDir() {
        return dir;
    }

    /**
     * Get the execution point for this action.
     *
     * @return execution point
     */
    public Point getExecutionPoint() {
        return executionPoint;
    }

    /**
     * Sets wether or not the action should be removed after it is ran.
     *
     * @param status the boolean status
     */
    public void setDeleteOnExecution(boolean status) {
        removeOnExecution = status;
    }

    /**
     * @return wether or not this action should be deleted
     */
    public boolean getDeleteOnExecution() {
        return removeOnExecution;
    }
}
