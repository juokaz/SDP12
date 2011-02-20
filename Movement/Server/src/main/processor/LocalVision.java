package main.processor;

import java.io.IOException;
import java.io.InputStream;

import main.Processor;

public class LocalVision extends VisionStreamProcessor implements Processor {
	
	private String command = null;
	
	protected Process process;
	
	public LocalVision(String command) {
		this.command = command;
	}

	public void run() {
		
		// this needed to set running to true or set it manually
		super.run();
		
		try {
			// execute Vision program
			process = Runtime.getRuntime().exec(command);

		    // Get the input stream and read from it
		    InputStream in = process.getErrorStream();
		    
			// start processing data, method in VisionStreamProcessor
			process(in);
		} catch (IOException e) {
			System.out.println("Processor error: " + e.getMessage());
		}
	}
	
	public void stop() {
		super.stop();
		
		if (process != null)
			process.destroy();
	}
}
