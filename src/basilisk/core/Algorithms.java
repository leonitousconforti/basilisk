package basilisk.core;

import basilisk.algorithms.*;

// Manages handling the different algorithm types, running them, and executing their desired actions
public class Algorithms {
    // The name of the current algorithm running
    private AlgorithmBase algorithmRunning = null;
    // ALl the algorithms programmed in
    private final String[] algorithmNames = {"Random-Movement", "A-star", "Hamiltonian"};
    // Action manager for the algorithm
    private ActionsManager actions;

    // All the programed algorithms
    Random random_movement;
    A_StarSearch a_starSearch;
    HamiltonianPath hamiltonianPath;

    // Threads for the algorithms to run in
    private Thread random_movementThread;
    private Thread a_starSearchThread;
    private Thread hamiltonianPathThread;

    // Add all objects to and array in the same order for easier correlation
    private AlgorithmBase[] algorithms;
    private Thread[] algorithmThreads;
    
    public Algorithms(String name) {
        actions = new ActionsManager();

        random_movement = new Random(actions);
        a_starSearch = new A_StarSearch(actions);
        hamiltonianPath = new HamiltonianPath(actions);
        algorithms = new AlgorithmBase[] { random_movement, a_starSearch, hamiltonianPath };
        algorithmThreads = new Thread[] { random_movementThread, a_starSearchThread, hamiltonianPathThread };

        setAlgorithm(name);

        // choose a random algorithm to set
        if (algorithmRunning == null) {
            // int r = (int) Math.floor(Math.random() * algorithmNames.length) + 0;
            // setAlgorithm( algorithmNames[r] );
            setAlgorithm("Random-Movement");
        }

        // Initialize the thread objects
        for (int i = 0; i < algorithmNames.length; i++) {
            algorithmThreads[i] = new Thread( null, algorithms[i], algorithmNames[i] );
            algorithmThreads[i].start();
        }
    }

    /**
	 * Sets the algorithm to be used by the AI
	 * @param _algorithm the algorithm to use in [A-star, Hamiltonian]
	 */
	@SuppressWarnings("all")
	public final void setAlgorithm(String _algorithm) {

		// Loop over every algorithm and make sure the user provided a valid selection
		for (int i = 0; i < algorithmNames.length; i++) {
			// Set selection upon verification and return
			if ( _algorithm == algorithmNames[i] ) {
                algorithmRunning = algorithms[i];
				return;
			}
		}

		// Otherwise, the loop finished and was not able to verify that the user provided a valid selection
		// So no modification to the algorithm
		algorithmRunning = algorithmRunning;

		// No need to stop the program for this, just return
        // throw new RuntimeException("When using setAlgorithm, you must provided a valid selection");
		return;
    }
    
    /**
	 * Get the requested algorithm
	 * @return algorithm
	 */
    public AlgorithmBase getAlgorithm(String _algorithm) {
		// Loop over every algorithm and make sure the user provided a valid selection
		for (int i = 0; i < algorithmNames.length; i++) {
			// Get selection upon verification and return
			if ( _algorithm == algorithmNames[i] ) {
                return algorithms[i];
			}
		}

		// Otherwise, the loop finished and was not able to verify that the user provided a valid selection
        return null;
    }

	/**
	 * Get the algorithm the AI is using. If no algorithm is running, the random
     * movement algorithm is set and used
	 * @return algorithm
	 */
	public AlgorithmBase getRunningAlgorithm() {
        return this.algorithmRunning;
    }
    
    /**
     * Run an algorithm
     * @param _algo the algorithm to run
     */
    public void run(AlgorithmBase _algo) {
        for (int i = 0; i < algorithms.length; i++) {
            if (_algo == algorithms[i]) {
                if (!algorithms[i].isSelected()) {
                    algorithms[i].setSelected();
                }
            } else {
                algorithms[i].unSelect();
            }
        }
    }

    /**
     * Get the current ActionManager
     * @return actions
     */
    public ActionsManager getActionManager() {
        return actions;
    }
}
