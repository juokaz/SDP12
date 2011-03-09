package main.strategy;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import main.gui.Drawable;
import main.gui.DrawablesListener;
import main.strategy.pFStrategy.PFPlanning;
import main.strategy.pFStrategy.Pos;
import main.strategy.pFStrategy.RobotConf;
import main.strategy.pFStrategy.VelocityVec;
import main.Runner;
import main.Strategy;
import main.Executor;
import main.data.Ball;
import main.data.CircularBuffer;
import main.data.Goal;
import main.data.Point;
import main.data.Robot;

public abstract class AbstractStrategy implements Strategy {
	
	/**
	 * Pitch dimensions
	 */
	protected int PITCH_X_MIN = 0;
	protected int PITCH_Y_MIN = 0;
	protected int PITCH_X_MAX = 540;
	protected int PITCH_Y_MAX = 290;
	protected String rotateState = "straight";
	protected String rotationState = "straight";

	/**
	 * Thresholds
	 */
	private final static int ballWidth = 10;
	private final static int possesionDistance = 50;
	private int wallThreshold = 30;
	private int optimalGap = 50;
	private int gap = 30;
	// TODO: calculate width of the opponent taking into account its angle (?)
	private int opponentWidth = 75;

	/**
	 * Executor which is going to execute these commands
	 */
	protected Executor executor = null;

	/**
	 * Drawables to display on GUI
	 */
	protected ArrayList<Drawable> drawables;
	protected CircularBuffer ballBuffer = new CircularBuffer(6);
	
	/**
	 * Listener accepting drawables
	 */
	protected DrawablesListener listener = null;

	/**
	 * Set executor
	 * 
	 * @param executor
	 */
	public void setExecutor(Executor executor) {
		this.executor  = executor;
	}
	
	/**
	 * Set drawables listener
	 * 
	 * @param listener
	 */
	public void setDrawablesListener(DrawablesListener listener) 
	{
		this.listener = listener;
	}
	
	/**
	 * Move robot from robotX and robotY to a Point position in a pitch
	 * 
	 * Point can be a Ball, Pitch or other Robot
	 * 
	 * @param robot
	 * @param point
	 */
	protected void moveToPoint(Robot robot, Point point) {
		moveToPoint(robot, point, false);
	}
	
	
	/**
	 * Move robot from robotX and robotY to a Point position in a pitch
	 * 
	 * Point can be a Ball, Pitch or other Robot, or a point...
	 * 
	 * @param robot
	 * @param point
	 * @param dribble_mode
	 */
	protected void moveToPoint(Robot robot, Point point, boolean dribble_mode) {
		
		double dirAngle = 0;
		int left=1;
		int right=1;
		double currentAngle = robot.getTDegrees();

		// find the distance between the robot and the ball
		double dy = robot.getY() - point.getY();
		double dx = robot.getX() - point.getX();
		double distance = Math.sqrt(dx*dx + dy*dy);
		
		dirAngle = Math.toDegrees(robot.angleBetweenPoints(point));

		
		double angleDifference = currentAngle - dirAngle;
		
		while (angleDifference > 180) {
			angleDifference = angleDifference - 360;
		}
		while (angleDifference < -180) {
			angleDifference = angleDifference + 360;
		}
		
		if (rotateState.equals("straight")){
			if (angleDifference > 180)
				rotateState = "left";
			else if (angleDifference >= 0 && angleDifference <= 180)
				rotateState = "right";
			else if (angleDifference < 0) // default condition
				rotateState = "left";
			else
				rotateState = "straight";	
		}
		
		if (rotateState.equals("left")){
			left = -1;
			right = 1;
		}
		if (rotateState.equals("right")){
			left = 1;
			right = -1;
		}
		
		int pointThreshold = point.getPT();
		// once the robot is facing in direction of the ball, move towards it at
		// a velocity proportional to the distance between them
	
		// TODO: Check threshold is acceptable for real robot. 
		if(Math.abs(dirAngle - robot.getTDegrees()) % 360 < 30) {
			left = (int) (1*distance)/(pointThreshold - 5);
			right = (int) (1*distance)/(pointThreshold - 5);	
			rotateState = "straight";
		}
		
		// max speed factor
		int max = 4;

		if (dribble_mode) {
			max = 1;
		}
		
		// Limits Max speed to X*70
		left = Math.min(left, max);
		right = Math.min(right, max);
		
		executor.rotateWheels(left*35, right*35);
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		drawables.add(new Drawable(Drawable.LABEL, "Distance: " + formatter.format(distance), 50, 30, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL, "dirAngle: " + formatter.format(dirAngle), 50, 50, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL, "robotAngle: " + formatter.format(currentAngle), 50, 70, Color.WHITE));

	}
	
