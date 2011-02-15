package main.strategy;

import main.Strategy;
import main.Executor;

public abstract class AbstractStrategy implements Strategy {

	protected Executor executor = null;

	public void setExecutor(Executor executor) {
		this.executor  = executor;
	}
}
