package basilisk.userPrivilegeTests;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ServerHandshake;

import basilisk.core.Config;

/**
 * Tests the functionality of the java websockets against user privileges
 */
public class userPrivilegeTests_websockets {
    private URI serverUri = null;
    private WebSocketServer server;
    private WebSocketClient testingClient = null;

    /**
     * Tests the functionality of the java websockets against user privileges
     */
    public userPrivilegeTests_websockets(WebSocketServer _server) {
        server = _server;

        // Construct a uri to our websocket server
        try {
            serverUri = new URI( "ws://0.0.0.0:" + server.getAddress().getPort() );
        } catch (URISyntaxException ignore) {}

        // System.out.println(server.getAddress());
        // System.out.println(serverUri);

        // Construct the java test websocket client
        testingClient = new WebSocketClient(serverUri) {

            @Override
            public void onOpen(ServerHandshake arg0) {
                System.out.println("websocket test client opened");
            }
        
            @Override
            public void onMessage(String arg0) {
                if (!arg0.equals("up")) {
                    System.out.println("java websockets is unable to inject key presses on your system");
                    Config.ActionsManagerConfigs.websocketCanInjectKeys = false;
                } else {
                    System.out.println("java websockets is able to inject key presses on your system");
                    Config.ActionsManagerConfigs.websocketCanInjectKeys = true;
                }
            }

            @Override
            public void onError(Exception arg0) {
                System.out.println(arg0);
            }
        
            @Override
            public void onClose(int arg0, String arg1, boolean arg2) {}
        };
        testingClient.connect();
    }

    /**
     * Method that performs the testing
     */
    public void canInjectKeyPresses() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignore) {}
        System.out.println("testing websockets...");
        server.broadcast("up");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {}
        testingClient.close();
        System.out.println("websocket testing done");
    }
}
