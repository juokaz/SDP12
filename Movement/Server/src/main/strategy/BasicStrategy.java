package main.strategy;

import main.Strategy;
import main.data.Ball;
import main.data.Location;
import main.data.Point;
import main.data.Robot;
import main.data.VelocityVec;


/**
 * 
 * This strategy should find a position behind the ball and move the robot to it.
 * I think the methods used in finding a point behind the ball might be more useful 
 * than the movement strategy that could be implemented here.
 * 
 * @author calumjackson
 *
 */
public class BasicStrategy extends AbstractStrategy implements Strategy {

	private int gap;
	
	@Override
	public void updateLocation(Location data) {
		// TODO Auto-generated method stub
		
		/**
		 * Get position of ball, robot and angle of robot to ball
		 */
		
		Robot robotA = data.getRobotA();
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
		
		// Calculates the angle between the 
		double ballGoalAngle = calculateBallToGoalAngle(ball);
		
		calculatePosBehindBall(ballGoalAngle, ball, dirPoint);
		
	
		
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
	 * 
	 * Find the angle between the ball and  the centre of the goal.
	 * TODO: Adapt to find distance as well?
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

	@Override
	public VelocityVec getVelocity() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setGap(int newGap) {
		gap = newGap;
	}
	
	public int getGap() {
		return gap;
	}

}
