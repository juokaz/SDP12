package main;

/**
 * Executor interface
 * 
 * This interface defines component executing commands from strategy
 */
public interface Executor {

	/**
	 * Set wheels spends to X and Y for a robot
	 * 
	 * @param X
	 * @param Y
	 */
	public void rotateWheels(int leftWheelSpeed, int rightWheelSpeed);
	
	/**
	 * Execute a kick
	 */
	public void kick();
	
	/**
	 * Rotate robot
	 * 
	 * @param T
	 */
	public void rotate(int T);
	
	/**
	 * Start executor running (initialise what is needed)
	 */
	public void start();
	
	/**
	 * Stop executor running
	 */
	public void stop();
	
	/**
	 * Celebrate
	 */
	public void celebrate();
	
	/**
	 * Take Penalty
	 */
	public void takePenalty();
	
	/**
	 * Send exit command to executor
	 */
	public void exit();
}
