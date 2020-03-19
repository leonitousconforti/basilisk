package basilisk.algorithms;

import basilisk.core.ActionsManager;
import basilisk.algorithms.shared.Action;

public abstract class AlgorithmBase implements Runnable {
    ActionsManager actionsManager;
    // The name of the algorithm
    private String name = "";
    // Is this thread selected and initialized?
    private volatile boolean selected;

    private int delay = 0;

    public AlgorithmBase(String _name, ActionsManager _actionsManager) {
        actionsManager = _actionsManager;

        name = _name;
        selected = false;
    }

    @Override
    public void run() {
        while (true) {
            while (!selected) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Something interrupted the AI thread: " + e);
                    e.printStackTrace();
                }
            }

            if (selected) {
                calcPath();
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.out.println("Something interrupted the AI thread: " + e);
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }

    public Boolean isSelected() {
        return selected;
    }

    public void setSelected() {
        selected = true;
        this.actionsManager.wipe();
        init();
    }

    public void unSelect() {
        selected = false;
    }

    public void addAction(Action a) {
        this.actionsManager.addAction(a);
    }

    public void setDelayBetweenLoops(int millis) {
        delay = millis;
    }

    public abstract void init();
    public abstract void calcPath();
}
