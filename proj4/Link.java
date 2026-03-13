// Name: Shirley Lin
// Date: 10/3/25
// Assignment: Project 4 - Inheritance & Polymorphism

import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class Link extends Sprite {
    public static final int LINK_WIDTH = 45;
    public static final int LINK_HEIGHT = 45;
    public static final int STARTING_X = 80;
    public static final int STARTING_Y = 80;

    private final int TOTAL_NUM_IMAGES = 44;
    private final int NUM_DIRECTIONS = 4;
    private final int MAX_IMAGES_PER_DIRECTION = 11;

    private int px, py; // Previous x, previous y
    private double speed;
    private int imageNum;
    private int direction; // 0: down, 1: left, 2: right, 3: up
    private boolean updateImage;
    private static BufferedImage images[][] = null;

    public Link() {
        super(STARTING_X, STARTING_Y, LINK_WIDTH, LINK_HEIGHT);
        this.px = this.x;
        this.py = this.y;
        this.imageNum = 0;
        this.direction = 0;
        this.updateImage = false;
        this.speed = 8;

        // Load all the Link images
        if(images == null) {
            images = new BufferedImage[NUM_DIRECTIONS][MAX_IMAGES_PER_DIRECTION];
            int index = 1;
            for(int i = 0; i < NUM_DIRECTIONS; i++) {
                for(int j = 0; j < MAX_IMAGES_PER_DIRECTION; j++) {
                    images[i][j] = View.loadImage("images/link" + index + ".png");
                    index++;
                }
            }
        }

        // System.out.println(toString());
    }

    public Json marshal() {
        return null;
    }

    public boolean update() {
        // Animate Link by cycling through the images
        // Doesn't animate if updateImage is false (no arrow keys are being pressed)
        if(updateImage) {
            if(imageNum < MAX_IMAGES_PER_DIRECTION - 1)
                imageNum++;
            else
                imageNum = 0;
        } else
            // Reset Link to standing image
            imageNum = 0;

        return true;
    }

    public void drawYourself(Graphics g, int currentRoomX, int currentRoomY) {
        g.drawImage(images[direction][imageNum], x - currentRoomX, y - currentRoomY, w, h, null);
    }
    
    public void updateLocation(String direction) {
        switch(direction) {
            case "left":
                this.direction = 1;
                x -= speed;
                break;
            case "right":
                this.direction = 2;
                x += speed;
                break;
            case "up":
                this.direction = 3;
                y -= speed;
                break;
            case "down":
                this.direction = 0;
                y += speed;
                break;
        }
    }

    // Called by Model if Link is colliding with any other sprite to readjust his position to take him out
    public void fixCollision(Sprite s) {
        int spriteLeft = s.getX();
        int spriteRight = s.getX() + s.getW();
        int spriteTop = s.getY();
        int spriteBottom = s.getY() + s.getH(); 

        if((px + w <= spriteLeft) && (x + w >= spriteLeft)) // Link is colliding from the left
            x = spriteLeft - w - 1;
        else if((px >= spriteRight) && (x <= spriteRight)) // Link is colliding from the right
            x = spriteRight + 1;
        if((py + h <= spriteTop) && (y + h >= spriteTop)) // Link is colliding from the top
            y = spriteTop - h - 1;
        else if((py >= spriteBottom) && (y <= spriteBottom)) // Link is colliding from the bottom
            y = spriteBottom + 1;
    }

    public void setUpdateImage(boolean value) {
        updateImage = value;
    }

    public void setPX() {
        px = x;
    }

    public void setPY() {
        py = y;
    }

    public int getDirection() {
        return direction;
    }

    @Override
    public boolean isLink() {
        return true;
    }

    @Override
    public String toString() {
        return "Link (x, y) = (" + x + ", " + y + "), w = " + w + ", h = " + h;
    }
}