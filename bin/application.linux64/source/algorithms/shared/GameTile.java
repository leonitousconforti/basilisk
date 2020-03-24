package basilisk.algorithms.shared;

import java.util.List;
import java.util.ArrayList;

// Stores information about a tile in the game; ex. position, heuristic, wall?, and neighbors
public class GameTile {
    public int x, y;
    public float f;
    public float g;
    public float heuristic;
    public List<GameTile> neighbors;
    public GameTile previous;
    public boolean wall;

    public GameTile(int _x, int _y) {
        x = _x;
        y = _y;
        f = 0;
        g = 0; 
        heuristic = 0;

        neighbors = new ArrayList<GameTile>();
        previous = null;
        wall = false;
    }

    /**
     * Adds the positions of the neighbors, only use lateral and horizontal movements and do not add diagonals
     * @param grid the grid to add neighbors from
     */
    public void addNeighbors(GameTile[][] grid) {
        if (x > 0) {
            neighbors.add(grid[x- 1][y]);
        }
        if (y > 0) {
            neighbors.add(grid[x][y - 1]);
        }
        if (x < 17 - 1) {
            neighbors.add(grid[x + 1][y]);
        }
        if (y < 15 - 1) {
            neighbors.add(grid[x][y + 1]);
        }
    }

    /**
     * Resets a game tile to the state when it was created
     */
    public void reset() {
        f = 0;
        g = 0; 
        heuristic = 0;

        previous = null;
        wall = false;
    }
}
