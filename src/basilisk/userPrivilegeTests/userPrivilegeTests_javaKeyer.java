package basilisk.userPrivilegeTests;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

import basilisk.core.Config;

/**
 * Class for testing java keyer input against user privileges
 */
public class userPrivilegeTests_javaKeyer extends KeyAdapter {
    private long timeStarted = 0;
    
    private long timeAtKeyPress = 0;
    private long timeAtKeyRelease = 0;

    private Robot keyer;
    private int testingKeyCode;

    /**
     * Tests java keyer input against user privileges
     */
    public userPrivilegeTests_javaKeyer(Robot _keyer) {
        keyer = _keyer;
        testingKeyCode = KeyEvent.VK_UP;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // When we detect the key press, start a timer
        if (e.getKeyCode() == testingKeyCode) {
            timeAtKeyPress = System.currentTimeMillis();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // When we detect the key release, end the timer
        if (e.getKeyCode() == testingKeyCode) {
            timeAtKeyRelease = System.currentTimeMillis();
        }
    }

    /**
     * Method that performs the testing
     */
    public void canInjectKeyPresses() {
        timeStarted = System.currentTimeMillis();

        // Simulate the key presses 
        keyer.keyPress(testingKeyCode);
        keyer.keyRelease(testingKeyCode);

        while ( (timeAtKeyRelease - timeAtKeyPress == 0) && (System.currentTimeMillis() - timeStarted < 2000) ) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {}
        }

        // Set the result
        long result = timeAtKeyRelease - timeAtKeyPress;
        if (result == 0) {
            System.out.println("java keyer is unable to inject key presses on your system");
            Config.ActionsManagerConfigs.keyerCanInjectKeys = false;
        } else {
            System.out.println("java keyer is able to inject key presses on your system");
            Config.ActionsManagerConfigs.keyerCanInjectKeys = true;
        }
    }
}
