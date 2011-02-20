package main.strategy;

import main.Strategy;
import main.data.Ball;
import main.data.Location;
import main.data.Point;
import main.data.Robot;


/**
 * This strategy should find a position behind the ball and move the robot to it.
 * I think the methods used in finding a point behind the ball might be more useful 
 * than the movement strategy that could be implemented here.
 */
public class BasicStrategy extends AbstractStrategy implements Strategy {

	// Gap is the distance behind ball for the point we want to move to.
	private int gap;
	
	@Override
	public void updateLocation(Location data) {
		// TODO Auto-generated method stub
		
		/**
		 * Get position of ball, robot, and move to ball
		 */
		
		Robot robotA = data.getOurRobot();
		Ball ball = data.getBall();
		
		
		
		// Create point object to store data to store 
		Point dirPoint = new Point(0,0);
		
		
		moveToBall(robotA, ball, dirPoint);
		
	}
	
	/**
	 * Main method to move to the ball.
	 * 
	 * @param robot
	 * @param ball
	 */
	public void moveToBall(Robot robot, Ball ball, Point dirPoint) {
		
		
		
		// Gets the angle between the goal and the ball
		
		double ballGoalAngle = calculateBallToGoalAngle(ball);
		
		// Sets the points in Point dirPoint to the position we want to move to
		calculatePosBehindBall(ballGoalAngle, ball, dirPoint);
		
		// Get angle between robot and the point to move to
		double robotPointAngle = calculateRobotPointAngle(robot, dirPoint);
		
		// Rotate robot to angle needed
		executor.rotate((int) robotPointAngle);
		
		double distance = calculateRobotPointDistance(robot, dirPoint);
		int velocity = (int) distance%200;
		executor.rotateWheels(velocity, velocity);
		
		
		// Once robot has reached ball, turn to face ball
		//executor.rotate((int) (calculateBallToGoalAngle(ball)-robot.getT()));
		executor.rotate((int) calculateRobotPointAngle(robot, ball));
		
	}
	
	/**
	 * Calculates the distance between the robot and a point
	 * @param robot
	 * @param dirPoint
	 * @return
	 */
	private double calculateRobotPointDistance(Robot robot, Point dirPoint) {
		
		double dx = robot.getX()-dirPoint.getX();
		double dy = robot.getY()-dirPoint.getY();
		
		double distance = Math.sqrt(dx*dx+dy*dy);
		
		return distance;
	}

	/**
	 * Find a position behind the ball to navigate to
	 * Uses the 'gap' parameter as a distance behind the ball
	 * 
	 * @param ballGoalAngle
	 */
	private void calculatePosBehindBall(double ballGoalAngle, Ball ball, Point dirPoint) {

		// Set the distance behind ball we want to move.
		
		
		// Need to work out sin and cos distances to get new X and Y positions
		double xOffset = gap*Math.cos(ballGoalAngle);
		double yOffset = gap*Math.sin(ballGoalAngle);
			
		// Sets the position for the robot to move to
		dirPoint.setX(ball.getX()+xOffset);
		dirPoint.setY(ball.getY()+yOffset);
		
	}

	/**
	 * Find the angle between the ball and  the centre of the goal.
	 * 
	 * @param robot
	 * @param ball
	 * @return
	 */
	public double calculateBallToGoalAngle(Ball ball) {
		
		//Middle of the goal position.
		double goalX = 0, goalY = 175;
		// Gets the angle between the ball and the centre of the goal.
		double angle = Math.atan2(goalY-ball.getY(), goalX-ball.getX());
	
		if (angle < 0) {
			angle = (-Math.PI - angle);
		} else {
			angle = (Math.PI - angle);
		}
		
		return angle;
		
	}
	
	/** 
	 * Works out angle between robot and the point the robot wants to move to,
	 * taking into account the angle the robot is currently at.
	 * @param robotA
	 * @param point
	 * @return
	 */
	public double calculateRobotPointAngle(Robot robot, Point point) {
		
		double dirAngle = 0;
		double robotAngle = robot.getT();
		//Angle that robot needs to turn to head towards ball
		double turnAngle = 0;
		
		//Direction angle between robot and point to move to
		dirAngle = Math.atan2(point.getY()-robot.getY(), point.getX()-robot.getX());
		if (dirAngle < 0) {
			dirAngle = (-Math.PI - dirAngle);
		} else {
			dirAngle = (Math.PI - dirAngle);
		}
		
		// Works out difference between angles
		turnAngle = dirAngle - robotAngle;			
			
		
		
		return turnAngle;
		
	}
	
	public void setGap(int newGap) {
		gap = newGap;
	}
	
	public int getGap() {
		return gap;
	}

}
