package codeclash.model;

import java.awt.*;

public class MovableSprite extends Sprite {

	private Point.Float movement;

	public Point.Float getMovement() {
		return movement;
	}

	public void setMovement(Point.Float movement) {
		this.movement = movement;
	}

	public MovableSprite(Point.Float position, Point.Float movement, Image image) {
			super(position, image);
			this.setMovement(movement);
		}

	public void update() {
		Point.Float newPosition = new Point.Float(getPosition().x + getMovement().x, getPosition().y + getMovement().y);
		this.setPosition(newPosition);
	}
}