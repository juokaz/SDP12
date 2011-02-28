package main.executor;

import main.Executor;

public class Simulator implements Executor {
	main.gui.Simulator simulator;

	public Simulator(main.gui.Simulator simulator) {
		this.simulator = simulator;
	}

	@Override
	public void rotateWheels(final int leftWheelSpeed, final int rightWheelSpeed) {
		// TODO FIX THIS
		simulator.getRobot1().move(rightWheelSpeed, leftWheelSpeed);
	}

	@Override
	public void kick() {
		simulator.getRobot1().kick();
	}

	@Override
	public void rotate(int T) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void exit() {
		
	}

	@Override
	public void stop() {
		simulator.getRobot1().move(0, 0);
	}

	@Override
	public void celebrate() {
		// TODO Auto-generated method stub
		
	}
}
