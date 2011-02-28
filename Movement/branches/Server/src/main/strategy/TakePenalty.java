package main.strategy;

import main.Strategy;
import main.data.Location;
import main.data.Robot;
import main.data.Point;

public class TakePenalty extends AbstractStrategy implements Strategy {

	//a simple strategy for taking a penalty 
	
	public void updateLocation(Location data) {

		Robot robotA = data.getOurRobot();
		
		//define a threshold for knowing which way the robot is facing
		float angThresh = 30;
		float theta = robotA.getT();
		
		//the following calculations are based on the image size being 350/55
		// TODO double check these numbers and adjust the maths formulas accordingly
		
		if (theta <= 0 + angThresh && theta >= 0 - angThresh) {			
			//get current position of robot			
			Point robot = new Point((int)robotA.getX(),(int) robotA.getY());
			
			//do some crazy maths to work out the required angle
			double reqTheta = Math.atan((265-robot.getY())/(500-robot.getX()));
			
			executor.rotate((int)(reqTheta-theta));
			
			executor.kick();
		}
		
		else if ( theta <= 180 - angThresh && theta >= 180 + angThresh ) {			
			//get current position of robot			
			Point robot = new Point((int)robotA.getX(),(int) robotA.getY());
			
			//do some crazy maths to work out the required angle
			double reqTheta = Math.atan((265-robot.getY())/(robot.getX()));
			
			executor.rotate((int)(reqTheta-theta));
			
			executor.kick();
		}
		
		else {
			System.out.println("TakePenalty::Cannot find suitable theta");
		}
	}
}