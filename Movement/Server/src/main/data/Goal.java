package main.data;

public class Goal extends Point {

	

	
	// TODO: Put in real post points, these are not correct, just examples.
	protected int leftPost = 100;
	protected int rightPost = 250;
	protected int centre;
	
	
	/**
	 * Store the position for the goal.
	 * Only really need to change X value as posts and centre point will be the same
	 * TODO: Decide whether angle to ball is stored here.
	 * 
	 * @param X
	 * @param Y
	 */
	public Goal(double X, double Y) {
		super(X, Y);
	}
	
	
	private double calculateGoalBallAngle(Ball ball) {
		//Middle of the goal position.
		double goalX = getX(), goalY = getY();
		// Gets the angle between the ball and the centre of the goal.
		double angle = Math.atan2(goalY-ball.getY(), goalX-ball.getX());
		if (getX() == 0) {	
			if (angle < 0) {
				angle = (-Math.PI - angle);
			} else {
				angle = (Math.PI - angle);
			}
		}
		
		return angle;
	}
	
	
	private double calculateGoalRobotAngle(Robot robot) {
		//Middle of the goal position.
		double goalX = getX(), goalY = getY();
		// Gets the angle between the ball and the centre of the goal.
		double angle = Math.atan2(goalY-robot.getY(), goalX-robot.getX());
		
		if (getX() == 0) {			
			if (angle < 0) {
				angle = (-Math.PI - angle);
			} else {
				angle = (Math.PI - angle);
			}
		}
		
		return angle;
	}
	

	private int getLeftPost() {
		return leftPost;
	}
	private int getRightPost() {
		return rightPost;
	}

}
