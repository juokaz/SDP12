package main.gui;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public abstract class AbstractSimulatedObject {
	/**
	 * Name of the object
	 */
	protected String name;
	
	/**
	 * x and y positions of the object
	 */
	protected double xPos;
	protected double yPos;
	protected double theta;
	
	/**
	 * width and height of the object
	 */
	protected int width;
	protected int height;
	
	/**
	 * Is the object enabled?
	 */
	protected boolean isEnabled;
	
	/**
	 * Moving objects have an image
	 */
	protected BufferedImage image;
	
	/*
	 * Remapping constants
	 */
	protected final double xOffset = 10;
	protected final double xRatio = 1.3;
	protected final double yOffset = 120;
	protected final double yRatio = 1.1;
	
	/**
	 * Load an image from a file 
	 * 	set it as the image for the object
	 * 	and use its dimensions to set
	 * 	the width and the height of the object
	 * 
	 * @param file
	 */
	protected void loadAndSetImage(String file) {
		try {
			image = ImageIO.read(AbstractSimulatedObject.class.getResource(file));
			setWidth(image.getWidth());
			setHeight(image.getHeight());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Enable and disable object
	 */
	public void toggleEnabled() {
		if(isEnabled) {
			isEnabled = false;
		} else {
			isEnabled = true;
		}
	}

	/**
	 * Default draw method for moving objects
	 * 	xPos and yPos are defined in the centre of the object
	 * 
	 * Subclasses to override this method if they handle drawing differently
	 * 
	 * @param g2d
	 */
	public void draw(Graphics2D g2d) {
		// Draw object by rotating it theta degrees around its center and translating it
		AffineTransform xform = new AffineTransform();
		xform.rotate(getTheta(), getXPos(), getYPos());
		xform.translate(getXPos() - getWidth()/2, getYPos() - getHeight()/2);
		
		g2d.drawImage(getImage(), xform, null);
	}
	
	/**
	 * Get the shape of the rotated rectangular object by taking
	 * 	the bounding rectangle of the image and rotating it
	 * 	at the appropriate degree
	 * 
	 * @return
	 */
	public Shape getShape() {
		Shape robotShape = new Rectangle2D.Double(getXPos() - getWidth()/2, 
												  getYPos() - getHeight()/2,
												  getWidth(),
												  getHeight());
		AffineTransform xform = new AffineTransform();
		xform.rotate(getTheta(), getXPos(), getYPos());
		
		return xform.createTransformedShape(robotShape);
	}
	
	/**
	 * Get corners of a shape object
	 * 
	 * @return an ArrayList of Point2D objects
	 */
	public ArrayList<Point2D> getCorners() {
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		Shape objectShape = getShape();
		PathIterator pathIterator = objectShape.getPathIterator(null);
		
        while (!pathIterator.isDone()) {
            float[] pts = new float[6];
            switch ( pathIterator.currentSegment(pts) ) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    points.add(new Point2D.Double(pts[0], pts[1]));
                    break;
            }
            pathIterator.next();
        }
        
        return points;
	}
	
	/**
	 * Get the lines that represents each 
	 * 	side of the shape of the object
	 * 
	 * @return
	 */
	public ArrayList<Line2D> getShapeSides() {
		ArrayList<Point2D> corners = getCorners();
		ArrayList<Line2D> shapeSides = new ArrayList<Line2D>();
		
		for(int i = 1; i < corners.size(); i++) {
			Line2D side = new Line2D.Double(corners.get(i-1), corners.get(i));
			shapeSides.add(side);
		}
		
		// Get the line connecting the last point with the first
		shapeSides.add(new Line2D.Double(corners.get(corners.size() - 1), corners.get(0)));
		
		return shapeSides;
	}
	
	/*
	 * GETTERS AND SETTERS
	 */
	
	public double getXPos() { return xPos; }
	public double getYPos() { return yPos; }
	public double getTheta() { return theta; }
	public double getThetaDegrees() { return Math.toDegrees(theta); }
	public BufferedImage getImage() { return this.image; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public String getName() { return name; }
	public double getXPosRemapped() { return xPos/xRatio - xOffset; }
	public double getYPosRemapped() { return yPos/yRatio - yOffset; }
	
	public void setXPos(double xPos) { this.xPos = xPos; }
	public void setYPos(double yPos) { this.yPos = yPos; }
	public void setTheta(double theta) { this.theta = theta; }
	public void setThetaDegrees(double theta) { this.theta = Math.toRadians(theta); }
	public void setWidth(int width) { this.width = width; }
	public void setHeight(int height) {	this.height = height; }	
	public void setName(String name) { this.name = name; }
	public void setXPosRemapped(double xPos) { this.xPos = xPos*xRatio + xOffset; }
	public void setYPosRemapped(double yPos) { this.yPos = yPos*yRatio + yOffset; }
	
	/*
	 * Extra methods used to remap an x or y position using the constants
	 */
	public double remapXPos(double xPos) { return xPos*xRatio + xOffset; }
	public double remapYPos(double yPos) { return yPos*yRatio + yOffset; }
}
