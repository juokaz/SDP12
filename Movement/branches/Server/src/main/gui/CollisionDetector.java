package main.gui;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CollisionDetector {
	/**
	 * ArrayList that holds registered collision listeners
	 */
	ArrayList<CollisionListener> collisionListeners;
	
	/**
	 * The constructor initializes the ArrayList 
	 * 	that hold the listeners registered for 
	 * 	collision detection
	 */
	public CollisionDetector() {
		collisionListeners = new ArrayList<CollisionListener>();
	}
	
	/**
	 * Check for each listener whether it contains 
	 * 	the corners of any of the other listeners
	 * 	and send both of them a collision packet
	 * 	with the relevant information about 
	 * 	the collision
	 */
	public void checkCollisions() {
//		if(collisionListeners == null) {
//			return;
//		}
		
		for(CollisionListener currentListener : collisionListeners) {
			for(CollisionListener otherListener : collisionListeners) {
				if(currentListener.equals(otherListener)) {
					continue;
				}
				
				ArrayList<Point2D> otherObjectCorners = otherListener.getCorners();
				for(Point2D corner : otherObjectCorners) {
					if(currentListener.getShape().contains(corner)) {
						Line2D sideOfCollision = 
							getSideCollidedWith(currentListener.getShapeSides(), corner);
						
						/*
						 * Send collision packet to the object
						 * 	whose side touches the corner of the
						 * 	other object 
						 */
						Collision collisionCurrent = new Collision(otherListener, sideOfCollision);
						currentListener.collisionDetected(collisionCurrent);
						
						/*
						 * Send collision packet to the object
						 *  whose corner touches the side of the
						 *  other object
						 */
						Collision collisionOther = new Collision(currentListener, sideOfCollision);
						otherListener.collisionDetected(collisionOther);
					}
				}
			}
		}
	}
	
	/**
	 * Loop through the sides of the shape, calculating the
	 * 	distance from the corner to each, and return
	 * 	the closest side
	 * This is where the collision should have occured
	 * 
	 * @param shapeSides
	 * @param corner
	 * @return
	 */
	private Line2D getSideCollidedWith(ArrayList<Line2D> shapeSides, Point2D corner) {
		Line2D closestSide = shapeSides.get(0);
		
		for(Line2D shapeSide : shapeSides) {
			if(shapeSide.ptLineDist(corner) < closestSide.ptLineDist(corner)) {
				closestSide = shapeSide;
			}
		}
		
		return closestSide;
	}
	
	/**
	 * Add a listener to the list
	 * 
	 * @param collisionListener
	 */
	public void addListener(CollisionListener collisionListener) {
		collisionListeners.add(collisionListener);
	}
}
