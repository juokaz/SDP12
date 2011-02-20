package main.strategy;

import main.Strategy;
import main.data.Ball;
import main.data.Location;
import main.data.Robot;
import main.data.VelocityVec;


/**
 * This strategy should find a position behind the ball and move the robot to it.
 * I think the methods used in finding a point behind the ball might be more useful 
 * than the movement strategy that could be implemented here.
 */
public class GoToBall extends AbstractStrategy implements Strategy {

	// Gap is the distance behind ball for the point we want to move to.
	private int gap;
	
	@Override
	public void updateLocation(Location data) {
		Ball ball = data.getBall();
		Robot robotA = data.getRobotB();

		moveToBall(robotA, ball);
		
	}
	
	/**
	 * Main method to move to the ball.
	 * 
	 * @param robot
	 * @param ball
	 */
	public void moveToBall(Robot robot, Ball ball) {
		
		// find the angle the robot must turn to face the ball
		double dirAngle = -1*Math.atan2(ball.getY()-robot.getY(), ball.getX()-robot.getX());
		
		// find the distance between the robot and the ball
		double dy = robot.getY() - ball.getY();
		double dx = robot.getX() - ball.getX();
		double distance = Math.sqrt(dx*dx + dy*dy);
		System.out.println("CopyOfBasicStrategy::Distance " + distance);
		
		// convert to degrees
		dirAngle = Math.toDegrees(dirAngle);
		System.out.println("CopyOfBasicStrategy::dirAngle " + dirAngle);
		System.out.println("CopyOfBasicStrategy::dAngle " + Math.abs(dirAngle - robot.getT()));
		
		// decide whether to turn left or right to face the ball
		int a=1;
		int b=1;
		if(dirAngle > robot.getT()){
			//System.out.println("Turning left");
			a=-1;
			b=1;
		} 
		if (dirAngle < robot.getT())
		{
			//System.out.println("Turning right");
			a=1;
			b=-1;
			
		}
		// once the robot is facing in direction of the ball, move towards it at
		// a velocity proportional to the distance between them
		if(Math.abs(dirAngle -robot.getT())<30){
			a = (int) (1*distance)/35;
			b = (int) (1*distance)/35;		
		}
		executor.rotateWheels(a*50, b*50);
		
	}

	@Override
	public VelocityVec getVelocity() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setGap(int newGap) {
		gap = newGap;
	}
	
	public int getGap() {
		return gap;
	}

}
