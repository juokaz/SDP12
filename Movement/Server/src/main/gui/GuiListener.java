package main.gui;

import main.data.Location;
import main.processor.Listener;

public class GuiListener implements Listener {
	
	protected Pitch pitch;

	public GuiListener(Pitch pitch) {
		this.pitch = pitch;
	}
	
	public void updateLocation(Location location) {
		pitch.getRobot1().setXPosRemapped(location.getOurRobot().getX());
		pitch.getRobot1().setYPosRemapped(location.getOurRobot().getY());
		pitch.getRobot1().setTheta(location.getOurRobot().getT());
		pitch.getRobot2().setXPosRemapped(location.getOpponentRobot().getX());
		pitch.getRobot2().setYPosRemapped(location.getOpponentRobot().getY());
		pitch.getRobot2().setTheta(location.getOpponentRobot().getT());
		pitch.getBall().setXPosRemapped(location.getBall().getX());
		pitch.getBall().setYPosRemapped(location.getBall().getY());
	}
}
