package main.strategy;

import java.awt.Color;

import main.Strategy;
import main.data.Ball;
import main.data.CircularBuffer;
import main.data.Goal;
import main.data.Location;
import main.data.Robot;
import main.data.Point;
import main.gui.Drawable;


/**
 * This strategy should find a position behind the ball and move the robot to it.
 * I think the methods used in finding a point behind the ball might be more useful 
 * than the movement strategy that could be implemented here.
 */
public class GoToBall extends AbstractStrategy implements Strategy {

	boolean goingToAvoid = false;
	boolean goingToBehind = false;
	
	@Override
	public void updateLocation(Location data) {
		Ball ball = data.getBall();
		Robot robot = data.getOurRobot();
		Robot opponent = data.getOpponentRobot();
		Goal goal = data.getGoal();
		Point optimum = getOptimumPoint(ball, goal);
		Point behind = getPointToFaceBallFromCorrectSide(robot, ball, optimum, goal);
	//	ballBuffer.addPoint(ball);
	//	optimumBuffer.addPoint(optimum);
		//behindBuffer.addPoint(behind);

		// statistics
		addDrawables(robot, opponent, ball, optimum, goal);
		drawPoint(opponent, "Opponent");
		drawPoint(robot, "Robot");
		drawPoint(goal, "Goal");
		drawPoint(optimum, "Optimum");	
		drawPoint(ball, "Ball");

		
		// This state machine covers the basic stages robot can be in 
		if (robot.isObstacleInFront(opponent, optimum, opponentWidth))
		{			
			setIAmDoing("Obstacle in front - going to Avoid");
			Point point = getPointToAvoidObstacle(robot, opponent, optimum);
			drawPoint(point, "Avoid");
			//moveToPoint(robot, point);
			pfsMoveToPoint(robot, opponent, point);
		}
		else if (isBallBehindRobot(robot, ball, optimum, goal))
		{
			setIAmDoing("Ball behind robot - going to Behind");
			//Point point = getPointToFaceBallFromCorrectSide(robot, ball, optimum, goal);
			// Checks to see if there is an obstacle in the way of the new point,
			// if there is, set new point to avoid obstacle.
			if (robot.isObstacleInFront(opponent, behind, opponentWidth)) {
				behind = getPointToAvoidObstacle(robot, opponent, behind);
			}
			drawPoint(behind, "Behind");
			
			//Point predictedBehind = getPredictionPoint(behind, lineLength, behindBuffer);
			//moveToPoint(robot, point);
			pfsMoveToPoint(robot, opponent, behind);

		}
		// TODO: Check threshold on obstacleWidth - currently 30
		else if (!isRobotInOptimumPosition(robot, optimum) && !(ball.isObstacleInFront(robot, optimum, 30)))
		{
			
			setIAmDoing("Not in optimum - going to Optimum");
			//moveToPoint(robot, optimum);
			//Point predictedOptimum = getPredictionPoint(optimum, lineLength, optimumBuffer);
			pfsMoveToPoint(robot, opponent, optimum);

		}
		/**
		 * Leaving out for the time being, see if new method works with robot.
		 * else if (isBallReached(robot, ball) && !isRobotCloseToGoal(robot, goal))
		{
			setIAmDoing("Moving to Goal");
			moveToPoint(robot, goal);
		}
		*/
	/**
		else if (isBallKickable(robot, ball, goal))// && isRobotInPossession(robot, ball, goal))
		{	
			setIAmDoing("Kick");
			executor.kick();
		}
		*/
		else if (isBallReached(robot, goal, ball) && isFacingGoal(robot, goal)) {
			
			setIAmDoing("Kick");
			executor.kick();
		}
		
		// Possibly utilise this later on, method needs more work though
		else {
			setIAmDoing("Reaching ball");
			//moveToPoint(robot, ball);
			pfsMoveToPoint(robot, opponent, ball);
		}
		
		
		
		 
		setDrawables(drawables);
	}
	
	private boolean isFacingGoal(Robot robot, Goal goal) {
		double angle = robot.angleBetweenPoints(goal);
		if (goal.getX() == 0) {
			return (robot.getT() < Math.toRadians(-90) || robot.getT() > Math.toRadians(90));
		} else {
			return (robot.getT() > Math.toRadians(-90) || robot.getT() < Math.toRadians(90));
		}
	//	return (robot.getT() <= angle + Math.toRadians(90) && robot.getT() >= angle + Math.toRadians(90));
		//return false;
	}

	/**
	 * Do we have a ball
	 * 
	 * @param robot
	 * @param optimum
	 * @param ball
	 * @return
	 */
	private boolean haveBall(Robot robot, Point optimum, Ball ball) {
		return optimum.isObstacleInFront(robot, ball, 10) && robot.getDistanceBetweenPoints(ball) <= 15;
	}

