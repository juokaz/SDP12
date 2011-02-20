package main;

import main.data.Location;
import main.data.VelocityVec;

/**
 * Strategy interface
 * 
 * This part comes up with moves to execute
 */
public interface Strategy {

	/**
	 * Every piece of data comes from processor to here
	 * 
	 * @param data
	 */
	public void updateLocation(Location data);
	
	/**
	 * Executor is required
	 * 
	 * @param executor
	 */
	public void setExecutor(Executor executor);
}
