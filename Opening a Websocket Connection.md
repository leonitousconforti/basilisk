# Opening a Websocket Connection

Sometimes, the AI needs extra accessability permissions to use the keyboard. When running the application for the first time without elevated permissions, it should prompt you to grant it accessibility permissions in System Settings. If you can't or don't want to (why not? lol), the alternative method is to open the chrome developer tools and make a websocket connection to the AI. These step assume that you have chrome open and are at the "play snake" google page (chrome isn't necessary but thats the only browser this has been tested on).

1. right click anywhere on the page and select 'inspect'
2. select the console tab in the developer window
3. paste the following javascript code into the console
4. hit enter to run

```javascript
var socket = new WebSocket('ws://localhost:61888/');// Attempts to open a websocket connection to localhost
                                                    // port 61888, notice that this connection goes to
                                                    // localhost (A.K.A you computer) and not to an outbound website

socket.addEventListener('message', function (event) {  // When the socket receives any data, which the AI will be
    console.log('Message from server ', event.data);   // sending, log it to the console.


    // Make the corresponding javascript KeyboardEvent using the information that the AI sent us and then dispatch
    // that event to the current chrome tab (which should be the google snake page). Also there is a little delay
    // because sometimes the AI tries to think to fast and then it goes somewhere before it meant to, but 10ms is
    // enough to cancel that out.
    if (event.data == "up") {
        var e = new KeyboardEvent('keydown', {'keyCode': 38, 'which': 38 }); // up arrow keycode

        setTimeout( function() {
            document.dispatchEvent(e);
        }, 10 );

    }
    else if (event.data == "down") {
        var e = new KeyboardEvent('keydown', {'keyCode': 40, 'which': 40 }); // down arrow keycode

        setTimeout( function() {
            document.dispatchEvent(e);
        }, 10 );

    }
    else if (event.data == "right") {
        var e = new KeyboardEvent('keydown', {'keyCode': 39, 'which': 39 }); // right arrow keycode

        setTimeout( function() {
            document.dispatchEvent(e);
        }, 10 );

    }
    else if (event.data == "left") {

        var e = new KeyboardEvent('keydown', {'keyCode': 37, 'which': 37 }); // left arrow keycode

        setTimeout( function() {
            document.dispatchEvent(e);
        }, 10 );
    }
});
```

You can read more about javascript keyboardEvent at [Mozilla](https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent)
