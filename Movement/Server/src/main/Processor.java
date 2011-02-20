package main;

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
	public void run(boolean our_robot);
	
	/**
	 * Stop processing data
	 */
	public void stop();
	
	/**
	 * Strategy is required for this to work
	 * 
	 * @param strategy
	 */
	public void setStrategy(Strategy strategy);
}
