package main.gui;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public interface CollisionListener {
	/**
	 * CollisionListeners should have names for comparison reasons
	 * 
	 * @return
	 */
	public String getType();
	
	/**
	 * Get the corners of the shape to check
	 * 	whether they are contained in the shape 
	 * 	of the object we are testing for collisons against
	 * 
	 * @return
	 */
	public ArrayList<Point2D> getCorners();
	
	/**
	 * Return the shape of the object to test for collisions
	 * 
	 * @return
	 */
	public Shape getShape();
	
	/**
	 * Return the lines that connect the corners of the object's shape
	 * Used to check which is the closest line to the corner
	 * 	of the object that has been intersected
	 * 
	 * @return
	 */
	public ArrayList<Line2D> getShapeSides();
	
	/**
	 * Pass a collision object that describes the state of 
	 * 	the collision and use it to handle the specific collisions 
	 * 	of each different object
	 * 
	 * @param collision
	 */
	public void collisionDetected(Collision collision);
}
