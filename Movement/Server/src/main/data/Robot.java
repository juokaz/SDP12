package main.data;

/**
 * A robot
 */
public class Robot extends Point {

	protected float T;
	
	public Robot(int X, int Y, float T) {
		super(X, Y);
		this.T = T;
	}

	public float getT() {
		return T;
	}

	public void setT(float t) {
		T = t;
	}
	
	public double getTDegrees() {
		double t = Math.toDegrees(T);
		while ( t > 180 ){
			t = t - 360;
		}
		while ( t < -180 ){
			t = t + 360;
		}
		return t;
	}
	
	/**
	 * Checks to see if the robot is at the ball - different from isAtPoint
	 * as stops it returning true when robot is still behind ball.
	 * 
	 * @param ball
	 * @param goal
	 * @param threshold
	 * @return
	 */
	public boolean isAtBall(Ball ball, Goal goal, int threshold) {
		double x = getX();
		double y = getY();
		double p_x = ball.getX();
		double p_y = ball.getY();
			
		if (goal.getX() == 0) {
			return x <= p_x + threshold && x >= p_x && 
				y <= p_y + threshold && y >= p_y - threshold;
		} else {
			return x <= p_x && x >= p_x - threshold && 
				y <= p_y + threshold && y >= p_y - threshold;
		}
	}
}
