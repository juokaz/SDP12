package main.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Wall extends AbstractSimulatedObject {

	private Rectangle wall;
	
	public static final String TOP_WALL = "top";
	public static final String BOTTOM_WALL = "bottom";
	public static final String UPPER_LEFT_WALL = "upperleft";
	public static final String LOWER_LEFT_WALL = "lowerleft";
	public static final String UPPER_RIGHT_WALL = "upperright";
	public static final String LOWER_RIGHT_WALL = "lowerright";
	
	public Wall(Rectangle wall) {
		this.wall = wall;
		setRectangleProperties(wall);
	}
	
	public Wall(Rectangle wall, String name) {
		this(wall);
		this.name = name;
	}
	
	private void setRectangleProperties(Rectangle wall) {
		setXPos(wall.getX());
		setYPos(wall.getY());
		setWidth((int) wall.getWidth());
		setHeight((int) wall.getHeight());
	}
	
	public void draw(Graphics2D g2d) {
		g2d.draw(getWallRectangle());
	}

	/*
	 * GETTERS AND SETTERS
	 */

	public Rectangle getWallRectangle() { return wall; }
}
