package main.processor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import main.Processor;

public class File extends VisionStreamProcessor implements Processor {
	
	private String file = null;
	
	public File(String filename) {
		file = filename;
		
		// How input elements are separated
		SEPARATOR = " ";
	}

	public void run(boolean our_robot) {
		
		// this needed to set running to true or set it manually
		super.run(our_robot);
		
		try {
			java.io.File file = new java.io.File(this.file);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			InputStream stdout = new DataInputStream(bis);
		    
			// start processing data, method in VisionStreamProcessor
			process(stdout);
		    
		} catch (IOException e) {
			System.out.println("Processor error: " + e.getMessage());
		}
	}
}
