package main.processor;

import java.io.InputStream;

import main.Processor;

public class LocalVision extends VisionStreamProcessor implements Processor {
	
	/**
	 * Command pointing to Vision program
	 */
	private String command = null;
	
	/**
	 * Process instance
	 */
	protected Process process;
	
	/**
	 * Local vision progress
	 * 
	 * @param command
	 */
	public LocalVision(String command) {
		this.command = command;
	}

	/**
	 * Run processor
	 * 
	 * @param our_robot
	 * @param left_foal
	 */
	public void run(boolean our_robot, boolean left_goal) {
		
		// this needed to set running to true or set it manually
		super.run(our_robot, left_goal);
		try {
			// execute Vision program
			process = Runtime.getRuntime().exec(command);

		    // Get the error stream and read from it
		    InputStream in = process.getErrorStream();
		    
		    // Get the input stream and read from it
		    InputStream debug = process.getInputStream();
		    
			// start processing data, method in VisionStreamProcessor
			process(in, debug);
		} catch (Exception e) {
			System.out.println("Processor error: " + e.getMessage()+" Restarting:");
			// TODO what if it fails not because of crashing?
			run(our_robot,left_goal);
		}
	}
	
	/**
	 * Stop processor
	 */
	public void stop() {
		super.stop();
		
		if (process != null)
			process.destroy();
	}
}
