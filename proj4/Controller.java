// Name: Shirley Lin
// Date: 10/3/25
// Assignment: Project 4 - Inheritance & Polymorphism

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Controller implements ActionListener, MouseListener, KeyListener
{
	private Model model;
	private View view;
	private boolean keepGoing;
	private boolean keyLeft;
	private boolean keyRight;
	private boolean keyUp;
	private boolean keyDown;
	private boolean keySpace;
	private static boolean editMode;
	private static boolean addMapItem;
	
	public Controller(Model m)
	{
		this.model = m;
		this.keepGoing = true;
		this.keySpace = false;
		editMode = false;
		addMapItem = true;
	}
	
	public void actionPerformed(ActionEvent e) {}

	public boolean update()
	{	
		// Save Link's previous position; used for collision fixing
		model.savePreviousPosition();

		// Tells Model that an arrow key is being pressed and to update Link's location and start animating his movements
		if(keyLeft)
			model.moveLink("left");
		if(keyRight)
			model.moveLink("right");
		if(keyUp)
			model.moveLink("up");
		if(keyDown)
			model.moveLink("down");

		// Tells Model that no arrow key is being pressed and to stop animating Link's movement
		if(!keyLeft && !keyRight && !keyUp && !keyDown)
			model.resetLink();

		// The Controller keeps track of whether or not we have quit the program and
		// Returns this value to the Game engine of whether or not to continue the game loop
		return keepGoing;
	}

	public void mousePressed(MouseEvent e) {
		int mouseX = e.getX();
        int mouseY = e.getY();

		// Check if the user is clicking within the edit box
		boolean changeEditItem = false;
		if((mouseX >= 0 && mouseX < View.EDIT_BOX_WIDTH) && (mouseY >= 0 && mouseY < View.EDIT_BOX_HEIGHT))
            changeEditItem = true;

		// Change the current item being added/deleted if the user clicks on the edit box while in edit mode
		if(editMode && changeEditItem)
			model.setItemIndex();
		else if(editMode && addMapItem) {
			// If the user is not clicking within the edit box, then add a new item to the map
			Sprite s = null;
			
			// Checks what item is being added and creates a new instance of that item
			if((model.getItemCanAdd()).isTree()) {
				int x = Math.floorDiv(mouseX + view.getCurrentRoomX(), Tree.TREE_WIDTH) * Tree.TREE_HEIGHT;
				int y = Math.floorDiv(mouseY + view.getCurrentRoomY(), Tree.TREE_WIDTH) * Tree.TREE_HEIGHT;
				s = new Tree(x, y, Tree.TREE_WIDTH, Tree.TREE_HEIGHT);
			}
			else if((model.getItemCanAdd()).isTreasureChest()) {
				int x = mouseX + view.getCurrentRoomX();
				int y = mouseY + view.getCurrentRoomY();
				s = new TreasureChest(x, y, TreasureChest.CHEST_WIDTH, TreasureChest.CHEST_HEIGHT);
			}

			// Add the corresponding item to the map
			if(!model.imageCollision(s) && s != null)
				model.addSprite(s);
		}
		else if(editMode)
			// Delete corresponding item on map
			model.removeSprite(mouseX, mouseY, view.getCurrentRoomX(), view.getCurrentRoomY());
	}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}

	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				keyRight = true;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = true;
				break;
			case KeyEvent.VK_UP:
				keyUp = true;
				break;
			case KeyEvent.VK_DOWN:
				keyDown = true;
				break;
			case KeyEvent.VK_SPACE:
				// Creates a Boomerang from Link's location when the user presses space; only allows one Boomerang per key press
				if(!keySpace) {
					model.createBoomerang();
					keySpace = true;
				}
				break;
			case 'e':
			case 'E':
				editMode = !editMode;
				break;
			case 'a':
			case 'A':
				if(editMode)
					addMapItem = true;
				break;
			case 'r':
			case 'R':
				if(editMode)
					addMapItem = false;
				break;
			case 'c':
			case 'C':
				if(editMode)
					model.clearSprites();
				break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				keyRight = false;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = false;
				break;
			case KeyEvent.VK_UP:
				keyUp = false;
				break;
			case KeyEvent.VK_DOWN:
				keyDown = false;
				break;
			case KeyEvent.VK_SPACE:
				keySpace = false;
				break;
			case KeyEvent.VK_ESCAPE:
				keepGoing = false;
		}

		char c = Character.toLowerCase(e.getKeyChar());
		switch(c) {
			case 'l': // Load map
				Json loadObject = Json.load(Game.filename);
				model.unmarshal(loadObject);
				System.out.println("File " + Game.filename + " loaded!");
				break;
			case 's': // Save map
				Json saveObject = model.marshal();
				saveObject.save(Game.filename);
				System.out.println("Saved " + Game.filename + " file!");
				break;
			case 'q': // Exit
				keepGoing = false;
				break;
		}
	}

	public void keyTyped(KeyEvent e) {}

	public void setView(View v) {
		this.view = v;
	}

	public static boolean getEditMode() {
		return editMode;
	}

	public static boolean getaddMapItem() {
		return addMapItem;
	}
}
