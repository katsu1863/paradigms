// Name: Shirley Lin
// Date: 10/3/25
// Assignment: Project 4 - Inheritance & Polymorphism

import java.awt.image.BufferedImage;
import java.awt.Graphics;

public abstract class Sprite {
    protected int x, y;
    protected int w, h;

    public Sprite(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public boolean spriteExists(int mouseX, int mouseY) {
        if((mouseX >= x && mouseX < x + w) && (mouseY >= y && mouseY < y + h)) {
            return true;
        }

        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public boolean isTree() {
        return false;
    }

    public boolean isLink() {
        return false;
    }

    public boolean isTreasureChest() {
        return false;
    }

    public boolean isBoomerang() {
        return false;
    }

    abstract boolean update();
    abstract void drawYourself(Graphics g, int currentRoomX, int currentRoomY);
    abstract Json marshal();
}