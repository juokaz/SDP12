package main.gui;

import main.data.Location;
import main.processor.Listener;

public class GuiListener implements Listener {
	
	protected Pitch pitch;

	public GuiListener(Pitch pitch) {
		this.pitch = pitch;
	}
	
	public void updateLocation(Location location) {
		pitch.robot1.setXPos(location.getOurRobot().getX());
		pitch.robot1.setYPos(location.getOurRobot().getY());
		pitch.robot1.setTheta(location.getOurRobot().getT());
		pitch.robot2.setXPos(location.getOpponentRobot().getX());
		pitch.robot2.setYPos(location.getOpponentRobot().getY());
		pitch.robot2.setTheta(location.getOpponentRobot().getT());
		pitch.ball.setXPos(location.getBall().getX());
		pitch.ball.setYPos(location.getBall().getY());
	}
}
