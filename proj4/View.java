// Name: Shirley Lin
// Date: 10/3/25
// Assignment: Project 4 - Inheritance & Polymorphism

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import javax.swing.JButton;
import java.awt.Color;
import java.util.ArrayList;

public class View extends JPanel
{
	public static final int EDIT_BOX_WIDTH = 100;
	public static final int EDIT_BOX_HEIGHT = 100;

	private Model model;
	private int currentRoomX;
	private int currentRoomY;

	public View(Controller c, Model m)
	{
		this.model = m;
		c.setView(this);
	}

	// Loads the specific image into memory
	public static BufferedImage loadImage(String filename) {
		BufferedImage image = null; // Image must be initialized

		try {
			image = ImageIO.read(new File(filename));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}

		// System.out.println("Successfully loaded " + filename + "!");
		return image;
	}

	public void paintComponent(Graphics g) {
		g.setColor(new Color(72, 152, 72));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		// Draw all the Sprites stored in the array list
		for(int i = 0; i < model.getSpritesSize(); i++) {
			Sprite s = model.getSprite(i);
			s.drawYourself(g, currentRoomX, currentRoomY);
		}

		// Calls a function in Model to check if Link has crossed any of the room boundaries, and updates the camera view
		int direction = model.crossBoundary(currentRoomX, currentRoomY);
		switch(direction) {
			case 1: // Move camera right
				currentRoomX += Game.WINDOW_WIDTH;
				break;
			case 2: // Move camera left
				currentRoomX -= Game.WINDOW_WIDTH;
				break;
			case 3: // Move camera up
				currentRoomY -= Game.WINDOW_HEIGHT;
				break;
			case 4: // Move camera down
				currentRoomY += Game.WINDOW_HEIGHT;
				break;
			default: // Do nothing if Link has not crossed any of the boundaries
				break;
		}

		// Displays current map item being drawn if editMode is on
		if(Controller.getEditMode()) {
			// Changes background color to either green or red if items are being added or removed respectively
			if(Controller.getaddMapItem())
				g.setColor(new Color(56, 186, 20));
			else
				g.setColor(new Color(166, 30, 20));

			// Displays the item currently being added/removed
			g.fillRect(0, 0, EDIT_BOX_WIDTH, EDIT_BOX_HEIGHT);
			Sprite s = model.getItemCanAdd();
			s.drawYourself(g, 0, 0);
		}
	}

	public int getCurrentRoomX() {
		return currentRoomX;
	}

	public int getCurrentRoomY() {
		return currentRoomY;
	}
}
