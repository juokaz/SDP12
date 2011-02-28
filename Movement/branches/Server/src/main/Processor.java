package main;

import main.processor.Listener;

/**
 * Processor interface
 * 
 * This component processed data and sends commands to stragey
 */
public interface Processor {

	/**
	 * Start processing data
	 * 
	 * @param our_robot
	 */
	public void run(boolean our_robot, boolean left_goal);
	
	/**
	 * Stop processing data
	 */
	public void stop();
	
	/**
	 * Add location data listener
	 * 
	 * @param listener
	 */
	public void addListener(Listener listener);
}
