package main.gui;

import main.data.Location;
import main.processor.Listener;

public class GuiListener implements Listener {
	
	/**
	 * simulator to update on
	 */
	protected Simulator simulator;

	/**
	 * Gui listener
	 * 
	 * @param simulator
	 */
	public GuiListener(Simulator simulator) {
		this.simulator = simulator;
	}
	
	/**
	 * Update locations on screen
	 * 
	 * @param data
	 */
	public void updateLocation(Location location) {
		simulator.getRobot1().setXPosRemapped(location.getOurRobot().getX());
		simulator.getRobot1().setYPosRemapped(location.getOurRobot().getY());
		simulator.getRobot1().setTheta(location.getOurRobot().getT());
		simulator.getRobot2().setXPosRemapped(location.getOpponentRobot().getX());
		simulator.getRobot2().setYPosRemapped(location.getOpponentRobot().getY());
		simulator.getRobot2().setTheta(location.getOpponentRobot().getT());
		simulator.getBall().setXPosRemapped(location.getBall().getX());
		simulator.getBall().setYPosRemapped(location.getBall().getY());
	}
}
