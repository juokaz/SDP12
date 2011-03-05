package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import main.gui.Ball;
import main.gui.Pitch;

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
	 * Button for start/stop strategy
	 */
	private JButton start_strategy;
	
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
	 * Pitch
	 */
	Pitch pitch;

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
		
		frame.setLayout(new BorderLayout() ); 
		// TODO: Line below can turn off pitch, commenting out will 
		addPitch();
		// frame size
		frame.setSize(frame.getWidth(), frame.getHeight() + 150);
		
		//frame.setSize(900, 200);

		// enable system.out pipe
		System.setOut(aPrintStream); // catches System.out messages

		// add buttons and dropboxes
		addControls();

		// show a window
		frame.setVisible(true);
	}
	
	private void addPitch() {
		
		// TODO FIX THIS!!
		main.gui.Robot robot1 = new main.gui.Robot("images/tb.jpg", 400, 400, 180);
		main.gui.Robot robot2 = new main.gui.Robot("images/ty.jpg", 500, 250, 0);
		Ball ball = new Ball("images/ball.jpg", 200, 400);

		pitch = new Pitch(robot1, robot2, ball);
		
		frame.setSize(pitch.getPitchWidth() + 350, pitch.getPitchHeight());
		frame.add(pitch);
	}

	/**
	 * Add various buttons and dropboxes for program control
	 */
	private void addControls() {

		// start button
		button = new JButton("Start");

		// add onclick listener to make runner initialize once clicked
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runner.toggle((String) processor.getSelectedItem(),
						(String) executor.getSelectedItem());
			}
		});
		
		start_strategy = new JButton("Start execution");
		start_strategy.setEnabled(false);
		
		// add onclick listener to make runner start/stop once clicked
		start_strategy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runner.toggleExecution((String) strategy.getSelectedItem(),
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
		JPanel panel_main = new JPanel();
		
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
		panel.add(start_strategy);
		
		aTextArea.setColumns(100);
		aTextArea.setRows(5);
		
		JPanel panel2 = new JPanel();
		panel2.add(aTextArea);

		// layout for controls
	    GridBagLayout layout = new GridBagLayout();

	    // constrains, relative position on Y axis and horizontal fill
	    GridBagConstraints gbcR = new GridBagConstraints();
	    gbcR.gridx = 1;
	    gbcR.gridy = GridBagConstraints.RELATIVE;
	    gbcR.fill = GridBagConstraints.HORIZONTAL;

	    layout.setConstraints(panel2, gbcR);
	    layout.setConstraints(panel, gbcR);

		panel_main.setLayout(layout);
		panel_main.add(panel2);
		panel_main.add(panel);
		
		// add to main container
		contentPane.add(panel_main, BorderLayout.SOUTH);
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
	 * Update execution button disable/enable it
	 * 
	 * @param label
	 * @param enabled
	 */
	public void setButtonExecution(String label, boolean enabled) {
		start_strategy.setText(label);
		start_strategy.setEnabled(enabled);
	}
	
	/**
	 * Get pitch
	 * 
	 * @return
	 */
	public Pitch getPitch() {
		return this.pitch;
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
