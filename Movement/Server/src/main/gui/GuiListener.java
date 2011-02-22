package main.gui;

import main.data.Location;
import main.processor.Listener;

public class GuiListener implements Listener {
	
	protected Pitch pitch;

	public GuiListener(Pitch pitch) {
		this.pitch = pitch;
	}
	
	public void updateLocation(Location location) {
		pitch.robot1.setXPosRemapped(location.getOurRobot().getX());
		pitch.robot1.setYPosRemapped(location.getOurRobot().getY());
		pitch.robot1.setTheta(location.getOurRobot().getT());
		pitch.robot2.setXPosRemapped(location.getOpponentRobot().getX());
		pitch.robot2.setYPosRemapped(location.getOpponentRobot().getY());
		pitch.robot2.setTheta(location.getOpponentRobot().getT());
		pitch.ball.setXPosRemapped(location.getBall().getX());
		pitch.ball.setYPosRemapped(location.getBall().getY());
	}
}
