package codeclash.model;


public class CreatureStats {

	public final static int MAX_TOTAL_POINTS = 6; 
	
	private int speed, strength, life, foodCapacity, viewRange;
	
	public int getViewRange() {
		return viewRange;
	}

	public void setViewRange(int viewRange) {
		checkValue(viewRange);
		this.viewRange = viewRange;
	}

	
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		checkValue(speed);
		this.speed = speed;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		checkValue(strength);
		this.strength = strength;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		checkValue(life);
		this.life = life;
	}

	public int getFoodCapacity() {
		return foodCapacity;
	}

	public void setFoodCapacity(int foodCapacity) {
		checkValue(foodCapacity);
		this.foodCapacity = foodCapacity;
	}

	public CreatureStats(int speed,int strength,int life,int foodCapacity,int viewRange) {
		this.setSpeed(speed);
		this.setStrength(strength);
		this.setLife(life);
		this.setFoodCapacity(foodCapacity);
		this.setViewRange(viewRange);
		checkPointDistribution();
	}

	private void checkValue(int value) {
		if(value < 1 || value > 3) { throw new IllegalArgumentException("All attribute values must be from 1 to 3!");}
	}

	
	private void checkPointDistribution() {		
		if(getPointSum() > MAX_TOTAL_POINTS) {throw new IllegalArgumentException("Maximum used points (" + MAX_TOTAL_POINTS + ") exceeded!");}
	}
	
	public int getPointSum() {
		return getSpeed() + getStrength() + getFoodCapacity() + getLife() + getViewRange();
	}
}