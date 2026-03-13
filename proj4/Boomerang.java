// Name: Shirley Lin
// Date: 10/5/25
// Assignment: Project 4 - Inheritance & Collision

import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class Boomerang extends Sprite {
    public static final int BOOMERANG_HEIGHT = 20;
    public static final int BOOMERANG_WIDTH = 20;

    private final int TOTAL_NUM_IMAGES = 4;

    private int speed;
    private int direction; // 0: down, 1: left, 2: right, 3: up
    private boolean isValid;
    private int imageIndex;
    private static BufferedImage images[] = null;

    public Boomerang(int x, int y, int direction) {
        super(x, y, BOOMERANG_WIDTH, BOOMERANG_HEIGHT);
        this.speed = 12;
        this.direction = direction;
        this.isValid = true;
        this.imageIndex = 0;

        if(images == null) {
            images = new BufferedImage[TOTAL_NUM_IMAGES];
            for(int i = 0; i < TOTAL_NUM_IMAGES; i++) {
                images[i] = View.loadImage("images/boomerang" + (i + 1) + ".png");
            }
        }

        // System.out.println(toString());
    }

    public Json marshal() {
        return null;
    }

    public boolean update() {
        // Animates the boomerang
        if(imageIndex < TOTAL_NUM_IMAGES - 1)
            imageIndex++;
        else
            imageIndex = 0;

        // Moves the boomerang across the screen in the direction Link is facing
        switch(direction) {
            case 0:
                y += speed;
                break;
            case 1:
                x -= speed;
                break;
            case 2:
                x += speed;
                break;
            case 3:
                y -= speed;
                break;
        }

        return isValid;
    }

    public void drawYourself(Graphics g, int currentRoomX, int currentRoomY) {
        g.drawImage(images[imageIndex], x - currentRoomX, y - currentRoomY, w, h, null);
    }

    public void deleteBoomerang() {
        isValid = false;
    }

    @Override
    public boolean isBoomerang() {
        return true;
    }

    @Override
    public String toString() {
        return "Boomerang (x, y) = (" + x + ", " + y + "), w = " + w + ", h = " + h;
    }
}