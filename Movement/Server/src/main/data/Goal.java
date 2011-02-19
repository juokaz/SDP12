package main.data;

public class Goal extends Point {

	protected boolean ownGoal = true;
	
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
	public Goal(double X, double Y, boolean ownGoal) {
		super(X, Y);
		// Centre will always be the same
		this.Y = 175;
		this.ownGoal = ownGoal;
		
		// Set which end of the pitch the goal centre is at
		setXVal();
		
		
	}
	
	
	/**
	 * Sets which end the goal is at
	 */
	private void setXVal() {
		if (ownGoal) {
			super.setX(0);
		} else {
			// TODO: check pitch size to get correct entry here
			super.setX(550);
		}
	}

	private int getLeftPost() {
		return leftPost;
	}
	private int getRightPost() {
		return rightPost;
	}

	public boolean isOwnGoal() {
		return ownGoal;
	}
	
	public void setOwnGoal(boolean own) {
		ownGoal = own;
	}
}
