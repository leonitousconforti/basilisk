package basilisk.algorithms;

import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

import basilisk.core.ActionsManager;
import basilisk.algorithms.shared.*;

// A-Star search algorithm to find optimal path
public class A_StarSearch extends AlgorithmBase {
    // The layout of the game board in terms of tiles
    private GameTile[][] grid;

    // Sets containing the tile searched and the tiles not yet searched
    private ArrayList<GameTile> path;
    private ArrayList<GameTile> openSet;
    private ArrayList<GameTile> closedSet;

    // Points
    Point start, goal;

    // Previous apple pos to determine wether or not to tun A* again
    Point previousApplePos;
    Point previousSnakePos;

    /**
     * Constructs a new A* search algorithm
     * @param actionsManager the action manager
     */
    public A_StarSearch(ActionsManager actionsManager) {
        super("A* search", actionsManager);
    
        // Declare sets and path
        path = new ArrayList<GameTile>();
        openSet = new ArrayList<GameTile>();
        closedSet = new ArrayList<GameTile>();
        grid = new GameTile[17][15];

        // Declare points
        start = new Point(-1, -1);
        goal = new Point(-1, 1);
        previousApplePos = new Point(-1, -1);
        previousSnakePos = new Point(-1, -1);

        // Initialize grid
        for (int i  = 0; i < 17; i++) {
            for (int j = 0; j < 15; j++) {
                grid[i][j] = new GameTile(i, j);
            }
        }
        // Add neighbors
        for (int i  = 0; i < 17; i++) {
            for (int j = 0; j < 15; j++) {
                grid[i][j].addNeighbors(grid);
            }
        }
    }

    // Runs once every time the A* algorithm is selected by the user.
    @Override
    public void init() {
        System.out.println("A* taking over!");

        setDelayBetweenLoops(0);
        enableSupportsPathSkipping();
        openSet.clear();
        closedSet.clear();
        path.clear();
    }

    // Runs every time it can, as fast as your code runs
    // Never put a loop here that will run forever!
    @Override
    public void calcPath() {
        // Check to run again
        if ((previousApplePos.x == getApplePos().x) && (previousApplePos.y == getApplePos().y)) {
            if ((previousSnakePos.x == getSnakeHead().x) && (previousSnakePos.y == getSnakeHead().y)) {
                // Skip everything else and return from this run
                return;
            } else 
            if ((getApplePos().x < 0) || (getApplePos().y < 0)) {
                // No point in running the algorithm if we don't have a target node to reach
                return;
            }
        }

        // Set the previous apple position when it moves
        previousApplePos.x = getApplePos().x;
        previousApplePos.y = getApplePos().y;

        previousSnakePos.x = getSnakeHead().x;
        previousSnakePos.y = getSnakeHead().y;

        // Clear the previous loop iteration data
        openSet.clear();
        closedSet.clear();
        path.clear();

        // Reset gameTiles
        for (int i  = 0; i < 17; i++) {
            for (int j = 0; j < 15; j++) {
                grid[i][j].reset();
            }
        }

        // Make the start and end points into GameTiles
        start = getSnakeHead();
        goal = getApplePos();

        // Doesn't work because they do not have their neighbors initialized
        // GameTile gameTileStart = new GameTile( start.x, start.y );
        // GameTile gameTileGoal = new GameTile( goal.x, goal.y );
        // Easier to just do:
        GameTile gameTileStart = grid[start.x][start.y];
        GameTile gameTileGoal = grid[goal.x][goal.y];

        GameTile current;

        // Add the starting tile to the open set
        openSet.add( gameTileStart );

        // Update the walls
        for (Point p : getSnakeParts()) {
            grid[p.x][p.y].wall = true;
        }

        // Perform the search
        while (openSet.size() > 0) {
            // Find the next GameTile with the lowest f
            int winner = 0;
            for (int i = 0; i < openSet.size(); i++) {
                if (openSet.get(i).f < openSet.get(winner).f) {
                    winner = i;
                }
            }
            current = openSet.get(winner);

            // Is the new best path the end?
            if (current == gameTileGoal) {
                // Determine the path and add the actions
                reconstructPath(current);
                List<Action> nextActionList = makeActionList(path);
                // List<Action> queuedActions = getActionsQueued();

                // Add actions
                clearActions();
                for (Action a : nextActionList) {
                    addAction(a);
                }

                System.out.println("A* done! added " + nextActionList.size() + " actions to the que");
                return;
            }

            // Best option moves from openSet to closedSet
            openSet.remove( current );
            closedSet.add( current );

            // Check all the neighbors
            for (int i = 0; i < current.neighbors.size(); i++) {
                GameTile neighbor = current.neighbors.get(i);

                // Valid next spot? 
                if ((!closedSet.contains(neighbor)) && (!neighbor.wall)) {
                    float tempG = current.g + heuristic(neighbor, current);
                
                    // Is this a better path than before?
                    boolean newPath = false;
                    if (openSet.contains(neighbor)) {
                        if (tempG < neighbor.g) {
                            neighbor.g = tempG;
                            newPath = true;
                        }
                    } else {
                        neighbor.g = tempG;
                        newPath = true;
                        openSet.add(neighbor);
                    }

                    // Yes, it's a better path
                    if (newPath) {
                        neighbor.heuristic = heuristic(neighbor, gameTileGoal);
                        neighbor.f = neighbor.g + neighbor.heuristic;
                        neighbor.previous = current;
                    }
                }
            }
        }

        // No Solution!
        System.out.println("A* done and no solution :(");
        return;
    }

