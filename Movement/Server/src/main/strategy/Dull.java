package main.strategy;

import main.Strategy;
import main.data.Location;

/**
 * This strategy is used to make sure no commands are being sent to robot
 */
public class Dull extends AbstractStrategy implements Strategy {
	
	private boolean sentStop = false;
	
	@Override
	public void updateLocation(Location data) {
		if (sentStop) {
			executor.stop();
			sentStop = true;
		}
	}
}