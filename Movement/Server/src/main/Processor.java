package main;

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
