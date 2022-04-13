package codeclash.model;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Random;

import codeclash.ui.FiniteStateMachinePanel;

//sprite class which has different behaviors (movement and visual representation) based on its state: searching for food, eating or dead.
public class FiniteStateCreature  extends Creature {
	
	//Private variables ///////////
	Random rnd = new Random();
	//private float speed = .1f;
	//monster image from https://omagerio.itch.io/1200-tiny-monsters-sprites-16x16
	public enum CreatureState {FORAGING, EATING, FIGHTING, TARGET_FOCUSED, DEAD}
	//Fearful creatures always flee, normal creatures fight if attacked until they are at 25% life, Aggressive creatures fight until death
	//public enum CreaturePersonality {FEARFUL, NORMAL, AGGRESSIVE}
	private CreatureState currentState = CreatureState.FORAGING;
	//private CreaturePersonality  personality;
	private Dimension2D terrainBorder;
	private ArrayList<Food> foodPlacements;
	private Food currentFood;
	private Point currentFoodTarget;

	private Image regularImage, chewImage, deadImage, fightingImage, focusedImage;

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
		
		case FORAGING: return regularImage;
		
		case DEAD : return deadImage;
		
		case EATING: 
			//This creates a chewing animation by
			//returning either the normal image or the eating image (open mouth)
			//based on which quarter it is of the current time's second
			long millisecondPartOfCurrentTime = System.currentTimeMillis() % 1000;
			int quarterOfSecond = (int)millisecondPartOfCurrentTime / 250; 
			boolean isQuarterEvenNumber = quarterOfSecond % 2 == 0;
			if(isQuarterEvenNumber) return chewImage;
			else return regularImage;
		
		default: return null;
		}
	}

	public void setImage(Image image) {
		this.regularImage = image;
	}

	
	public Dimension2D getTerrainBorder() {
		return terrainBorder;
	}

	public void setTerrainBorder(Dimension2D terrainBorder) {
		this.terrainBorder = terrainBorder;
	}
	

	
	// Constructor ////////////////////////////
	public FiniteStateCreature(Point.Float position, Point.Float movement, Image image, Image chewImage, Image deadImage, ArrayList<Food> foodPlacements, CreatureState currentState, Dimension2D terrainBorder) {
		super(position, movement, deadImage, 100);
		this.regularImage = image;
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
		g.setColor(Color.white);
		g.setFont(FiniteStateMachinePanel.FONT);
		g.drawString(getCurrentState().toString(), (int)getPosition().x - 45, (int)getPosition().y-20);
		drawLifebar(g);
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
	}

	
	private Color getColorBasedOnPercentage(float currentValue, float maxValue) {
		
		float percentage = currentValue / maxValue;
		if(percentage> .7f) {return Color.green;}
		else if(percentage > .5f) {return Color.yellow;}
		else if(percentage > .3f) {return Color.orange;}
		else {return Color.red;}
	}
}