package main.data;

/**
 * A ball 
 */
public class Ball extends Point {

	public Ball(int X, int Y) {
		super(X, Y);
	}

	/**
	 * Find the angle between the ball and the centre of the goal
	 * @param goal
	 * @return
	 */
	public double calculateBallToGoalAngle(Goal goal) {
		
			
		//Middle of the goal position.
		double goalX = 0, goalY = 175;
		// Gets the angle between the ball and the centre of the goal.
		double angle = Math.atan2(goalY-getY(), goalX-getX());
		
		
		
		if (goal.isLeftGoal()) {
			if (angle < 0) {
				angle = (-Math.PI - angle);
			} else {
				angle = (Math.PI - angle);
			}
		}
		
		
		return angle;
		
	}

	/**
	 * Calculates the new X,Y co-ordinates for a point behind the ball.
	 * Currently does this in relation to the goal at the far end of the pitch
	 * TODO: Create method that takes into account which goal we are aiming towards.
	 * @param ballGoalAngle
	 * @param ball
	 * @param dirPoint
	 * @param gap
	 */
	private void calculatePosBehindBall(double ballGoalAngle, Ball ball, Point dirPoint, int gap) {

		
		// Set the distance behind ball we want to move.
		
		// Need to work out sin and cos distances to get new X and Y positions
		double xOffset = gap*Math.cos(ballGoalAngle);
		double yOffset = gap*Math.sin(ballGoalAngle);
			
		// Sets the position for the robot to move to
		dirPoint.setX(ball.getX()+xOffset);
		dirPoint.setY(ball.getY()+yOffset);
		
	}


}
