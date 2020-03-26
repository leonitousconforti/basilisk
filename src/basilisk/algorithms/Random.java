package basilisk.algorithms;

import java.awt.Point;
import java.util.ArrayList;

import basilisk.algorithms.shared.Action;
import basilisk.core.ActionsManager;

// An algorithm to move the snake in random directions
public class Random extends AlgorithmBase {
    private ArrayList<String> dirs = new ArrayList<String>();
    private ArrayList<Point> walls;
    private Point lastActionPoint = new Point(-1, -1);

    public Random(ActionsManager actionsManager) {
        super("Random-Movement", actionsManager);
        initDirs();
    }

    @Override
    public void init() {
        setDelayBetweenLoops( 0 );
        System.out.println("Random algorithm initialized");
    }

    @Override
    public void calcPath() {
        walls = getSnakeParts();

        if ( (getSnakeHead().x != lastActionPoint.x) || (getSnakeHead().y != lastActionPoint.y) ) {
            int randomDir = (int) Math.floor(Math.random() * dirs.size()) + 0;
            Action a = new Action( dirs.get(randomDir), new Point(-1, -1) );
            Point end = a.getEndingLocation( this.getSnakeHead() );
            Boolean certainDeathIsImminentFromAction = (walls.contains(end)) || (end.x < 0) || (end.x >= 17) || (end.y < 0) || (end.y >= 15);

            if (!certainDeathIsImminentFromAction) {
                lastActionPoint.x = getSnakeHead().x;
                lastActionPoint.y = getSnakeHead().y;
                addAction( a );
                initDirs();
                // System.out.println("Sent " + a.dir + " at (" + getSnakeHead().x + ", " + getSnakeHead().y + ")");
            } else if (certainDeathIsImminentFromAction) {
                dirs.remove(randomDir);

                if (dirs.size() == 0) {
                    System.out.println("Certain Death Is Imminent! There is no escape:(");
                    System.exit(0);
                }
            }
        }
    }

    private void initDirs() {
        if (dirs.size() != 4) {
            dirs.clear();
            dirs.add("up");
            dirs.add("down");
            dirs.add("left");
            dirs.add("right");
        }
    }
}
