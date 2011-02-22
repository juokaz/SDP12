package main.strategy;

import main.Strategy;
import main.data.Location;
import main.processor.Listener;

public class ProcessorListener implements Listener {
	
	protected Strategy strategy;

	public ProcessorListener(Strategy strategy) 
	{
		this.strategy = strategy;
	}
	
	public void updateLocation(Location data) 
	{
		strategy.updateLocation(data);
	}
}
