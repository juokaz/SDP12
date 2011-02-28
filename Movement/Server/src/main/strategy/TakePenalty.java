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
import main.gui.Drawable;

/**
 * @author Marc Howarth
 *
 */
/**
 * @author Marc Howarth
 *
 */
/**
 * @author Marc Howarth
 *
 */
/**
 * @author Marc Howarth
 *
 */
/**
 * @author Marc Howarth
 *
 */
public class TakePenalty extends AbstractStrategy implements Strategy {

	private boolean haveKicked = false;

	private double angleThreshold = Math.PI / 8;
	private int rotateSpeed = 100;
	private int forwardSpeed = 100;
	private int offsetGoal = 120; // used to aim 120 away from the centre of the goal

	public void updateLocation(Location data) {

		Ball ball = data.getBall();
		Robot robot = data.getOurRobot();
		Goal goal = data.getGoal();

		addDrawables(robot, ball, goal);

		if (!isAtRequiredAngle(robot, goal, ball) && !haveKicked) {
			// rotate to the correct angle
			double reqAngle = getRequiredAngle(goal, ball);
			rotateToShootAngle(robot, goal, reqAngle);
			setIAmDoing("Rotating");
		} else if (!haveKicked) {
			kick();
			setIAmDoing("Kicking");
		} else {
			setIAmDoing("Moving forward");
			moveForwardToShootAgain();
		}

		setDrawables(drawables);
	}

	/**
	 * Activate the kicker
	 */
	protected void kick() {
		executor.kick();
		haveKicked = true;
	}	
	
	/**
	 * Moves the robot forward if the ball has been missed
	 */
	protected void moveForwardToShootAgain() {
		executor.rotateWheels(forwardSpeed, forwardSpeed);
		haveKicked = false;
	}

	/**
	 * Uses the offset to ensure the robot doesn't shoot penalty in the centre
	 * 
	 * @param goal
	 * @param ball
	 * @return
	 */
	protected double getRequiredAngle(Goal goal, Ball ball) {
		double reqTheta = Math.atan2(goal.getY() + offsetGoal - ball.getY(),
				goal.getX() - ball.getX());
		return reqTheta;
	}

	/**
	 * Rotate the robot to face an angle ready for penalty
	 * 
	 * @param robot
	 * @param goal
	 * @param angle
	 */
	protected void rotateToShootAngle(Robot robot, Goal goal, Double angle) {
		while (Math.abs(robot.getT() - angle) > angleThreshold) {
			executor.rotateWheels(rotateSpeed, -rotateSpeed);
		}
	}
	
	/**
	 * Check if the robot is facing the required angle for taking penalty.
	 * 
	 * @param robot
	 * @param goal
	 * @param ball
	 * @return
	 */
	protected boolean isAtRequiredAngle(Robot robot, Goal goal, Ball ball) {
		if (Math.abs(robot.getT() - getRequiredAngle(goal, ball)) > angleThreshold)
			return false;
		else
			return true;
	}

	protected void setIAmDoing(String name) {
		drawables.add(new Drawable(Drawable.LABEL, "I am doing: " + name, 800,
				140, Color.RED));
	}

	protected void addDrawables(Robot robot, Ball ball, Goal goal) {
		// Creating new object every time because of reasons related to
		// the way Swing draws stuff. Will be fixed later.
		drawables = new ArrayList<Drawable>();

		NumberFormat formatter = new DecimalFormat("#0.00");

		// Robot states of execution
		drawables.add(new Drawable(Drawable.LABEL, "isAtRequiredAngle: "
				+ isAtRequiredAngle(robot, goal, ball), 800, 30, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL, "haveKicked: " + haveKicked,
				800, 50, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL, "Ball (X, Y): "
				+ formatter.format(ball.getX()) + " "
				+ formatter.format(ball.getY()), 450, 50, Color.WHITE));
	}

}