package main.strategy;

import java.awt.Color;
import java.util.ArrayList;

import sdp12.simulator.Drawable;
import main.Strategy;
import main.Executor;
import main.data.Ball;
import main.data.Point;
import main.data.Robot;

public abstract class AbstractStrategy implements Strategy {

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
			double dirAngle = -1*Math.atan2(point.getY()-robot.getY(), point.getX()-robot.getX());
			
			// find the distance between the robot and the ball
			double dy = robot.getY() - point.getY();
			double dx = robot.getX() - point.getX();
			double distance = Math.sqrt(dx*dx + dy*dy);
			System.out.println("CopyOfBasicStrategy::Distance " + distance);
			
			// convert to degrees
			dirAngle = Math.toDegrees(dirAngle);
			System.out.println("CopyOfBasicStrategy::dirAngle " + dirAngle);
			System.out.println("CopyOfBasicStrategy::dAngle " + Math.abs(dirAngle - robot.getT()));
			
			// decide whether to turn left or right to face the ball
			int a=1;
			int b=1;
			if(dirAngle > robot.getT()){
				//System.out.println("Turning left");
				a=-1;
				b=1;
			} 
			if (dirAngle < robot.getT())
			{
				//System.out.println("Turning right");
				a=1;
				b=-1;
				
			}
			// once the robot is facing in direction of the ball, move towards it at
			// a velocity proportional to the distance between them
			if(Math.abs(dirAngle -robot.getT())<30){
				a = (int) (1*distance)/35;
				b = (int) (1*distance)/35;		
			}
			executor.rotateWheels(a*50, b*50);
			
			ArrayList<Drawable> drawables = new ArrayList<Drawable>();
			drawables.add(new Drawable(Drawable.LABEL, "Distance: " + distance, 50, 30, Color.WHITE));
			drawables.add(new Drawable(Drawable.LABEL, "dirAngle: " + dirAngle, 50, 50, Color.WHITE));
			drawables.add(new Drawable(Drawable.LABEL, "robotAngle: " + robot.getT(), 50, 70, Color.WHITE));
			
			executor.setDrawables(drawables);
			
		}
		
		
	}

