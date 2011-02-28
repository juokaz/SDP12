package main.processor;

import main.Processor;
import main.data.Ball;
import main.data.Goal;
import main.data.Location;
import main.data.Robot;
import main.gui.Pitch;

public class SimulatorProcessor extends AbstractProcessor implements Processor {
	main.gui.Robot robot1;
	main.gui.Robot robot2;
	main.gui.Ball ball;
	main.gui.Pitch pitch;
	
	public SimulatorProcessor(Pitch simulator) {
		this.robot1 = simulator.getRobot1();
		this.robot1.getKicker().enable();
		this.robot2 = simulator.getRobot2();
		this.robot2.getKicker().enable();
		this.ball = simulator.getBall();
		this.pitch = simulator;
	}
	
	@Override
	public void run(boolean our_robot, boolean left_goal) {
		super.run(our_robot, left_goal);
		
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
		
		/*
		 * Totally a hack - flip robot pointers so that 
		 * 					executor has to just send the commands
		 * 					to robot1 and not check which robot is which
		 */
		if (!isOurRobotFirst()) {
			pitch.flipRobots();
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
				// If this thread was interrupted by another thread
			}
		}
	}
}
