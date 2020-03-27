package basilisk.core;

import java.awt.Point;
import java.awt.Robot;
import java.util.List;
import java.util.ArrayList;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;

import basilisk.algorithms.shared.*;
import basilisk.userPrivilegeTests.*;

// Defines a movement action to be simulated by the keyboard
public class ActionsManager {
    // Java keyer can simulate the keyboard, but we need special permissions on some devices
    // so we might not be able to use it everywhere
    private Robot keyer;

    // Websockets are the alternative to java keyer, a websocket connection can be made through the
    // web-browsers developer tools and can simulate key presses there.
    WebSocketServer WsServer;

    // Which method is being used to inject key strokes
    boolean useKeyer, useWebsocket;

    // A list of the current actions dictated by the selected algorithm
    private ArrayList<Action> actions;
    private Point prevSnakePos;

    /**
     * All the ways one can inject the keystrokes to allow the AI to play snake!
     */
    public ActionsManager() {
        prevSnakePos = new Point(-1, -1);

        // Initialize the java Robot to inject key presses
        try {
            keyer = new Robot();
            keyer.setAutoDelay(25);
        } catch (AWTException e) {
            System.out.println("Could not initialize java keyer: " + e);
        }

        // Initialize the websocket server
        WsServer = new WebSocketServer( new InetSocketAddress( Config.ActionsManagerConfigs.websocketConnectionPort ) ) {
            @Override
            public void onStart() { 
                System.out.println("websocket server started");
            }

            @Override
            public void onOpen(WebSocket arg0, ClientHandshake arg1) {
                System.out.println("websocket opened!");
            }

            @Override
            public void onMessage(WebSocket arg0, String arg1) { }

            @Override
            public void onError(WebSocket arg0, Exception arg1) { }

            @Override
            public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) { }
        };
        WsServer.start();

        // Set variables
        useKeyer = false;
        useWebsocket = true;
        actions = new ArrayList<Action>();

        // Test if either methods are invalid (ie. because of user account privileges)
        // testForWebsocketInput();
        // testForKeyerInput();
    }

    /**
     * Adds and action to the list
     * @param action the action to add
     */
    public void addAction(Action action) {
        actions.add(action);
    }

    /**
     * Returns the next action to be executed as determined by the selected algorithm
     * @return actions[0]
     */
    public Action getNextAction() {
        if (actions.size() == 0) {
            return null;
        }

        return actions.get(0);
    }

    /**
     * Checks that an action is ready to be executed
     * 
     * @param action the action to check
     * @param snakePos the current position of the snake
     * @return true if ready to be executed, false if need to wait
     */
    public boolean checkAction(Action action, Point snakePos) {
        if (action == null) {
            return false;
        }

        if ((action.executePoint.x == -1) && (action.executePoint.y == -1)) {
            return true;
        }

        // If the snake is in the execution point position return true
        if ( (action.executePoint.x == snakePos.x) && (action.executePoint.y == snakePos.y) ) {
            return true;
        } 

        // Otherwise, the AI needs to wait until the snake is at the execution point
        return false;
    }

    /**
     * Attempt to get an action that can be performed where the snake is right now
     * @param snakeHead the position of the snake's head
     */
    @SuppressWarnings("unchecked")
    public Action findActionForPosition(Point snakeHead) {
        if ( (snakeHead.x == prevSnakePos.x) && (snakeHead.y == prevSnakePos.y) ) {
            return null;
        }

        Action a = null;
        for (Action available : (ArrayList<Action>) actions.clone()) {
            if ((available.executePoint.x == snakeHead.x) && (available.executePoint.y == snakeHead.y)) {
                a = available;
                break;
            }
        }

        if (a != null) {
            int index = actions.indexOf(a);

            for (int i = index - 1; i >= 0; i--) {
                actions.remove(i);
            }
        }

        prevSnakePos.x = snakeHead.x;
        prevSnakePos.y = snakeHead.y;

        return a;
    }

    /**
     * Removes an action from the Que
     * @param action the action to remove
     */
    public void removeAction(Action action) {
        this.actions.remove(action);
    }

    /**
     * Performs an action
     * @param action the action to execute
     */
    public void go(Action action) {
        if (action == null) {
            return;
        }

        //System.out.println("going " + action.dir + " @ (" + action.executePoint.x + ", " + action.executePoint.y + ")");

        // If the java keyer is selected, figure out the KeyEvent to inject
        if (useKeyer) {
            int event = -1;

            switch( action.dir ) {
                case "left":
                    event = KeyEvent.VK_LEFT;
                    break;
                case "right":
                    event = KeyEvent.VK_RIGHT;
                    break;
                case "up":
                    event = KeyEvent.VK_UP;
                    break;
                case "down":
                    event = KeyEvent.VK_DOWN;
                    break;
                default:
                    break;
            }

            // Wait for the keyer to response
            keyer.waitForIdle();

            // Inject the key strokes
            keyer.keyPress(event);
            keyer.keyRelease(event);

            actions.remove(action);
            return;
        } 
        
        // If the websocket method is selected
        else if (useWebsocket) {
            WsServer.broadcast(action.dir);

            actions.remove(action);
            return;
        } 

        System.out.println("No method has been selected to inject key strokes");
    }

    /**
     * Wipes the list
     */
    public void wipe() {
        this.actions.clear();
    }

    /**
     * Tests the functionality of the websocket implementation against user privileges
     */
    private final void testForWebsocketInput() {
        userPrivilegeTests_websockets websocketsKeyTests = new userPrivilegeTests_websockets(WsServer);
        websocketsKeyTests.canInjectKeyPresses();
    }

    /**
     * Tests the functionality of the java keyer against user privileges
     * testingJavaKeyer Has to be in its own class to take advantages of java.awt.event.KeyAdapter
     */
    private final void testForKeyerInput() {
        userPrivilegeTests_javaKeyer javaKeyerTests = new userPrivilegeTests_javaKeyer(keyer);
        javaKeyerTests.canInjectKeyPresses();
    }

    /**
     * Get a list of the current actions
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Action> getActions() {
        return (ArrayList<Action>) this.actions.clone();
    }
}



// var socket = new WebSocket('ws://localhost:61888/');
// socket.addEventListener('message', function (event) {
//     console.log('Message from server ', event.data);

//     if (event.data == "up") { 
//         var e = new KeyboardEvent('keydown', {'keyCode': 38, 'which': 38 });
//         document.dispatchEvent(e);
//     } else if (event.data == "down") {
//         var e = new KeyboardEvent('keydown', {'keyCode': 40, 'which': 40 });
//         document.dispatchEvent(e);
//     } else if (event.data == "right") { 
//         var e = new KeyboardEvent('keydown', {'keyCode': 39, 'which': 39 });
//         document.dispatchEvent(e);
//     } else if (event.data == "left") { 
//         var e = new KeyboardEvent('keydown', {'keyCode': 37, 'which': 37 });
//         document.dispatchEvent(e);
//     }
// });
