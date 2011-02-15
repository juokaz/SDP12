package main.strategy;

import main.Strategy;
import main.data.Location;

import main.strategy.pFStrategy.*;

public class PFStrategy extends AbstractStrategy implements Strategy {

	private pfplanning planner;
	VelocityVec current;
	public PFStrategy(double b,double r)
	{
		RobotConf conf=new RobotConf(b, r);
		planner=new pfplanning(conf);
		current=new VelocityVec(0, 0);
		
	}
	
	public void updateLocation(Location data) {
		double dist=Math.sqrt((data.getRobotA().getX()-data.getBall().getX())*(data.getRobotA().getX()-data.getBall().getX())
				+(data.getRobotA().getY()-data.getBall().getY())*(data.getRobotA().getY()-data.getBall().getY()));
		if(dist<5)
		{
			this.executor.exit(data.getRobotA());
			return;
		}
		Pos current= new Pos(new Point(data.getRobotA().getX(), data.getRobotA().getY()), data.getRobotA().getT());
		Pos opponent= new Pos(new Point(data.getRobotB().getX(), data.getRobotB().getY()), data.getRobotB().getT());
		Point ball=new Point(data.getBall().getX(),data.getBall().getY());
		VelocityVec vector= planner.update(current,opponent, ball, false, false);
		int left = (int) Math.toDegrees(vector.getLeft());
		int right = (int) Math.toDegrees(vector.getRight());
		this.current=vector;
		if (left < 0 && Math.abs(left - right)>400){
			this.current = new VelocityVec(0, this.current.getRight());
		}
		if ( right < 0 && Math.abs(left - right)>400){
			this.current = new VelocityVec(this.current.getLeft(), 0);
		}
		int max_speed=200;
		if ( left > max_speed ){
			right=(int)(max_speed*right/left);
			left=max_speed;
			this.current = new VelocityVec(Math.toRadians(left), Math.toRadians(right));
		}
		if ( right > max_speed ){
			left=(int)(max_speed*left/right);
			right=max_speed;
			this.current = new VelocityVec(Math.toRadians(left), Math.toRadians(right));
		}
		vector = this.current;
		executor.rotateWheels(data.getRobotA(),(int) Math.toDegrees(vector.getLeft()), (int) Math.toDegrees(vector.getRight()));
		
		}

	@Override
	public VelocityVec getVelocity() {
		// TODO Auto-generated method stub
		return current;
	}
	

}
