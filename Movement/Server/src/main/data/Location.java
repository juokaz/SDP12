package main.data;

/**
 * Data structure holding two robots and a ball
 * 
 * Robot A should always be our controllable robot
 */
public class Location {

	private Robot ours;
	private Robot opponent;
	private Ball ball;
	private Goal goal;
	
	public Location(Robot ours, Robot oponent, Ball ball, Goal oppGoal) {
		this.ours = ours;
		this.opponent = oponent;
		this.ball = ball;
		this.goal = oppGoal;
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
	
	public Goal getGoal() {
		return goal;
	}
	
	public void setGoal(Goal goal) {
		this.goal = goal;
	}
}
