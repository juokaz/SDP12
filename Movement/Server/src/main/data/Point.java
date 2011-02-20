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
	 * Fings the distance between two points, using dirPoint as the point to move to
	 * @param dirPoint
	 * @return
	 */
	private double distanceBetweenPoints(Point dirPoint) {
		
		double dx = getX()-dirPoint.getX();
		double dy = getY()-dirPoint.getY();
		
		double distance = Math.sqrt((dx*dx) + (dy*dy));
		
		return distance;
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
