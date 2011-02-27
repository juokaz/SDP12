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
	
	//Thresholds
	private int wallThreshold = 30;

	private int opponentWidth;
	boolean goingToAvoid = false;
	boolean goingToBehind = false;
	
	@Override
	public void updateLocation(Location data) {
		Ball ball = data.getBall();
		Robot robot = data.getOurRobot();
		Robot opponent = /*new Robot(-1,-1,-1);*/data.getOpponentRobot();
		Goal goal = data.getGoal();
		Point optimum = getOptimumPoint(ball, goal);
		// TODO: calculate width of the opponent taking into account its angle (?)
		opponentWidth = 60; 
		ballBuffer.addPoint(ball);
		
		// statistics
		addDrawables(robot, opponent, ball, optimum, goal);
		drawPoint(opponent, "Opponent");
		drawPoint(robot, "Robot");
		drawPoint(goal, "Goal");
		drawPoint(optimum, "Optimum");	
		drawPoint(ball, "Ball");

		
		// This state machine covers the basic stages robot can be in 
		if (isBallOutOfPitch(ball))
		{
			setIAmDoing("Ball out of pitch");
			// We have scored (hopefully)
		//	executor.celebrate();
			executor.stop();
		}
		else if (isBallInACorner(ball))
		{
			// TODO: execute a better strategy for this area.
			setIAmDoing("Ball in a corner");
			// Don't do anything, wait for it move from there
			executor.stop();
		}
		else if (robot.isObstacleInFront(opponent, optimum, opponentWidth) || goingToAvoid)
		{			
			goingToAvoid = true;
			setIAmDoing("Obstacle in front - going to Avoid");
			Point point = getPointToAvoidObstacle(robot, opponent, optimum);
			if (robot.isInPoint(point)) 
				goingToAvoid = false;
			drawPoint(point, "Avoid");
			moveToPoint(robot, point);
		}
		else if (isBallBehindRobot(robot, ball, optimum, goal) || goingToBehind)
		{
			goingToBehind = true;
			setIAmDoing("Ball behind robot - going to Behind");
			Point point = getPointToFaceBallFromCorrectSide(robot, ball, optimum, goal);
			if (robot.isInPoint(point))
				goingToBehind = false;
			drawPoint(point, "Behind");
			moveToPoint(robot, point);
		}
		else if (!isRobotInOptimumPosition(robot, optimum))
		{
			setIAmDoing("Not in optimum - going to Optimum");
			moveToPoint(robot, optimum);
		}
		else if (isBallReached(robot, ball) && !isRobotCloseToGoal(robot, goal))
		{
			setIAmDoing("Moving to Goal");
			moveToPoint(robot, goal, true);
		}
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
	 * TODO: Calculate if is too close to the walls
	 * 
	 * @param robot
	 * @param opponent
	 * @param optimum
	 * @return
	 */
	protected Point getPointToAvoidObstacle(Robot robot, Robot opponent, Point optimum) {
		
		Point pointA = (calculatePosBehindBall(optimum.getAngleBetweenPoints(opponent) + Math.PI/4, opponent, gap*4));
		Point pointB = calculatePosBehindBall(optimum.getAngleBetweenPoints(opponent) - Math.PI/4, opponent, gap*4);
		double distancePointA = robot.getDistanceBetweenPoints(pointA) + pointA.getDistanceBetweenPoints(optimum);
		double distancePointB = robot.getDistanceBetweenPoints(pointB) + pointB.getDistanceBetweenPoints(optimum);
		
		if (!robot.isObstacleInFront(opponent, optimum, opponentWidth) && !robot.isObstacleInFront(opponent, optimum, opponentWidth)) {
			if (distancePointA < distancePointB) {
				return pointA;
			} else {
				return pointB;
			}
		}
		
		if (robot.isObstacleInFront(opponent, pointA, opponentWidth)) {
			return pointB;
		} 
		else if (robot.isObstacleInFront(opponent, pointB, opponentWidth)) 
		{	
			return pointA;
		} else {
			return pointA;
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
		int behindGap = 100;
		int ballWidth = 10;
		double angle = Math.PI/8;
		
		// checks to see if ball is inbetween behind point (robot too close to ball)
		if (robot.isObstacleInFront(ball, optimum, ballWidth)) {
			// Checks to see which side of the ball the robot is on
			if (goal.getX() == 0) {
				if (robot.getY() > optimum.getY()) {
				
					return calculatePosBehindBall(optimum.getAngleBetweenPoints(ball) + angle, ball, behindGap);

				} else {

					return calculatePosBehindBall( optimum.getAngleBetweenPoints(ball) - angle , ball, behindGap);

				}
			
		} else {
				// Checks to see which side of the ball the robot is on
				if (robot.getY() < optimum.getY()) {

					return calculatePosBehindBall(optimum.getAngleBetweenPoints(ball) + angle, ball, behindGap);

				} else {

					return calculatePosBehindBall( optimum.getAngleBetweenPoints(ball) - angle , ball, behindGap);
				
				}
		}
		} else {
			// TODO : FIX - point not correct
			// If ball is inbetween behind point and robot 
			return new Point(ball.getX(), ball.getY() + 50);
		}
	}
	
	/**
	 * Get point outside of ball far enough to have enough space to turn
	 * 
	 * @param ball
	 * @param goal
	 * @return
	 */
	protected Point getOptimumPoint(Ball ball, Goal goal) {
		return calculatePosBehindBall(goal.calculateGoalAndPointAngle(ball), ball, optimalGap, goal);
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
		int threshold = 30;
		
		return ball.getX() < PITCH_X_MIN + threshold && ball.getY() < PITCH_Y_MIN + threshold ||
			   ball.getX() > PITCH_X_MAX - threshold && ball.getY() < PITCH_Y_MIN + threshold ||
			   ball.getX() > PITCH_X_MAX - threshold && ball.getY() > PITCH_Y_MAX - threshold ||
			   ball.getX() < PITCH_X_MIN + threshold && ball.getY() > PITCH_Y_MAX - threshold;
	}
	
	/**
	 * Is ball out of pitch
	 * 
	 * @param ball
	 * @return
	 */
	protected boolean isBallOutOfPitch(Ball ball) {
		return ball.getX() < PITCH_X_MIN || ball.getX() > PITCH_X_MAX ||
			   ball.getY() < PITCH_Y_MIN || ball.getY() > PITCH_Y_MAX;
	}

	/**
	 * Calculates the new X,Y co-ordinates for a point behind the ball.
	 * Currently does this in relation to the goal at the far end of the pitch
	 * 
	 * @param ballGoalAngle
	 * @param ball
	 * @param gap
	 */
	protected Point calculatePosBehindBall(double ballGoalAngle, Point ball, int gap, Goal goal) {
		// Need to work out sin and cos distances to get new X and Y positions
		
		double xOffset = gap*Math.cos(ballGoalAngle);
		double yOffset = gap*Math.sin(ballGoalAngle);
			
		if (goal.getX() == 0) {
			return new Point(ball.getX()+xOffset, ball.getY()+yOffset);
		} else {
			return new Point(ball.getX()-xOffset, ball.getY()+yOffset);
		}
	}
	
	/**
	 * TODO:  Not sure if this is needed, need to change all other methods
	 * to take goal into account though, so will leave until that is done 
	 * 
	 * @param ballGoalAngle
	 * @param ball
	 * @param gap
	 * @return
	 */
	protected Point calculatePosBehindBall(double ballGoalAngle, Point ball, int gap) {
		// Need to work out sin and cos distances to get new X and Y positions
		double xOffset = gap*Math.cos(ballGoalAngle);
		double yOffset = gap*Math.sin(ballGoalAngle);
		
		
		return new Point(ball.getX()+xOffset, ball.getY()+yOffset);
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
	 * Is point close to walls
	 * 
	 * @param point
	 * @return
	 */
	protected boolean isWallClose(Point point){
		double x = point.getX();
		double y = point.getY();
		return (PITCH_X_MIN + wallThreshold >= x || PITCH_Y_MIN + wallThreshold >= y ||
				PITCH_Y_MAX - wallThreshold <= y || PITCH_X_MAX - wallThreshold <= x);
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
						"isObstacleInFront: " + robot.isObstacleInFront(opponent, optimum, opponentWidth),
						800, 50, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
						"isBallBehindRobot: " + isBallBehindRobot(robot, ball, optimum, goal),
						800, 70, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
						"!isRobotInOptimumPosition: " + !isRobotInOptimumPosition(robot, optimum),
						800, 90, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
						"isBallReached: " + isBallReached(robot, ball),
						800, 110, Color.BLACK));
		
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
				"Angle between optimum and opponent from robot view: " + formatter.format(Math.toDegrees(Math.abs(Math.abs(opponent.getAngleBetweenPoints(robot)) - Math.abs(optimum.getAngleBetweenPoints(robot))))),
				800, 190, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Difference of angles between robots: " + formatter.format(Math.abs(Math.abs(robot.getAngleBetweenPoints(optimum)) - Math.abs(opponent.getAngleBetweenPoints(optimum)))),
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
				"Ball Intercept: " + parameters[0], 800, 270, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Ball Slope: " + parameters[1], 800, 290, Color.BLACK));
		if(Math.abs(ballBuffer.getXPosAt(ballBuffer.getCurrentPosition())) -
				Math.abs(ballBuffer.getYPosAt(ballBuffer.getLastPosition())) < 0)
		//	parameters[1] = parameters[1] - Math.PI;
		drawables.add(new Drawable(Drawable.LINE,
				(int) ball.getX(), (int) (parameters[1]*ball.getX() + parameters[0]),
				(int) ball.getX()+100, (int) (parameters[1]*(ball.getX()+100) + parameters[0]),
				Color.CYAN, true));
		else
			drawables.add(new Drawable(Drawable.LINE,
					(int) ball.getX(), (int) (parameters[1]*ball.getX() + parameters[0]),
					(int) ball.getX()-100, (int) (parameters[1]*(ball.getX()-100) + parameters[0]),
					Color.CYAN, true));
		drawables.add(new Drawable(Drawable.LABEL,
				"Current: " + ballBuffer.getCurrentPosition(), 800, 310, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Last: " + ballBuffer.getLastPosition(), 800, 330, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Last: " + (ballBuffer.getLastPosition() == ballBuffer.getCurrentPosition()), 800, 350, Color.BLACK));
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
}
