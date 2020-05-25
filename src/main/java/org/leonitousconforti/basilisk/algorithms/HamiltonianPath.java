package org.leonitousconforti.basilisk.algorithms;

import java.awt.Point;
import java.util.ArrayList;

import org.leonitousconforti.basilisk.algorithms.shared.Action;

/**
 * Path finds using a Hamiltonian path cycle.
 *
 * @see https://en.wikipedia.org/wiki/Hamiltonian_path
 */
public class HamiltonianPath extends AlgorithmBase {
    private final ArrayList<Action> path;

    /**
     * Path finds using a hamiltonian path/cycle, looks really boring.
     */
    public HamiltonianPath() {
        super("Hamiltonian Path");

        path = new ArrayList<Action>();
        constructRigidPath(path);
    }

    @Override
    public void init() {
        setDelayBetweenLoops(0);

        for (Action a : path) {
            addAction(a);
            a.setDeleteOnExecution(false);
        }
    }

    @Override
    public void calcPath() {
        runOnNextDataChange();
    }

    /**
     * Make a simple path that passes through every point in a 17x15 grid once.
     * Rigid because it can not take shortcuts
     *
     * @param path the path to add the Actions to
     */
    private void constructRigidPath(ArrayList<Action> pathToConstruct) {
        pathToConstruct.clear();

        // The "normal" configuration
        final int cols = 16; // MUST BE EVEN!
        final int rows = 15;

        // Make the straight lines up and down, the u-turns at the top and bottom, and
        // the flat line going back across the top
        for (int i = 0; i < cols; i++) {
            // Add down as the first element
            if (i == 0) {
                pathToConstruct.add(0, new Action("down", new Point(0, 0)));
            }

            // If it is an even row going down
            if (i % 2 == 0) {
                // Add a down movement at the top of the column
                pathToConstruct.add(new Action("down", new Point(i, 1)));

                // Go over every row and add an action
                for (int j = 2; j < rows - 1; j++) {
                    pathToConstruct.add(new Action("down", new Point(i, j)));
                }

                // Add a right movement to the bottom of the column
                pathToConstruct.add(new Action("right", new Point(i, rows - 1)));
            }

            if (i % 2 == 1) {
                // Add a up action to the bottom of the column
                pathToConstruct.add(new Action("up", new Point(i, rows - 1)));

                // Go over every row backwards, so we keep the right action order
                for (int j = rows - 2; j >= 2; j--) {
                    pathToConstruct.add(new Action("up", new Point(i, j)));
                }

                if (i != cols - 1) {
                    // Add a right movement to the top of the column
                    pathToConstruct.add(new Action("right", new Point(i, 1)));

                    // Next loop iteration
                    continue;
                }

                // Add the missing up tile!
                pathToConstruct.add(new Action("up", new Point(i, 1)));
            }

            if (i == cols - 1) {
                for (int h = cols - 1; h > 0; h--) {
                    pathToConstruct.add(new Action("left", new Point(h, 0)));
                }
            }
        } // End for-loop
    } // End rigid path construction
}
