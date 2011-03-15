package main.data;

/**
 * Point object
 * 
 * A piece of data containing X and Y
 */
public class Point {

	protected double X;
	protected double Y;
	
	protected int pointThreshold = 40;
	
	public Point(double X, double Y) {
		this.X = X;
		this.Y = Y;
	}
	
	/**
	 * Finds the distance between two points, using dirPoint as the point to move to
	 * 
	 * @param dirPoint
	 * @return
	 */
	public double getDistanceBetweenPoints(Point dirPoint) {
		double dx = getX()-dirPoint.getX();
		double dy = getY()-dirPoint.getY();
		
		return Math.sqrt((dx*dx) + (dy*dy));
	}
	
	/**
	 * Finds the angle between 2 points, giving the angle from the point 
	 * which calls this method.
	 * @param point
	 * @return
	 */
	public double angleBetweenPoints(Point point) {
		return Math.atan2((point.getY() - getY()), (point.getX() - getX()));
	}
	
	
	
	/**
	 * Finds out if there is an obstacle inbetween two points
	 * 
	 * @param robot
	 * @param ball
	 * @param optimum
	 * @return
	 */
	public boolean isObstacleInFront(Point possibleObstacle, Point destination, int obstacleWidth) {
		// angle between lines going through opponent center and edge. opponent width is doubled in order to take into account width of both robots.
		int theta = (int) Math.abs(Math.toDegrees(Math.atan2((obstacleWidth), getDistanceBetweenPoints(possibleObstacle))));
		// angle between obstacle and optimum point from robot's point of view
		int theta2 = (int) Math.toDegrees(Math.abs(angleBetweenPoints(possibleObstacle) - angleBetweenPoints(destination)));
		if (theta2 < theta && getDistanceBetweenPoints(possibleObstacle) < getDistanceBetweenPoints(destination)){
			return true;
		} else return false;
	}


	
	/**
	 * Is this point in area of destination point 
	 * 
	 * @param point
	 * @return
	 */
	public boolean isInPoint(Point point) {
		return isInPoint(point, pointThreshold);
	}
	
	/**
	 * Is this point in area of threshold of destination point 
	 * 
	 * @param point
	 * @param threshold
	 * @return
	 */
	public boolean isInPoint(Point point, int threshold) {
		double x = getX();
		double y = getY();
		double p_x = point.getX();
		double p_y = point.getY();
			
		return x <= p_x + threshold && x >= p_x - threshold && 
			   y <= p_y + threshold && y >= p_y - threshold;
	}
	
	public double getX() {
		return X;
	}
	
	public void setX(double x) {
		X = x;
	}
	
	public double getY() {
		return Y;
	}
	
	public void setY(double y) {
		Y = y;
	}
	
	public void setPT(int threshold) {
		pointThreshold = threshold;
	}
	
	public int getPT() {
		return pointThreshold;
	}
}
