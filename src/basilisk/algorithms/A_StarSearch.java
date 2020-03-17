package basilisk.algorithms;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import basilisk.algorithms.shared.GameTile;

// A-Star search algorithm to find optimal path
public class A_StarSearch implements Runnable {
    private GameTile[][] grid;
    private List<GameTile> openSet;
    private List<GameTile> closedSet;

    private GameTile start;
    private GameTile goal;
    private GameTile current;
    private List<GameTile> path;

    public A_StarSearch(Point _start, Point target) {
        grid = new GameTile[17][15];
        openSet = new ArrayList<GameTile>();
        closedSet = new ArrayList<GameTile>();
        path = new ArrayList<GameTile>();
    
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 15; j++) {
                grid[i][j] = new GameTile(i, j);
            }
        }
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 15; j++) {
                grid[i][j].addNeighbors(grid);
            }
        }

        start = grid[ _start.x ][ _start.y ];
        openSet.add(start);
        goal = grid[ target.x ][ target.y ];
    }

    @Override
    public void run() {
        while (openSet.size() > 0) {
            int winner = 0;
            for (int i = 0; i < openSet.size(); i++) {
                if (openSet.get(i).f < openSet.get(winner).f) {
                    winner = i;
                }
            }
            current = openSet.get(winner);
            if (current == goal) {
                System.out.println("a-star done");
                break;                
            }
    
            openSet.remove(current);
            closedSet.add(current);
            List<GameTile> neighbors = current.neighbors;
            
            for (int i = 0; i < neighbors.size(); i++) {
                GameTile neighbor = neighbors.get(i);
                if (!closedSet.contains(neighbor) && !neighbor.wall) {
                    float tempG = current.g + heuristic(neighbor, current);
            
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
            
                    if (newPath) {
                        neighbor.heuristic = heuristic(neighbor, goal);
                        neighbor.f = neighbor.g + neighbor.heuristic;
                        neighbor.previous = current;
                    }
                }
            }
        }
    
        path = new ArrayList<GameTile>();
        GameTile temp = current;
        path.add(temp);
        while (temp.previous != null) 
        {
            path.add(temp.previous);
            temp = temp.previous;
        }
    }


    float heuristic(GameTile a, GameTile b) {
        float d = 0;
        
        // if (settings.huersticSQRT) {
        //     d = dist(a.x, a.y, b.x, b.y);
        // } else if (!settings.huersticSQRT) {
        //     d = abs(a.x - b.x) + abs(a.y - b.y);
        // }
        d = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        return d;
    }
}
