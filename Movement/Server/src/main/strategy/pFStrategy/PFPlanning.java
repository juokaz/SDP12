package main.strategy.pFStrategy;

import java.util.ArrayList;
import java.util.List;

import main.Runner;

public class PFPlanning {
	Pos robot;
	PointObject opponent;
	PointObject ball;
	double default_power = 5;
	RobotConf config;
	double Stopdistance = 30;
	List<Object> objects;
	// power for opponent.
	double opponentPower;
	// influence distance for opponent
	double opponentInf;
	// power for goal location.
	double ballPower;
	// power orientation, extended potential field.
	double opponentAlphaPower;

	// Constructor for PFPlanning:
	// conf: Configuration parameters of the robot.
	// opponentPower: Repulsive power for opponent
	// opponentInf: Influence distance for opponent.
	// ballPower: Power for goal position
	public PFPlanning(RobotConf conf, double opponentPower, double opponentInf,
			double targetPower) {
		this.config = conf;
		objects = new ArrayList<Object>();
		this.opponentPower = opponentPower;
		this.opponentInf = opponentInf;
		this.ballPower = targetPower;
	}

	// Constructor for PFPlanning:
	// conf: Configuration parameters of the robot.
	// opponentPower: Repulsive power for opponent
	// opponentInf: Influence distance for opponent.
	// ballPower: Power for goal position
	// alpha: Orientation power
	public PFPlanning(RobotConf conf, double opponentPower, double opponentInf,
			double targetPower, double alpha) {
		this.config = conf;
		objects = new ArrayList<Object>();
		this.opponentPower = opponentPower;
		this.opponentInf = opponentInf;
		this.ballPower = targetPower;
		this.opponentAlphaPower = alpha;
	}


	private void init(Pos robot, Pos opponent, Point ball,
			boolean orig)
	{
		PointObject opponentObj = new PointObject(opponent.getLocation(),
				opponentPower, opponentInf, opponentAlphaPower);
		PointObject ballObj = new PointObject(ball, ballPower, Double.MAX_VALUE);
		this.robot = robot;
		this.opponent = opponentObj;
		this.ball = ballObj;
		
	}
	public VelocityVec update(Pos robot, Pos opponent,Point goal, Point ball,
			boolean orig){
		init(robot,opponent,ball,orig);
		Vector vgoal=new Vector(goal);
		Vector vball=new Vector(ball);
		Vector vres=vgoal.subtract(vball);
		Vector threshold=new Vector(20,20);
		Vector finalres=vres.subtract(threshold);
		PointObject obj=new PointObject(finalres, 10000, this.opponentInf);
		List<Object> complList =  new ArrayList<Object>(objects);
		complList.add((Object) this.opponent);
		complList.add(obj);
		Vector res = GoTo(complList, this.ball, robot.getLocation());
		System.out.println("Result Vector: " + res.toString());
		if (orig)
			return (VelocityVec) res;
		else
			return getVelocity(res, robot);


	}

	// Given Pos of different objects, this function will create PointObjects to
	// be used later.
	// robot: robot Pos.
	// opponent: opponent Pos.
	// ball: ball Location.
	// srr: add surrounding objects for target position to approach the target
	// position
	// from specific direction.
	// orig: return original vector, if unset return vector is left,right wheels
	// velocity, if
	// if set it will return the basic velocity vector created by PFPlanner.
	public VelocityVec update(Pos robot, Pos opponent, Point ball,
			boolean orig) {

		init(robot,opponent,ball,orig);
		List<Object> complList =  new ArrayList<Object>(objects);
		complList.add((Object) this.opponent);
		Vector res = GoTo(complList, this.ball, robot.getLocation());
		System.out.println("Result Vector: " + res.toString());
		if (orig)
			return (VelocityVec) res;
		else
			return getVelocity(res, robot);

	}

	// Adds static object in the arena to list of static obstacles.
	public void AddObjects(Object r) {
		objects.add(r);
	}

	// Actual function which computes the the next velocity vector to be applied
	// to the robor.
	public Vector GoTo(List<Object> obstacles, PointObject dest_obj,
			Point start_point) {
		// calculate distance so if we reached the target position we stop.
		double dist = Math.sqrt((start_point.getX() - dest_obj.getX())
				* (start_point.getX() - dest_obj.getX())
				+ (start_point.getY() - dest_obj.getY())
				* (start_point.getY() - dest_obj.getY()));
		if (dist < Stopdistance) {
			return new Vector(0, 0);
		}

		Vector rep = new Vector(0, 0);
		// iterate through all obstacles and compute sum of all repulsive
		// vectors
		for (int i = 0; i < obstacles.size(); i++) {
			rep = rep.add(obstacles.get(i).getVector(start_point, true));
		}
		// Compute attractive vector.
		Vector att = dest_obj.getVector(start_point, false);

		return att.add(rep);

	}

	// Extended Potential Field.This function takes into account the orientation
	// of Robot.
	public Vector GoTo(List<Object> obstacles, PointObject dest_obj,
			Pos start_point) {
		// calculate distance so if we reached the target position we stop.
		double dist = Math.sqrt((start_point.getLocation().getX() - dest_obj
				.getX())
				* (start_point.getLocation().getX() - dest_obj.getX())
				+ (start_point.getLocation().getY() - dest_obj.getY())
				* (start_point.getLocation().getY() - dest_obj.getY()));
		if (dist < Stopdistance) {
			return new Vector(0, 0);
		}

		Vector rep = new Vector(0, 0);
		// iterate through all obstacles and compute sum of all repulsive
		// vectors
		for (int i = 0; i < obstacles.size(); i++) {
			rep = rep.add(obstacles.get(i).getVector(start_point, true));
		}
		// Compute attractive vector.
		if (Runner.DEBUG){
			System.out.println("PFPlanning::PointObject::attractive Force, Clac. attractive forc");
		}
		Vector att = dest_obj.getVector(start_point, false);
		
		return att.add(rep);

	}

	// This function translates linear and angular velocities to left and right
	// velocities.
	private VelocityVec CvtVelocity(double Vlin, double VAng, double r) {
		double left = Vlin - r * Math.sin(VAng);
		double right = Vlin + r * Math.sin(VAng);
		VelocityVec vector = new VelocityVec(left, right);
		// System.out.println("r: " + r+" left" + left + "right" + right);
		return vector;
	}

	// Given current Pos of the robot and current velocity this function
	// computes linear and angular
	// velocities for a differential drive robot using input parameters of the
	// robot.
	private VelocityVec getVelocity(Vector inputVel, Pos current) {

		
		double size = inputVel.size();
		if(size==0)
			return new VelocityVec(0, 0);
		double alpha = inputVel.normalAngle();
		double dist_alpha = alpha - current.getAngle();
		if(Runner.DEBUG)
			System.out.println("Current T: "+current.getAngle()+", dist_alpha="+dist_alpha);
		double Vlin = Math.cos(dist_alpha) * size;
		double Vang = dist_alpha*Math.min(2,1/size);//*size/200;// Math.sin(dist_alpha)*size;
		if(Runner.DEBUG)
			System.out.println(Vlin + " " + Vang);
		return CvtVelocity(Vlin, Vang, config.getr());
	}

}
