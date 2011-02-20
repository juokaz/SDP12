package main.data;

/**
 * Data structure holding two robots and a ball
 * 
 * Robot A should always be our controllabe robot
 */
public class Location {

	private Robot ours;
	private Robot opponent;
	private Ball ball;
	
	public Location(Robot ours, Robot oponent, Ball ball) {
		this.ours = ours;
		this.opponent = oponent;
		this.ball = ball;
	}

	public Robot getOurRobot() {
		return ours;
	}

	public void setOurRobot(Robot robot) {
		this.ours = robot;
	}

	public Robot getOpponentRobot() {
		return opponent;
	}

	public void setOpponentRobot(Robot robot) {
		this.opponent = robot;
	}

	public Ball getBall() {
		return ball;
	}

	public void setBall(Ball ball) {
		this.ball = ball;
	}
}