	/**
	 * Move to a point using PFS
	 * @param point
	 */
	protected void pfsMoveToPoint(Robot robot, Robot opponent,  Point point) {
		
		PFPlanning planner;
		RobotConf conf = new RobotConf(15.2, 8.27);
		planner = new PFPlanning(conf, 0, 180, 0.045, 250000.0);
		
		Pos current = new Pos(new main.strategy.pFStrategy.Point(robot.getX(), robot.getY()),robot.getT());
		
		Pos opponentA = new Pos(new main.strategy.pFStrategy.Point(opponent.getX(), opponent.getY()), opponent.getT());
		main.strategy.pFStrategy.Point ball = new main.strategy.pFStrategy.Point(point.getX(), point.getY());
		// getting new velocity vectors
		VelocityVec vector = planner.update(current, opponentA, ball,
				false);
		
		// Converting Radians/sec to Degrees/sec
		int left = (int) Math.toDegrees(vector.getLeft());
		int right = (int) Math.toDegrees(vector.getRight());
		if(vector.getLeft()==0&&vector.getRight()==0)
		{
			executor.stop();
			return;
		}
		
		if (Runner.DEBUG){
			System.out.println("Final Command:"+left+","+right);
		}
		executor.rotateWheels(left,right);
		
		
		
		
	}

	
	/**
	 * Change to positive
	 * 
	 * @param angle
	 * @return
	 */
	public double changeToPositive(double angle) {
		angle = angle % 360;
		
		if(angle < 0) {
			angle = 360 - Math.abs(angle);
		}
		
		return angle;
	}
	
