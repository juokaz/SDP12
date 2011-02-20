package main.data;

/**
 * A ball 
 */
public class Ball extends Point {

	public Ball(int X, int Y) {
		super(X, Y);
	}

	/**
	 * Calculates the new X,Y co-ordinates for a point behind the ball.
	 * Currently does this in relation to the goal at the far end of the pitch
	 * TODO: Create method that takes into account which goal we are aiming towards.
	 * 
	 * @param ballGoalAngle
	 * @param ball
	 * @param gap
	 */
	public Point calculatePosBehindBall(double ballGoalAngle, Ball ball, int gap) {
		// Set the distance behind ball we want to move.
		
		// Need to work out sin and cos distances to get new X and Y positions
		double xOffset = gap*Math.cos(ballGoalAngle);
		double yOffset = gap*Math.sin(ballGoalAngle);
		
		return new Point(ball.getX()+xOffset, ball.getY()+yOffset);
	}
}
