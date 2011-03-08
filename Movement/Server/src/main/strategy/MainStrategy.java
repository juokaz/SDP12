package main.strategy;

import main.Executor;
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
	private DefensiveStrategy stratDef;
	
	public MainStrategy() {
		stratGTB = new GoToBall();
		stratGBFW = new GetBallFromWall();
		stratDef = new DefensiveStrategy();
	}
	
	@Override
	public void updateLocation(Location data) {
		Ball ball = data.getBall();
		Robot robot = data.getOurRobot();
		Robot opponent = /*new Robot(-1,-1,-1);*/data.getOpponentRobot();
		Goal goal = data.getGoal();
		
		
		addDrawables(robot, opponent, ball, robot, goal);
		setDrawables(drawables);
		
		if (isRobotInPossession(opponent, ball, goal)) {
			setIAmDoing("Opponent in possesion");
			stratDef.updateLocation(data);
		}
		else if (isWallClose(ball))
		{
			setIAmDoing("Ball is close to wall");
			// If ball is close to the wall, call the GetBallFromWall strategy
			stratGBFW.updateLocation(data);
		}
		else if (isBallInACorner(ball))
		{
			setIAmDoing("Ball is in corner");
			// TODO: Strategy for ball in corner
		} 
		else if (ball.isPointOutOfPitch())
		{
			setIAmDoing("Ball out of pitch");
			// We have scored (hopefully)
		//	executor.celebrate();
			executor.stop();
		} 
		else 
		{
			setIAmDoing("Go To Ball");
			// Attacking case
			stratGTB.updateLocation(data);
		}	
	}
	
	public void setExecutor(Executor executor) {
		super.setExecutor(executor);
		stratGTB.setExecutor(executor);
		stratGBFW.setExecutor(executor);
	}
}