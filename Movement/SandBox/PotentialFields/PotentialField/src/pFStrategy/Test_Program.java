package pFStrategy;
import java.io.*;
public class Test_Program {
	public static void main(String[] args)
	{
		double default_power=300;
		double b=10;
		double r=4;
		RobotConf conf=new RobotConf(b, r);
		pfplanning plann=new pfplanning(conf);
		double wall_Thickness=0;
		Point top_left=new Point(0,0);
		Point bottom_right=new Point(50,50);
		RectObject left_wall=new RectObject(new Point(top_left.getX()-wall_Thickness,top_left.getY()),new Point(top_left.getX()+wall_Thickness,bottom_right.getY()),default_power, Double.MAX_VALUE);
		System.out.println("Left wall:"+left_wall.toString());
		RectObject right_wall=new RectObject(new Point(bottom_right.getX()-wall_Thickness,top_left.getY()),new Point(bottom_right.getX()+wall_Thickness,bottom_right.getY()), default_power, Double.MAX_VALUE);
		System.out.println("Right wall:"+right_wall.toString());
		RectObject top_wall=new RectObject(new Point(top_left.getX(),top_left.getY()-wall_Thickness),new Point(bottom_right.getX(),top_left.getY()+wall_Thickness), default_power, Double.MAX_VALUE);
		System.out.println("Top wall:"+top_wall.toString());
		RectObject bottom_wall=new RectObject(new Point(top_left.getX(),bottom_right.getY()-wall_Thickness),new Point(bottom_right.getX(),bottom_right.getY()+wall_Thickness), default_power, Double.MAX_VALUE);
		System.out.println("Bottom wall:"+bottom_wall.toString());
		//plann.AddObjects(left_wall);
		//plann.AddObjects(right_wall);
		//plann.AddObjects(top_wall);
		//plann.AddObjects(bottom_wall);
		
		//Point robot=new Point(.5*10,0.5*50);
		
		
		//opponent=null;
		//ball=null;
		//ouput_allVectors(top_left, bottom_right,ball,opponent, plann,0.5,false);
		Pos initPos  =new Pos(new Point(25, 25),0);
		Pos opponent = new Pos(new Point(35,35),0);
		Point ball = new Point(45,45);
		output_path(initPos,0.1, ball, opponent, plann, false,conf);
		
		
		
		
	}
	public static Vector CvtVelocity(double Vlin,double VAng,double r)
	{
		double left=Vlin-r*Math.sin(VAng);
		double right=Vlin+r*Math.sin(VAng);
		Vector vector=new Vector(left, right);
		return vector;
	}

	public static void output_path(Pos init,double time_step,Point ball,Pos opponent,pfplanning planner,boolean srr,RobotConf config)
	{
		File path=new File("pathOutput.txt");
		FileWriter writer;
		try {
			writer = new FileWriter(path);
		
			Simulate sim=new Simulate(init,config.getb());
			Pos current=init;
			
			double distance=Math.sqrt((current.getLocation().getX()-ball.getX())*(current.getLocation().getX()-ball.getX())+(current.getLocation().getY()-ball.getY())*(current.getLocation().getY()-ball.getY()));
			while(distance>0.75)
			{
			VelocityVec res=planner.update(current,opponent,ball,srr,false);
			current=sim.move(res.getLeft(), res.getRight(), time_step);
			distance=Math.sqrt((current.getLocation().getX()-ball.getX())*(current.getLocation().getX()-ball.getX())+(current.getLocation().getY()-ball.getY())*(current.getLocation().getY()-ball.getY()));
			writer.write(current.getLocation().getX()+","+current.getLocation().getY()+","+res.getX()+","+res.getY()+"\n");
			}
		
		writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void ouput_allVectors(Point top_left,Point bottom_right,Point ball,Pos opponent,pfplanning planner,double resolution,boolean srr)
	{
		File vel=new File("vel-Output.txt");
		FileWriter writer;
		try {
			writer = new FileWriter(vel);
		
		
		
		for(double x=top_left.getX();x<=bottom_right.getX();x+=resolution)
		{
			for(double y=top_left.getY();y<=bottom_right.getY();y+=resolution)
			{
				Pos robot=new Pos(new Point(x,y),0);
				Vector vec;
				Point b;
				
				if(ball!=null)
					b=ball;
				else
					b=robot.getLocation();
				if(opponent!=null)
					vec=planner.update(robot,opponent,b,srr,true);
				else
					vec=planner.update(robot,b,srr,true);
				writer.write(String.valueOf(x)+","+String.valueOf(y)+","+String.valueOf(vec.getX())+","+String.valueOf(vec.getY())+"\n");
			}
		}
		writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	}

