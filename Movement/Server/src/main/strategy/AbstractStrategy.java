package main.strategy;

import java.awt.Color;
import java.util.ArrayList;

import sdp12.simulator.Drawable;
import main.Strategy;
import main.Executor;
import main.data.Point;
import main.data.Robot;

public abstract class AbstractStrategy implements Strategy {
	
	protected final int PITCH_X_MIN = 0;
	protected final int PITCH_Y_MIN = 114;
	protected final int PITCH_X_MAX = 735; // TODO check those
	protected final int PITCH_Y_MAX = 460; // TODO check those

	/**
	 * Drawables field to be used throughout the strategy and passed 
	 * 	to the simulator at the end of updateLocation() if needed
	 */
	ArrayList<Drawable> drawables;
	
	protected Executor executor = null;

	public void setExecutor(Executor executor) {
		this.executor  = executor;
	}
	
	/**
	 * Move robot from robotX and robotY to a Point position in a pitch
	 * 
	 * Point can be a Ball, Pitch or other Robot
	 * 
	 * @param robot
	 * @param point
	 */
	protected void moveToPoint(Robot robot, Point point) {
			
		// find the angle the robot must turn to face the ball
		double dirAngle = Math.atan2(point.getY()-robot.getY(), point.getX()-robot.getX());
		
		// find the distance between the robot and the ball
		double dy = robot.getY() - point.getY();
		double dx = robot.getX() - point.getX();
		double distance = Math.sqrt(dx*dx + dy*dy);
		
		// convert to degrees
		//dirAngle = Math.toDegrees(dirAngle);
		
		int a=1;
		int b=1;
		if(Math.abs(normalizeAngle(dirAngle) - normalizeAngle(robot.getT())) > Math.PI) {
			a=-1;
			b=1;
		} else {
			a=1;
			b=-1;
		}
		// once the robot is facing in direction of the ball, move towards it at
		// a velocity proportional to the distance between them
		if(Math.abs(dirAngle - robot.getT()) % Math.toRadians(360) < Math.toRadians(30)) {
			a = (int) (1*distance)/35;
			b = (int) (1*distance)/35;		
		}
		executor.rotateWheels(a*50, b*50);
		
		drawables.add(new Drawable(Drawable.LABEL, "Distance: " + distance, 50, 30, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL, "dirAngle: " + normalizeAngle(dirAngle), 50, 50, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL, "robotAngle: " + normalizeAngle(robot.getT()), 50, 70, Color.WHITE));
	}
	
	public double normalizeAngle(double angle) {
		angle = angle % Math.PI*2;
		
		if(angle < 0) {
			return Math.PI*2 - Math.abs(angle);
		}
		
		return angle;
	}
}