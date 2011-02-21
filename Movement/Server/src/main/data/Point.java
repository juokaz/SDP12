package main.data;

/**
 * Point object
 * 
 * A piece of data containing X and Y
 */
public class Point {

	protected double X;
	protected double Y;
	
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
	 * Finds the distance between two points, using dirPoint as the point to move to
	 * 
	 * @param dirPoint
	 * @return
	 */
	public double getAngleBetweenPoints(Point dirPoint) {
		return Math.atan2(getY()-dirPoint.getY(), getX()-dirPoint.getX());
	}
	
	/**
	 * Is this point in area of destination point 
	 * 
	 * @param point
	 * @return
	 */
	public boolean isInPoint(Point point) {
		// TODO check 40
		return isInPoint(point, 40);
	}
	
	/**
	 * Is this point in area of threshold of destination point 
	 * 
	 * @param point
	 * @param treshold
	 * @return
	 */
	public boolean isInPoint(Point point, int threshold) {
		double x = getX();
		double y = getY();
		double p_x = point.getX();
		double p_y = point.getY();
			
		return x < p_x + threshold && x > p_x - threshold && 
			   y < p_y + threshold && y > p_y - threshold;
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
}
