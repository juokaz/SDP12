package main.executor;

import main.Executor;
import main.data.Ball;
import main.data.Goal;
import main.data.Location;
import main.data.Robot;
import main.processor.AbstractProcessor;

public class Simulator implements Executor {
	main.gui.Pitch pitch;

	public Simulator(main.gui.Pitch pitch) {
		this.pitch = pitch;
		pitch.getRobot1().getKicker().enable();
		pitch.getRobot2().getKicker().enable();
	}

	@Override
	public void rotateWheels(final int leftWheelSpeed, final int rightWheelSpeed) {
		// TODO FIX THIS
		pitch.getRobot1().move(rightWheelSpeed, leftWheelSpeed);
	}

	@Override
	public void kick() {
		pitch.getRobot1().kick();
	}

	@Override
	public void rotate(int T) {
		// TODO Auto-generated method stub
		pitch.getRobot1().rotate(T);
	}
	
	@Override
	public void exit() {
		
	}

	@Override
	public void stop() {
		pitch.getRobot1().move(0, 0);
	}

	@Override
	public void celebrate() {
		// TODO Auto-generated method stub
		
	}
}
