package main.gui;

import java.awt.Color;
import java.awt.Graphics2D;

public class Drawable extends BaseEntity {

	public static final int CIRCLE = 0;
	public static final int LINE = 1;
	public static final int RECTANGLE = 2;
	public static final int LABEL = 3;
	
	private int drawableWidth;
	private int drawableHeight;
	private int drawableType;
	private String drawableString;
	private Color color;
	
	// DEFAULT VALUES
	public static final int DEFAULT_WIDTH_HEIGHT = 5;
	public static final Color DEFAULT_COLOR = Color.WHITE;
	
	/**
	 * Drawable instance
	 * 
	 * @param drawableType
	 * @param xPos
	 * @param yPos
	 */
	public Drawable(int drawableType, int xPos, int yPos) {
		this(drawableType, xPos, yPos, DEFAULT_WIDTH_HEIGHT, DEFAULT_WIDTH_HEIGHT, Color.WHITE);
	}
	
	/**
	 * Drawable instance
	 * 
	 * @param drawableType
	 * @param xPos
	 * @param yPos
	 * @param color
	 */
	public Drawable(int drawableType, int xPos, int yPos, Color color) {
		this(drawableType, xPos, yPos, DEFAULT_WIDTH_HEIGHT, DEFAULT_WIDTH_HEIGHT, color);
	}
	
	/**
	 * Drawable instance
	 * 
	 * @param drawableType
	 * @param drawableString
	 * @param xPos
	 * @param yPos
	 * @param color
	 */
	public Drawable(int drawableType, String drawableString, int xPos, int yPos, Color color) {
		this(drawableType, xPos, yPos, DEFAULT_WIDTH_HEIGHT, DEFAULT_WIDTH_HEIGHT, color, drawableString);
	}
	
	/**
	 * Drawable instance
	 * 
	 * @param drawableType
	 * @param xPos
	 * @param yPos
	 * @param drawableWidth
	 * @param drawableHeight
	 * @param color
	 */
	public Drawable(int drawableType, int xPos, int yPos, int drawableWidth, int drawableHeight, Color color) {
		this(drawableType, xPos, yPos, drawableWidth, drawableHeight, color, null);
	}
	
	/**
	 * Drawable instance
	 * 
	 * @param drawableType
	 * @param x
	 * @param y
	 * @param color
	 * @param remap
	 */
	public Drawable(int drawableType, int x, int y, Color color, boolean remap) {
		this(drawableType, x, y, color);

		if (remap) {
			setXPosRemapped(x);
			setYPosRemapped(y);
		}
	}

	/**
	 * Drawable instance
	 * 
	 * @param drawableType
	 * @param label
	 * @param x
	 * @param y
	 * @param color
	 * @param remap
	 */
	public Drawable(int drawableType, String label, int x, int y, Color color, boolean remap) {
		this(drawableType, label, x, y, color);

		if (remap) {
			setXPosRemapped(x);
			setYPosRemapped(y);
		}
	}
	

	/**
	 * Drawable instance 
	 * 
	 * @param drawableType
	 * @param xPos
	 * @param yPos
	 * @param drawableWidth
	 * @param drawableHeight
	 * @param color
	 * @param remap
	 */
	public Drawable(int drawableType, int xPos, int yPos,
			int drawableWidth, int drawableHeight, Color color, boolean remap) {
		this(drawableType, xPos, yPos, drawableWidth, drawableHeight, color);
		
		if (remap) {
			setXPosRemapped(xPos);
			setYPosRemapped(yPos);
			this.drawableWidth = (int) remapXPos(drawableWidth);
			this.drawableHeight = (int) remapYPos(drawableHeight); 
		}
	}
	
	/**
	 * Drawable instance
	 * 
	 * @param drawableType
	 * @param xPos
	 * @param yPos
	 * @param drawableWidth
	 * @param drawableHeight
	 * @param color
	 * @param drawableString
	 */
	public Drawable(int drawableType, int xPos, int yPos, int drawableWidth, int drawableHeight, Color color, String drawableString) {
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.drawableWidth = drawableWidth;
		this.drawableHeight = drawableHeight;
		this.drawableType = drawableType;
		this.drawableString = drawableString;
		this.color = color;
	}

	/**
	 * Draw drawable
	 * 
	 * @param g2d
	 */
	public void draw(Graphics2D g2d) {
		
		switch(drawableType) {
			case CIRCLE: {
				g2d.setColor(color);
				g2d.drawOval((int) xPos, (int) yPos, drawableWidth, drawableHeight);
				break;
			}
				
			case LABEL: {
				g2d.setColor(color);
				g2d.drawString(drawableString, (int) xPos, (int) yPos);
				break;
			}
			
			case RECTANGLE: {
				g2d.setColor(color);
				g2d.drawRect((int) xPos, (int) yPos, drawableWidth, drawableHeight);
				break;
			}
			
			/* 
			 * LINE uses drawableWidth and drawableHeight as the position 
			 * 	for the end point of the line
			 */
			case LINE: {
				g2d.setColor(color);
				g2d.drawLine((int) xPos, (int) yPos, drawableWidth, drawableHeight);
			}
		}	
	}
}