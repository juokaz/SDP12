package main.processor;

import java.io.IOException;
import java.io.InputStream;

import main.Processor;
import main.Runner;

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
	 * Input stream
	 */
	protected InputStream in;
	
	/**
	 * Debug stream
	 */
	protected InputStream debug;
	
	/**
	 * Local vision progress
	 * 
	 * @param command
	 * @throws IOException 
	 */
	public LocalVision(String command) throws IOException {
		this.command = command;
		
		initialize();
	}

	/**
	 * Run processor
	 * 
	 * @param our_robot
	 * @param left_foal
	 */
	public void run() {
		
		// this needed to set running to true or set it manually
		super.run();
		try {		    
			// start processing data, method in VisionStreamProcessor
			if (Runner.DEBUG) {
				process(in, debug);
			} else {
				process(in);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Processor error: " + e.getMessage()+" Restarting:");
			
			// reinitialize program
			try {
				process.destroy();
				initialize();
				
				// TODO what if it fails not because of crashing?
				run();
			} catch (IOException e1) {
				System.out.println("Processor cannot be restarted");
			}
		}
	}
	
	/**
	 * Initialises local vision process
	 * @throws IOException 
	 */
	private void initialize() throws IOException
	{
		// execute Vision program
		process = Runtime.getRuntime().exec(command);
	    in = process.getErrorStream();
	    debug = process.getInputStream();
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
