package main.strategy;

import main.Strategy;
import main.data.Ball;
import main.data.Goal;
import main.data.Location;
import main.data.Point;
import main.data.Robot;

/** 
 * This is the strategy to be run when the opponent is in possession. 
 *
 */
public class DefensiveStrategy extends AbstractStrategy implements Strategy {

	@Override
	public void updateLocation(Location data) {
		
		Ball ball = data.getBall();
		Robot robot = data.getOurRobot();
		Robot opponent = data.getOpponentRobot();
		Goal goal = data.getGoal();

		Point defencePoint = getDefencePoint(opponent, goal);
		
		moveToPoint(robot, defencePoint);
	}

	/**
	 * Calculate a point inbetween the robot and the goal.
	 * @param opponent
	 * @param goal
	 * @return
	 */
	private Point getDefencePoint(Robot opponent, Goal goal) {
		double distance = 100;
		Goal opponentGoal = new Goal((550 - goal.getX()), goal.getY());
		double angle = opponent.angleBetweenPoints(opponentGoal);
		return null;
	}
}