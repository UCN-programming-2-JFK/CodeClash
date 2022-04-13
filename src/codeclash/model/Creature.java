package codeclash.model;

import java.awt.Image;
import java.awt.Point;

public class Creature extends MovableSprite {
	private float directionInRadians;
	private float maxFoodInbelly, currentFoodInBelly;
	private float maxLife, currentLife;
	private float speed;
	
	public float getMaxFoodInbelly() {
		return maxFoodInbelly;
	}

	public void setMaxFoodInbelly(float maxFoodInbelly) {
		this.maxFoodInbelly = maxFoodInbelly;
	}

	public float getCurrentFoodInBelly() {
		return currentFoodInBelly;
	}

	public void setCurrentFoodInBelly(float currentFoodInBelly) {		
		this.currentFoodInBelly = clamp(currentFoodInBelly, 0, getMaxFoodInbelly());
	}

	public float getMaxLife() {
		return maxLife;
	}

	public void setMaxLife(float maxLife) {
		this.maxLife = maxLife;
	}

	public float getCurrentLife() {
		return currentLife;
	}

	public void setCurrentLife(float currentLife) {
	 this.currentLife =	clamp(currentLife, 0, getMaxFoodInbelly());
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getDirectionInRadians() {
		return directionInRadians;
	}

	public void setDirectionInRadians(float directionInRadians) {
		this.directionInRadians = directionInRadians;
		this.getMovement().x = (float)Math.cos(getDirectionInRadians())*speed;
		this.getMovement().y = (float)Math.sin(getDirectionInRadians())*speed;
	}
	
	public Creature(Point.Float position, Point.Float movement, Image image, int maxFoodInBelly) {
		super(position, movement, image);
		setMaxFoodInbelly(maxFoodInBelly);
		setCurrentFoodInBelly(maxFoodInBelly);
	}
}