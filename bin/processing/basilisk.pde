/*
    Basilisk is an artificial intelligence program designed to play the google Snake game perfectly.
    Its core is written in Java, however, it also uses Processing to create an optional graphical UI.
*/

import java.util.Arrays;

// use to get your screen configuration, this program only runs on 1920x1080 and 1440x900
static final int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
static final int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
static final int[][] screenModes = { {1920, 1080}, {1440, 900} };

final ScreenCapture cap = new ScreenCapture();

void setup() 
{
    background(255);
    surface.setAlwaysOnTop(true);
    surface.setLocation(100, screenHeight / 2 - height / 2);
    size(300, 300);
    frameRate(15);

    // checks your current screen size against the screen sizes that it can run on
    int[] currentScreenMode = {screenWidth, screenHeight};
    if (Arrays.equals(screenModes[0], currentScreenMode))
    {
        System.out.println("you are running 1920x1080p");
    } else if (Arrays.equals(screenModes[1], currentScreenMode))
    {
        System.out.println("you are running 1440x900p");
    } else {
        throw new RuntimeException("your screen configuration is not supported");
    }

    PImage out = new PImage(cap.getFrame());
    out.resize(width, height);
    image(out, 0, 0);
}

void draw() 
{

}





// Render the apple: 
// strokeWeight(6);
// fill(255);
// stroke(200, 100, 200);
// ellipse(i * 32 + 28 + 16, j * 32 + 95 + 16, 32, 32);

// Render the snakeParts:
// strokeWeight(6);
// fill(255);
// stroke(100, 100, 200);
// rect(i * 32 + 28, j * 32 + 95, 32, 32);

// Render the snakeHead:
// fill(0);
// noStroke();
// rect(28 + snakeHead.x * 32, 95 + snakeHead.y * 32, 32, 32);
// if (settings.debug) {
//     println("snake is located in col " + snakeHead.x + ", row " + snakeHead.y);
//     println("apple is located in col " + applePos.x + ", row " + applePos.y);
//     println("prev apple is @ col " + previousApplePos.x + ", row " + previousApplePos.y);
// }