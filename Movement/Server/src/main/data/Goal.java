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
	
	/**
	 * Calculates an angle between a point and goal
	 * 
	 * @param point
	 * @return
	 */
	public double calculateGoalAndPointAngle(Point point) {
		double angle = getAngleBetweenPoints(point);
		
		if (getX() == 0) {	
			if (angle < 0) {
				angle = (Math.PI - angle);
			} else {
				angle = (-Math.PI - angle);
			}
		}
		
		return -angle;
	}	

	public int getLeftPost() {
		return leftPost;
	}
	
	public int getRightPost() {
		return rightPost;
	}
}
