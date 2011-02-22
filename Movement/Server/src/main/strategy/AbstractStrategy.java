package main.strategy;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import main.gui.Drawable;
import main.gui.DrawablesListener;
import main.Strategy;
import main.Executor;
import main.data.Point;
import main.data.Robot;

public abstract class AbstractStrategy implements Strategy {
	
	/**
	 * Pitch dimensions
	 */
	protected int PITCH_X_MIN = 0;
	protected int PITCH_Y_MIN = 0;
	protected int PITCH_X_MAX = 550; // TODO check those
	protected int PITCH_Y_MAX = 350; // TODO check those

	protected ArrayList<Drawable> drawables;
	
	protected Executor executor = null;
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
		
		double dirAngle = 0;
		int left=1;
		int right=1;

		// find the distance between the robot and the ball
		double dy = robot.getY() - point.getY();
		double dx = robot.getX() - point.getX();
		double distance = Math.sqrt(dx*dx + dy*dy);
		
		// find the angle the robot must turn to face the ball
		// TODO FIX THIS ASAP!
		if (executor instanceof main.executor.Simulator) {
			dirAngle = Math.toDegrees(point.getAngleBetweenPoints(robot));
			
			// Convert both angles to positive within the range (0, 360)
			// 	clockwise is positive
			dirAngle = changeToPositive(dirAngle);
			robot.setT((float) changeToPositive(Math.toDegrees(robot.getT())));
			//robot.setT((float) Math.toDegrees(robot.getT()));
			
			double angleDifference = robot.getT() - dirAngle;
			
			if((angleDifference < 0 && Math.abs(angleDifference) < 180)
					|| (angleDifference > 180 && Math.abs(angleDifference) > 180)) {
				// turn left
				left=1;
				right=-1;
			} else {
				// turn right
				left=-1;
				right=1;
			}
		} else {
			dirAngle = 180 - Math.toDegrees(robot.getAngleBetweenPoints(point));
			
			if(dirAngle < robot.getT()) {
				// turn left
				left=-1;
				right=1;
			} else {
				// turn right
				left=1;
				right=-1;
			}
		}
		
		// once the robot is facing in direction of the ball, move towards it at
		// a velocity proportional to the distance between them
		if(Math.abs(dirAngle - robot.getT()) % 360 < 30) {
			left = (int) (1*distance)/35;
			right = (int) (1*distance)/35;		
		}

		left = Math.min(left, 4);
		right = Math.min(right, 4);
		
		executor.rotateWheels(left*50, right*50);
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		drawables.add(new Drawable(Drawable.LABEL, "Distance: " + formatter.format(distance), 50, 30, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL, "dirAngle: " + formatter.format(dirAngle), 50, 50, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL, "robotAngle: " + formatter.format(robot.getT()), 50, 70, Color.WHITE));
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
