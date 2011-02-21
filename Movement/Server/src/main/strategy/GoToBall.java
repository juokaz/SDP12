package main.strategy;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import sdp12.simulator.Drawable;
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
	private int gap = 50;
	
	@Override
	public void updateLocation(Location data) {
		Ball ball = data.getBall();
		Robot robot = data.getOurRobot();
		Robot opponent = data.getOpponentRobot();
		Goal goal = data.getGoal();
		Point optimum = getOptimumPoint(ball, goal);
		
		// statistics
		addDrawables(robot, opponent, ball, optimum);
		drawPoint(opponent, null);
		drawPoint(goal, "Goal");
		drawPoint(optimum, "Optimum");		

		System.out.println(PITCH_Y_MAX);
		// This state machine covers the basic stages robot can be in 
		if (isBallOutOfPitch(ball))
		{
			setIAmDoing("Ball out of pitch");
			// We have scored (hopefully)
			executor.stop();
		}
		else if (isBallInACorner(ball))
		{
			setIAmDoing("Ball in a corner");
			// Don't do anything, wait for it move from there
			executor.stop();
		}
		else if (isObstacleInFront(robot, opponent, optimum))
		{
			setIAmDoing("Obstacle in front");
			Point point = getPointToAvoidObstacle(robot, opponent, optimum);
			drawPoint(point, "Avoid");
			moveToPoint(robot, point);
		}
		else if (isBallBehindRobot(robot, ball, optimum))
		{
			setIAmDoing("Ball behind robot");
			Point point = getPointToFaceBallFromCorrectSide(robot, ball, optimum);
			drawPoint(point, "Behind");
			moveToPoint(robot, point);
		}
		else if (!isRobotInOptimumPosition(robot, optimum))
		{
			setIAmDoing("Not in optimum");
			moveToPoint(robot, optimum);
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
		
		executor.setDrawables(drawables);
	}
	
	/**
	 * Is it possible for a ball to be reached by going straight?
	 * Currently this checks to see if there is an obstacle inbetween the robot and
	 * the optimum point, judging this by the difference in angles between the 
	 * optimum to Robot angle and the optimum to Opponent angle. 
	 * 
	 * TODO: Calculate this for when the ball is between two Y-axis values
	 * TODO: Make this take into account distance from the obstacle, as difference in 
	 * angle will need to be larger the further from the ball we are.
	 * 
	 * @param robot
	 * @param opponent
	 * @param optimum
	 * @return
	 */
	protected boolean isObstacleInFront(Robot robot, Robot opponent, Point optimum) {
		return  Math.abs(Math.abs(robot.getAngleBetweenPoints(optimum)) - Math.abs(opponent.getAngleBetweenPoints(optimum))) < 0.5 &&
				robot.getDistanceBetweenPoints(optimum) > opponent.getDistanceBetweenPoints(optimum) && 
				((opponent.getX() <= robot.getX() && opponent.getX() >= optimum.getX() || 
						(opponent.getX() >= robot.getX() && opponent.getX() <= optimum.getX())));
	}
	
	/**
	 * Get a point which would make robot to avoid obstacle and go to a point
	 * which will have straight path to a ball
	 * 
	 * TODO: Calculating based on Y-axis doesn't work.
	 * 
	 * @param robot
	 * @param opponent
	 * @param optimum
	 * @return
	 */
	protected Point getPointToAvoidObstacle(Robot robot, Robot opponent, Point optimum) {
		if (robot.getY() > opponent.getY()) {				

			return calculatePosBehindBall(optimum.getAngleBetweenPoints(opponent) + 45, opponent, gap*2);
		} else {	
			return calculatePosBehindBall(optimum.getAngleBetweenPoints(opponent) - 45, opponent, gap*2);
		}
	}
	
	/**
	 * Is robot is further away from a goal than a ball or in other words
	 * if we would go to a ball, can we kick from there or do we need to go to
	 * the other side?
	 * 
	 * TODO: Change this or take into account earlier that robot could be in position 
	 * where it could get to ball 
	 * 
	 * @param robot
	 * @param ball
	 * @param optimum
	 * @return
	 */
	protected boolean isBallBehindRobot(Robot robot, Ball ball, Point optimum) {
		//TODO
		Point point = getPointToFaceBallFromCorrectSide(robot, ball, optimum);
		if (robot.isInPoint(point, 5)) {
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
	protected Point getPointToFaceBallFromCorrectSide(Robot robot, Ball ball, Point optimum) {		
		// If robot is not very close to the ball	
		if (robot.getDistanceBetweenPoints(ball) > 20) {
			// Checks to see which side of the ball the robot is on
			if (robot.getY() > optimum.getY()) {				

				return calculatePosBehindBall(optimum.getAngleBetweenPoints(ball) + 45, ball, gap);
			} else {	
				return calculatePosBehindBall( optimum.getAngleBetweenPoints(ball) - 45 , ball, gap);
			}
		} else {
			// TODO: maybe just turn and move ball?
		}
		
		
		
		return new Point(ball.getX(), ball.getY() + 50);
		
	}
	
	/**
	 * Get point outside of ball far enough to have enough space to turn
	 * 
	 * @param ball
	 * @param goal
	 * @return
	 */
	protected Point getOptimumPoint(Ball ball, Goal goal) {
		return calculatePosBehindBall(goal.calculateGoalAndPointAngle(ball), ball, gap);
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
	 * TODO: Create method that takes into account which goal we are aiming towards.
	 * 
	 * @param ballGoalAngle
	 * @param ball
	 * @param gap
	 */
	protected Point calculatePosBehindBall(double ballGoalAngle, Point ball, int gap) {
		// Need to work out sin and cos distances to get new X and Y positions
		double xOffset = gap*Math.cos(ballGoalAngle);
		double yOffset = gap*Math.sin(ballGoalAngle);
		
		return new Point(ball.getX()+xOffset, ball.getY()+yOffset);
	}
	
	/**
	 * Add drawables
	 * 
	 * @param robot
	 * @param opponent
	 * @param ball
	 * @param optimum
	 */
	protected void addDrawables(Robot robot, Robot opponent, Ball ball, Point optimum) {
		// Creating new object every time because of reasons related to
		// the way Swing draws stuff. Will be fixed later.
		drawables = new ArrayList<Drawable>();
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		// Robot states of execution
		drawables.add(new Drawable(Drawable.LABEL,
						"isBallInACorner: " + isBallInACorner(ball),
						800, 30, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
						"isObstacleInFront: " + isObstacleInFront(robot, opponent, optimum),
						800, 50, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
						"isBallBehindRobot: " + isBallBehindRobot(robot, ball, optimum),
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
				"Angle between optimum and robot: " + formatter.format(Math.toDegrees(robot.getAngleBetweenPoints(optimum))),
				800, 170, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Angle between optimum and opponent: " + formatter.format(Math.toDegrees(opponent.getAngleBetweenPoints(optimum))),
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
		drawables.add(new Drawable(Drawable.CIRCLE,
						(int) point.getX(), (int) point.getY(), Color.WHITE));
		if (label != null) {
			drawables.add(new Drawable(Drawable.LABEL,
					label,
					(int) point.getX() + 5, (int) point.getY(), Color.WHITE));
		}
	}
	
	public void setGap(int newGap) {
		gap = newGap;
	}
	
	public int getGap() {
		return gap;
	}
}
