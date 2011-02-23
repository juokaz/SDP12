package main.processor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import main.Processor;

public class File extends VisionStreamProcessor implements Processor {
	
	/**
	 * File to process
	 */
	private String file = null;
	
	/**
	 * File processor instance
	 * 
	 * @param filename
	 */
	public File(String filename) {
		file = filename;
		
		// How input elements are separated
		SEPARATOR = " ";
	}

	/**
	 * Start processing
	 * 
	 * @param our_robot
	 * @param left_foal
	 */
	public void run(boolean our_robot, boolean left_goal) {
		
		// this needed to set running to true or set it manually
		super.run(our_robot, left_goal);
		
		try {
			java.io.File file = new java.io.File(this.file);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			InputStream stdout = new DataInputStream(bis);
		    
			// start processing data, method in VisionStreamProcessor
			//process(stdout);
		    
		} catch (IOException e) {
			System.out.println("Processor error: " + e.getMessage());
		}
	}
}
