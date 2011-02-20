package sdp12.simulator;

import java.awt.Color;
import java.awt.Graphics2D;

public class Drawable {

	public static final int CIRCLE = 0;
	public static final int LINE = 1;
	public static final int RECTANGLE = 2;
	public static final int LABEL = 3;
	
	private int xPos;
	private int yPos;
	private int drawableWidth;
	private int drawableHeight;
	private int drawableType;
	private String drawableString;
	private Color color;
	
	// DEFAULT VALUES
	public static final int DEFAULT_WIDTH_HEIGHT = 5;
	public static final Color DEFAULT_COLOR = Color.WHITE;
	
	public Drawable(int drawableType, int xPos, int yPos) {
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.drawableType = drawableType;
		
		// Default values
		this.drawableWidth = DEFAULT_WIDTH_HEIGHT;
		this.drawableHeight = DEFAULT_WIDTH_HEIGHT;
		color = Color.WHITE;
		
	}
	
	public Drawable(int drawableType, int xPos, int yPos, Color color) {
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.drawableType = drawableType;
		this.color = color;
		
		// Default values
		this.drawableWidth = DEFAULT_WIDTH_HEIGHT;
		this.drawableHeight = DEFAULT_WIDTH_HEIGHT;
		
	}
	
	public Drawable(int drawableType, String drawableString,
						int xPos, int yPos, Color color) {
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.drawableType = drawableType;
		this.drawableString = drawableString;
		this.color = color;
		
		// Default values
		this.drawableWidth = DEFAULT_WIDTH_HEIGHT;
		this.drawableHeight = DEFAULT_WIDTH_HEIGHT;
		
	}
	
	public Drawable(int drawableType, int xPos, int yPos,
						int drawableWidth, int drawableHeight, Color color) {
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.drawableWidth = drawableWidth;
		this.drawableHeight = drawableHeight;
		this.drawableType = drawableType;
		this.color = color;
		
	}
	
	public void draw(Graphics2D g2d) {
		
		switch(drawableType) {
		
		case CIRCLE: {
		
			g2d.setColor(color);
			g2d.drawOval(xPos, yPos, drawableWidth, drawableHeight);
			break;
		
		}
			
		case LABEL: {
			
			g2d.setColor(color);
			g2d.drawString(drawableString, xPos, yPos);
			break;
			
		}
		
		case RECTANGLE: {
			
			g2d.setColor(color);
			g2d.drawRect(xPos, yPos, drawableWidth, drawableHeight);
			break;
			
		}
		
		/* 
		 * LINE uses drawableWidth and drawableHeight as the position 
		 * 	for the end point of the line
		 */
		case LINE: {
			
			g2d.setColor(color);
			g2d.drawLine(xPos, yPos, drawableWidth, drawableHeight);
			
		}
		
		}
		
	}
	
	public void setLabel(String drawableString) {
		
		this.drawableString = drawableString;
		
	}
	
}
