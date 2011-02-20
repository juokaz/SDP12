package main.strategy;

import main.Strategy;
import main.data.Ball;
import main.data.Location;
import main.data.Robot;
import main.data.Point;


/**
 * This strategy should find a position behind the ball and move the robot to it.
 * I think the methods used in finding a point behind the ball might be more useful 
 * than the movement strategy that could be implemented here.
 */
public class GoToBall extends AbstractStrategy implements Strategy {

	// Gap is the distance behind ball for the point we want to move to.
	private int gap;
	
	@Override
	public void updateLocation(Location data) {
		Ball ball = data.getBall();
		Robot robot = data.getOurRobot();
		Robot opponent = data.getOpponentRobot();

		// This state machine covers the basic stages robot can be in 
		if (isBallInACorner(ball)) {
			// Don't do anything, wait for it move from there
		} else if (isObstacleInFront(robot, opponent)) {
			Point point = getPointToAvoidObstacle(robot, opponent);
			moveToPoint(robot, point);
		} else if (!isBallInFront(robot, ball)) {
			Point point = getPointToFaceBallFromCorrectSide(robot, ball);
			moveToPoint(robot, point);
		} else if (isBallReached(robot, ball)) {
			executor.kick();
		} else {
			moveToPoint(robot, ball);
		}
	}
	
	/**
	 * Is it possible for a ball to be reached by going straight?
	 * 
	 * @param robot
	 * @param opponent
	 * @return
	 */
	protected boolean isObstacleInFront(Robot robot, Robot opponent) {
		// TODO implement his
		return false;
	}
	
	/**
	 * Get a point which would make robot to avoid obstacle and go to a point
	 * which will have straight path to a ball
	 * 
	 * @param robot
	 * @param opponent
	 * @return
	 */
	protected Point getPointToAvoidObstacle(Robot robot, Robot opponent) {
		// TODO implement this
		return new Point(0, 0);
	}
	
	/**
	 * Is robot is further away from a goal than a ball or in other words
	 * if we would go to a ball, can we kick from there or do we need to go to
	 * the other side?
	 * 
	 * @param robot
	 * @param ball
	 * @return
	 */
	protected boolean isBallInFront(Robot robot, Ball ball) {
		// TODO implement this
		return false;
	}
	
	/**
	 * Get a point which will move robot to a point having direct path to a ball
	 * and also a point which would allow a kick
	 * 
	 * @param robot
	 * @param ball
	 * @return
	 */
	protected Point getPointToFaceBallFromCorrectSide(Robot robot, Ball ball) {
		// TODO implement this
		return new Point(0, 0);
	}
	
	/**
	 * Are we in a point which allows a kick
	 * 
	 * @param robot
	 * @param ball
	 * @return
	 */
	protected boolean isBallReached(Robot robot, Ball ball) {
		// TODO implement this
		return false;
	}
	
	/**
	 * Is a ball in once of the corners
	 * 
	 * @param ball
	 * @return
	 */
	protected boolean isBallInACorner(Ball ball) {
		// TODO implement this
		return false;
	}
	
	public void setGap(int newGap) {
		gap = newGap;
	}
	
	public int getGap() {
		return gap;
	}
}
