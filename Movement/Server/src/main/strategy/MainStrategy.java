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
 */
public class MainStrategy extends AbstractStrategy implements Strategy {

	private GoToBall stratGTB;
	private GetBallFromWall stratGBFW;
	private DefensiveStrategy stratDef;
	private boolean initial = true;
	
	public MainStrategy() {
		// instantiate sub-strategies
		// make them use parent drawables list
		stratGTB = new GoToBall();
		stratGTB.useDrawables(drawables);
		stratGBFW = new GetBallFromWall();
		stratGBFW.useDrawables(drawables);
		stratDef = new DefensiveStrategy();
		stratDef.useDrawables(drawables);
	}
	
	@Override
	public void updateLocation(Location data) {
		Ball ball = data.getBall();
		Robot opponent = data.getOpponentRobot();
		Goal goal = data.getGoal();
		Robot robot = data.getOurRobot();
		/*
		if (initial) {
			if (!isBallReached(robot,goal,ball))
				executor.rotateWheels(460, 500);
			else {
				executor.kick();
				executor.rotateWheels(0, 0);
				initial = false;
			} 
		} else */if (isRobotInPossession(opponent, ball, goal)) {
			setIAmDoingSecond("[Master] Opponent in possesion");
			stratDef.updateLocation(data);
		}
		else if (isWallClose(ball))
		{
			setIAmDoingSecond("[Master] Ball is close to wall");
			// If ball is close to the wall, call the GetBallFromWall strategy
			stratGBFW.updateLocation(data);
		}
		else if (isBallInACorner(ball))
		{
			setIAmDoingSecond("[Master] Ball is in corner");
			// TODO: Strategy for ball in corner
		} 
		else if (isPointOutOfPitch(ball))
		{
			setIAmDoingSecond("[Master] Ball out of pitch");
			// We have scored (hopefully)
		//	executor.celebrate();
			executor.stop();
		} 
		else 
		{
			setIAmDoingSecond("[Master] Go To Ball");
			// Attacking case
			stratGTB.updateLocation(data);
		}	
		
		setDrawables(drawables);
	}
	
	/**
	 * Set executor
	 * 
	 * @param executor
	 */
	public void setExecutor(Executor executor) {
		super.setExecutor(executor);
		stratGTB.setExecutor(executor);
		stratGBFW.setExecutor(executor);
		stratDef.setExecutor(executor);
	}
}