package main.strategy;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import main.Strategy;
import main.data.Ball;
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

	// Gap is the distance behind ball for the point we want to move to.
	private int optimalGap = 50;
	private int gap = 30;
	// This is the distance between the opponent and the ball that defines 
	// the opponent to be in possession
	//Thresholds

	// TODO: calculate width of the opponent taking into account its angle (?)
	private int opponentWidth = 75;
	boolean goingToAvoid = false;
	boolean goingToBehind = false;
	
	@Override
	public void updateLocation(Location data) {
		Ball ball = data.getBall();
		Robot robot = data.getOurRobot();
		Robot opponent = /*new Robot(-1,-1,-1);*/data.getOpponentRobot();
		Goal goal = data.getGoal();
		Point optimum = getOptimumPoint(ball, goal);
		ballBuffer.addPoint(ball);

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
			moveToPoint(robot, point);
		}
		else if (isBallBehindRobot(robot, ball, optimum, goal))
		{
			setIAmDoing("Ball behind robot - going to Behind");
			Point point = getPointToFaceBallFromCorrectSide(robot, ball, optimum, goal);
			// Checks to see if there is an obstacle in the way of the new point,
			// if there is, set new point to avoid obstacle.
			if (robot.isObstacleInFront(opponent, point, opponentWidth)) {
				point = getPointToAvoidObstacle(robot, opponent, point);
			}
			drawPoint(point, "Behind");
			moveToPoint(robot, point);
		}
		else if (!isRobotInOptimumPosition(robot, optimum))
		{
			setIAmDoing("Not in optimum - going to Optimum");
			moveToPoint(robot, optimum);
		}
		/**
		else if (isBallReached(robot, ball) && !isRobotCloseToGoal(robot, goal))
		{
			setIAmDoing("Moving to Goal");
			moveToPoint(robot, goal);
		}
		*/
		else if (isBallReached(robot, ball))
		{
			setIAmDoing("Kick");
			executor.kick();
		}
		else {
			setIAmDoing("Reaching ball");
			moveToPoint(robot, ball);
		}
		
		 
		setDrawables(drawables);
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
		boolean belowOpponent = false;
		if (robot.getY() > opponent.getY()) {
			belowOpponent = true;
		}
		
		// Get the two possible points available.
		Point pointAbove = calculateAvoidancePoint(robot, opponent, 100, false);
		Point pointBelow = calculateAvoidancePoint(robot, opponent, 100, true);

		// Get the overall distance that will be travelled to reach ball.
		double distancePointA = robot.getDistanceBetweenPoints(pointAbove) + pointAbove.getDistanceBetweenPoints(optimum);
		double distancePointB = robot.getDistanceBetweenPoints(pointBelow) + pointBelow.getDistanceBetweenPoints(optimum);
		
		// If there are no obstacles inbetween the robot and either point, and neither point is outside the pitch, 
		// go to the one which has the shortest distance to the ball
		if (!robot.isObstacleInFront(opponent, pointAbove, opponentWidth) && !robot.isObstacleInFront(opponent, pointBelow, opponentWidth)
				&& !pointAbove.isPointOutOfPitch() && !pointBelow.isPointOutOfPitch()) {
			if (distancePointA < distancePointB) {
				return pointAbove;
			} else {
				return pointBelow;
			}
		}
		// If there is an obstacle in front of the point or it is off the pitch, go to the other point
		if (robot.isObstacleInFront(opponent, pointAbove, opponentWidth) || pointAbove.isPointOutOfPitch()) {
			return pointBelow;
		} 
		else if (robot.isObstacleInFront(opponent, pointBelow, opponentWidth) || pointAbove.isPointOutOfPitch()) 
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
		int behindGap = 50;
		// Check if robot is below the ball and which side of the ball it is on
		boolean belowBall = true;
		if (goal.getX() != 0) {
			belowBall = false;
			if (robot.getY() > optimum.getY()) 
				belowBall = true;
		}
		if (robot.getY() > optimum.getY()) {
			belowBall = false;
		}
		// Calculate the avoid point, if point is out of pitch, change to other point.
		// TODO check the need for this, as robot is unlikely to reach the avoid point.
		Point point = calculateAvoidancePoint(robot, ball, behindGap, belowBall);
		if (point.isPointOutOfPitch()) {
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
		return robot.isInPoint(optimum, 30);
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
	 * Add drawables
	 * 
	 * @param robot
	 * @param opponent
	 * @param ball
	 * @param optimum
	 */
	protected void addDrawables(Robot robot, Robot opponent, Ball ball, Point optimum, Goal goal) {
		// Creating new object every time because of reasons related to
		// the way Swing draws stuff. Will be fixed later.
		drawables = new ArrayList<Drawable>();
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		// Robot states of execution
		drawables.add(new Drawable(Drawable.LABEL,
						"isBallInACorner: " + isBallInACorner(ball),
						800, 30, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"isOpponentInPossession: " + isOpponentInPossession(opponent, ball, goal),
				800, 50, Color.BLACK));

		drawables.add(new Drawable(Drawable.LABEL,
						"isObstacleInFront: " + robot.isObstacleInFront(opponent, optimum, opponentWidth),
						800, 70, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
						"isBallBehindRobot: " + isBallBehindRobot(robot, ball, optimum, goal),
						800, 90, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
						"!isRobotInOptimumPosition: " + !isRobotInOptimumPosition(robot, optimum),
						800, 110, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
						"isBallReached: " + isBallReached(robot, ball),
						800, 130, Color.BLACK));
		
		drawables.add(new Drawable(Drawable.LABEL,
						"gap: " + gap,
						450, 30, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL,
						"Ball (X, Y): " + formatter.format(ball.getX()) + " " + formatter.format(ball.getY()),
						450, 50, Color.WHITE));

		drawables.add(new Drawable(Drawable.LABEL,
				"Angle between robot and obstacle edge: " + formatter.format(Math.abs(Math.toDegrees(Math.atan((2*opponentWidth)/robot.getDistanceBetweenPoints(opponent))))),
				800, 170, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Angle between optimum and opponent from robot view: " + formatter.format(Math.toDegrees(Math.abs(Math.abs(opponent.angleBetweenPoints(robot)) - Math.abs(optimum.angleBetweenPoints(robot))))),
				800, 190, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Difference of angles between robots: " + formatter.format(Math.abs(Math.abs(robot.angleBetweenPoints(optimum)) - Math.abs(opponent.angleBetweenPoints(optimum)))),
				800, 210, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Distance between optimum and robot: " + formatter.format(robot.getDistanceBetweenPoints(optimum)),
				800, 230, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Distance between optimum and opponent: " + formatter.format(opponent.getDistanceBetweenPoints(optimum)),
				800, 250, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Distance between robot and goal: " + formatter.format(goal.getDistanceBetweenPoints(robot)),
				800, 270, Color.BLACK));
	
		
		// Prediction test code
		Predictor predictor = new Predictor();
		for(int i = 0; i < ballBuffer.getBufferLength(); i++)
			drawPoint(ballBuffer.getPointAt(i), "");
		double[] parameters = new double[4];
		predictor.fitLine(parameters, ballBuffer.getXBuffer(), ballBuffer.getYBuffer(),
							null, null, ballBuffer.getBufferLength());
		drawables.add(new Drawable(Drawable.LABEL,
				"Ball Intercept: " + parameters[0], 800, 290, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Ball Slope: " + parameters[1], 800, 310, Color.BLACK));
		
		//get the x offset value such that distance of will always equal 100
		int lineLength = 100;
		int xOffset = (int) (lineLength / Math.sqrt(1 + parameters[1]*parameters[1]));
		
		//draw line of best fit for points in ballBuffer
		if(Math.abs(ballBuffer.getXPosAt(ballBuffer.getCurrentPosition())) -
				Math.abs(ballBuffer.getXPosAt(ballBuffer.getLastPosition())) > 0)
			xOffset = xOffset * -1;

		//define coordinates of the line to draw
		int x1 = (int) ball.getX();
		int y1 = (int) (parameters[1]*ball.getX() + parameters[0]);
		int x2 = (int) ball.getX()+xOffset;
		int y2 = (int) (parameters[1]*(ball.getX()+xOffset) + parameters[0]);
		
		drawables.add(new Drawable(Drawable.LINE,
				x1, y1, x2, y2, Color.CYAN, true));
		
		//check if the line is going out of the pitch
		
		
		/*
		drawables.add(new Drawable(Drawable.LABEL,
				"Current position: " + ballBuffer.getCurrentPosition(), 800, 330, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Last position: " + ballBuffer.getLastPosition(), 800, 350, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Last == Current: " + (ballBuffer.getLastPosition() == ballBuffer.getCurrentPosition()), 800, 370, Color.BLACK));
		*/
	}
	
	/**
	 * Set information about what I'm doing doing right now
	 * 
	 * @param name
	 */
	protected void setIAmDoing(String name) {
		drawables.add(new Drawable(Drawable.LABEL,
						"I am doing: " + name,
						800, 140, Color.RED));
	}
	
	/**
	 * Draw point on screen to show its position
	 * 
	 * @param point
	 * @param label
	 */
	protected void drawPoint(Point point, String label) {
		drawPoint(point, label, true);
	}
	
	/**
	 * Draw point on screen to show its position
	 * 
	 * @param point
	 * @param label
	 * @param remap
	 */
	protected void drawPoint(Point point, String label, boolean remap) {
		if(drawables==null)
			drawables=new ArrayList<Drawable>();
		drawables.add(new Drawable(Drawable.CIRCLE,
						(int) point.getX(), (int) point.getY(), Color.WHITE, remap));
		if (label != null) {
			drawables.add(new Drawable(Drawable.LABEL,
					label,
					(int) point.getX() + 5, (int) point.getY(), Color.WHITE, remap));
		}
	}
	
	public void setGap(int newGap) {
		gap = newGap;
	}
	
	public int getGap() {
		return gap;
	}
	
	protected int getOptimalGap() {
		return optimalGap;
	}
}
