package sdp12.simulator;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class LegoSimulator {

	// Hold pitch
	Pitch pitch;
	
	static JFrame simulatorFrame;
	
	// Constructor for LegoSimulator
	public LegoSimulator() {
		
		pitch = new Pitch();
	}
	
	public LegoSimulator(RobotT robot1, RobotT robot2) {
		
		pitch = new Pitch(robot1, robot2);
	
	}
	
	public LegoSimulator(RobotT robot1, RobotT robot2, RobotT ball) {
		
		pitch = new Pitch(robot1, robot2, ball);
		
	}
	
	// Set GUI properties
	public void createAndShowGUI() {
		
		simulatorFrame = new JFrame("Simulator");
		// todo handle this
		//simulatorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulatorFrame.setSize(pitch.getPitchWidth(), pitch.getPitchHeight());
		simulatorFrame.add(pitch);
		simulatorFrame.setVisible(true);
	}
	
	public void close() {
		simulatorFrame.dispose();
	}	
}
