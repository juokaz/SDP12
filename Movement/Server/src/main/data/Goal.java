package main.data;

public class Goal extends Point {

	
	/**
	 * This boolean will show whether we are attacking the goal on the 
	 * near side of the pitch (True = (0,175), False = (550, 175)).
	 * This will affect the angles being calculated in Ball and Goal class
	 */
	protected boolean leftGoal = true;
	
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
	public Goal(double X, double Y, boolean leftGoal) {
		super(X, Y);
		// Centre will always be the same
		this.Y = 175;
		this.leftGoal = leftGoal;
		
		// Set which end of the pitch the goal centre is at
		setXVal();
		
		
	}
	
	
	
	/**
	 * Sets which end the goal is at
	 */
	private void setXVal() {
		if (leftGoal) {
			super.setX(0);
		} else {
			// TODO: check pitch size to get correct entry here
			super.setX(550);
		}
	}
	
	private double calculateGoalBallAngle(Ball ball) {
		//Middle of the goal position.
		double goalX = getX(), goalY = getY();
		// Gets the angle between the ball and the centre of the goal.
		double angle = Math.atan2(goalY-ball.getY(), goalX-ball.getX());
		if (leftGoal) {	
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
		
		if (leftGoal) {			
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

	public boolean isLeftGoal() {
		return leftGoal;
	}
	
	public void setLeftGoal(boolean own) {
		leftGoal = own;
	}
}
