package basilisk.algorithms.shared;

import java.awt.Point;

// Defines an action that the AI can take
public class Action {
    // What direction to travel in
    public String dir;
    // When to execute this action
    public Point executePoint;

    public Action(String d, Point p) {
        dir = d;
        executePoint = p;
    }
}
