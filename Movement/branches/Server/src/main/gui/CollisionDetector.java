package main.gui;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CollisionDetector {
	ArrayList<CollisionListener> collisionListeners;
	
	public CollisionDetector() {
		collisionListeners = new ArrayList<CollisionListener>();
	}
	
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
						Collision collision = new Collision(otherListener, sideOfCollision);
						
						currentListener.collisionDetected(collision);
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
