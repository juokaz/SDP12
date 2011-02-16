package main.executor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import main.Executor;
import main.data.Robot;
import sdp12.simulator.*;

public class Simulator implements Executor {

	RobotT robot1;
	RobotT robot2;
	RobotT ball;
	LegoSimulator simulator;
	
	public static int i = 0;
	
	public Simulator() {
		
		Runnable doCreateAndShowGUI = new Runnable() {
		
			public void run() {
			
				robot1 = new RobotT("images/ty.jpg", 65, 292, 0);
				robot2 = new RobotT("images/ty.jpg", 550, 300, 180);
				ball = new RobotT("images/ball.jpg", 768/2, 576/2, 0);
				simulator = new LegoSimulator(robot1, robot2, ball);
				simulator.createAndShowGUI();
				
			}
		
		};
		
		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}
	
	@Override
	public void move(Robot robot, int X, int Y) {
		// TODO Auto-generated method stub
//		@SuppressWarnings("serial")
//		Action u = new AbstractAction() {
//			public void actionPerformed (ActionEvent e) {
//				
//				if(i%2==0) robot1.move(10);
//				else robot1.rotate(30*Math.pow(-1, i));
//				i++;
//			}
//		};
//		Timer timer = new Timer(500,u);
//		timer.setRepeats(false);
//		timer.start();
		
	}

	@Override
	public void rotateWheels(Robot robot, int X, int Y) {
		// TODO Auto-generated method stub
		robot1.move(X, Y);
	}

	@Override
	public void kick(Robot robot) {
		// TODO Auto-generated method stub
	}

	@Override
	public void rotate(Robot robot, int T) {
		// TODO Auto-generated method stub
	}

	@Override
	public void exit() {
		simulator.close();
	}

	@Override
	public void exit(Robot robot) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stop(Robot robot) {
		// TODO Auto-generated method stub
		
	}
}