	/**
	 * This method detects whether the opponent is in possession of the ball.
	 * It does this by checking if the ball is in-between the opponent and the 
	 * goal they are attacking, and seeing if they are within the possession 
	 * distance of the ball
	 * 
	 * @param robot
	 * @param ball
	 * @param opponentGoal
	 * @return
	 */
	protected boolean isRobotInPossession(Robot robot, Ball ball, Goal goal) {
		
		Goal opponentGoal = new Goal(0, 175);
		if (goal.getX() == 0) {
			opponentGoal.setX(550);
		}
		// Checks to see if ball is inbetween opponent and the opponents goal (goal
		// we are defending), and if it is within a distance that defines possesion.
		if(robot.isObstacleInFront(ball, opponentGoal, ballWidth) 
			&& robot.getDistanceBetweenPoints(ball) <= possesionDistance) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Is point close to walls
	 * 
	 * @param point
	 * @return
	 */
	protected boolean isWallClose(Point point){
		double x = point.getX();
		double y = point.getY();
		return (PITCH_X_MIN + wallThreshold >= x || PITCH_Y_MIN + wallThreshold >= y ||
				PITCH_Y_MAX - wallThreshold <= y || PITCH_X_MAX - wallThreshold <= x);
	}
	
	/**
	 * Is a ball in once of the corners
	 * 
	 * @param ball
	 * @return
	 */
	protected boolean isBallInACorner(Ball ball) {
		int threshold = 30;
		
		return ball.getX() < PITCH_X_MIN + threshold && ball.getY() < PITCH_Y_MIN + threshold ||
			   ball.getX() > PITCH_X_MAX - threshold && ball.getY() < PITCH_Y_MIN + threshold ||
			   ball.getX() > PITCH_X_MAX - threshold && ball.getY() > PITCH_Y_MAX - threshold ||
			   ball.getX() < PITCH_X_MIN + threshold && ball.getY() > PITCH_Y_MAX - threshold;
	}
	
	
	/**
	 * Set listener
	 * 
	 * @param drawables
	 */
	protected void setDrawables(ArrayList<Drawable> drawables) 
	{
		if (listener != null) {
			listener.setDrawables(drawables);
		}
	}
	
	/**
	 * Returns true if the robot is in a position where it can kick the ball at the goal.
	 * 
	 * @param robot
	 * @param ball
	 * @param goal
	 * @return
	 */
	protected boolean isBallKickable(Robot robot, Ball ball, Goal goal) {
		if (isBallReached(robot, ball) && atShootingAngle(robot, goal)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns true if the robot is facing the correct angle to shoot at goal.
	 * 
	 * @param robot
	 * @param goal
	 * @return
	 */
	protected boolean atShootingAngle(Robot robot, Point goal) {
		if (robot.getT() <= robot.angleBetweenPoints(goal) + Math.toRadians(20) 
				&& robot.getT() >= robot.angleBetweenPoints(goal) - Math.toRadians(20)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Are we in a point which allows a kick
	 * 
	 * @param robot
	 * @param ball
	 * @return
	 */
	protected boolean isBallReached(Robot robot, Ball ball) {
		return robot.isInPoint(ball);
	}
	
	
	
	
	/**
	 * Add drawables
	 * 
	 * @param robot
	 * @param opponent
	 * @param ball
	 * @param optimum
	 */
	protected void addDrawables(Robot robot, Robot opponent, Ball ball, Point optimum, Goal goal) {
		// Creating new object every time because of reasons related to
		// the way Swing draws stuff. Will be fixed later.
		drawables = new ArrayList<Drawable>();
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		// Robot states of execution
		drawables.add(new Drawable(Drawable.LABEL,
						"isBallInACorner: " + isBallInACorner(ball),
						800, 30, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"isOpponentInPossession: " + isRobotInPossession(opponent, ball, goal),
				800, 50, Color.BLACK));

		drawables.add(new Drawable(Drawable.LABEL,
						"isObstacleInFront: " + robot.isObstacleInFront(opponent, optimum, opponentWidth),
						800, 70, Color.BLACK));
		
		drawables.add(new Drawable(Drawable.LABEL,
						"gap: " + gap,
						450, 30, Color.WHITE));
		drawables.add(new Drawable(Drawable.LABEL,
						"Ball (X, Y): " + formatter.format(ball.getX()) + " " + formatter.format(ball.getY()),
						450, 50, Color.WHITE));

		drawables.add(new Drawable(Drawable.LABEL,
				"Angle between robot and obstacle edge: " + formatter.format(Math.abs(Math.toDegrees(Math.atan((2*opponentWidth)/robot.getDistanceBetweenPoints(opponent))))),
				800, 170, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Angle between optimum and opponent from robot view: " + formatter.format(Math.toDegrees(Math.abs(Math.abs(opponent.angleBetweenPoints(robot)) - Math.abs(optimum.angleBetweenPoints(robot))))),
				800, 190, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Difference of angles between robots: " + formatter.format(Math.abs(Math.abs(robot.angleBetweenPoints(optimum)) - Math.abs(opponent.angleBetweenPoints(optimum)))),
				800, 210, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Distance between optimum and robot: " + formatter.format(robot.getDistanceBetweenPoints(optimum)),
				800, 230, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Distance between optimum and opponent: " + formatter.format(opponent.getDistanceBetweenPoints(optimum)),
				800, 250, Color.BLACK));
		drawables.add(new Drawable(Drawable.LABEL,
				"Distance between robot and goal: " + formatter.format(goal.getDistanceBetweenPoints(robot)),
				800, 270, Color.BLACK));
	
	}
	
	/**
	 * Set information about what I'm doing doing right now
	 * 
	 * @param name
	 */
	protected void setIAmDoing(String name) {
		if(drawables==null)
			drawables=new ArrayList<Drawable>();
		drawables.add(new Drawable(Drawable.LABEL,
						"I am doing: " + name,
						800, 140, Color.RED));
	}
	
	/**
	 * Draw point on screen to show its position
	 * 
	 * @param point
	 * @param label
	 */
	protected void drawPoint(Point point, String label) {
		drawPoint(point, label, true);
	}
	
	/**
	 * Draw point on screen to show its position
	 * 
	 * @param point
	 * @param label
	 * @param remap
	 */
	protected void drawPoint(Point point, String label, boolean remap) {
		if(drawables==null)
			drawables=new ArrayList<Drawable>();
		drawables.add(new Drawable(Drawable.CIRCLE,
						(int) point.getX(), (int) point.getY(), Color.WHITE, remap));
		if (label != null) {
			drawables.add(new Drawable(Drawable.LABEL,
					label,
					(int) point.getX() + 5, (int) point.getY(), Color.WHITE, remap));
		}
	}
	
	protected void setGap(int newGap) {
		gap = newGap;
	}
	
	protected int getGap() {
		return gap;
	}
	
	protected int getOptimalGap() {
		return optimalGap;
	}
	
	protected int getOppenentWidth(){
		return opponentWidth;
	}
}
