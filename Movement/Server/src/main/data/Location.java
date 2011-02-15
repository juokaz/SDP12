package main.data;

/**
 * Data structure holding two robots and a ball
 * 
 * Robot A should always be our controllabe robot
 */
public class Location {

	private Robot robotA;
	private Robot robotB;
	private Ball ball;
	
	public Location(Robot robotA, Robot robotB, Ball ball) {
		this.robotA = robotA;
		this.robotB = robotB;
		this.ball = ball;
	}

	public Robot getRobotA() {
		return robotA;
	}

	public void setRobotA(Robot robotA) {
		this.robotA = robotA;
	}

	public Robot getRobotB() {
		return robotB;
	}

	public void setRobotB(Robot robotB) {
		this.robotB = robotB;
	}

	public Ball getBall() {
		return ball;
	}

	public void setBall(Ball ball) {
		this.ball = ball;
	}
}
