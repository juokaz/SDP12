package main.data;

public class Goal extends Point {

	// TODO: Put in real post points, these are not correct, just examples.
	protected int leftPost = 100;
	protected int rightPost = 250;
	protected int centre;
	
	/**
	 * Store the position for the goal.
	 * Only really need to change X value as posts and centre point will be the same
	 * 
	 * @param X
	 * @param Y
	 */
	public Goal(double X, double Y) {
		super(X, Y);
	}

	public int getLeftPost() {
		return leftPost;
	}
	
	public int getRightPost() {
		return rightPost;
	}
}
