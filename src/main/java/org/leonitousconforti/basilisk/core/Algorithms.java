package org.leonitousconforti.basilisk.core;

import java.awt.Point;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.leonitousconforti.basilisk.algorithms.A_StarSearch;
import org.leonitousconforti.basilisk.algorithms.AlgorithmBase;
import org.leonitousconforti.basilisk.algorithms.HamiltonianPath;
import org.leonitousconforti.basilisk.algorithms.RandomMovement;

/**
 * Handles managing the different algorithm types, running them, and executing
 * their desired actions.
 */
@SuppressWarnings("checkstyle:MemberName")
public class Algorithms {
    // The name of the current algorithm running
    private AlgorithmBase algorithmRunning;
    // Action manager for the algorithm
    private final ActionsManager actionsManager;

    // All the programed algorithms
    private final AlgorithmBase randomMovement;
    private final AlgorithmBase a_starSearch;
    private final AlgorithmBase hamiltonianPath;

    // Mapping the algorithms to threads
    private final Map<AlgorithmBase, Thread> algorithmThreadMap;

    /**
     * Handles managing the different algorithm types, running them, and executing
     * their desired actions.
     */
    public Algorithms() {
        // Initialize components
        actionsManager = new ActionsManager();
        algorithmThreadMap = new HashMap<AlgorithmBase, Thread>();

        // Construct algorithms
        randomMovement = new RandomMovement();
        a_starSearch = new A_StarSearch();
        hamiltonianPath = new HamiltonianPath();

        // Initialize ALgorithms
        initializeAlgorithm(randomMovement);
        initializeAlgorithm(a_starSearch);
        initializeAlgorithm(hamiltonianPath);

        // Setup an algorithm if there isn't one running
        if (getRunningAlgorithm() == null) {
            int rnd = new Random().nextInt(getAllLoadedAlgorithms().length);
            setRunningAlgorithm(getAllLoadedAlgorithms()[rnd]);
        }
    }

    /**
     * Initializes an algorithm to be ready to run. It provides the algorithm access
     * to the actions manager and creates a new thread for the algorithms to run in.
     * The thread is started and put into a map with the algorithm as the key
     *
     * @param algorithmToInitialize the algorithm to initialize
     */
    private void initializeAlgorithm(AlgorithmBase algorithmToInitialize) {
        // Setup the actions manager
        algorithmToInitialize.setActionsManager(actionsManager);

        // Add it to the hashmap with a tread
        algorithmThreadMap.put(algorithmToInitialize,
                new Thread(null, algorithmToInitialize, algorithmToInitialize.getName()));

        // Start the newly created thread
        algorithmThreadMap.get(algorithmToInitialize).start();
    }

    /**
     * Get the names of all the loaded algorithms. Just returns a list of the names,
     * not the algorithms themselves. To get a particular algorithm that has been
     * loaded and initialized, use the {@link #getAlgorithm(String) getAlgorithm}
     * method with the name of the algorithm
     *
     * @return an array of all the loaded algorithms by name
     */
    public String[] getAllLoadedAlgorithms() {
        // Get all the keys, which are the algorithms themselves, of the hashmap and
        // convert it to an array
        AlgorithmBase[] keySet = algorithmThreadMap.keySet().toArray(new AlgorithmBase[algorithmThreadMap.size()]);

        // Get all the names of the algorithms from the key set
        return Arrays.stream(keySet).map(AlgorithmBase::getName).toArray(size -> new String[keySet.length]);
    }

    /**
     * Set the algorithm to be ran with each iteration of information. This method
     * ensures that with each batch of new information, the selected algorithm to be
     * ran gets that new information to make decisions. This method will prevent all
     * other algorithms form running in the background when they are not selected.
     * To run a specific algorithm just once, use the
     * {@link #run(AlgorithmBase, Point, ArrayList, Point) run} method and provide
     * the specific algorithm
     *
     * @param algorithmName the name of the algorithm to set as selected
     */
    public void setRunningAlgorithm(String algorithmName) {
        // Get the loaded algorithms
        String[] loadedAlgorithms = getAllLoadedAlgorithms();

        // check if it is loaded. If it is loaded, set it as selected
        if (Arrays.asList(loadedAlgorithms).contains(algorithmName)) {
            algorithmRunning = getAlgorithm(algorithmName);
            algorithmRunning.setSelected();
        }

        // Get all the keys, which are the algorithms themselves, of the hashmap and
        // convert it to an array
        AlgorithmBase[] keySet = algorithmThreadMap.keySet().toArray(new AlgorithmBase[algorithmThreadMap.size()]);

        // Unselect all other loaded algorithms from running in the background
        Arrays.stream(keySet).filter(a -> a.getName() != algorithmName).forEach(a -> a.unSelect());
    }

