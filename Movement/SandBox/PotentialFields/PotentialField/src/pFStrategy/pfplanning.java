package pFStrategy;

import java.util.ArrayList;
import java.util.List;


public class pfplanning {
	PointObject robot;
	PointObject opponent;
	PointObject ball;
	double default_power=5;
	RobotConf config;
	
	List<Object> objects;
public pfplanning(RobotConf conf)
{
	this.config=conf;
	objects=new ArrayList<Object>();
}

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
public pFStrategy.VelocityVec update(Pos robot,Point ball,boolean srr,boolean orig)
{
	PointObject robotObj=new PointObject(robot.getLocation(),0,20);
	PointObject opponentObj=new PointObject(new Point(0, 0),0,20);
	PointObject ballObj=new PointObject(ball, 0, 20);
	pFStrategy.Vector res=updateLocal(robotObj,opponentObj,ballObj,srr);
	if(orig)
	{
		return (VelocityVec) res;
	}
	else
	return getVelocity(res,robot);
	
}
public pFStrategy.VelocityVec update(Pos robot,Pos opponent,Point ball,boolean srr,boolean orig)
{
	PointObject robotObj=new PointObject(robot.getLocation(),0,20);
	PointObject opponentObj=new PointObject(opponent.getLocation(),400,20);
	PointObject ballObj=new PointObject(ball, 0, 20);
	pFStrategy.Vector res=updateLocal(robotObj,opponentObj,ballObj,srr);
	if(orig)
		return (VelocityVec) res;
	else
		return getVelocity(res,robot);
	
}
private pFStrategy.Vector updateLocal(PointObject robot,PointObject opponent,PointObject ball,boolean srr)
{
	this.robot=robot;
	this.opponent=opponent;
	this.ball=ball;
	List<Object> compl=new ArrayList<Object>(objects);
	compl.add((Object) this.opponent);
	if(srr)
	{
		compl.addAll(getSorroundinObj(ball, 5));
	}
	pFStrategy.Vector res =GoTo(compl, ball, robot);
	
	System.out.println("Result Vector: "+res.toString());
	return res;
	
	
}
public void update(int robot_x,int robot_y,double robot_angle,int opponent_x,int opponent_y,double opponent_angle,int ball_x,int ball_y)
{
	robot=new PointObject(robot_x, robot_y,1,Double.MAX_VALUE);
	opponent=new PointObject(opponent_x, opponent_y,1,Double.MAX_VALUE);
	ball=new PointObject(ball_x, ball_y,1,Double.MAX_VALUE);
}
public void AddObjects(Object r)
{
	objects.add(r);
}
public pFStrategy.Vector GoTo(List<Object> obstacles, Point dest_point,Point start_point)
{
	pFStrategy.Vector rep=new pFStrategy.Vector(0,0);
	for(int i=0;i<obstacles.size();i++)
	{
		rep=rep.add(obstacles.get(i).getVector(start_point, true));
	}
	PointObject dest_obj=new PointObject(dest_point, 1, Double.MAX_VALUE);
	pFStrategy.Vector att=dest_obj.getVector(start_point, false);
	
	return att.add(rep);

}
private VelocityVec CvtVelocity(double Vlin,double VAng,double r)
{
	double left=Vlin-r*Math.sin(VAng);
	double right=Vlin+r*Math.sin(VAng);
	VelocityVec vector=new VelocityVec(left, right);
	return vector;
}
private VelocityVec getVelocity(pFStrategy.Vector inputVel,Pos current)
{
	
	double size=inputVel.size();
	double alpha=inputVel.normalAngle();
	double dist_alpha=alpha-current.getAngle();
	double Vlin=Math.cos(dist_alpha)*size;
	double Vang=dist_alpha;//Math.sin(dist_alpha)*size;
	return CvtVelocity(Vlin,Vang,config.getr());
}


}
