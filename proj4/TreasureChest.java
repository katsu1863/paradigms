// Name: Shirley Lin
// Date: 10/5/25
// Assignment: Project 4 - Inheritance & Polymorphism

import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class TreasureChest extends Sprite {
    public static final int CHEST_HEIGHT = 35;
    public static final int CHEST_WIDTH = 35;

    public static final int TOTAL_NUM_IMAGES = 2;

    private int imageIndex;
    private int countdown;
    private boolean canCollect;
    private boolean isValid;
    private static BufferedImage images[] = null;

    public TreasureChest(int x, int y, int w, int h) {
        super(x, y, w, h);
        this.imageIndex = 0;
        this.canCollect = false;
        this.isValid = true;

        if(images == null) {
            images = new BufferedImage[TOTAL_NUM_IMAGES];
            images[0] = View.loadImage("images/treasurechest.png");
            images[1] = View.loadImage("images/rupee.png");
        }

        // System.out.println(toString());
    }

    // Unmarshalling constructor
    public TreasureChest(Json ob) {
        super((int)ob.getLong("x"), (int)ob.getLong("y"), (int)ob.getLong("w"), (int)ob.getLong("h"));
        this.imageIndex = 0;
        this.canCollect = false;
        this.isValid = true;

        if(images == null) {
            images = new BufferedImage[TOTAL_NUM_IMAGES];
            images[0] = View.loadImage("images/treasurechest.png");
            images[1] = View.loadImage("images/rupee.png");
        }

        // System.out.println(toString());
    }

    public Json marshal() {
        Json ob = Json.newObject();
        ob.add("x", this.x);
        ob.add("y", this.y);
        ob.add("w", this.w);
        ob.add("h", this.h);

        return ob;
    }

    public boolean update() {
        // Once Link or Boomerang interacts with the treasure chest start countdown
        if(imageIndex == 1) {
            countdown--;
            if(countdown < 0)
                isValid = false;
            else if(countdown < 40)
                canCollect = true;
        }

        return isValid;
    }

    public void drawYourself(Graphics g, int currentRoomX, int currentRoomY) {
        g.drawImage(images[imageIndex], x - currentRoomX, y - currentRoomY, w, h, null);
    }

    public void openChest() {
        if(imageIndex == 0) {
            imageIndex = 1;
            countdown = 45;
        }
    }

    public boolean getCanCollect() {
        return canCollect;
    }

    public void deleteChest() {
        isValid = false;
    }

    @Override
    public boolean isTreasureChest() {
        return true;
    }

    @Override
    public String toString() {
        return "Treasure Chest (x, y) = (" + x + ", " + y + "), w = " + w + ", h = " + h;
    }
}