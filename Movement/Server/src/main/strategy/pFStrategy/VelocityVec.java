package main.strategy.pFStrategy;

public class VelocityVec extends Vector implements main.data.VelocityVec {

	public VelocityVec(double left, double right) {
		super(left, right);
		
	}
	public double getLeft()
	{
		return this.getX();
	}
	public double getRight()
	{
		return this.getY();
	}

}
