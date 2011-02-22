package main.processor;

import java.util.ArrayList;
import java.util.List;

import main.Processor;
import main.data.Location;

public abstract class AbstractProcessor implements Processor {
	
	protected boolean stopped = false;
	
	private boolean our_robot_first = false;
	
	private boolean left_goal = true;
	
	private List<Listener> listeners;
	
	public void stop() {
		stopped = true;
	}
	
	public void run(boolean our_robot, boolean left_goal) {
		stopped = false;
		this.our_robot_first = our_robot;
		this.left_goal = left_goal;
	}
	
	/**
	 * Add listener
	 * 
	 * @param listener
	 */
	public void addListener(Listener listener)
	{
		if (listeners == null) {
			listeners = new ArrayList<Listener>();
		}
		
		listeners.add(listener);
	}
	
	/**
	 * Update location in all listeners
	 * 
	 * @param location
	 */
	protected void propogateLocation(Location location)
	{
		for (Listener listener : listeners) {
			listener.updateLocation(location);
		}
	}
	
	/**
	 * Is the first robot returned by processor (given that it returns ROBOT BALL ROBOT)
	 * our robot or opponent robot
	 * 
	 * @return
	 */
	protected boolean isOurRobotFirst() {
		return our_robot_first;
	}
	
	/**
	 * Is the Goal we are aiming towards the left or the right.
	 * 
	 * @return
	 */
	protected boolean isGoalLeft() {
		return left_goal;
	}
}
