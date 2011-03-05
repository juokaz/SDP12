package main.processor;

import main.data.Location;

/**
 * Listener of data from processor
 */
public interface Listener {

	/**
	 * Receive new location
	 * 
	 * @param location
	 * @param isOurRobotFirst
	 */
	public void updateLocation(Location location, boolean isOurRobotFirst);
}
