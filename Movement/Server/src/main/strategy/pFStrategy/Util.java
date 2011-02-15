package main.strategy.pFStrategy;

public class Util {
	public static VelocityVec convertVeltoDegree(VelocityVec vector)
	{
		return new VelocityVec(vector.getLeft()/(Math.PI)*180, vector.getRight()/(Math.PI)*180);
		
	}
}
