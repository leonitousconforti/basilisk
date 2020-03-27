package basilisk.algorithms;

import java.awt.Point;
import java.util.ArrayList;

import basilisk.core.ActionsManager;
import basilisk.algorithms.shared.Action;

public class HamiltonianPath extends AlgorithmBase {
    private final ArrayList<Action> path;
    private Point prevSnakePos;

    public HamiltonianPath(ActionsManager actionsManager) {
        super("Hamiltonian", actionsManager);
        path = new ArrayList<Action>();
        prevSnakePos = new Point(-1, -1);

        constructRigidPath(path);
    }

    public void init() {
        // enableSupportsPathSkipping();
        disableSupportsPathSkipping();
        setDelayBetweenLoops(0);
        System.out.println("Hamiltonian path running...");
        
        for (Action a : path) {
            addAction(a);
        }
    }

    public void calcPath() {
        if ( (getSnakeHead().x != prevSnakePos.x) || (getSnakeHead().y != prevSnakePos.y) ) {
            prevSnakePos.x = getSnakeHead().x;
            prevSnakePos.y = getSnakeHead().y;
        
            if (getActionsQueued().size() != path.size()) {
                ArrayList<Action> queActions = getActionsQueued();
    
                for (Action a : path) {
                    if (!queActions.contains(a)) {
                        addAction(a);
                    }
                }
    
                // System.out.println("refreshed hamiltonian path");
            }
        }
    }

    /**
     * Make a simple path that passes through every point in a 17x15 grid once.
     * Rigid because it can not take shortcuts
     * 
     * @param path the path to add the Action to
     */
    private void constructRigidPath(ArrayList<Action> path) {
        path.clear();

        // The "normal" configuration
        final int cols = 16;
        final int rows = 15;

        // Make the straight lines up and down, the u-turns at the top and bottom, and the flat line going back across the top
        for (int i = 0; i < cols; i++) {
            // Add down as the first element
            if (i == 0) {
                path.add( 0, new Action("down", new Point(0, 0)) );
            }

            // If it is an even row going down
            if (i % 2 == 0) {
                // Add a down movement at the top of the column
                path.add( new Action("down", new Point(i, 1)) );

                // Go over every row and add an action
                for (int j = 2; j < rows - 1; j++) {
                    path.add( new Action("down", new Point(i, j)) );
                }

                // Add a right movement to the bottom of the column
                path.add( new Action("right", new Point(i, rows - 1)) );
            }

            if (i % 2 == 1) {
                // Add a up action to the bottom of the column
                path.add( new Action("up", new Point(i, rows - 1)) );

                // Go over every row backwards, so we keep the right action order
                for (int j = rows - 2; j >= 2; j--) {
                    path.add( new Action("up", new Point(i, j)) );
                }

                if (i != cols - 1) {
                    // Add a right movement to the top of the column
                    path.add( new Action("right", new Point(i, 1)) );

                    // Next loop iteration
                    continue;
                }

                // Add the missing up tile!
                path.add( new Action("up", new Point(i, 1)) );
            }

            if (i == cols - 1) {
                for (int h = cols - 1; h > 0; h--) {
                    path.add( new Action("left", new Point(h, 0)) );
                }
            }
        } // End for-loop
    } // End rigid path construction
}
