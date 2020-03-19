package basilisk.algorithms;

import java.awt.Point;
import basilisk.algorithms.shared.Action;
import basilisk.core.ActionsManager;

public class Random extends AlgorithmBase {
    private final String[] dirs = { "up", "left", "down", "right" };

    public Random(ActionsManager actionsManager) {
        super("Random", actionsManager);
    }

    @Override
    public void init() {
        System.out.println("Random algo initialized");
        setDelayBetweenLoops( 200 );
    }

    @Override
    public void calcPath() {
        int dir = (int) Math.floor(Math.random() * dirs.length) + 0;
        addAction( new Action(dirs[dir], new Point(-1, -1)) );
    }
}
