package main.strategy;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import main.Runner;
import main.Strategy;
import main.data.Location;

import main.strategy.pFStrategy.*;

public class PFStrategy extends AbstractStrategy implements Strategy {

	private PFPlanning planner;
	VelocityVec current;
	BufferedWriter fstream;
	Calendar cal;
	SimpleDateFormat sdf;
	public PFStrategy(double b, double r) {
		RobotConf conf = new RobotConf(b, r);
		//opponent power: 10000000
		planner = new PFPlanning(conf, 0, 180, 0.04, 250000.0);
		current = new VelocityVec(0, 0);
		cal= Calendar.getInstance();
		String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
		sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		File outputFile=new File("./logs/pfs/"+sdf.format(cal.getTime())+".log");
		
		try {
			fstream =new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//double wallPowers=500;
		//double wallinf=60;
		//planner.AddObjects(new RectObject(new Point(0, 0), new Point(540,30), wallPowers,wallinf));
		//planner.AddObjects(new RectObject(new Point(0, 280), new Point(540,290), wallPowers,wallinf));
	}

	// updateLocation called in each cycle with fresh location updates.
	public void updateLocation(Location data) {
		
		Pos current = new Pos(new Point(data.getOurRobot().getX(), data
				.getOurRobot().getY()),data.getOurRobot().getT());
		
		Pos opponent = new Pos(new Point(data.getOpponentRobot().getX(), data
				.getOpponentRobot().getY()), data.getOpponentRobot().getT());
		Point ball = new Point(data.getBall().getX(), data.getBall().getY());
		//ball=new Point(260,80);
		Point goal=new Point(data.getGoal().getX(),data.getGoal().getY());
		// getting new velocity vectors
		VelocityVec vector = planner.update(current, opponent,goal, ball,
				false);
		Vector orig_vector = planner.update(current, opponent,goal, ball,
				true);
		double dist=Math.sqrt((ball.getX()-current.getLocation().getX())*(ball.getX()-current.getLocation().getX())+
		(ball.getY()-current.getLocation().getY())*(ball.getY()-current.getLocation().getY()));
		if(dist<5)
			vector=new VelocityVec(0, 0);
		// Converting Radians/sec to Degrees/sec
		int left = (int) Math.toDegrees(vector.getLeft());
		int right = (int) Math.toDegrees(vector.getRight());
		if(vector.getLeft()==0&&vector.getRight()==0)
		{
			executor.stop();
			return;
		}

		this.current = vector;
		try {
			fstream.write(current.getLocation().getX()+","+current.getLocation().getY()+","+current.getAngle()+
					","+ball.getX()+","+ball.getY()+","+
					opponent.getLocation().getX()+","+opponent.getLocation().getY()+","+opponent.getAngle()+","+
					orig_vector.getX()+","+orig_vector.getY()+","+cal.getTimeInMillis()+"\n");
			fstream.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		if (Runner.DEBUG){
			System.out.println("Final Command:"+left+","+right);
		}
		executor.rotateWheels(left,right);

	}
}