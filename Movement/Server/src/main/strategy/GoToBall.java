package main.strategy;

import main.Strategy;
import main.data.Ball;
import main.data.Location;
import main.data.Robot;


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
		Robot robotA = data.getRobotB();
		Ball ball = data.getBall();
	
		moveToPoint(robotA, ball);
	}
	
	public void setGap(int newGap) {
		gap = newGap;
	}
	
	public int getGap() {
		return gap;
	}

}
