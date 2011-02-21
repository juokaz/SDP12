package main.strategy;

import java.awt.Color;
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
	private int gap;
	
	@Override
	public void updateLocation(Location data) {
		// Creating new object every time because of reasons related to
		// the way Swing draws stuff. Will be fixed later.
		drawables = new ArrayList<Drawable>();
		setGap(50);
		Ball ball = data.getBall();
		Robot robot = data.getOurRobot();
		Robot opponent = data.getOpponentRobot();
		Goal goal = data.getGoal();
		
		Point optimum = getOptimumPoint(ball, goal);
		
		// statistics
		addDrawables(robot, opponent, ball, goal, optimum);		
		
		// This state machine covers the basic stages robot can be in 
		if (isBallOutOfPitch(ball)) {
			setIAmDoing("Ball out of pitch");
			// We have scored (hopefully)
			executor.stop();
		} else if (isBallInACorner(ball)) {
			setIAmDoing("Ball in a corner");
			// Don't do anything, wait for it move from there
			executor.stop();
		} else if (isObstacleInFront(robot, opponent, optimum)) {
			setIAmDoing("Obstacle in front");
			Point point = getPointToAvoidObstacle(robot, opponent, optimum);
			moveToPoint(robot, point);
		} else if (isBallBehindRobot(robot, ball, optimum)) {
			setIAmDoing("Ball behing robot");
			Point point = getPointToFaceBallFromCorrectSide(robot, ball, optimum);
			moveToPoint(robot, point);
		} else if (!isRobotInOptimumPosition(robot, optimum)) {
			setIAmDoing("Not in optimum");
			moveToPoint(robot, optimum);
		} else if (isBallReached(robot, ball)) {
			setIAmDoing("Kick");
			executor.kick();
		} else {
			setIAmDoing("Reaching ball");
			moveToPoint(robot, ball);
		}
		
		executor.setDrawables(drawables);
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
				return calculatePosBehindBall(ball.getAngleBetweenPoints(optimum) + 45, ball, gap*2);
			} else {
				return calculatePosBehindBall(ball.getAngleBetweenPoints(optimum) - 45, ball, gap*2);
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
		// TODO implement this
		return false;
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
	protected Point calculatePosBehindBall(double ballGoalAngle, Ball ball, int gap) {
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
	 * @param goal
	 * @param optimum
	 */
	protected void addDrawables(Robot robot, Robot opponent, Ball ball, Goal goal, Point optimum) {

		drawables.add(new Drawable(Drawable.CIRCLE,
						(int) goal.getX(), (int) goal.getY(), Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL,
						"Goal",
						(int) goal.getX() + 5, (int) goal.getY(), Color.WHITE));
		drawables.add(new Drawable(Drawable.CIRCLE,
						(int) opponent.getX(), (int) opponent.getY(), Color.WHITE));
		drawables.add(new Drawable(Drawable.CIRCLE,
						(int) optimum.getX(), (int) optimum.getY(), Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL,
						"Optimum",
						(int) optimum.getX() + 5, (int) optimum.getY(), Color.WHITE));
		
		drawables.add(new Drawable(Drawable.LABEL,
						"Optimum (X, Y): " + (int) optimum.getX() + " " + (int) optimum.getY(),
						250, 30, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL,
						"goalAndPointAngle: " + (float) Math.toDegrees(goal.calculateGoalAndPointAngle(ball)),
						250, 50, Color.WHITE));
		
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
						"Ball (X, Y): " + ball.getX() + " " + ball.getY(),
						450, 50, Color.WHITE));
	}
	
	/**
	 * Set information about what I'm doing doing right now
	 * 
	 * @param name
	 */
	protected void setIAmDoing(String name) {
		drawables.add(new Drawable(Drawable.LABEL,
						"I am doing: " + name,
						800, 150, Color.RED));
	}
	
	public void setGap(int newGap) {
		gap = newGap;
	}
	
	public int getGap() {
		return gap;
	}
}
