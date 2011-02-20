package main.processor;

import main.Processor;
import main.Strategy;

public abstract class AbstractProcessor implements Processor {
	
	protected Strategy strategy = null;
	
	protected boolean stopped = false;
	
	protected boolean our_robot_first = false;

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
	public void stop() {
		stopped = true;
	}
	
	public void run(boolean our_robot) {
		stopped = false;
		this.our_robot_first = our_robot;
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
}
