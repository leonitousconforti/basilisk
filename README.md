# Basilisk

Basilisk is an artificial intelligence program designed to play the Google [Snake](https://www.google.com/search?q=play%20snake) game. Its core functionality and algorithms are written in Java, however, it also uses [Processing](https://processing.org/) under the hood to help create the GUI to display stats to the user.

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

**_If you are on a system where you do not have elevated privileges (particularity on macosx), the AI will not be able to inject keystrokes to chrome. The alternative method is to open the developer tools in chrome, and open a websocket connection to the AI. Chrome has the permissions required to presses keys for element in its own window and this will allow the AI to inject keystrokes without needing elevated permission. see [Opening a Websocket Connection](#opening-a-websocket-connection) for more info_**

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

## Opening a Websocket Connection

For right now this is going to stay here, will probably move it in the future because it is long and could have its own file.
**_This is only necessary for those who are running without elevated permissions on macosx_**

On macosx, the AI needs extra accessability permissions to use the keyboard. When running the application for the first time without elevated permissions, it should prompt you to grant it accessibility permissions in System Preferences. If you can't or don't want to (why not? lol), the alternative method is to open the chrome developer tools and make a websocket connection to the AI. These step assume that you have chrome open and are at the "play snake" google page (chrome isn't necessary but thats the only browser this has been tested on!).

1. right click anywhere on the page and select 'inspect'
2. select the console tab in the developer window
3. paste the following javascript code into the console
4. hit enter to run

```javascript
var socket = new WebSocket('ws://localhost:61888/');   // Attempts to open a websocket connection to localhost:61888, notice that this connection goes to localhost (A.K.A you computer) and not to an outbound website.

socket.addEventListener('message', function (event) {  // When the socket receives any data, which the AI will be
    console.log('Message from server ', event.data);   // sending, log it to the console.


    // Make the corresponding javascript KeyboardEvent using the information that the AI sent us and then dispatch that event to the current chrome tab (which should be the google snake page). Also there is a little delay because sometimes the AI tries to think to fast and then it goes somewhere before it meant to, but 30ms is enough to cancel that out.
    if (event.data == "up") {
        var e = new KeyboardEvent('keydown', {'keyCode': 38, 'which': 38 }); // up arrow keycode

        setTimeout( function() {
            document.dispatchEvent(e);
        }, 30 );

    }
    else if (event.data == "down") {
        var e = new KeyboardEvent('keydown', {'keyCode': 40, 'which': 40 }); // down arrow keycode

        setTimeout( function() {
            document.dispatchEvent(e);
        }, 30 );

    }
    else if (event.data == "right") {
        var e = new KeyboardEvent('keydown', {'keyCode': 39, 'which': 39 }); // right arrow keycode

        setTimeout( function() {
            document.dispatchEvent(e);
        }, 30 );

    }
    else if (event.data == "left") {

        var e = new KeyboardEvent('keydown', {'keyCode': 37, 'which': 37 }); // left arrow keycode

        setTimeout( function() {
            document.dispatchEvent(e);
        }, 30 );  
    }
});
```

You can read more about javascript keyboardEvent at [Mozilla](https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent)
