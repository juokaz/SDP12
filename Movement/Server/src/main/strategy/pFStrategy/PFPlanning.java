package main.strategy.pFStrategy;

import java.util.ArrayList;
import java.util.List;


public class PFPlanning {
	Pos robot;
	PointObject opponent;
	PointObject ball;
	double default_power=5;
	RobotConf config;
	double Stopdistance=0;
	List<Object> objects;
	//power for opponent.
	double opponentPower;
	//influence distance for opponent 
	double opponentInf;
	//power for goal location.
	double ballPower;
	//power orientation, extended potential field.
	double opponentAlphaPower;
//Constructor for PFPlanning:
//conf: Configuration parameters of the robot.
//opponentPower: Repulsive power for opponent
//opponentInf: Influence distance for opponent.
//ballPower: Power for goal position
public PFPlanning(RobotConf conf,double opponentPower,double opponentInf,double targetPower)
{
	this.config=conf;
	objects=new ArrayList<Object>();
	this.opponentPower=opponentPower;
	this.opponentInf=opponentInf;
	this.ballPower=targetPower;
}
//Constructor for PFPlanning:
//conf: Configuration parameters of the robot.
//opponentPower: Repulsive power for opponent
//opponentInf: Influence distance for opponent.
//ballPower: Power for goal position
//alpha: Orientation power
public PFPlanning(RobotConf conf,double opponentPower,double opponentInf,double targetPower,double alpha)
{
	this.config=conf;
	objects=new ArrayList<Object>();
	this.opponentPower=opponentPower;
	this.opponentInf=opponentInf;
	this.ballPower=targetPower;
	this.opponentAlphaPower=alpha;
}
//under development! this function will add small obstacles around the goal position to 
//force robot to approach target position from specific orientation. 
private List<Object> getSorroundinObj(Point center,double len)
{
	
	//Point p1=new Point(center.getX()-len/2, center.getY()-thresh/2-len/2);
	//Point p2=new Point(center.getX()+len/2, center.getY()+thresh/2-len/2);
	//RectObject a=new RectObject(p1, p2, default_power, Double.MAX_VALUE);
	PointObject a;//=new PointObject(center.getX()-len/2, center.getY(), default_power, Double.MAX_VALUE);
	List<Object> res=new ArrayList<Object>();
	//res.add(a);
	a=new PointObject(center.getX(), center.getY()-1*len/3, default_power*10, Double.MAX_VALUE);
	res.add(a);
	a=new PointObject(center.getX()-len/2, center.getY(), default_power, Double.MAX_VALUE);
	res.add(a);
	//a=new PointObject(center.getX()+len/2, center.getY(), default_power, Double.MAX_VALUE);
	//res.add(a);
	a=new PointObject(center.getX()+len/2, center.getY(), default_power, Double.MAX_VALUE);
	res.add(a);
//	p1=new Point(center.getX()-thresh/2-len/2, center.getY()-len/2);
//	p2=new Point(center.getX()+thresh/2-len/2, center.getY()+len/2);
//	a=new RectObject(p1, p2, default_power, Double.MAX_VALUE);
//	res.add(a);
//	p1=new Point(center.getX()+thresh/2+len/2, center.getY()-len/2);
//	p2=new Point(center.getX()+thresh/2+len/2, center.getY()+len/2);
//	a=new RectObject(p1, p2, default_power, Double.MAX_VALUE);
//	res.add(a);
	return res;
	
}
//Given Pos of different objects, this function will create PointObjects to be used later.
//robot: robot Pos.
//opponent: opponent Pos.
//ball: ball Location.
//srr: add surrounding objects for target position to approach the target position
//	   from specific direction.
//orig: return original vector, if unset return vector is left,right wheels velocity, if
//      if set it will return the basic velocity vector created by PFPlanner. 
public VelocityVec update(Pos robot,Pos opponent,Point ball,boolean srr,boolean orig)
{
	
	PointObject opponentObj=new PointObject(opponent.getLocation(),opponentPower,opponentInf,opponentAlphaPower);
	PointObject ballObj=new PointObject(ball,ballPower, Double.MAX_VALUE);
	this.robot=robot;
	this.opponent=opponentObj;
	this.ball=ballObj;
	List<Object> complList=updateLocal(srr);
	Vector res =GoTo(complList, this.ball, robot);
	System.out.println("Result Vector: "+res.toString());
	if(orig)
		return (VelocityVec) res;
	else
		return getVelocity(res,robot);
	
}
//Finalizes the list of obstacles in the arena, if srr is set it will try to add extra objects around goal
//position to force robot to approach the target from specific orienation.
private List<Object> updateLocal(boolean srr)
{
	List<Object> compl=new ArrayList<Object>(objects);
	compl.add((Object) this.opponent);
	if(srr)
	{
		compl.addAll(getSorroundinObj(ball, 5));
	}
	
	return compl;
}
//Adds static object in the arena to list of static obstacles.
public void AddObjects(Object r)
{
	objects.add(r);
}
//Actual function which computes the the next velocity vector to be applied to the robor.
public Vector GoTo(List<Object> obstacles, PointObject dest_obj,Point start_point)
{
	//calculate distance so if we reached the target position we stop.
	double dist=Math.sqrt((start_point.getX()-dest_obj.getX())*(start_point.getX()-dest_obj.getX())
			+(start_point.getY()-dest_obj.getY())*(start_point.getY()-dest_obj.getY()));
	if(dist<Stopdistance)
	{
		return new Vector(0, 0);
	}
	
	Vector rep=new Vector(0,0);
	//iterate through all obstacles and compute sum of all repulsive vectors 
	for(int i=0;i<obstacles.size();i++)
	{
		rep=rep.add(obstacles.get(i).getVector(start_point, true));
	}
	//Compute attractive vector. 
	Vector att=dest_obj.getVector(start_point, false);
	
	return att.add(rep);

}
//Extended Potential Field.This function takes into account the orientation of Robot.
public Vector GoTo(List<Object> obstacles, PointObject dest_obj,Pos start_point)
{
	//calculate distance so if we reached the target position we stop.
	double dist=Math.sqrt((start_point.getLocation().getX()-dest_obj.getX())*(start_point.getLocation().getX()-dest_obj.getX())
			+(start_point.getLocation().getY()-dest_obj.getY())*(start_point.getLocation().getY()-dest_obj.getY()));
	if(dist<Stopdistance)
	{
		return new Vector(0, 0);
	}
	
	Vector rep=new Vector(0,0);
	//iterate through all obstacles and compute sum of all repulsive vectors 
	for(int i=0;i<obstacles.size();i++)
	{
		rep=rep.add(obstacles.get(i).getVector(start_point, true));
	}
	//Compute attractive vector. 
	Vector att=dest_obj.getVector(start_point, false);
	
	return att.add(rep);

}
//This function translates linear and angular velocities to left and right velocities. 
private VelocityVec CvtVelocity(double Vlin,double VAng,double r)
{
	double left=Vlin-r*Math.sin(VAng);
	double right=Vlin+r*Math.sin(VAng);
	VelocityVec vector=new VelocityVec(left, right);
	//System.out.println("r: " + r+" left" + left + "right" + right);
	return vector;
}
//Given current Pos of the robot and current velocity this function computes linear and angular
//velocities for a differential drive robot using input parameters of the robot.
private VelocityVec getVelocity(Vector inputVel,Pos current)
{
	
	double size=inputVel.size();
	double alpha=inputVel.normalAngle();
	double dist_alpha=alpha-current.getAngle();
	double Vlin=Math.cos(dist_alpha)*size;
	double Vang=dist_alpha;//Math.sin(dist_alpha)*size;
	//System.out.println(Vlin + " " + Vang);
	return CvtVelocity(Vlin,Vang,config.getr());
}


}
