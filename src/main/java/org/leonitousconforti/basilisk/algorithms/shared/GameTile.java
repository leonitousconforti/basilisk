package org.leonitousconforti.basilisk.algorithms.shared;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.leonitousconforti.basilisk.Config;

/**
 * Stores information about the tiles on the board for an algorithm.
 */
public class GameTile {
    // Where this tile is on the board
    private int x;
    private int y;
    private Point location;

    // References of its neighbors
    private List<GameTile> neighbors;

    // Any custom attributes for a particular algorithm get added here
    private Map<String, Object> attributes;

    /**
     * Stores information about a tile on the board. As is, it only stores its
     * position on the board and references to its neighbors. Any algorithm can add
     * custom attributes to describe more of a gameTile for that algorithm with
     * setAttribute
     *
     * @param xPos the x position of this tile
     * @param yPos the y position of this tile
     */
    public GameTile(int xPos, int yPos) {
        x = xPos;
        y = yPos;
        location = new Point(x, y);

        attributes = new HashMap<>();
        neighbors = new ArrayList<GameTile>();
        attributes.clear();
    }

    /**
     * Allows algorithms to set custom attributes in the GameTile definition.
     *
     * @param attributeName  the attribute to define
     * @param attributeValue the value of the attribute
     */
    public void setAttribute(String attributeName, Object attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    /**
     * Get the value of a custom attribute in the GameTile definition.
     *
     * @param attributeName the name of the attribute
     */
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * Adds the positions of the neighbors, only use lateral and horizontal
     * movements and do not add diagonals.
     *
     * @param grid the grid to add neighbors from
     */
    public void addNeighbors(GameTile[][] grid) {
        // Add the neighbor to the left
        if (x > 0) {
            neighbors.add(grid[x - 1][y]);
        }
        // Add the neighbor above
        if (y > 0) {
            neighbors.add(grid[x][y - 1]);
        }
        // Add the neighbor to the right
        if (x < Config.NumberOfColsOnGameBoard - 1) {
            neighbors.add(grid[x + 1][y]);
        }
        // Add the neighbor below
        if (y < Config.NumberOfRowsOnGameBoard - 1) {
            neighbors.add(grid[x][y + 1]);
        }
    }

    /**
     * Resets a game tile to the state when it was created.
     */
    public void reset() {
        attributes.replaceAll((key, value) -> 0);
    }

    /**
     * Get the GameTile's location on the board.
     */
    public Point getLocation() {
        return location;
    }
}
