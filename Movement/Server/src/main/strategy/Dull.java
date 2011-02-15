package main.strategy;

import main.Strategy;
import main.data.Location;
import main.data.Robot;
import main.data.VelocityVec;

public class Dull extends AbstractStrategy implements Strategy {

	public void updateLocation(Location data) {
		System.out.printf("Updated location: RobotA(%f, %f, %f), RobotB(%f, %f, %f), Ball(%f, %f)\n"
				, data.getRobotA().getX(), data.getRobotA().getY(), data.getRobotA().getT()
				, data.getRobotB().getX(), data.getRobotB().getY(), data.getRobotB().getT()
				, data.getBall().getX(), data.getBall().getY());
		
		Robot robotA = data.getRobotA();
		
		executor.move(robotA, (int) robotA.getX()+5, (int) robotA.getY());
		
		if (robotA.getX() == 5) {
			executor.kick(robotA);
		}
	}

	@Override
	public VelocityVec getVelocity() {
		// TODO Auto-generated method stub
		return null;
	}

}
