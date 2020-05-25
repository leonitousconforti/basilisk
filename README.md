# Basilisk

Basilisk is an artificial intelligence program designed to play the [Google Snake](https://www.google.com/search?q=play%20snake) game by using vision processing to watch the snake. Its core functionality and algorithms are written in Java, and, it also uses [Processing](https://processing.org/) under the hood to help create the GUI to display stats to the user.

Basilisk works by taking a screenshot of the snake game, finding where the apple is located and snake's location, passing that information over to which ever path finding algorithm is running the show, getting the next action queued from the algorithm, and performing/injecting that action with key presses. All this happens in real time at 45/fps

## Getting Started

See the [releases](https://github.com/leonitousconforti/basilisk/releases) tab to download a 'double clickable' program you can run on your system. There are pre-built packages for

* Macosx
* Windows32 / Windows64
* Linux32 / Linux64

## Prerequisites and Running

All pre-build packages you can download include the necessary java library dependencies, simply run the 'double clickable'. If you are building the project from source (see: [Building from Source](#building-from-source)) you must have jre and jdk 1.8 installed on your system as well as maven.

**_If you are on a system where you do not have elevated privileges (particularity on macosx), the AI will not be able to inject keystrokes to chrome. The alternative method is to open the developer tools in chrome, and open a websocket connection to the AI. Chrome has the permissions required to presses keys for element in its own window and this will allow the AI to inject keystrokes without needing elevated permission. see [Opening a Websocket Connection](Opening a Websocket Connection.md) for more info_**

## Building From Source

This project targets java v1.8 and to build, you **should** have java jdk and jre v1.8 installed (not sure it it will work with other versions) as well as maven. To build:

1. Download and unzip this archive
2. ```cd /location/where/you/saved/the/download/```
3. ```mvn compile && mvn package``` to build

Compiled sources and the .jar can be found in ```/target```

## Road-map

My goal when starting this project was to learn vision processing. I had many other attempts before this code here, some of which included libraries such as opencv. Ultimately, I decided that solutions with opencv introduced far to many complexities and were slowing my program down, so i scrapped them and wrote the vision processing myself. The snake game is very simple and in the end, you don't need the complex vision processing libraries such as opencv.

The [A*](src/main/java/org/leonitousconforti/basilisk/algorithms/A_StarSearch.java) path finding and [Random movement](src/main/java/org/leonitousconforti/basilisk/algorithms/RandomMovement.java) algorithms have yet to achieve a perfect game. Thoughts for other algorithms I might implement in the future include:

* [hamiltonian path](https://en.wikipedia.org/wiki/Hamiltonian_path) - I have no doubts that this will be able to play perfectly, but to me it is not the AI I really want to implement because it is very rigid. It will just follow a preprogrammed path and not really go for the apple.
* [Breadth-first search](https://en.wikipedia.org/wiki/Breadth-first_search)
* [Dijkstra](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm) - I don't think this will beat out A* but it would be cool to see how it works
* [NEAT genetic algorithm](https://en.wikipedia.org/wiki/Neuroevolution_of_augmenting_topologies) - If im feeling bold

_Upon further inspection of the [Google Snake](https://www.google.com/search?q=play%20snake) game, a perfect run is impossible because the game board is 17 columns by 15 rows. There is not path that will ever be able to pass through all nodes on a grid that size only once, thus making a perfect game impossible._

## Think you can write a better algorithm than me?

Be my guest. The path finding algorithms are written in an OOP way making it easy for you to write one yourself. The requirements for writing your own algorithm are:

1. have jre and jdk 1.8 installed
2. get your hands on the ```AlgorithmBase.class``` file - which can be downloaded from the [releases](https://github.com/leonitousconforti/basilisk/releases) tab or you can get it from compiling the sources yourself.

To write your own algorithm:

1. create a new .java file which the filename of your algorithm's name
2. import that package ```org.leonitousconforti.basilisk.algorithms.AlgorithmBase```
3. Your algorithm should extend ```AlgorithmBase```
4. Override the ```init()``` method
5. Override the ```calcPath()``` method and write your code
7. In a terminal ```javac /path/to/my/algorithm.java --classpath /path/to/AlgorithmBase.class```
8. Use the GUI settings and select the option to ```load an out-sourced algorithm```
9. Select you algorithm to run from the GUI settings

Simple algorithm.java:

```java
import java.awt.Point;
import java.util.ArrayList;

import org.leonitousconforti.basilisk.algorithms.AlgorithmBase;
import org.leonitousconforti.basilisk.algorithms.shared.Action;

/**
 * Path finds using my really cool method.
 */
public class MyReallyCoolAlgorithm extends AlgorithmBase {
    /**
     * Path finds using my really cool method, it will never die!
     */
    public MyReallyCoolAlgorithm() {
        // This is the name of your algorithm and is how the re rest
        // of the program will identify you algorithm, it can be
        // whatever you want but know that this is the name that will
        // show up in places like the GUI settings
        super("My Really Cool Algorithm");
    }

    // Write your code here that only needs to be
    // ran once every time the algorithm is selected
    @Override
    public void init() {
    }

    // This is where you write the code that determines
    // how the snake behaves
    @Override
    public void calcPath() {
        // The first thing we need to do it get the positions of
        // the snake, apple, and snake parts
        Point p1 = getSnakeHead();
        Point p2 = getApplePos();
        ArrayList<Point> p3 = getSnakeParts();

        // Do some calculations
        ...

        // Actions are what the snake should do,
        // they need a direction and an execution point
        Action a = new Action("left", p1);
        addAction(a);

        // Wait until the snake has move to run again
        runOnNextDataChange();
    }
}
```

The algorithm base class includes many helpful methods to make writing algorithms easier:

* getSnakeHead() - returns a point where the snake head is located, (X column, Y row) in grid space
* getApplePos() - returns a point where the apple/thing to eat is located, (X column, Y row) in grid space
* getSnakeParts() - returns an array list of points where the snake parts are, does not include snake head
* addAction(action) - adds an action to the queue, see [Action](src/main/java/org/leonitousconforti/basilisk/algorithms/shared/Action.java) for how actions work
* setDelayBetweenLoops(millis) - sets the time to wait between running you code again
* runOnNextDataChange() - wait to run your code again until the snake has moved

Below is a copy of the [RandomMovement](src/main/java/org/leonitousconforti/basilisk/algorithms/RandomMovement.java) algorithm code:

```java
import java.awt.Point;
import java.util.ArrayList;

import org.leonitousconforti.basilisk.algorithms.AlgorithmBase;
import org.leonitousconforti.basilisk.algorithms.shared.Action;

/**
 * Path finds using random movement.
 */
public class RandomMovement extends AlgorithmBase {
    // All the directions to travel in
    private final String[] randomDirs = { "left", "right", "up", "down" };

    // Places I don't want to go
    private ArrayList<Point> placesThatWillGetMeKilled;

    /**
     * Path finds using random movement that should not get it killed.
     */
    public RandomMovement() {
        super("Random Movement");
        placesThatWillGetMeKilled = new ArrayList<Point>();
    }

    /**
     * Runs once when the algorithm is selected bu the user.
     */
    @Override
    public void init() {
    }

    /**
     * Does the number crunching.
     */
    @Override
    public void calcPath() {
        // Get the list of the snake's body parts
        placesThatWillGetMeKilled = getSnakeParts();

        // Pick a random direction to travel in
        String randomDir = randomDirs[(int) Math.floor(Math.random() * 4)];

        // Make an action for the direction and get where the snake would end up after
        // taking this action
        Action action = new Action(randomDir, new Point(-1, -1));
        Point end = action.getEndingLocation(getSnakeHead());

        // Check to make sure that this action will not get the snake killed
        boolean certainDeathFromAction = (placesThatWillGetMeKilled.contains(end)) || (end.x < 0) || (end.y < 0)
                || (end.x >= 17) || (end.y >= 15);

        // If the action will not get us killed, then add it to the queue
        if (!certainDeathFromAction) {
            addAction(action);

            // Run the next loop next time the data changes. Naturally, the algorithms are
            // going to try to run as fast as they can but that is not necessary for the
            // random algorithm because it only needs to make decisions once every time the
            // snake moves (unless that decision meant certain death, in which case it tries
            // to find another option)
            runOnNextDataChange();
        }
    }
}
```

## Authors

* **Leo Conforti** - [leonitousconforti](https://github.com/leonitousconforti/)
