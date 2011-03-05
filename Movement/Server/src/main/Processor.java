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
	 */
	public void run();
	
	/**
	 * Is processing data
	 * 
	 * @return boolean
	 */
	public boolean isRunning();
	
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
