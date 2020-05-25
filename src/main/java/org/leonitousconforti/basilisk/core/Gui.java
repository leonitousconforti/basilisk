package org.leonitousconforti.basilisk.core;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import org.leonitousconforti.basilisk.Basilisk;
import org.leonitousconforti.basilisk.Config;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Draws to the gui window.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class Gui extends PApplet {
    // Needs a lot of information, so just give it access to all of it
    private final Basilisk basilisk;

    // References for everything that is going to be drawn onto the screen
    private Point snakeHead;
    private Point applePos;
    private ArrayList<Point> snakeParts;
    private BufferedImage gameImg;

    // Images to be used
    private PImage settingsIcon;

    /**
     * Makes things interactive for the user.
     *
     * @param basil the basilisk program to get information from
     */
    public Gui(Basilisk basil) {
        basilisk = basil;

        // Load settings icon
        try {
            settingsIcon = new PImage(ImageIO.read(basilisk.loadResource("SettingsIcon", ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the elements on the screen to their new positions.
     */
    public void update() {
        applePos = basilisk.getGameElementDetection().getApplePos();
        snakeHead = basilisk.getGameElementDetection().getSnakeHead();
        snakeParts = basilisk.getGameElementDetection().getSnakeParts();
        gameImg = basilisk.getProcessedGameImage();
    }

    // Processing settings method
    @Override
    public void settings() {
        size(300, 300);
    }

    // Processing setup method
    @Override
    public void setup() {
        frameRate(30);
        surface.setAlwaysOnTop(true);
        surface.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) - 350,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - height / 2);
        surface.setTitle("Basilisk AI");
        settingsIcon.resize(300, 300);
    }

    // Processing draw method
    @Override
    public void draw() {
        background(255);
        scale((float) 1 / 2);

        // Update
        this.update();

        // Draw the current screen shot
        if (gameImg != null) {
            image(new PImage(gameImg), 0, 0);
        }

        // Draw settings icon
        if ((mouseX > 75) && (mouseX < width - 75) && (mouseY > 75) && (mouseY < height - 75)) {
            image(settingsIcon, 150, 150);
            fill(0);
            textMode(CENTER);
            textSize(32);
            text("Click for settings", width / 2, height / 2 + 100);
        }

        // Draw where we think the apple is
        strokeWeight(6);
        fill(255);
        stroke(200, 100, 200);
        ellipse(applePos.x * 32 + 28 + 16, applePos.y * 32 + 95 + 16, 32, 32);

        // Draw all the snake parts we found
        strokeWeight(6);
        fill(255);
        stroke(100, 100, 200);

        for (Point p : snakeParts) {
            rect(p.x * 32 + 28, p.y * 32 + 95, 32, 32);
        }

        // Draw where we think the snake head is
        fill(0);
        noStroke();
        rect(28 + snakeHead.x * 32, 95 + snakeHead.y * 32, 32, 32);
    }

    // Processing mouse clicked
    @Override
    public void mouseClicked() {
        if ((mouseX > 75) && (mouseX < width - 75) && (mouseY > 75) && (mouseY < height - 75)) {
            Config.showSettingsMenu = true;
        }
    }
}
