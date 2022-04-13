package codeclash.model;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import codeclash.ui.FiniteStateMachinePanel;

//sprite class which has different behaviors (movement and visual representation) based on its state: searching for food, eating or dead.
public class FiniteStateCreature  extends Creature {
	
	Random rnd = new Random();
	//monster image from https://omagerio.itch.io/1200-tiny-monsters-sprites-16x16
	public enum CreatureState {FORAGING, EATING, FIGHTING, TARGET_FOCUSED, DEAD}
	private CreatureState currentState = CreatureState.FORAGING;
	private BufferedImage chewImage, deadImage, fightingImage, focusedImage;
	//private CreaturePersonality  personality;
	private Dimension2D terrainBorder;
	private ArrayList<Food> foodPlacements;
	private Food currentFood;
	private Point currentFoodTarget;
	private String name;
	private Color color;
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		generateNewImages();
	}

	private void generateNewImages() {
		this.setImage(dye((BufferedImage)super.getImage(), getColor()));
		this.chewImage = dye(this.chewImage, getColor());
		this.deadImage= dye(this.deadImage, getColor());
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	//setters and getters /////////////////////////////////////
	public CreatureState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(CreatureState currentState) {
		this.currentState = currentState;
		System.out.println(currentState);
	}
	
	public Image getImage() {
		switch (getCurrentState()) {
		
		case FORAGING: return super.getImage();
		
		case DEAD : return deadImage;
		
		case EATING: 
			//This creates a chewing animation 	by
			//returning either the normal image or the eating image (open mouth)
			//based on which quarter it is of the current time's second
			long millisecondPartOfCurrentTime = System.currentTimeMillis() % 1000;
			int quarterOfSecond = (int)millisecondPartOfCurrentTime / 250; 
			boolean isQuarterEvenNumber = quarterOfSecond % 2 == 0;
			if(isQuarterEvenNumber) return chewImage;
			else  return super.getImage();
		
		default: return super.getImage();
		}
	}
	
	public Dimension2D getTerrainBorder() {
		return terrainBorder;
	}

	public void setTerrainBorder(Dimension2D terrainBorder) {
		this.terrainBorder = terrainBorder;
	}
	

	
	// Constructor ////////////////////////////
	public FiniteStateCreature(Point.Float position, Point.Float movement, BufferedImage image, BufferedImage chewImage, BufferedImage deadImage, ArrayList<Food> foodPlacements, CreatureState currentState, Dimension2D terrainBorder) {
		super(position, movement, image, 100);
		this.setCurrentState(CreatureState.FORAGING);
		this.chewImage = chewImage;
		this.deadImage = deadImage;
		this.setCurrentState(currentState);
		this.setTerrainBorder(terrainBorder);
		this.foodPlacements = foodPlacements;
		this.setSpeed(.1f);
		this.setMaxLife(100);
		this.setCurrentLife(getMaxLife());
	}

	// Update and related methods //////////////////////////////
	public void update(double msElapsedSinceLastUpdate) {
		

		this.setCurrentFoodInBelly((float) (getCurrentFoodInBelly()-0.005f * msElapsedSinceLastUpdate));
		
		if(getCurrentFoodInBelly() <= 0) {
			setCurrentLife(getCurrentLife()-.1f);
		}
		else {
			if(getCurrentLife() < getMaxLife() && getCurrentFoodInBelly() > 0) {
				float amountOfFoodToMove = 0.005f * (float)msElapsedSinceLastUpdate;
				this.setCurrentFoodInBelly((float) (getCurrentFoodInBelly()-amountOfFoodToMove));
				this.setCurrentLife((float) (getCurrentLife() + amountOfFoodToMove));
			}
		}
		if(getCurrentLife()<= 0) {
			setCurrentState(CreatureState.DEAD);
		}
		switch (getCurrentState()) {
		case FORAGING:
			wander(msElapsedSinceLastUpdate);	
			break;
		case EATING:
			eat();
			break;
		default:
			break;
		}
	}
//
	//wanders randomly, staying within the borders of the JPanel and eats food that is close enough 
	private void wander(double msElapsedSinceLastUpdate) {
		turnSlightlyRightOrLeftAtRandom();
		Point.Float newPosition = new Point.Float(getPosition().x + getMovement().x * (float)msElapsedSinceLastUpdate, getPosition().y + getMovement().y* (float)msElapsedSinceLastUpdate);
		if(!touchesBorderAtPosition(newPosition)) {
			this.setPosition(newPosition);
		}
		else{
			findNewRandomDirection();
		}
		eatFoodIfCloseEnough();
	}
//
	//this method alters the direction the creature is going slightly either right or left at random 
	private void turnSlightlyRightOrLeftAtRandom() {
		float randomDirectionChangeInRadians = (rnd.nextFloat() * (float)Math.PI / 8) -  (float)Math.PI / 16;
		this.setDirectionInRadians(this.getDirectionInRadians()+randomDirectionChangeInRadians);
	}

	private boolean touchesBorderAtPosition(java.awt.geom.Point2D.Float newPosition) {
		return newPosition.x - getWidth()/2 < 0 || newPosition.x + getWidth()/2 > getTerrainBorder().getWidth()  ||
				newPosition.y - getHeight()/2 < 0 || newPosition.y + getHeight()/2 > getTerrainBorder().getHeight();
		}


	private void findNewRandomDirection() {
		float randomDirectionInRadians = rnd.nextFloat() * (float)Math.PI * 2;
		this.setDirectionInRadians(randomDirectionInRadians);
	}

	private void eatFoodIfCloseEnough() {
		Food closestFoodPlacement = getClosestFood();
		if(closestFoodPlacement != null) {
			Point.Float position = getPosition();
			double distanceToClosestFood = closestFoodPlacement.distance(position);
			if(distanceToClosestFood < this.getWidth()) {
				this.setCurrentState(FiniteStateCreature.CreatureState.EATING);
				this.currentFood = closestFoodPlacement;
			}
		}
	}
//
	private Food getClosestFood(){
		
		double smallestDistanceSoFar = Float.MAX_VALUE;
		Food closestFoodPlacementSoFar = null;
		
		for (int i = 0; i < foodPlacements.size(); i++) {
			Food currentPlacement = foodPlacements.get(i);
			double distanceToCurrentPlacement = currentPlacement.distance(getPosition());
			if(distanceToCurrentPlacement < smallestDistanceSoFar) {
				closestFoodPlacementSoFar = currentPlacement;
				smallestDistanceSoFar = distanceToCurrentPlacement;
			}
		}
		return closestFoodPlacementSoFar;
	}
//	
	private void eat() {
		float amountToEatPerUpdate = 1f;
		if(currentFood != null) {
			this.setCurrentFoodInBelly(getCurrentFoodInBelly() + amountToEatPerUpdate);
			this.currentFood.setRemainingFood(this.currentFood.getRemainingFood()- amountToEatPerUpdate);
			if(this.currentFood.getRemainingFood() <= 0) {
				foodPlacements.remove(this.currentFood);
				setCurrentState(CreatureState.FORAGING);
			}
			
		}
	}
//	
//	// Draw and related methods  //////////////////////
	public void draw(Graphics g){				
		g.drawImage(getImage(), (int)getPosition().x - getWidth()/2, (int)getPosition().y- getHeight()/2, null);
		g.setColor(Color.black);
		Rectangle nameRectangle = new Rectangle((int)getPosition().x - 45, (int)getPosition().y-40, 90, 20);
		Rectangle stateRectangle =	new Rectangle((int)getPosition().x - 45, (int)getPosition().y+40, 90, 20);
		String nameString = "\"" + getName().toUpperCase()  + "\"";
		g.setFont(FiniteStateMachinePanel.BIGFONT);

		FiniteStateMachinePanel.drawCenteredString(g, nameString, nameRectangle, FiniteStateMachinePanel.BIGFONT);
		g.setFont(FiniteStateMachinePanel.FONT);
		FiniteStateMachinePanel.drawCenteredString(g, getCurrentState().toString(), stateRectangle, FiniteStateMachinePanel.FONT);		drawLifebar(g);
		nameRectangle.translate(1, -1);
		stateRectangle.translate(1, -1);
		g.setColor(Color.white);
		g.setFont(FiniteStateMachinePanel.BIGFONT);

		FiniteStateMachinePanel.drawCenteredString(g, nameString, nameRectangle, FiniteStateMachinePanel.BIGFONT);
		g.setFont(FiniteStateMachinePanel.FONT);

		FiniteStateMachinePanel.drawCenteredString(g, getCurrentState().toString(), stateRectangle, FiniteStateMachinePanel.FONT);		drawLifebar(g);
		drawFoodbar(g);
	}
//	
	private void drawLifebar(Graphics g) {
			int width = 30;
			int height = 6;
			int left = (int)getPosition().x - width/2;
			int top = (int)getPosition().y + getHeight() / 2 + 12;
			
			g.setColor(Color.black);
			g.fillRect(left, top, width, height);
			
			g.setColor(getColorBasedOnPercentage(getCurrentLife(), getMaxLife()));
			g.fillRect(left, top, (int) (width * getCurrentLife()/getMaxLife()), height);
			
			g.setColor(Color.black);
			g.drawRect(left, top, width, height);
			g.drawRect(left-1, top-1, width+2, height+2);
		}

	private void drawFoodbar(Graphics g) {
		int width = 30;
		int height = 6;
		int left = (int)getPosition().x - width/2;
		int top = (int)getPosition().y + getHeight() / 2 + 3;
		
		g.setColor(Color.black);
		g.fillRect(left, top, width, height);
		
		g.setColor(getColorBasedOnPercentage(getCurrentFoodInBelly(), getMaxFoodInbelly()));
		g.fillRect(left, top, (int) (width * getCurrentFoodInBelly()/getMaxFoodInbelly()), height);
		
		g.setColor(Color.black);
		g.drawRect(left, top, width, height);
		g.drawRect(left-1, top-1, width+2, height+2);
	}

	
	private Color getColorBasedOnPercentage(float currentValue, float maxValue) {
		
		float percentage = currentValue / maxValue;
		if(percentage> .7f) {return Color.green;}
		else if(percentage > .5f) {return Color.yellow;}
		else if(percentage > .3f) {return Color.orange;}
		else {return Color.red;}
	}
	 private static BufferedImage dye(BufferedImage image, Color color)
	    {
		 // get width and height
	        int width = image.getWidth();
	        int height = image.getHeight();
	  
	        // convert to red image
	        for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	                int p = image.getRGB(x, y);
	                if(p != 0) {
	                	int a = (p >> 24) & 0xff;
	                	int r = (p >> 16) & color.getRed();
	                	int g = (p >> 8) & color.getGreen();
	                	int b =  p & color.getBlue();
	                	
	                	// set new RGB keeping the r
	                	// value same as in original image
	                	// and setting g and b as 0.
	                	p = (a << 24) | (r << 16) | (g << 8) | b;
	                	
	                	image.setRGB(x, y, p);
	                }
	                }
	        }
	        return image;
	    }
	
}