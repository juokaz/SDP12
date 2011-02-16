package main.strategy.pFStrategy;
//some utility functions.
public class Util {
	//converts a Radians velocity vector to Degrees.
	public static VelocityVec convertVeltoDegree(VelocityVec vector)
	{
		
		return new VelocityVec(vector.getLeft()/(Math.PI)*180, vector.getRight()/(Math.PI)*180);
		
	}
}
