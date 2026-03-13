// Name: Shirley Lin
// Date: 10/3/25
// Assignment: Project 4 - Inheritance & Polymorphism

import java.lang.Math;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class Tree extends Sprite {
    public static final int TREE_WIDTH = 75;
    public static final int TREE_HEIGHT = 75;

    private static BufferedImage image = null;

    public Tree(int x, int y, int w, int h) {
        super(x, y, w, h);

        // Load tree image
        if(image == null)
            image = View.loadImage("images/tree.png");

        // System.out.println(toString());
    }

    // Unmarshaling constructor
    public Tree(Json ob) {
        super((int)ob.getLong("x"), (int)ob.getLong("y"), (int)ob.getLong("w"), (int)ob.getLong("h"));

        if(image == null)
            image = View.loadImage("images/tree.png");

        // System.out.println(toString());
    }

    // Marshalls Tree into a Json node
    public Json marshal() {
        Json ob = Json.newObject();
        ob.add("x", this.x);
        ob.add("y", this.y);
        ob.add("w", this.w);
        ob.add("h", this.h);

        return ob;
    }

    public boolean update() {
        return true;
    }

    public void drawYourself(Graphics g, int currentRoomX, int currentRoomY) {
        g.drawImage(image, x - currentRoomX, y - currentRoomY, w, h, null);
    }

    @Override
    public boolean isTree() {
        return true;
    }

    @Override
    public String toString() {
        return "Tree (x, y) = (" + x + ", " + y + "), w = " + w + ", h = " + h;
    }
}