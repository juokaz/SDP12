package main.strategy;

import main.Strategy;
import main.data.Ball;
import main.data.Goal;
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
		Goal goal = data.getGoal();
		
		Point optimum = getOptimumPoint(ball, goal);
		
		
		// This state machine covers the basic stages robot can be in 
		if (isBallInACorner(ball)) {
			// Don't do anything, wait for it move from there
		} else if (isObstacleInFront(robot, opponent, optimum)) {
			Point point = getPointToAvoidObstacle(robot, opponent, optimum);
			moveToPoint(robot, point);
		} else if (isBallBehindRobot(robot, ball, optimum)) {
			Point point = getPointToFaceBallFromCorrectSide(robot, ball, optimum);
			moveToPoint(robot, point);
		} else if (!isRobotInOptimumPosition(robot, optimum)) {
			moveToPoint(robot, optimum);
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
	 * @param optimum
	 * @return
	 */
	protected boolean isObstacleInFront(Robot robot, Robot opponent, Point optimum) {
		/** 
		 * TODO: edit to take into account size of opponent, will probably have to 
		 * change so that checks if inbetween two angles.
		 */
		return robot.getAngleBetweenPoints(optimum) == robot.getAngleBetweenPoints(opponent);
	}
	
	/**
	 * Get a point which would make robot to avoid obstacle and go to a point
	 * which will have straight path to a ball
	 * 
	 * @param robot
	 * @param opponent
	 * @param optimum
	 * @return
	 */
	protected Point getPointToAvoidObstacle(Robot robot, Robot opponent, Point optimum) {
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
	 * @param optimum
	 * @return
	 */
	protected boolean isBallBehindRobot(Robot robot, Ball ball, Point optimum) {
		return ((optimum.getX() < ball.getX() && ball.getX() < robot.getX()) || 
				(optimum.getX() > ball.getX() && ball.getX() > robot.getX()));
	}
	
	/**
	 * Get a point which will move robot to a point having direct path to a ball
	 * and also a point which would allow a kick
	 * 
	 * @param robot
	 * @param ball
	 * @param optimum
	 * @return
	 */
	protected Point getPointToFaceBallFromCorrectSide(Robot robot, Ball ball, Point optimum) {
		// TODO
		
		// If robot is not very close to the ball	
		if (robot.getDistanceBetweenPoints(ball) > 20) {
			// Checks to see which side of the ball the robot is on
			if (robot.getAngleBetweenPoints(ball) - ball.getAngleBetweenPoints(optimum) > 180) {				
				ball.calculatePosBehindBall(ball.getAngleBetweenPoints(optimum) + 45, ball, gap*2);
			} else {
				ball.calculatePosBehindBall(ball.getAngleBetweenPoints(optimum) - 45, ball, gap*2);
			}
		} else {
			// TODO: maybe just turn and move ball?
		}
		return new Point(0, 0);
	}
	
	/**
	 * Get point outside of ball far enough to have enough space to turn
	 * 
	 * @param ball
	 * @param goal
	 * @return
	 */
	protected Point getOptimumPoint(Ball ball, Goal goal) {
		return ball.calculatePosBehindBall(goal.calculateGoalAndPointAngle(ball), ball, gap);
	}
	
	/**
	 * Is robot in a position where it can turn and reach a ball facing correct direction for a kick
	 * 
	 * @param robot
	 * @param optimum
	 * @return
	 */
	protected boolean isRobotInOptimumPosition(Robot robot, Point optimum) {
		return robot.isInPoint(optimum);
	}
	
	/**
	 * Are we in a point which allows a kick
	 * 
	 * @param robot
	 * @param ball
	 * @return
	 */
	protected boolean isBallReached(Robot robot, Ball ball) {
		return robot.isInPoint(ball);
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
