package main.strategy.pFStrategy;

public class Vector extends Point {

	public Vector(double x, double y) {
		super(x, y);
		
	}
	public Vector(Point point) {
		super(point.getX(),point.getY());
	}
	public Vector add(Vector vector)
	{
		return new Vector(this.getX()+vector.getX(),this.getY()+vector.getY());
	}
	public Vector subtract(Vector vector)
	{
		return new Vector(this.getX()-vector.getX(),this.getY()-vector.getY());
	}
	public Vector mult(double scalar)
	{
		return new Vector(this.getX()*scalar,this.getY()*scalar);
	}
	public double normalAngle()
	{
		return Math.atan2(getY(),getX());
	}
	public double size()
	{
		return Math.sqrt(getY()*getY()+getX()*getX());
	}
	@Override
	public String toString()
	{
		return super.toString();
	}
}
