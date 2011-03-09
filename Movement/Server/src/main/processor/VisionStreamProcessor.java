package main.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.Runner;
import main.data.Ball;
import main.data.Goal;
import main.data.Location;
import main.data.Robot;

public abstract class VisionStreamProcessor extends AbstractProcessor {
	
	/**
	 * Separator for data lines
	 */
	protected String SEPARATOR = ",";

	/**
	 * Process input stream
	 * 
	 * @param stream
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	protected void process(InputStream stream) throws NumberFormatException, IOException {
		process(stream, null);
	}
	
	/**
	 * Process input stream
	 * 
	 * @param stream
	 * @param debug
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	protected void process(InputStream stream, InputStream debug) throws NumberFormatException, IOException {
		
	    // clean up if any output in std out
	    BufferedReader brCleanUp = new BufferedReader (new InputStreamReader (stream));
	    BufferedReader backup = null;
	    if (debug != null) {
	    	backup = new BufferedReader (new InputStreamReader (debug));
	    }
	    
		String line = null;
		
	    while (true) {
	    	
	    	// stop this from running
	    	if (stopped || Thread.currentThread().isInterrupted()) {
	    		System.out.println("Stopping processor");
	    		return;
	    	}
	    	
	    	while ((line = brCleanUp.readLine ()) != null) {
	    		try {
			    	// stop this from running
			    	if (stopped || Thread.currentThread().isInterrupted()) {
			    		System.out.println("Stopping processor");
			    		return;
			    	}

			    	if (Runner.DEBUG) {
				    	if (backup != null && backup.ready()){
				    		System.out.println ("[Stdout] " + backup.readLine());
			    		}
			    	}
			    	
			    	String[] lines = line.split(SEPARATOR);
			    	int robotAX = (int) Float.parseFloat(lines[0]);
			    	int robotAY = (int) Float.parseFloat(lines[1]);
			    	float robotAT = Float.parseFloat(lines[2]);
			    	
			    	Robot robotA = new Robot(robotAX, robotAY, robotAT);
			    	
			    	int ballX = (int) Float.parseFloat(lines[3]);
			    	int ballY = (int) Float.parseFloat(lines[4]);		    
			    	
			    	Ball ball = new Ball(ballX, ballY);
			    	
			    	int robotBX = (int) Float.parseFloat(lines[5]);
			    	int robotBY = (int) Float.parseFloat(lines[6]);
			    	float robotBT = Float.parseFloat(lines[7]);  
			    	
			    	Robot robotB = new Robot(robotBX, robotBY, robotBT);  
			    	
			    	Goal goal = null;
					
			    	if (this.isGoalLeft()){
			    		goal = new Goal(0,155);
			    	} else {
			    		goal = new Goal(550,155);
			    	}	
			    	
			    	Location loc = null;
			    	
			    	if (isOurRobotFirst()) {
			    		loc = new Location(robotA, robotB, ball, goal);
			    	} else {
			    		loc = new Location(robotB, robotA, ball, goal);		    		
			    	}
			    	
			    	propogateLocation(loc);
	    		} catch(Exception e){
	    			System.out.println("Exception in vision processing: " + e.getMessage());
	    			continue;
	    		}
	    	}
	    }
	}
}
