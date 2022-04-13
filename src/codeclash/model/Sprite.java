package codeclash.model;

import java.awt.*;

//simple sprite class for storing an image at a location and drawing it centered, when asked to

public class Sprite  {
	
	private Point.Float position;
	private Image image;
	
	public int getWidth() {
		return getImage().getWidth(null);
	}

	public int getHeight() {
		return getImage().getHeight(null);
	}
	
	public Point.Float getPosition() {
		return position;
	}

	public void setPosition(Point.Float position) {
		this.position = position;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Sprite(Point.Float position, Image image) {
		this.setPosition(position);
		this.setImage(image);
		
	}
	
	public void draw(Graphics g){
		g.drawImage(getImage(), (int)(getPosition().x - getWidth()/2), (int)(getPosition().y- getHeight()/2), null);
	}
	
	protected int clamp ( int value, int min, int max) {
		return Math.max(Math.min(value, max), min);
	}
	
	protected float clamp ( float value, float min, float max) {
		return Math.max(Math.min(value, max), min);
	}
}