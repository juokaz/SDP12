package main.executor;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import main.Executor;
import main.data.Ball;
import main.data.Location;
import main.data.Robot;
import main.processor.AbstractProcessor;
import sdp12.simulator.Drawable;
import sdp12.simulator.LegoSimulator;
import sdp12.simulator.RobotT;

public class Simulator extends AbstractProcessor implements Executor {

	RobotT robot1;
	RobotT robot2;
	sdp12.simulator.Ball ball;
	LegoSimulator simulator;

	public static int i = 0;

	public Simulator() {

		Runnable doCreateAndShowGUI = new Runnable() {

			public void run() {

				robot1 = new RobotT("images/ty.jpg", 65, 292, 0);
				robot2 = new RobotT("images/ty.jpg", 550, 300, 180);
				ball = new sdp12.simulator.Ball("images/ball.jpg", 768 / 2,
						576 / 2);
				simulator = new LegoSimulator(robot1, robot2, ball);
				simulator.createAndShowGUI();
				// simulator.writeToFile();
				// Simulator.this.strategy.updateLocation()
			}

		};

		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	public Simulator(final int X1, final int Y1, final float theta1,
			final int X2, final int Y2, final int BallX, final int BallY) {

		Runnable doCreateAndShowGUI = new Runnable() {

			public void run() {

				robot1 = new RobotT("images/ty.jpg", X1, Y1, theta1);
				robot2 = new RobotT("images/ty.jpg", X2, Y2, theta1);
				ball = new sdp12.simulator.Ball("images/ball.jpg", BallX, BallY);
				simulator = new LegoSimulator(robot1, robot2, ball);
				simulator.createAndShowGUI();

			}

		};

		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	@SuppressWarnings("static-access")
	public void run() {

		super.run();
		try {
			Thread.currentThread().sleep(2000);// sleep for 1000 ms
		} catch (Exception ie) {
			// If this thread was intrrupted by nother thread
		}
		Robot robotA = new Robot((int) robot1.getXPos(),
				(int) robot1.getYPos(), (float) robot1.getTheta());
		Robot robotB = new Robot((int) robot2.getXPos(),
				(int) robot2.getYPos(), (float) robot2.getTheta());
		Ball ball = new Ball((int) this.ball.getXPos(),
				(int) this.ball.getYPos());

		Location data = new Location(robotA, robotB, ball);
		while (true) {
			// stop this from running
			if (stopped) {
				System.out.println("Stopping processor");
				return;
			}

			// simulate wait
			data.getRobotA().setX(robot1.getCenterX());
			data.getRobotA().setY(robot1.getCenterY());
			data.getRobotA().setT((float) (robot1.getTheta()));
			data.getRobotB().setX(robot2.getCenterX());
			data.getRobotB().setY(robot2.getCenterY());
			data.getRobotB().setT((float) (robot2.getTheta()));
			data.getBall().setX(ball.getX());
			data.getBall().setY(ball.getY());
			System.out.println(data.getRobotA().getT());
			strategy.updateLocation(data);
			try {
				Thread.currentThread().sleep(40);// sleep for 1000 ms
			} catch (Exception ie) {
				// If this thread was intrrupted by nother thread
			}
		}

	}

	@Override
	public void rotateWheels(final int leftWheelSpeed, final int rightWheelSpeed) {

		robot1.move(leftWheelSpeed, rightWheelSpeed);
	}

	@Override
	public void kick() {
		robot1.kick();
	}

	@Override
	public void rotate(int T) {
		// TODO Auto-generated method stub
		robot1.rotate(T);
	}

	public void setDrawables(ArrayList<Drawable> drawables) {
		robot1.setDrawables(drawables);
	}
	
	@Override
	public void exit() {
		simulator.close();
	}

	@Override
	public void stop() {
		robot1.move(0, 0);
	}
}