    /**
     * Get the algorithm with the specified name. If the algorithm has not been
     * loaded with the {@link #loadOutSourcedAlgorithm(String)
     * loadOutSourcedAlgorithm} method or has not been initialized with the
     * {@link #initializeAlgorithm(AlgorithmBase) initializeAlgorithm} method, then
     * null will be returned.
     *
     * @param algorithmName the name of the desired algorithm
     * @return the algorithm if found or null if not loaded or not found
     */
    public AlgorithmBase getAlgorithm(String algorithmName) {
        // Get all the keys, which are the algorithms themselves, of the hashmap and
        // convert it to an array
        AlgorithmBase[] keySet = algorithmThreadMap.keySet().toArray(new AlgorithmBase[algorithmThreadMap.size()]);

        // Get the algorithm with the name
        return Arrays.stream(keySet).filter(a -> a.getName() == algorithmName).findAny().orElse(null);
    }

    /**
     * Runs the provided algorithm despite which algorithm is set as the running
     * algorithm. The information about the snake and apple will be passed to the
     * algorithm and then the algorithm's code will be ran once
     *
     * @param algorithm  the algorithm to run
     * @param snakeHead  the position of the snake head
     * @param snakeParts the positions of the snake parts
     * @param applePos   the position of the apple
     */
    public void run(AlgorithmBase algorithm, Point snakeHead, ArrayList<Point> snakeParts, Point applePos) {
        // Send information to the algorithm if it want to use it
        algorithm.update(snakeHead, snakeParts, applePos);

        // Check to see if it needs this
        if (!algorithm.getName().equals(getRunningAlgorithm().getName())) {
            algorithm.runOnce();
        }
    }

    /**
     * Runs the running algorithm set by calling {@link #setRunningAlgorithm(String)
     * setRunningAlgorithm}. If there is not current algorithm running, then this
     * method can not be used because it does not know what algorithm to run.
     * Instead, use {@link #run(AlgorithmBase, Point, ArrayList, Point) run} and
     * provided a specific algorithm name
     *
     * @param snakeHead  the position of the snake head
     * @param snakeParts the positions of the snake parts
     * @param applePos   the position of the apple
     */
    public void run(Point snakeHead, ArrayList<Point> snakeParts, Point applePos) {
        // If there is no current algorithm running, then this method can not be used
        if (getRunningAlgorithm() == null) {
            return;
        }

        run(algorithmRunning, snakeHead, snakeParts, applePos);
    }

    /**
     * Returns the the current running algorithm set by the
     * {@link #setRunningAlgorithm(String) setRunningAlgorithm} method. This is the
     * algorithm that will be ran when using the
     * {@link #run(Point, ArrayList, Point) run} while not specific algorithm name
     *
     * @return the name of the running algorithm
     */
    public AlgorithmBase getRunningAlgorithm() {
        return algorithmRunning;
    }

    /**
     * Loads an algorithm that is not located inside the project. The file must be a
     * .class file with no package space. The class must extend from
     * {@link org.leonitousconforti.basilisk.algorithms.AlgorithmBase AlgorithmBase}
     * and must implement the abstract methods
     *
     * @param absoluteFilePath the location of the file
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     *
     * @see https://stackoverflow.com/questions/2946338/how-do-i-programmatically-compile-and-instantiate-a-java-class
     * @see https://stackoverflow.com/questions/41886306/java-compile-inheriting-class-at-runtime
     * @see https://stackoverflow.com/questions/60764/how-to-load-jar-files-dynamically-at-runtime?rq=1
     */
    @SuppressWarnings("NoWhitespaceAfter")
    public void loadOutSourcedAlgorithm(String absoluteFilePath)
            throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Create the file object on the root of the directory containing the class file
        File sourceFile = new File(absoluteFilePath);
        String algorithmNameToLoad = sourceFile.getName().split("\\.")[0];

        // Compile source file.
        // JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // compiler.run(null, null, null, sourceFile.getPath());

        // Get new class loader with folder directory
        URLClassLoader classLoader = URLClassLoader.newInstance(
                new URL[] { sourceFile.getParentFile().toURI().toURL() }, this.getClass().getClassLoader());

        // Attempt to load the class
        Class<?> cls = Class.forName(algorithmNameToLoad, true, classLoader);

        // Instantiate the new class
        Object instance = cls.newInstance();

        // Cast the object and initialize the algorithm with the others
        AlgorithmBase algorithm = (AlgorithmBase) instance;
        initializeAlgorithm(algorithm);
    }

    /**
     * @return the actions manager
     */
    public ActionsManager getActionsManager() {
        return actionsManager;
    }
}
