package codeclash.model;

import java.awt.Point;

public class Food extends Point {
	private static final long serialVersionUID = 1L;
	private float remainingFood = MAX_REMAINING_FOOD;
	public static final float MAX_REMAINING_FOOD = 100;

	public float getRemainingFood() {
		return remainingFood;
	}

	public void setRemainingFood(float remainingFood) {
		this.remainingFood = remainingFood;
	}

	public Food(int x, int y) {
		super(x, y);
	}
}