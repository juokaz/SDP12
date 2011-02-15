package main.processor;

import main.Processor;
import main.Strategy;

public abstract class AbstractProcessor implements Processor {
	
	protected Strategy strategy = null;
	
	protected boolean stopped = false;

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
	public void stop() {
		stopped = true;
	}
	
	public void run() {
		stopped = false;
	}
}
