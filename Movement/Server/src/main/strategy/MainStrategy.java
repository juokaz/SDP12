package main.strategy;

import main.Strategy;
import main.data.Ball;
import main.data.Goal;
import main.data.Location;
import main.data.Robot;

/**
 * Implement another state machine to decide on the strategy that needs to be used at 
 * the time on the pitch.?
 * 
 */
public class MainStrategy extends AbstractStrategy implements Strategy {

	private GoToBall stratGTB;
	private GetBallFromWall stratGBFW;
	
	public MainStrategy() {
		stratGTB = new GoToBall();
		stratGTB.setExecutor(executor);
		stratGBFW = new GetBallFromWall();
		stratGBFW.setExecutor(executor);
		// TODO: implement a defensive strategy.
	}
	
	@Override
	public void updateLocation(Location data) {
		Ball ball = data.getBall();
		Robot robot = data.getOurRobot();
		Robot opponent = /*new Robot(-1,-1,-1);*/data.getOpponentRobot();
		Goal goal = data.getGoal();

		if (isOpponentInPossession(opponent, ball, goal)) {
			// TODO: call defensive strategy.
		}
		else if (isWallClose(ball))
		{
			// If ball is close to the wall, call the GetBallFromWall strategy
			stratGBFW.updateLocation(data);
		}
		else if (isBallInACorner(ball))
		{
			// TODO: Strategy for ball in corner
		} 
		else if (ball.isPointOutOfPitch())
		{
		//	setIAmDoing("Ball out of pitch");
			// We have scored (hopefully)
		//	executor.celebrate();
			executor.stop();
		} 
		else 
		{
			stratGTB.updateLocation(data);
		}	
	}
}