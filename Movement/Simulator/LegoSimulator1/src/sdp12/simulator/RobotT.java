package sdp12.simulator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class RobotT implements ActionListener {

	// Image of robot
	private BufferedImage robotImage;
	
	private ArrayList<Wall> walls;
	private RobotT opponent;
	Pitch pitch;
	// Position and orientation of robot
	private double xPos;
	private double yPos;
	private double theta;
	
	private double xPrevPos;
	private double yPrevPos;
	private double prevTheta;
	boolean running = true;
	// Animation variables
	private Timer timer;
	private int animationDuration = 1000;
	private long animationStartTime;
	private int distance = DEFAULT_DISTANCE;
	private double startX;
	private double startY;
	private double startTheta;
	private double endTheta;
	private double speed;
	private double leftWheelSpeed;
	private double rightWheelSpeed;
	// Default values
	public static final int DEFAULT_TIMER_DELAY = 15;
	public static final int DEFAULT_DISTANCE = 200;
	
	Kicker kicker;
	Ball ball;
	
	public RobotT(String file, double xPos, double yPos, double theta) {
		
		try {
			
			robotImage = ImageIO.read(RobotT.class.getResource(file));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		defineRobot(xPos, yPos, Math.toRadians(theta));
		kicker = new Kicker(getXPos(), getYPos(), getTheta(), getHeight(), getWidth());
		//pitch = new Rectangle(30, 107, 705, 370);
		timer = new Timer(DEFAULT_TIMER_DELAY, this);
		timer.setInitialDelay(0);
	}
	
	public void setBall(Ball ball) {
		
		this.ball = ball;
		kicker.setBall(this.ball);
		
	}
	
	public void setOpponent(RobotT opponent) {
		
		this.opponent = opponent;
		
	}
	
	public void setWalls(ArrayList<Wall> walls) {
		
		this.walls = walls;
		
	}
	
	public Rectangle getRectangle() {
		
		return new Rectangle(
				(int) getXPos(),
				(int) getYPos(),
				getImage().getWidth(),
				getImage().getHeight()
				);
		
	}
	
	public Point2D[] getCorners() {
		
		double x = (-getWidth()/2)*Math.cos(getTheta())
						- (-getHeight()/2)*Math.sin(getTheta()) + getXPos() + getWidth()/2;
		double y = (-getWidth()/2)*Math.sin(getTheta())
						+ (-getHeight()/2)*Math.cos(getTheta()) + getYPos() + getHeight()/2;
		Point2D topLeft = new Point2D.Double(x, y);
		
		x = (getWidth()/2)*Math.cos(getTheta())
						- (-getHeight()/2)*Math.sin(getTheta()) + getXPos() + getWidth()/2;
		y = (getWidth()/2)*Math.sin(getTheta())
						+ (-getHeight()/2)*Math.cos(getTheta()) + getYPos() + getHeight()/2; 
		Point2D topRight = new Point2D.Double(x, y);
		
		x = (-getWidth()/2)*Math.cos(getTheta())
						- (getHeight()/2)*Math.sin(getTheta()) + getXPos() + getWidth()/2;
		y = (-getWidth()/2)*Math.sin(getTheta())
						+ (getHeight()/2)*Math.cos(getTheta()) + getYPos() + getHeight()/2;
		Point2D bottomLeft = new Point2D.Double(x, y);
		
		x = (getWidth()/2)*Math.cos(getTheta())
						- (getHeight()/2)*Math.sin(getTheta()) + getXPos() + getWidth()/2;
		y = (getWidth()/2)*Math.sin(getTheta())
						+ (getHeight()/2)*Math.cos(getTheta()) + getYPos() + getHeight()/2;
		Point2D bottomRight = new Point2D.Double(x, y);
		
		Point2D[] corners = {topLeft, topRight, bottomLeft, bottomRight};
		
		return corners;
		
	}
	
	public Shape getShape(double x, double y, double theta) {
		
		Shape robotShape = new Rectangle2D.Double(x, y,
									getImage().getWidth(), getImage().getHeight());
		AffineTransform xform = new AffineTransform();
		xform.rotate(getTheta(), getXPos() + getWidth()/2, getYPos() + getHeight()/2);
		
		return xform.createTransformedShape(robotShape);
		
	}
	
	public Shape getShape() {
		
		return getShape(getXPos(), getYPos(), getTheta());
		
	}
	
	public void draw(Graphics2D g2d) {
		
		kicker.draw(g2d);
		
		AffineTransform xform = new AffineTransform();
		xform.rotate(getTheta(), getXPos() + getWidth()/2, getYPos() + getHeight()/2);
		xform.translate(getXPos(), getYPos());
		
		g2d.drawImage(getImage(), xform, null);
		
		Point2D[] corners = getCorners();
		
		for(Point2D corner : corners) {
			
			g2d.drawOval((int)corner.getX(),(int) corner.getY(), 1, 1);
			
		}
		
	}
	
	public void setPreviousPositions() {
		
		xPrevPos = xPos;
		yPrevPos = yPos;
		prevTheta = theta;
		
	}
	
	public void usePreviousPositions() {
		
		defineRobot(xPrevPos, yPrevPos, prevTheta);
		
	}
	
	public void defineRobot(double xPos, double yPos, double theta) {
		
		setXPos(xPos);
		setYPos(yPos);
		setTheta(theta);
		
	}
	
	private void updateWheelSpeeds(double leftWheelSpeed, double rightWheelSpeed) {
		
		this.leftWheelSpeed = leftWheelSpeed;
		this.rightWheelSpeed = rightWheelSpeed;
		
	}
	
	public synchronized void move(double leftWheelSpeed, double rightWheelSpeed) {
		
		resetMovement();
		updateWheelSpeeds(leftWheelSpeed, rightWheelSpeed);
		startTimer();
		
	}
	
	public void kick() {
		
		kicker.kick();
		
	}
	
	public synchronized boolean isCollided(Shape robotShape) {
	
		Area robotArea = new Area(robotShape);
		Point2D[] corners = getCorners();
		
		for(Wall wall : walls) {
			
			//if(robotArea.intersects(wall.getWallRectangle())) {
			for(Point2D corner : corners) {
			
				if(wall.getWallRectangle().contains(corner))
					return true;
				
			}
			
		}
		
		//Point2D[] corners = getCorners();
		for(Point2D corner : corners) {
			
			if(opponent.getShape().contains(corner)) {
				
				return true;
				
			}
			
		}
		
		setPreviousPositions();
		return false;
		
	}
	
	public synchronized void animate2(double fraction) {
		
		if(leftWheelSpeed == rightWheelSpeed)
			rightWheelSpeed -= 0.00000001;
		
		//System.out.printf("%d %f %f - ", getHeight(), Math.sin(theta), Math.sin(startTheta));
		double theta = ((leftWheelSpeed-rightWheelSpeed)*fraction)/getHeight() + startTheta;
		double ratio = 
			(getHeight()*(leftWheelSpeed+rightWheelSpeed))/(2*(leftWheelSpeed-rightWheelSpeed));
		double x = startX + ratio*(Math.sin(theta) - Math.sin(startTheta));
		double y = startY - ratio*(Math.cos(theta) - Math.cos(startTheta));
	
		if(!isCollided(getShape(x, y, theta))) {
		
			setXPos(x);
			setYPos(y);
			setTheta(theta);
		//System.out.printf("%f %f %f\n", x, y, theta);
		
		} else { 
			
			resetMovement();
			usePreviousPositions();
			stop();
			move(-5, -5);
		
		} 
		
		kicker.updateLocation(x, y, theta);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		long currentTime = System.currentTimeMillis();
		long totalTime = currentTime - animationStartTime;
		
		float fraction = (float) totalTime / animationDuration;
		fraction = Math.min(1.0f, fraction);
		animate2(fraction);
		
		if(totalTime > animationDuration) {
			
			resetMovement();
			animationStartTime = currentTime;
			
		}
		
	}
	
	public void resetMovement() {
		
//		System.out.println("reset");
//		System.out.printf("Before reset: %f %f %f\n", startX, startY, startTheta);
		startX = getXPos();
		startY = getYPos();
		startTheta = getTheta();
//		System.out.printf("After reset: %f %f %f\n", startX, startY, startTheta);
		
	}
	
	/*
	 * START AND STOP TIMER
	 */
	
	public void startTimer() {
		
		
		animationStartTime = System.currentTimeMillis();
		timer.start();
		
	}
	
	public void stop() {
		
		timer.stop(); running = false;
		
	}
	
	/*
	 *  GETTERS AND SETTERS
	 */
	
	public BufferedImage getImage() {
		
		return robotImage;
		
	}
	
	public int getHeight() {
		
		return robotImage.getHeight();
		
	}
	
	public int getWidth() {
		
		return robotImage.getWidth();
		
	}
	
	public double getXPos() {
		
		return xPos;
		
	}

	public void setXPos(double xPos) {
		
		this.xPos = xPos;
		
	}

	public double getYPos() {
		
		return yPos;
		
	}

	public void setYPos(double yPos) {
		
		this.yPos = yPos;
		
	}

	public double getTheta() {
		
		return theta;
		
	}

	public void setTheta(double theta) {
		
		this.theta = theta;
		
	}
	
}
