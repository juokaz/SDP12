package main.executor;

import javax.swing.JOptionPane;

import main.Executor;
import main.data.Ball;
import main.data.Goal;
import main.data.Location;
import main.data.Robot;
import main.gui.Pitch;
import main.processor.AbstractProcessor;

public class Simulator extends AbstractProcessor implements Executor {

	main.gui.Robot robot1;
	main.gui.Robot robot2;
	main.gui.Ball ball;
	Pitch pitch;

	public Simulator(Pitch pitch) {
		this.pitch = pitch;
		robot1 = pitch.getRobot1();
		robot1.getKicker().enable();
		robot2 = pitch.getRobot2();
		robot2.getKicker().enable();
		ball = pitch.getBall();
	}

	@SuppressWarnings("static-access")
	public void run() {
		super.run();
		
		Robot robotA = new Robot((int) robot1.getXPos(),
				(int) robot1.getYPos(), (float) robot1.getTheta());
		Robot robotB = new Robot((int) robot2.getXPos(),
				(int) robot2.getYPos(), (float) robot2.getTheta());
		Ball ball = new Ball((int) this.ball.getXPos(),
				(int) this.ball.getYPos());

		Goal goal = null;
		
    	if (this.isGoalLeft()){
    		goal = new Goal(0,155);
    	} else {
    		goal = new Goal(550,155);
    	}	
		
		Location data = null;

		if (isOurRobotFirst()) {
			data = new Location(robotA, robotB, ball, goal);
		} else {
			data = new Location(robotB, robotA, ball, goal);
		}
		
		while (true) {
			// stop this from running
			if (stopped || Thread.currentThread().isInterrupted()) {
				System.out.println("Stopping processor");
				return;
			}

			// update robots positions
			if (isOurRobotFirst()) {
				data.getOurRobot().setX(robot1.getCenterXRemapped());
				data.getOurRobot().setY(robot1.getCenterYRemapped());
				data.getOurRobot().setT((float) (robot1.getTheta()));
				data.getOpponentRobot().setX(robot2.getCenterXRemapped());
				data.getOpponentRobot().setY(robot2.getCenterYRemapped());
				data.getOpponentRobot().setT((float) (robot2.getTheta()));
			} else {
				data.getOurRobot().setX(robot2.getCenterXRemapped());
				data.getOurRobot().setY(robot2.getCenterYRemapped());
				data.getOurRobot().setT((float) (robot2.getTheta()));
				data.getOpponentRobot().setX(robot1.getCenterXRemapped());
				data.getOpponentRobot().setY(robot1.getCenterYRemapped());
				data.getOpponentRobot().setT((float) (robot1.getTheta()));
			}
			
			// update ball position			
			data.getBall().setX(this.ball.getCenterXRemapped());
			data.getBall().setY(this.ball.getCenterYRemapped());
			
			propogateLocation(data);
			
			// make this somewhat slow to be easier to follow
			try {
				Thread.currentThread().sleep(40);// sleep for 1000 ms
			} catch (Exception ie) {
				// If this thread was intrrupted by nother thread
			}
		}
	}

	@Override
	public void rotateWheels(final int leftWheelSpeed, final int rightWheelSpeed) {
		// TODO FIX THIS Simulator things left is right, what a weird boy :-(
		if (isOurRobotFirst()) {
			robot1.move(rightWheelSpeed, leftWheelSpeed);
		} else {
			robot2.move(rightWheelSpeed, leftWheelSpeed);
		}
	}

	@Override
	public void kick() {
		if (isOurRobotFirst()) {
			robot1.kick();
		} else {
			robot2.kick();
		}
	}

	@Override
	public void rotate(int T) {
		robot1.rotate(T);
	}

	@Override
	public void start() {
		// re-enable robots as they might have been disabled
		robot1.setEnabled(true);
		robot2.setEnabled(true);
	}

	@Override
	public void stop() {
		// disable robots to stop them from moving
		robot1.setEnabled(false);
		robot2.setEnabled(false);
		// disable ball from rolling
		ball.getTimer().stop();
	}
	
	@Override
	public void exit() {
		stop();
	}

	@Override
	public void celebrate() {
		// Am what?...
		JOptionPane.showMessageDialog(pitch, "You have scored!", "Scored", JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void takePenalty() {
	}
}