    /**
     * Find the path by working backwards
     * @param _current the current node
     */
    private void reconstructPath(GameTile _current) {
        GameTile temp = _current;
        path.add(temp);
        while (temp.previous != null) {
            path.add(temp.previous);
            temp = temp.previous;
        }
    }

    /**
     * Converts the final path into a list of actions that the ActionsManager can interpret
     * 
     * @param path the final path from A*
     * @return the actions list
     */
    private List<Action> makeActionList(List<GameTile> path) {
        List<Action> actions = new ArrayList<Action>();
        
        if (path.size() == 0) {
            return null;
        }

        for (int i = path.size() - 1; i > 0; i--) {
            GameTile spot = path.get(i);
            GameTile spotGoingTo = path.get(i - 1);

            // What point on the game board is this action going to take place at
            Point p = new Point( spot.x, spot.y );

            // Determine the differences between the spots
            int dx = spot.x - spotGoingTo.x;
            int dy = spot.y - spotGoingTo.y;

            // If the next spot is on the same x and one below, we need to go down
            if ((dx == 0) && (dy == -1)) {
                Action a = new Action("down", p);
                actions.add(a);
            }
            // If the next spot is on the same x and one above, we need to go up
            else if ((dx == 0) && (dy == 1)) {
                Action a = new Action("up", p);
                actions.add(a);
            }
            // If the next spot is one over and on the same y, we need to go left
            else if ((dx == 1) && (dy == 0)) {
                Action a = new Action("left", p);
                actions.add(a);
            }
            // If the next spot is one behind and on the same y, we need to do right
            else if ((dx == -1) && (dy == 0)) {
                Action a = new Action("right", p);
                actions.add(a);
            }
        }

        return actions;
    }

    /**
     * Calculates an educated guess of how far it is between two points
     * 
     * @param a the start gameTile
     * @param b the end gameTile
     * @return the educated distance
     */
    private float heuristic(GameTile a, GameTile b) {
        // float d = (float) Math.hypot(a.x - b.x, a.y - b.y);
        float d = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        return d;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GameTile> getClosedSet() {
        // return this.closedSet;
        return (ArrayList<GameTile>) this.closedSet.clone();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GameTile> getOpenSet() {
        // return this.openSet;
        return (ArrayList<GameTile>) this.openSet.clone();
    }

    @Override
    public GameTile[][] getGrid() {
        return this.grid;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GameTile> getPath() {
        // return this.path;
        return (ArrayList<GameTile>) this.path.clone();
    }
}
