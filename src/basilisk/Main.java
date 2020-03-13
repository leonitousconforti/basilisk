package basilisk;

import processing.core.PApplet;

// The main entry method for java programs
public class Main {
    public static void main(String[] args) {
        // Create a new basilisk program
        Basilisk basilisk = new Basilisk();

        // Initialize the other half of the basilisk program with a Processing window
        // to display the graphical UI for the user.
        String[] processingArgs = { "Basilisk" };
        PApplet.runSketch(processingArgs, basilisk);
    }
}
