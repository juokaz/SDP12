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
