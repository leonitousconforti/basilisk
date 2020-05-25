# Basilisk

Basilisk is an artificial intelligence program designed to play the Google [Snake](https://www.google.com/search?q=play%20snake) game. Its core functionality and algorithms are written in Java, however, it also uses [Processing](https://processing.org/) under the hood to help create the GUI to display stats to the user.

Basilisk works by taking a screenshot of the snake game, finding where the apple is located and snake's location, passing that information to which ever path finding algorithm is running the show, getting the next action queued from the algorithm, and performing that action with key presses. Basilisk is able to maintain executing this main loop 60/sec and the path finding Algorithm thread runs 60/sec.

## Getting Started

See the '[releases](https://github.com/leonitousconforti/basilisk/releases)' tab to download a 'double clickable' program you can run on your system. There are pre-built packages for

* Macosx
* Windows32
* Windows64
* Linux64
* Linux32
* Linux arm

## Prerequisites and Running

All pre-build packages you can download include the necessary java library dependencies and the java runtime, simply run the 'double clickable'. If you are building the project from source (see: [Building from Source](#building-from-source)) you must have a valid jre and jdk installed on your system.

**_If you are on a system where you do not have elevated privileges (particularity on macosx), the AI will not be able to inject keystrokes to chrome. The alternative method is to open the developer tools in chrome, and open a websocket connection to the AI. Chrome has the permissions required to presses keys for element in its own window and this will allow the AI to inject keystrokes without needing elevated permission. see [Opening a Websocket Connection](./Opening%20a%20Websocket%20Connection.md) for more info_**

## Building From Source

This project targets java v1.8 and to build, you **must** have java jdk and jre v1.8 installed. You might be able to target newer versions of java by modifying the [.classpath](./.classpath) file, however, no guarantees this project will work with java > v1.8. All other java dependencies are included in ```/src/processing/``` and ```/src/websockets/```. To build:

1. Download and unzip this archive
2. ```cd /location/where/you/saved/the/download/``` in a terminal
3. ```./bin/build.sh``` to build

Compiled sources and the .jar can be found in ```/bin/compiler-out/``` and built apps can be found in ```/bin/'platform'/```

## Road-map

My goal for this project is to have it play a perfect game of snake, which the [A*](./src/basilisk/algorithms/A_StarSearch.java) path finding and [Random movement](./src/basilisk/algorithms/Random.java) algorithms have yet to achieve. Thoughts for other algorithms I might implement in the future include:

* [hamiltonian path](https://en.wikipedia.org/wiki/Hamiltonian_path) - I have no doubts that this will be able to play perfectly, but to me it is not the AI I really want to implement because it is very rigid. It will just follow a preprogrammed path and not really go for the apple.
* [Breadth-first search](https://en.wikipedia.org/wiki/Breadth-first_search)
* [Dijkstra](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm) - I don't think this will beat out A* but it would be cool to see how it works
* [NEAT genetic algorithm](https://en.wikipedia.org/wiki/Neuroevolution_of_augmenting_topologies) - If im feeling bold

This project is still under development as of 3/26/20.

## Contributing

Contributions are very much welcomed, this project is under constant development and I am looking for more path-finding algorithms and general improvements in the code-base. If you make a modification please submit a pull request and we can see it and review it.

## Authors

* **Leo Conforti** - [leonitousconforti](https://github.com/leonitousconforti/)
