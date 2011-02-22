package main.strategy;

import main.Strategy;
import main.data.Location;
import main.processor.Listener;

public class ProcessorListener implements Listener {
	
	/**
	 * Strategy to update to
	 */
	protected Strategy strategy;

	/**
	 * Processor listener
	 * 
	 * Updates strategy with new locations
	 * 
	 * @param strategy
	 */
	public ProcessorListener(Strategy strategy) 
	{
		this.strategy = strategy;
	}
	
	/**
	 * Update location for strategy
	 * 
	 * @param data
	 */
	public void updateLocation(Location data) 
	{
		strategy.updateLocation(data);
	}
}
