package main;

import main.data.Robot;

/**
 * Executor interface
 * 
 * This interface defines component executing commands from strategy
 */
public interface Executor {

	/**
	 * Move robot to X and Y location
	 * 
	 * @param robot
	 * @param X
	 * @param Y
	 */
	public void move(Robot robot, int X, int Y);
	
	/**
	 * Set wheels spends to X and Y for a robot
	 * 
	 * @param robot
	 * @param X
	 * @param Y
	 */
	public void rotateWheels(Robot robot, int leftWheelSpeed, int rightWheelSpeed);
	
	/**
	 * Execute a kick
	 * 
	 * @param robot
	 */
	public void kick(Robot robot);
	
	/**
	 * Rotate robot
	 * 
	 * @param robot
	 * @param T
	 */
	public void rotate(Robot robot, int T);
	
	/**
	 * Stop robot
	 * 
	 * @param robot
	 */
	public void stop(Robot robot);
	
	/**
	 * Send exit command to executor
	 */
	public void exit();
	
	/**
	 * Send exit command to robot connected to executor
	 * @param robot
	 */
	public void exit(Robot robot);
	
}
