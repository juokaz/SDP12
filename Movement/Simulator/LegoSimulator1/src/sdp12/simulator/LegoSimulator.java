package sdp12.simulator;

import javax.swing.JFrame;

public class LegoSimulator {

	JFrame simulatorFrame;
	
	// Hold pitch
	Pitch pitch;
	
	RobotT robot1;
	RobotT robot2;
	Ball ball;
	
	// Constructor for LegoSimulator
	public LegoSimulator() {
		
		pitch = new Pitch();
	}
	
	public LegoSimulator(RobotT robot1, RobotT robot2) {
		
		this.robot1 = robot1;
		this.robot2 = robot2;
		
		pitch = new Pitch(robot1, robot2);
	}
	
	public LegoSimulator(RobotT robot1, RobotT robot2, Ball ball) {
		
		this.robot1 = robot1;
		this.robot2 = robot2;
		this.ball = ball;
		
		pitch = new Pitch(robot1, robot2, ball);
	}
	
	public void close() {
		
		simulatorFrame.dispose();
	}
	
	// Set GUI properties
	public void createAndShowGUI() {
		
		simulatorFrame = new JFrame("Simulator");
		simulatorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulatorFrame.setSize(pitch.getPitchWidth() + 350, pitch.getPitchHeight());
		simulatorFrame.add(pitch);
		simulatorFrame.setVisible(true);
	}
}
