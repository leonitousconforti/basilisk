package org.leonitousconforti.basilisk.core;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import org.leonitousconforti.basilisk.Config;
import org.leonitousconforti.basilisk.algorithms.shared.Action;

/**
 * Helps inject/simulate key presses so the snake actually moves.
 */
public class ActionsManager {
    // Used for simulating key presses
    private Robot keyer;
    private final WebSocketServer wsServer;

    // The list of actions from the algorithm
    private final ArrayList<Action> actionsQueue;

    private String executorMethod;

    // For statistics
    private int webSocketNumConnectedClients;

    /**
     * Injects/simulates key presses so the snake actually moves. Can run in two
     * different modes, websocket mode where key press are sent to your web browser
     * via web sockets and keyer mode where basilisk simulates real keyboard key
     * presses
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public ActionsManager() {
        // Attempt to initialize the keyer
        try {
            keyer = new Robot();
            keyer.setAutoDelay(25);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        // Attempt to initialize a simple websocket server
        wsServer = new WebSocketServer(new InetSocketAddress(Config.websocketServerPort)) {
            // All these methods have to be here because they are abstract and need an
            // implementation
            @Override
            public void onStart() {
            }

            @Override
            public void onOpen(WebSocket arg0, ClientHandshake arg1) {
                webSocketNumConnectedClients++;
            }

            @Override
            public void onMessage(WebSocket arg0, String arg1) {
            }

            @Override
            public void onError(WebSocket arg0, Exception arg1) {
            }

            @Override
            public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
                webSocketNumConnectedClients--;
            }
        };
        wsServer.start();

        // Initialize the action queue
        actionsQueue = new ArrayList<Action>();
        executorMethod = "keyer";
        webSocketNumConnectedClients = 0;
    }

    /**
     * Get the next action loaded by the algorithm.
     *
     * @return the next action in the queue
     */
    public Action getNextAction() {
        // Prevent index out of bounds exceptions
        if (actionsQueue.size() == 0) {
            return null;
        }

        return actionsQueue.get(0);
    }

    /**
     * Checks that the provided action is ready to be executed.
     *
     * @param action   the action to be checked
     * @param snakePos the position of the snake's head
     * @return if this action is ready to be executed
     */
    public boolean checkAction(Action action, Point snakePos) {
        if (action == null) {
            return false;
        }

        // If the action is an instantaneous action with the point (-1, -1) or the snake
        // position matches that of the action then it is ready
        if ((action.getExecutionPoint().x <= -1) && (action.getExecutionPoint().y <= -1)) {
            return true;
        }
        if ((action.getExecutionPoint().x == snakePos.x) && (action.getExecutionPoint().y == snakePos.y)) {
            return true;
        }

        // Otherwise, it is not ready
        return false;
    }

    /**
     * Executes a desired action with the provided method.
     *
     * @param actionToExecute the desired action to execute
     * @param methodToExecute either 'websocket' or 'keyer'
     */
    @SuppressWarnings("checkstyle:RightCurly")
    public void go(Action actionToExecute, String methodToExecute) {
        // If the execution method is for the java keyer
        if ("keyer".equals(methodToExecute)) {
            int event = -1;

            // Get the right keycode for the keyer
            switch (actionToExecute.getDir()) {
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

            // Wait for the keyer to wake
            keyer.waitForIdle();

            // Dispatch the keyboard event
            keyer.keyPress(event);
            keyer.keyRelease(event);
        }
        // If the execution method is for the javascript websocket
        else if ("websocket".equals(methodToExecute)) {
            // Broadcast the direction to the client
            wsServer.broadcast(actionToExecute.getDir());
        }

        // Remove the action from the queue
        if (actionToExecute.getDeleteOnExecution()) {
            actionsQueue.remove(actionToExecute);
        } else {
            Collections.rotate(actionsQueue, -1);
        }
    }

    /**
     * Executes a desired action with the default method set from the
     * {@link #setDefaultExecutor(String) setDefaultExecutor} method.
     *
     * @param actionToExecute the desired action to execute
     */
    public void go(Action actionToExecute) {
        go(actionToExecute, executorMethod);
    }

    /**
     * Sets the default executor method, either "keyer" or "websocket".
     *
     * @param executor the name of the default executor
     */
    public void setDefaultExecutor(String executor) {
        if ((!"websocket".equals(executor)) && (!"keyer".equals(executor))) {
            return;
        }

        executorMethod = executor;
    }

    /**
     * @return the number of client connected to the websocket server
     */
    public int getNumberOfWebsocketConnections() {
        return webSocketNumConnectedClients;
    }

    /**
     * Adds an action to the actions queue.
     *
     * @param a the action to add
     */
    public void addAction(Action a) {
        actionsQueue.add(a);
    }

    /**
     * Clears all previous actions in the queue.
     */
    public void wipe() {
        actionsQueue.clear();
    }
}
