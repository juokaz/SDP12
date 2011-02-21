package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import java.io.*;

public class Window {

	/**
	 * Frame instance, a frame is more or less a window
	 */
	private JFrame frame;

	/**
	 * Runner instance is required to send run/stop commands to it
	 */
	private Runner runner;

	/**
	 * Button for start/stop
	 */
	private JButton button;
	
	/**
	 * Processors list
	 */
	private JComboBox processor;
	
	/**
	 * Strategies list
	 */
	private JComboBox strategy;
	
	/** 
	 * Executors list
	 */
	private JComboBox executor;
	
	/** 
	 * Robots list
	 */
	private JComboBox robots;
	
	/** 
	 * Goals list
	 */
	private JComboBox goals;

	/**
	 * Text field containg system.out
	 */
	TextArea aTextArea = new TextArea();
	
	/**
	 * Print stream required to pipe system.out to textarea above
	 */
	PrintStream aPrintStream = new PrintStream(new FilteredStream(
			new ByteArrayOutputStream()));

	/**
	 * Window instance
	 * 
	 * @param runner
	 */
	public Window(Runner runner) {

		this.runner = runner;

		// enable native look and feel
		setNativeLookAndFeel();

		// create main window
		frame = new JFrame("SDP12");
		// close whole program on exit click
		frame.addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent e) {
		        Window.this.runner.stopRunner();
		        System.exit(0);
		      }
		    });
		// default size
		frame.setSize(1000, 300);
		// text area
		frame.getContentPane().add("Center", aTextArea);

		// enable system.out pipe
		System.setOut(aPrintStream); // catches System.out messages

		// add buttons and dropboxes
		addControls();

		// show a window
		frame.setVisible(true);
	}

	/**
	 * Add various buttons and dropboxes for program control
	 */
	private void addControls() {

		// start button
		button = new JButton("Start");

		// add onclick listener to make runner start/stop once clicked
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				runner.toggle((String) processor.getSelectedItem(),
						(String) strategy.getSelectedItem(),
						(String) executor.getSelectedItem(),
						(String) robots.getSelectedItem(),
						(String) goals.getSelectedItem());
			}
		});

		// get content container (jframe contains one container)
		Container contentPane = frame.getContentPane();
		
		// goals combobox
		goals = new JComboBox(runner.goals);
		JLabel goals1 = new JLabel("Goal:");
		
		// robots combobox
		robots = new JComboBox(runner.robots);
		JLabel robotsl = new JLabel("Our Robot:");

		// processors combobox
		processor = new JComboBox(runner.processors);
		JLabel processorl = new JLabel("Processor:");

		// strategies combobox
		strategy = new JComboBox(runner.strategies);
		JLabel strategyl = new JLabel("Strategy:");

		// executors combobox
		executor = new JComboBox(runner.executors);
		JLabel executorl = new JLabel("Executor:");

		// use a panel, this is will make everything automatically aligned horizontaly
		JPanel panel = new JPanel();
		panel.add(goals1);
		panel.add(goals);
		panel.add(robotsl);
		panel.add(robots);
		panel.add(processorl);
		panel.add(processor);
		panel.add(strategyl);
		panel.add(strategy);
		panel.add(executorl);
		panel.add(executor);
		panel.add(button);
		
		// add to main container
		contentPane.add(panel, BorderLayout.SOUTH);
	}
	
	/**
	 * Update button label and disable/enable it
	 * 
	 * @param label
	 * @param enabled
	 */
	public void setButton(String label, boolean enabled) {
		button.setText(label);
		button.setEnabled(enabled);
	}

	/**
	 * Set native look and feel. Makes a program to look like native windows or linux app depending
	 * on machine it's running on.
	 */
	private void setNativeLookAndFeel() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}
	}

	/**
	 * Filter stream required to pipe system.out
	 */
	class FilteredStream extends FilterOutputStream {
		public FilteredStream(OutputStream aStream) {
			super(aStream);
		}

		public void write(byte b[]) throws IOException {
			String aString = new String(b);
			aTextArea.append(aString);
		}

		public void write(byte b[], int off, int len) throws IOException {
			String aString = new String(b, off, len);
			aTextArea.append(aString);
		}
	}
}
