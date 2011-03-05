package main.processor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import main.Processor;

public class File extends VisionStreamProcessor implements Processor {
	
	/**
	 * File to process
	 */
	private String file = null;
	
	/**
	 * Input stream
	 */
	private InputStream stdout = null;
	
	/**
	 * File processor instance
	 * 
	 * @param filename
	 * @throws FileNotFoundException 
	 */
	public File(String filename) throws FileNotFoundException {
		this.file = filename;
		
		// How input elements are separated
		SEPARATOR = " ";

		java.io.File file_ = new java.io.File(this.file);
		FileInputStream fis = new FileInputStream(file_);
		BufferedInputStream bis = new BufferedInputStream(fis);
		stdout = new DataInputStream(bis);
	}

	/**
	 * Start processing
	 */
	public void run() {
		
		// this needed to set running to true or set it manually
		super.run();
		
		try {
			// start processing data, method in VisionStreamProcessor
			process(stdout); 
		} catch (IOException e) {
			System.out.println("Processor error: " + e.getMessage());
		}
	}
}
