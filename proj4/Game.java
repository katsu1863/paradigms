// Name: Shirley Lin
// Date: 10/3/25
// Assignment: Project 4 - Inheritance & Polymorphism

import javax.swing.JFrame;
import java.awt.Toolkit;

public class Game extends JFrame
{
	public static final String filename = "map.json";
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;

	private boolean keepGoing;
	private Model model;
	private Controller controller;
	private View view;

	public Game()
	{
		this.model = new Model();
		this.controller = new Controller(model);
		this.view = new View(controller, model);
		this.keepGoing = true;
		view.addMouseListener(controller);
		this.addKeyListener(controller);
		this.setTitle("A4 - Inheritance & Polymorphism");
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setFocusable(true);
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void run() {
		// Load map upon booting up the game
		Json loadObject = Json.load(filename);
		model.unmarshal(loadObject);
		System.out.println("File " + filename + " loaded!");

		do {
			keepGoing = controller.update();
			model.update();
			view.repaint(); // This will indirectly call View.paintComponent()
			Toolkit.getDefaultToolkit().sync(); // Updates screen

			// Go to sleep for 50 milliseconds
			try {
				Thread.sleep(50);
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		} while(keepGoing);

		System.exit(0);
	}

	public static void main(String[] args)
	{
		Game g = new Game();
		g.run();
	}
}
