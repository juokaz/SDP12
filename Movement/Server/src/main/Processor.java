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
	public void run();
	
	/**
	 * Stop processing data
	 */
	public void stop();
	
	/**
	 * Set our robot
	 * 
	 * @param first
	 */
	public void setOurRobot(boolean first);
	
	/**
	 * Set left goal
	 * 
	 * @param left
	 */
	public void setLeftGoal(boolean left);
	
	/**
	 * Add location data listener
	 * 
	 * @param listener
	 */
	public void addListener(Listener listener);
}
