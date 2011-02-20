package main.data;

/**
 * Data structure holding two robots and a ball
 * 
 * Robot A should always be our controllabe robot
 */
public class Location {

	private Robot ours;
	private Robot oponent;
	private Ball ball;
	
	public Location(Robot ours, Robot oponent, Ball ball) {
		this.ours = ours;
		this.oponent = oponent;
		this.ball = ball;
	}

	public Robot getOurRobot() {
		return ours;
	}

	public void setOurRobot(Robot robot) {
		this.ours = robot;
	}

	public Robot getOponentRobot() {
		return oponent;
	}

	public void setOponentRobot(Robot robot) {
		this.oponent = robot;
	}

	public Ball getBall() {
		return ball;
	}

	public void setBall(Ball ball) {
		this.ball = ball;
	}
}
