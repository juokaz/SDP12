package main.strategy;

import main.Strategy;
import main.data.Goal;
import main.data.Location;
import main.data.Robot;

public class DefPenalty extends AbstractStrategy implements Strategy {

	//this strategy assumes that our robot is always facing +90 degrees.
	// i.e. facing the vision computers.
	String robotState = "Center";
	int rotateSpeed = 0;
	
	@Override
	public void updateLocation(Location data) {
		
		Robot opponent = data.getOpponentRobot();
		Goal goal = data.getGoal();
		
		double opponentAngle = opponent.getT();
		double threshold = Math.toRadians(10);
		
		if ( goal.getX() == 0 ){
			//opponent shooting left
			if ( opponentAngle > Math.PI - threshold )
				moveTo("Up");
			if ( opponentAngle < -Math.PI + threshold )
				moveTo("Down");
			else
				moveTo("Center");
				
		} else {
			//opponent shooting right
			if ( opponentAngle > threshold )
				moveTo("Up");
			if ( opponentAngle < -threshold )
				moveTo("Down");
			else
				moveTo("Center");
		}
		
	}

	private void moveTo(String moveTo) {
		
		rotateSpeed = 0;
		
		if (robotState == "Center" && moveTo == "Up")
			rotateSpeed = 50;
		if (robotState == "Center" && moveTo == "Down")
			rotateSpeed = -50;
		if (robotState == "Up" && moveTo == "Center")
			rotateSpeed = -50;	
		if (robotState == "Up" && moveTo == "Down")
			rotateSpeed = -100;
		if (robotState == "Down" && moveTo == "Center")
			rotateSpeed = 50;
		if (robotState == "Down" && moveTo == "Up")
			rotateSpeed = 100;
		
		executor.rotateWheels(rotateSpeed, rotateSpeed);
	}

	
}
