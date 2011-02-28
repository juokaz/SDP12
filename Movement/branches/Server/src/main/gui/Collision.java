package main.gui;

import java.awt.geom.Line2D;

public class Collision {
	private String collidedObjectType;
	private Line2D collisionSide;
	private double sideAngle;
	
	public Collision(CollisionListener collisionListener, Line2D collisionSide) {
		this.collisionSide = collisionSide;
		this.collidedObjectType = collisionListener.getType();
		this.sideAngle = getSideAngle(collisionSide);
	}
	
	
	private double getSideAngle(Line2D collisionSide) {
		if((collidedObjectType == "Robot")) 
		System.out.println(collisionSide.getX1() + " " + collisionSide.getY1()
							+ " " + collisionSide.getX2() + " " + collisionSide.getY2());
		
		return Math.atan2(collisionSide.getY2() - collisionSide.getY1(),
						  collisionSide.getX2() - collisionSide.getX1());
	}
	
	/*
	 * GETTERS (no need of setters)
	 */
	public String getCollidedObjectType() { return collidedObjectType; }
	public Line2D getcollisionSide() { return collisionSide; }
	public double getSideAngle() { return sideAngle; }
}
