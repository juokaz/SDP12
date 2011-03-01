package main.strategy;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import main.gui.Drawable;
import main.gui.DrawablesListener;
import main.Strategy;
import main.Executor;
import main.data.Ball;
import main.data.CircularBuffer;
import main.data.Goal;
import main.data.Point;
import main.data.Robot;

public abstract class AbstractStrategy implements Strategy {
	
	/**
	 * Pitch dimensions
	 */
	protected int PITCH_X_MIN = 0;
	protected int PITCH_Y_MIN = 0;
	protected int PITCH_X_MAX = 540;
	protected int PITCH_Y_MAX = 290;
	protected String rotateState = "straight";
	protected String rotationState = "straight";
	
	/**
	 * Executor which is going to execute these commands
	 */
	protected Executor executor = null;

	/**
	 * Drawables to display on GUI
	 */
	protected ArrayList<Drawable> drawables;
	protected CircularBuffer ballBuffer = new CircularBuffer(10);
	
	/**
	 * Listener accepting drawables
	 */
	protected DrawablesListener listener = null;

	/**
	 * Set executor
	 * 
	 * @param executor
	 */
	public void setExecutor(Executor executor) {
		this.executor  = executor;
	}
	
	/**
	 * Set drawables listener
	 * 
	 * @param listener
	 */
	public void setDrawablesListener(DrawablesListener listener) 
	{
		this.listener = listener;
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
		moveToPoint(robot, point, false);
	}
	
	
	/**
	 * Move robot from robotX and robotY to a Point position in a pitch
	 * 
	 * Point can be a Ball, Pitch or other Robot
	 * 
	 * @param robot
	 * @param point
	 * @param dribble_mode
	 */
	protected void moveToPoint(Robot robot, Point point, boolean dribble_mode) {
		
		double dirAngle = 0;
		int left=1;
		int right=1;
		double currentAngle = robot.getTDegrees();

		// find the distance between the robot and the ball
		double dy = robot.getY() - point.getY();
		double dx = robot.getX() - point.getX();
		double distance = Math.sqrt(dx*dx + dy*dy);

		dirAngle = Math.toDegrees(point.getAngleBetweenPoints(robot));

		double angleDifference = currentAngle - dirAngle;
		
		while (angleDifference > 180) {
			angleDifference = angleDifference - 360;
		}
		while (angleDifference < -180) {
			angleDifference = angleDifference + 360;
		}
		
		if (rotateState.equals("straight")){
			if (angleDifference > 180)
				rotateState = "left";
			else if (angleDifference >= 0 && angleDifference <= 180)
				rotateState = "right";
			else if (angleDifference < 0) // default condition
				rotateState = "left";
			else
				rotateState = "straight";	
		}
		
		if (rotateState.equals("left")){
			left = -1;
			right = 1;
		}
		if (rotateState.equals("right")){
			left = 1;
			right = -1;
		}
		
		int pointThreshold = point.getPT();
		// once the robot is facing in direction of the ball, move towards it at
		// a velocity proportional to the distance between them
	
		// TODO: Check threshold is acceptable for real robot. 
		if(Math.abs(dirAngle - robot.getTDegrees()) % 360 < 30) {
			left = (int) (1*distance)/(pointThreshold - 5);
			right = (int) (1*distance)/(pointThreshold - 5);	
			rotateState = "straight";
		}
		
		// max speed factor
		int max = 4;

		if (dribble_mode) {
			max = 1;
		}
		
		// Limits Max speed to X*70
		left = Math.min(left, max);
		right = Math.min(right, max);
		
		executor.rotateWheels(left*70, right*70);
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		drawables.add(new Drawable(Drawable.LABEL, "Distance: " + formatter.format(distance), 50, 30, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL, "dirAngle: " + formatter.format(dirAngle), 50, 50, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL, "robotAngle: " + formatter.format(currentAngle), 50, 70, Color.WHITE));

	}

	
	/**
	 * Change to positive
	 * 
	 * @param angle
	 * @return
	 */
	public double changeToPositive(double angle) {
		angle = angle % 360;
		
		if(angle < 0) {
			angle = 360 - Math.abs(angle);
		}
		
		return angle;
	}
	
	/**
	 * Set listener
	 * 
	 * @param drawables
	 */
	protected void setDrawables(ArrayList<Drawable> drawables) 
	{
		if (listener != null) {
			listener.setDrawables(drawables);
		}
	}
}
