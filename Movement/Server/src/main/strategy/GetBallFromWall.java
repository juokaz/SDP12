package main.strategy;

import main.data.Ball;
import main.data.Goal;
import main.data.Location;
import main.data.Point;
import main.data.Robot;

public class GetBallFromWall extends GoToBall {

	private double optimalGap = this.getOptimalGap();
	
	@Override
	public void updateLocation(Location data) {
		super.updateLocation(data);
	}
	
	/**
	 * Function gets and draws a point which the ball can rebound from
	 * To be used when the ball is near the edge
	 * 
	 * @param ball
	 * @param goal
	 * @return
	 */
	private Point getReboundPoint(Ball ball, Goal goal) {
		
		double y = 0;
		double xb = ball.getX();
		double yb = ball.getY();
		double xg = goal.getX();
		double yg = goal.getY();
		
		if ( yb > 155 ){
			y = 310;
		}

		double x = (xb*(yg-y) + xg*(yb-y)) / (yb + yg - 2*y);
		
		Point rebound = new Point (x,y);
		
		drawPoint(rebound, "Rebound");
		
		return rebound;
		
	}
	
	/**
	 * Get point outside of ball far enough to have enough space to turn
	 * 
	 * @param ball
	 * @param goal
	 * @return
	 */
	protected Point getOptimumPoint(Ball ball, Goal goal) {
		Point rebound = getReboundPoint(ball, goal);
		
		double xOffset = optimalGap*Math.cos(ball.angleBetweenPoints(rebound));
		double yOffset = optimalGap*Math.sin(ball.angleBetweenPoints(rebound));
			
		return new Point(ball.getX()-xOffset, ball.getY()-yOffset);
	}
	
	/**
	 * Is robot close enough to a goal
	 * 
	 * @param robot
	 * @param goal
	 * @return
	 */
	protected boolean isRobotCloseToGoal(Robot robot, Goal goal) {
		return true;	
	}
	
}
