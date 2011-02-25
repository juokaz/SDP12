package main.strategy;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import main.Runner;
import main.Strategy;
import main.data.Ball;
import main.data.Goal;
import main.data.Location;

import main.data.Robot;
import main.gui.Drawable;

import main.strategy.pFStrategy.*;

public class PFStrategy extends AbstractStrategy implements Strategy {

	private PFPlanning planner;
	VelocityVec current;

	public PFStrategy(double b, double r) {
		RobotConf conf = new RobotConf(b, r);
		planner = new PFPlanning(conf, 10000000, 100, 0.018, 250000.0);
		current = new VelocityVec(0, 0);

	}

	// updateLocation called in each cycle with fresh location updates.
	public void updateLocation(Location data) {


		Pos current = new Pos(new Point(data.getOurRobot().getX(), data
				.getOurRobot().getY()),data.getOurRobot().getT());
		
		Pos opponent = new Pos(new Point(data.getOpponentRobot().getX(), data
				.getOpponentRobot().getY()), data.getOpponentRobot().getT());
		Point ball = new Point(data.getBall().getX(), data.getBall().getY());
		Point goal=new Point(data.getGoal().getX(),data.getGoal().getY());
		// getting new velocity vectors
		VelocityVec vector = planner.update(current, opponent,goal, ball,
				false);
		
		// Converting Radians/sec to Degrees/sec
		int left = (int) Math.toDegrees(vector.getLeft());
		int right = (int) Math.toDegrees(vector.getRight());
		if(vector.getLeft()==0&&vector.getRight()==0)
		{
			executor.stop();
			return;
		}
		// Caps for produced velocities, This has to be removed once the
		// algorithm is tuned.
		this.current = vector;
		// if (left < 0 && Math.abs(left - right)>400){
		// this.current = new VelocityVec(0, this.current.getRight());
		// }
		// if ( right < 0 && Math.abs(left - right)>400){
		// this.current = new VelocityVec(this.current.getLeft(), 0);
		// }
		// int max_speed=10;
		// if ( Math.abs(left) > max_speed ){
		// right=(int)(Math.signum(right)*(max_speed*Math.abs(right)/Math.abs(left)));
		// left=(int) (Math.signum(max_speed)*max_speed);
		// this.current = new VelocityVec(Math.toRadians(left),
		// Math.toRadians(right));
		// }
		// if ( Math.abs(right) > max_speed ){
		// left=(int)(Math.signum(right)*(max_speed*Math.abs(left)/Math.abs(right)));
		// right=(int) Math.signum(max_speed)*max_speed;
		// this.current = new VelocityVec(Math.toRadians(left),
		// Math.toRadians(right));
		// }
		// vector = this.current;
		// Applying produced velocities to the executer.
		if (Runner.DEBUG){
			System.out.println("Final Command:"+left+","+right);
		}
		executor.rotateWheels(left,right);

	}


}