	/**
	 * Get a point which would make robot to avoid obstacle and go to a point
	 * which will have straight path to a ball
	 * 
	 * Currently does this by calculating 2 points either side, seeing if either 
	 * is obstructed, and then heading to the unobstructed point
	 * 
	 * TODO: Test to see if there is a situation where both can be obstructed..
	 * This is likely in a case when robot is close to the opponent. This needs a 
	 * much more accurate way of measuring if an obstacle is inbetween the robot 
	 * and its intended point.
	 * 
	 * @param robot
	 * @param opponent
	 * @param optimum
	 * @return
	 */
	protected Point getPointToAvoidObstacle(Robot robot, Robot opponent, Point optimum) {
		
		// Get the two possible points available.
		Point pointAbove = calculateAvoidancePoint(robot, opponent, 100, false);
		Point pointBelow = calculateAvoidancePoint(robot, opponent, 100, true);

		// Get the overall distance that will be travelled to reach ball.
		double distancePointA = robot.getDistanceBetweenPoints(pointAbove) + pointAbove.getDistanceBetweenPoints(optimum);
		double distancePointB = robot.getDistanceBetweenPoints(pointBelow) + pointBelow.getDistanceBetweenPoints(optimum);
		
		// If there are no obstacles inbetween the robot and either point, and neither point is outside the pitch, 
		// go to the one which has the shortest distance to the ball
		if (!robot.isObstacleInFront(opponent, pointAbove, opponentWidth) && !robot.isObstacleInFront(opponent, pointBelow, opponentWidth)
				&& !isPointOutOfPitch(pointAbove) && !isPointOutOfPitch(pointBelow)) {
			if (distancePointA < distancePointB) {
				return pointAbove;
			} else {
				return pointBelow;
			}
		}
		// If there is an obstacle in front of the point or it is off the pitch, go to the other point
		if (robot.isObstacleInFront(opponent, pointAbove, opponentWidth) || isPointOutOfPitch(pointAbove)) {
			return pointBelow;
		} 
		else if (robot.isObstacleInFront(opponent, pointBelow, opponentWidth) || isPointOutOfPitch(pointAbove)) 
		{	
			return pointAbove;
		} else {
			// TODO: change so that robot moves back into another position, as cannot currently avoid obstacle
			return pointAbove;
		}
	
	}
	
	
	
	
	/**
	 * If robot is further away from the goal than he ball or in other words
	 * if we would go to a ball, can we kick from there or do we need to go to
	 * the other side?
	 * 
	 * @param robot
	 * @param ball
	 * @param optimum
	 * @return
	 */
	protected boolean isBallBehindRobot(Robot robot, Ball ball, Point optimum, Goal goal) {
		Point point = getPointToFaceBallFromCorrectSide(robot, ball, optimum, goal);
		if (robot.isInPoint(point)) {
			return false;
		}
		
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
	protected Point getPointToFaceBallFromCorrectSide(Robot robot, Ball ball, Point optimum, Goal goal) {		
		int behindGap = 70;
		// Check if robot is below the ball and which side of the ball it is on
		boolean belowBall = true;
		if (robot.getY() > optimum.getY()) {
			belowBall = false;
		}
		if (goal.getX() != 0) {
			belowBall = false;
			if (robot.getY() > optimum.getY()) 
				belowBall = true;
		}
		// Calculate the avoid point, if point is out of pitch, change to other point.
		// TODO check the need for this, as robot is unlikely to reach the avoid point.
		Point point = calculateAvoidancePoint(robot, ball, behindGap, belowBall);
		if (isPointOutOfPitch(point)) {
			if(belowBall) {
				belowBall = false;
			} else {
				belowBall = true;
			}
		}
		return calculateAvoidancePoint(robot, ball, behindGap, belowBall);
	}
	
	/**
	 * Get point outside of ball far enough to have enough space to turn
	 * 
	 * @param ball
	 * @param goal
	 * @return
	 */
	protected Point getOptimumPoint(Ball ball, Goal goal) {
		double xOffset = optimalGap*Math.cos(ball.angleBetweenPoints(goal));
		double yOffset = optimalGap*Math.sin(ball.angleBetweenPoints(goal));
			
		return new Point(ball.getX()-xOffset, ball.getY()-yOffset);
	}
	
	/**
	 * Is robot in a position where it can turn and reach a ball facing correct direction for a kick
	 * 
	 * @param robot
	 * @param optimum
	 * @return
	 */
	protected boolean isRobotInOptimumPosition(Robot robot, Point optimum) {
		return robot.isInPoint(optimum, 40);
	}
	
	/**
	 * Calculates a point at a 90 degree angle to the ball in relation to the robot.
	 * 
	 * @param robot
	 * @param ball
	 * @param gap
	 * @param aboveBall
	 * @return
	 */
	protected Point calculateAvoidancePoint(Robot robot, Point point, int gap, boolean belowPoint) {
		int side = 1;
		if (belowPoint) {
			side = -1;
		}
		
		// Gets the angle and distance between the robot and the ball
		double robotBallAngle = robot.angleBetweenPoints(point);
		double robotBallDistance = robot.getDistanceBetweenPoints(point);
		// Calculate the distance between the robot and the destination point
		double hyp = Math.sqrt((robotBallDistance*robotBallDistance) + (gap*gap));
		
		// Calculate the angle between the robot and the destination point
		double robotPointAngle = Math.asin(gap / hyp);
		// Calculate the angle between the robot and the destination point.
		// Side is -1 if robot is below the ball, so will get the angle needed for a point
		// below the ball, whereas side = 1 will give a point above the ball
		double angle = robotBallAngle + (side * robotPointAngle);
		
		// Offsets are in relation to the robot
		double xOffset = hyp*Math.cos(angle);
		double yOffset = hyp*Math.sin(angle);
		
		return new Point(robot.getX() + xOffset, robot.getY() + yOffset);
	}
	
	/**
	 * Is robot close enough to a goal
	 * 
	 * @param robot
	 * @param goal
	 * @return
	 */
	protected boolean isRobotCloseToGoal(Robot robot, Goal goal) {
		int threshold = 100;
		
		return robot.getDistanceBetweenPoints(goal) < threshold;
	}
	
	/**
	 * Detects if a robot is in a point inbetween two points
	 * @param pointA
	 * @param pointB
	 * @return
	 */
	protected boolean isBetweenPoint(Robot robot, Point optimum, Point ball) {
		if (robot.isInPoint(getInBetweenPoint(optimum, ball), 30)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the point inbetween two points
	 * @param optimum
	 * @param ball
	 * @return
	 */
	protected Point getInBetweenPoint(Point optimum, Point ball) {
		
		double hyp = optimum.getDistanceBetweenPoints(ball)/2;
		double xOffset = hyp*Math.cos(optimum.angleBetweenPoints(ball));
		double yOffset = hyp*Math.sin(optimum.angleBetweenPoints(ball));
		
		Point point = new Point(optimum.getX() + xOffset, optimum.getY() + yOffset);
		
		return point;
	}
	
	/**
	 * Gets a point that is LENGTH away from the ball based on it's previous positions
	 * 
	 * @param ball
	 * @param length
	 * @return
	 */
	protected Point getPredictionPoint(Point point, double length, CircularBuffer ballBuffer) {

		Predictor predictor = new Predictor();
		for(int i = 0; i < ballBuffer.getBufferLength(); i++)
			drawPoint(ballBuffer.getPointAt(i), "");
		double[] parameters = new double[4];
		predictor.fitLine(parameters, ballBuffer.getXBuffer(), ballBuffer.getYBuffer(),
							null, null, ballBuffer.getBufferLength());
		
		//get the x offset value such that distance of will always equal 100
		double lineLength = length*distanceOfMovingPoints(point, ballBuffer);
		int xOffset = (int) (lineLength / Math.sqrt(1 + parameters[1]*parameters[1]));
		
		//changed the offset if the ball is travelling right
		if(Math.abs(ballBuffer.getXPosAt(ballBuffer.getCurrentPosition())) -
				Math.abs(ballBuffer.getXPosAt(ballBuffer.getLastPosition())) > 0)
			xOffset = xOffset * -1;

		//define coordinates of the line to draw
		int x1 = (int) point.getX();
		int y1 = (int) (parameters[1]*point.getX() + parameters[0]);
		int x2 = (int) point.getX()+xOffset;
		int y2 = (int) (parameters[1]*(point.getX()+xOffset) + parameters[0]);
		
		//draw the line between (x1,y1) and (x2,y2)
		drawables.add(new Drawable(Drawable.LINE, x1, y1, x2, y2, Color.CYAN, true));
		
		
		Point predictPoint = new Point(x2,y2);
		
		//check if the line is going out of the pitch
		while (isPointOutOfPitch(predictPoint)) {
			double x = predictPoint.getX();
			double y = predictPoint.getY();
			//use symmetry to get point in pitch
			if (x > PITCH_X_MAX) {
				predictPoint.setX(PITCH_X_MAX - Math.abs(x - PITCH_X_MAX));
			}
			else if (x < PITCH_X_MIN) {
				predictPoint.setX(PITCH_X_MIN + Math.abs(x - PITCH_X_MIN));
			}
			if (y > PITCH_Y_MAX) {
				predictPoint.setY(PITCH_Y_MAX - Math.abs(y - PITCH_Y_MAX));
			}
			else if (y < PITCH_Y_MIN) {
				predictPoint.setY(PITCH_Y_MIN + Math.abs(y - PITCH_Y_MIN));
			}			
		}
		
		return predictPoint;
	}
	
	private double distanceOfMovingPoints(Point point, CircularBuffer pointBuffer){
		Point oldPoint = new Point (pointBuffer.getXPosAt(pointBuffer.getLastPosition()),pointBuffer.getYPosAt(pointBuffer.getLastPosition()));
		
		return oldPoint.getDistanceBetweenPoints(point);
		
	}
}
