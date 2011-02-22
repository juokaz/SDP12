package main.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.Timer;

import main.gui.Ball;

public class Kicker implements ActionListener {

	private double xPos;
	private double yPos;
	private double theta;
	
	private int holderRobotHeight;
	private int holderRobotWidth;
	
	private double kickerPartShown;
	private double fraction;
	private long animationStartTime;
	private long animationDuration = 300;
	private Timer timer;
	
	public static final int DEFAULT_TIMER_DELAY = 15;
	
	private boolean disabled = true;
	
	private Rectangle kicker;
	Ball ball;
	
	public Kicker(double xPos, double yPos, double theta, int robotHeight, int robotWidth) {
		
		setHolderRobotHeight(robotHeight);
		setHolderRobotWidth(robotWidth);
		
		kickerPartShown = getHolderRobotWidth()/10;
		
		kicker = new Rectangle(0, 0, getHolderRobotWidth()/10, getHolderRobotHeight()/2);
		
		updateLocation(xPos, yPos, theta);
		
		timer = new Timer(DEFAULT_TIMER_DELAY, this);
		timer.setInitialDelay(0);
		
	}
	
	public void setBall(Ball ball) {
		
		this.ball = ball;
	}
	
	public void updateLocation(double xPos, double yPos, double theta) {
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.theta = theta;
		
	}
	
	public void kick() {
		
		animationStartTime = System.currentTimeMillis();
		timer.start();
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		long currentTime = System.currentTimeMillis();
		long totalTime = currentTime - animationStartTime;
		
		fraction = (float) totalTime / animationDuration;
		fraction = Math.min(1.0f, fraction);
		fraction = 1 - Math.abs(1 - 2*fraction);
		
		if(totalTime > animationDuration) {
			
			timer.stop();
			
		}
		
		checkCollisions();
		
	}
	
	public void checkCollisions() {
		
		if(getShape().intersects(ball.getRectangle())) {
			
			ball.kick(getTheta());
			
		}
		
	}
	
	public void draw(Graphics2D g2d) {
		
		if (!disabled) {
			g2d.draw(getShape());
		}
	}
	
	public Shape getShape() {
		
		AffineTransform xform = new AffineTransform();
		xform.rotate(getTheta(), getXPos() + getHolderRobotWidth()/2,
						getYPos() + getHolderRobotHeight()/2);
		xform.translate(getXPos() + getHolderRobotWidth() - getHolderRobotWidth()/10
									+ fraction*kickerPartShown, 
						getYPos() + getHolderRobotHeight()/2 - getHolderRobotHeight()/4);
		
		return xform.createTransformedShape(kicker);
		
	}
	
	public void enable() {
		disabled = false;
	}
	
	public void disable() {
		disabled = true;
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

	public void setHolderRobotHeight(int holderRobotHeight) {
		this.holderRobotHeight = holderRobotHeight;
	}

	public int getHolderRobotHeight() {
		return holderRobotHeight;
	}

	public void setHolderRobotWidth(int holderRobotWidth) {
		this.holderRobotWidth = holderRobotWidth;
	}

	public int getHolderRobotWidth() {
		return holderRobotWidth;
	}
	
}
