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
	 * Stop robot
	 */
	public void stop();
	
	/**
	 * Send exit command to executor
	 */
	public void exit();	
}
