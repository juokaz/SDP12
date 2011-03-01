package main.strategy;

import main.Strategy;
import main.data.Location;

public class TakePenalty2 extends AbstractStrategy implements Strategy {

	private boolean hasTaken = false;
	
	@Override
	public void updateLocation(Location data) {
		
		if ( hasTaken == false )
			takePenalty();
		
	}
	
	protected void takePenalty() {
		executor.takePenalty();
		hasTaken = true;
		executor.exit();
	}
}
