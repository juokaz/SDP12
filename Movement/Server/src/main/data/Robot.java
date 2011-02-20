package main.data;

/**
 * A robot
 */
public class Robot extends Point{

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
	
	private double calculateRobotPointDistance(Point dirPoint) {
		
		double dx = getX()-dirPoint.getX();
		double dy = getY()-dirPoint.getY();
		
		double distance = Math.sqrt(dx*dx+dy*dy);
		
		return distance;
	}
}
