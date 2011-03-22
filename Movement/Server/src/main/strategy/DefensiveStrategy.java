package main.strategy;

import main.Strategy;
import main.data.Ball;
import main.data.Goal;
import main.data.Location;
import main.data.Point;

/** 
 * This is the strategy to be run when the opponent is in possession. 
 *
 */
public class DefensiveStrategy extends GoToBall implements Strategy {

	@Override
	public void updateLocation(Location data) {
		super.updateLocation(data);
		
	}

	/**
	 * Calculate a point inbetween the robot and the goal.
	 * @param opponent
	 * @param goal
	 * @return
	 */
	private Point getDefencePoint(Ball ball, Goal goal) {
		Point defencePoint = new Point(0,0);
		Goal opponentGoal = new Goal((550 - goal.getX()), goal.getY());
		//divide by 2 in order to find mid point
		double distance = opponentGoal.getDistanceBetweenPoints(ball);
		double angle = opponentGoal.angleBetweenPoints(ball);
		
		//find a point that is the midpoint between the goal and the opponent
		defencePoint.setX(opponentGoal.getX() + (distance/2)*Math.cos(angle));
		defencePoint.setY(opponentGoal.getY() + (distance/2)*Math.sin(angle));
		
		//currently return goal to avoid null pointer exceptions
		return defencePoint;
	}
	
	/**
	 * Get an optimum point for defensive strategy
	 * Place the optimum point directly inbetween the opponent and their goal
	 * 
	 * @param ball
	 * @param goal
	 * @return
	 */
	protected Point getOptimumPoint(Ball ball, Goal goal) {
		
		Point defPoint = getDefencePoint(ball, goal);
					
		return defPoint;
	}
}