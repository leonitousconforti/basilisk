package org.leonitousconforti.basilisk.algorithms;

import java.awt.Point;
import java.util.ArrayList;

import org.leonitousconforti.basilisk.Config;
import org.leonitousconforti.basilisk.algorithms.shared.Action;

/**
 * Path finds using random movement.
 */
@SuppressWarnings({ "checkstyle:NoWhitespaceAfter", "checkstyle:MagicNumber" })
public class RandomMovement extends AlgorithmBase {
    // All the directions to travel in
    private final String[] randomDirs = { "left", "right", "up", "down" };

    // Places I don't want to go
    private ArrayList<Point> placesThatWillGetMeKilled;

    /**
     * Path finds using random movement that should not get it killed.
     */
    public RandomMovement() {
        super("Random Movement");
        placesThatWillGetMeKilled = new ArrayList<Point>();
    }

    /**
     * Runs once when the algorithm is selected bu the user.
     */
    @Override
    public void init() {
    }

    /**
     * Does the number crunching.
     */
    @Override
    public void calcPath() {
        // Get the list of the snake's body parts
        placesThatWillGetMeKilled = getSnakeParts();

        // Pick a random direction to travel in
        String randomDir = randomDirs[(int) Math.floor(Math.random() * 4)];

        // Make an action for the direction and get where the snake would end up after
        // taking this action
        Action action = new Action(randomDir, new Point(-1, -1));
        Point end = action.getEndingLocation(getSnakeHead());

        // Check to make sure that this action will not get the snake killed
        boolean certainDeathFromAction = (placesThatWillGetMeKilled.contains(end)) || (end.x < 0) || (end.y < 0)
                || (end.x >= Config.NumberOfColsOnGameBoard) || (end.y >= Config.NumberOfRowsOnGameBoard);

        // If the action will not get us killed, then add it to the queue
        if (!certainDeathFromAction) {
            addAction(action);

            // Run the next loop next time the data changes. Naturally, the algorithms are
            // going to try to run as fast as they can but that is not necessary for the
            // random algorithm because it only needs to make decisions once every time the
            // snake moves (unless that decision meant certain death, in which case it tried
            // to find another option)
            runOnNextDataChange();
        }
    }
}
