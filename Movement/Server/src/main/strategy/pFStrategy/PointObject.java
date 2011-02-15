package main.strategy.pFStrategy;

public class PointObject extends Vector implements Object {

	private final double power;
	private final double infl_distance;
	public PointObject(double x, double y,double power,double infl_distance) {
		super(x, y);
		this.power=power;
		this.infl_distance=infl_distance;
	}
	public PointObject(Point point,double power,double infl_distance) {
		this(point.getX(),point.getY(),power,infl_distance);
	}
	

	@Override
	public Vector getVector(Point point,boolean repulsive) {
		if(repulsive)
		{
			
			double distance=Math.sqrt((this.getX()-point.getX())*(this.getX()-point.getX())+(this.getY()-point.getY())*(this.getY()-point.getY()));
			if(distance <infl_distance)
			{
				double p= power*(1/distance-1/infl_distance)*1/(distance*distance)*1/distance;
				Vector out_point=new Vector(point);
				return out_point.subtract(this).mult(p);
			}
			else
				return new Vector(new Point(0, 0));
		}
		else
		{
			
			Vector out_point=new Vector(point);
			return out_point.subtract(this).mult(power*-1);
			
		}
		
	}
	@Override
	public String toString()
	{
		return super.toString();
	}
}
