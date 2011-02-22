package main.executor;

import main.Executor;
import main.data.Ball;
import main.data.Goal;
import main.data.Location;
import main.data.Robot;
import main.processor.AbstractProcessor;

public class Simulator extends AbstractProcessor implements Executor {

	main.gui.Robot robot1;
	main.gui.Robot robot2;
	main.gui.Ball ball;

	public Simulator(main.gui.Pitch pitch) {
		robot1 = pitch.getRobot1();
		robot1.getKicker().enable();
		robot2 = pitch.getRobot2();
		robot2.getKicker().enable();
		ball = pitch.getBall();
	}

	@SuppressWarnings("static-access")
	public void run(boolean our_robot, boolean left_goal) {

		super.run(our_robot, left_goal);
		try {
			Thread.currentThread().sleep(2000);// sleep for 2000 ms
		} catch (Exception ie) {
			// If this thread was interrupted by another thread
		}
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
			if (stopped) {
				System.out.println("Stopping processor");
				return;
			}

			// simulate wait
			data.getOurRobot().setX(robot1.getCenterXRemapped());
			data.getOurRobot().setY(robot1.getCenterYRemapped());
			data.getOurRobot().setT((float) (robot1.getTheta()));
			data.getOpponentRobot().setX(robot2.getCenterXRemapped());
			data.getOpponentRobot().setY(robot2.getCenterYRemapped());
			data.getOpponentRobot().setT((float) (robot2.getTheta()));
			data.getBall().setX(this.ball.getCenterXRemapped());
			data.getBall().setY(this.ball.getCenterYRemapped());
			
			propogateLocation(data);
			
			try {
				Thread.currentThread().sleep(40);// sleep for 1000 ms
			} catch (Exception ie) {
				// If this thread was intrrupted by nother thread
			}
		}

	}

	@Override
	public void rotateWheels(final int leftWheelSpeed, final int rightWheelSpeed) {
		if (isOurRobotFirst()) {
			robot1.move(leftWheelSpeed, rightWheelSpeed);
		} else {
			robot2.move(leftWheelSpeed, rightWheelSpeed);
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
		// TODO Auto-generated method stub
		robot1.rotate(T);
	}
	
	@Override
	public void exit() {
		
	}

	@Override
	public void stop() {
		if (isOurRobotFirst()) {
			robot1.move(0, 0);
		} else {
			robot2.move(0, 0);
		}
	}
}
