package basilisk.algorithms.shared;

import java.awt.Point;

// Defines an action that the AI can take
public class Action {
    // What direction to travel in
    public String dir;
    // When to execute this action
    public Point executePoint;

    /**
     * Defines an action that the snake can take
     * @param d the direction the snake should travel in relative to the board
     * @param p the point at which the snake should perform this action. If the executionPoint
     * is at (-1, -1) then the action will be executed instantaneously.
     */
    public Action(String d, Point p) {
        dir = d;
        executePoint = p;
    }

    /**
     * Determines where the snake would end up after this action
     * @param snakePos this position of the snake now
     */
    public Point getEndingLocation( Point snakePos ) {
        int dx = 0, dy = 0;

        if (dir == "up") {
            dx = 0; dy = -1;
        } else if (dir == "down") {
            dx = 0; dy = 1;
        } else if (dir == "left") {
            dx = -1; dy = 0;
        } else if (dir == "right") {
            dx = 1; dy = 0;
        }

        return new Point( snakePos.x + dx, snakePos.y + dy );
    }
}
