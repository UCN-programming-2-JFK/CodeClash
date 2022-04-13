package codeclash.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

import codeclash.model.FiniteStateCreature;
import codeclash.model.Food;

@SuppressWarnings("serial")
public class FiniteStateMachinePanel extends JPanel {

	//Private variables
	
	static int windowWidth = 1200, windowHeight = 800;
	public static final Font FONT = new Font("Courier New", Font.BOLD, 16);				//font for writing
	public static final Font BIGFONT = new Font("Courier New", Font.BOLD, 24);				//font for writing
	public static final Font TITLEFONT = new Font("Courier New", Font.BOLD, 64);				//font for writing
	//FiniteStateCreature creature;
	java.util.List<FiniteStateCreature> creatures = new ArrayList<FiniteStateCreature>();  
	long lastUpdate;							//the last time an update was performed. Used to calculate time since last update, to smooth animations
	ArrayList<Food> food = new ArrayList<>();		//the list to hold all positions of food
	BufferedImage creatureImage, creatureChewImage, creatureDeadImage, grass;
	Random rnd = new Random();
	int numberOfFood = 25;
	int numberOfCreatures = 4;
	String[] names = new String[] {"Hannah", "Bob", "Anders", "Elsa"};
	Color[] colors = new Color[] {Color.red, Color.cyan, new Color(70, 180, 20), new Color(70,200,200), new Color(190, 130, 230)};
	
	public static void main(String[] args) {
		
		FiniteStateMachinePanel examplePanel = new FiniteStateMachinePanel();//create our panel
		windowWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		windowHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		JFrame frame = new JFrame("Finite State Machine sample");			//create a Frame (window)
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 		// set the X button click to close the window
		frame.getContentPane().add(examplePanel); 					// add our  panel
		examplePanel.setPreferredSize( new Dimension(windowWidth, windowHeight));
		frame.pack();
		examplePanel.createCreatures();
		frame.setVisible(true); 									// show the window>>
		examplePanel.runGameLoop();
	}
	
	public FiniteStateMachinePanel() {
		
	}

	private void createCreatures() {
		creatureImage = loadImage("/graphics/finitestatecreature/finitestatecreature.png");
		creatureChewImage = loadImage("/graphics/finitestatecreature/finitestatecreature_chewimage.png");
		creatureDeadImage = loadImage("/graphics/finitestatecreature/finitestatecreature_deadimage.png");
	 
		for(int creatureCounter = 0; creatureCounter< names.length; creatureCounter++) {
			FiniteStateCreature creature = new FiniteStateCreature(new Point.Float(windowWidth/2-100*(creatureCounter - 2), windowHeight/2), new Point.Float(), creatureImage, creatureChewImage, creatureDeadImage, food, FiniteStateCreature.CreatureState.FORAGING, new Dimension(windowWidth, windowHeight));
			creature.setName(names[creatureCounter]);
			creature.setColor(colors[creatureCounter]);
			creatures.add(creature);	
		}
		
		grass = loadImage("/graphics/grass2.png");
	}

	private void addFoodAtRandomPositions() {
		for (int i = 0; i < numberOfFood; i++) {	
			food.add(new Food(rnd.nextInt(getWidth()), rnd.nextInt(getHeight())));
		}
	}

	public void paint(Graphics g) {
		drawBackground(g);
		drawFood(g);
		for(FiniteStateCreature creature: creatures) {
			creature.draw(g);
		}
	}

	private void drawFood(Graphics g) {
		int size = 10;
		
		for (int i = 0; i < food.size(); i++) {
			float pctFoodLeft = food.get(i).getRemainingFood() / Food.MAX_REMAINING_FOOD;
			int height = (int)(size * pctFoodLeft);
			int halfsize = size/2;
			int left = food.get(i).x - halfsize;
			int top = food.get(i).y - halfsize - height;
			g.setColor(Color.white);
			g.fillRect (left,top , size, height);
			g.setColor(Color.black);
			g.drawRect((int) left,top , size, height);
			g.drawRect(left-1,top-1, size+2, height+2);
		}
	}

	private void drawBackground(Graphics g) {
		int tileSize = 64;
		for(int x = 0; x < getWidth()/tileSize+1; x++) {
			for(int y = 0; y < getHeight()/tileSize+1; y++) {
				int imageIndex = (x+y)%2;
				g.drawImage(grass, x*tileSize, y* tileSize, (x+1)*tileSize, (y+1)* tileSize, imageIndex*tileSize, 0, (imageIndex+1)*tileSize, 64, null);
			}	
		}
		
	}

	//runs the game loop forever
	public void runGameLoop() {

		addFoodAtRandomPositions();
		
		long timePassedSinceLastUpdate = 0;
		lastUpdate = System.currentTimeMillis();
		
		// run as long as the window exists
		while (true) { 
			timePassedSinceLastUpdate = System.currentTimeMillis() - lastUpdate;;
			lastUpdate = System.currentTimeMillis();
			update(timePassedSinceLastUpdate);
			repaint(); // ask for the UI to be redrawn
			waitAShortInterval();
		}
	}
	
	private void update(long timePassedSinceLastUpdate) {
		for(FiniteStateCreature creature: creatures) {
			creature.update(timePassedSinceLastUpdate);
		}

	}

	private void waitAShortInterval() {
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
			
	private BufferedImage loadImage(String imagePathOrUrl)
	    {
		BufferedImage image = null;
		 try {
			 image = ImageIO.read(this.getClass().getResource(imagePathOrUrl));
			} catch (IOException e) {System.out.println(e.getMessage());}
		 return image;
	    }

	public static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    //g.drawString(text, x, y);
	    drawStringWithBorder(g, text, x,y, font, Color.white);
	}
	
	public static void drawStringWithShadow(Graphics g, String text, int x, int y, Font font, Color textColor) {
		Color preColor = g.getColor();
		g.setColor(Color.black);
		g.drawString(text, x-1, y+1);
		g.setColor(textColor);
		g.drawString(text, x, y);
		g.setColor(preColor);
	}
	
	public static void drawStringWithBorder(Graphics g, String text, int x, int y, Font font, Color textColor) {
		Color preColor = g.getColor();
		g.setColor(Color.black);
		for(int deltaX = -1; deltaX < 2; deltaX++) {
			for(int deltaY = -1; deltaY < 2; deltaY++) {
				g.drawString(text, x-deltaX, y+deltaY);		
			}	
		}
		
		g.setColor(textColor);
		g.drawString(text, x, y);
		g.setColor(preColor);
	}
	
	
}