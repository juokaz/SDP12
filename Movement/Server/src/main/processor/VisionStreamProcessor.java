package main.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.Runner;
import main.data.Ball;
import main.data.Location;
import main.data.Robot;

public abstract class VisionStreamProcessor extends AbstractProcessor {
	
	protected String SEPARATOR = ",";

	protected void process(InputStream stream) throws NumberFormatException, IOException {
		
	    // clean up if any output in stdout
	    BufferedReader brCleanUp = new BufferedReader (new InputStreamReader (stream));
		
		String line = null;
	    
	    while (true) {
	    	
	    	// stop this from running
	    	if (stopped) {
	    		System.out.println("Stopping processor");
	    		return;
	    	}
	    	
	    	while ((line = brCleanUp.readLine ()) != null) {
	    		
		    	// stop this from running
		    	if (stopped) {
		    		System.out.println("Stopping processor");
		    		return;
		    	}
		    	
		    	if (Runner.DEBUG) {
		    		System.out.println ("[Stdout] " + line);
		    	}
		    	
		    	String[] lines = line.split(SEPARATOR);
		    	int robotAX = (int) Float.parseFloat(lines[0]);
		    	int robotAY = (int) Float.parseFloat(lines[1]);
		    	float robotAT = Float.parseFloat(lines[2]);
		    	
		    	Robot robotB = new Robot(robotAX, robotAY, robotAT);
		    	
		    	int ballX = (int) Float.parseFloat(lines[3]);
		    	int ballY = (int) Float.parseFloat(lines[4]);		    
		    	
		    	Ball ball = new Ball(ballX, ballY);
		    	
		    	int robotBX = (int) Float.parseFloat(lines[5]);
		    	int robotBY = (int) Float.parseFloat(lines[6]);
		    	float robotBT = Float.parseFloat(lines[7]);    
		    	
		    	Robot robotA = new Robot(robotBX, robotBY, robotBT);
		    	
		    	Location loc = new Location(robotA, robotB, ball);
		    	
		    	strategy.updateLocation(loc);
	    	}
	    }
	}
}
