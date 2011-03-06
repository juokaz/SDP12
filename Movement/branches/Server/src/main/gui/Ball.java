package main.gui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Ball extends AbstractSimulatedObject implements ActionListener, CollisionListener {
	private final String TYPE = "Ball";

	private Timer timer;	
	private double fraction;
	private long animationStartTime;
	private long animationDuration = 15000;
	private double distance = DEFAULT_DISTANCE;
	
	public static final int DEFAULT_DISTANCE = 2;
	public static final int DEFAULT_TIMER_DELAY = 15;
	
	/**
	 * Constructor for ball - set position and load image
	 * 
	 * @param file
	 * @param xPos
	 * @param yPos
	 */
	public Ball(String file, double xPos, double yPos) {
		loadAndSetImage(file);
		
		setXPos(xPos);
		setYPos(yPos);
		
		timer = new Timer(DEFAULT_TIMER_DELAY, this);
		timer.setInitialDelay(0);
	}

	public Rectangle getRectangle() {
		
		return new Rectangle(
				(int) getXPos(),
				(int) getYPos(),
				getImage().getWidth(),
				getImage().getHeight()
				);
		
	}
	
	public void kick(double theta) {
		
		setTheta(theta);
		animationStartTime = System.currentTimeMillis();
		timer.start();
		
	}
	
	public void moveBall() {
		double x = getXPos() + (distance - (fraction)*distance)*Math.cos(getTheta());
		double y = getYPos() + (distance - (fraction)*distance)*Math.sin(getTheta());
		
		setXPos(x);
		setYPos(y);
		
		if(getXPos() + getImage().getWidth()*2 < 0 || getYPos() + getImage().getHeight()*2 < 0) {
			
			timer.stop();
			
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		long currentTime = System.currentTimeMillis();
		long totalTime = currentTime - animationStartTime;
		
		fraction = (float) totalTime / animationDuration;
		fraction = Math.min(1.0f, fraction);
		
		if(totalTime > animationDuration) {
			
			timer.stop();
			
		}
		
		moveBall();
		
	}
	
	/**
	 * Stop ball movement
	 */
	public void stop() {
		timer.stop();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public void collisionDetected(Collision collision) {
		//System.out.println("collision " + collision.getCollidedObjectType() + " " + changeToPositive(collision.getSideAngle()));
		System.out.println("Ball handling collision");
		System.out.println("ball angle before collision: " + Math.toDegrees(getTheta()));
		System.out.println(collision);
		kick(changeToPositive(collision.getSideAngle()));
		System.out.println("ball angle after collision: " + Math.toDegrees(getTheta()));
	}
	
	// TODO FINISH THIS
	public double getAngleDifference(double sideAngle, double ballAngle) {
		double angleDifference = changeToPositive(sideAngle) - changeToPositive(ballAngle);
		if ((angleDifference < 0 && Math.abs(angleDifference) < 180)
					|| (angleDifference > 180 && Math.abs(angleDifference) > 180)) {
			
		}
		return 0;
	}
	
	public double changeToPositive(double angle) {
		angle = angle % (2*Math.PI);
		
		if(angle < 0) {
			angle = (2*Math.PI) - Math.abs(angle);
		}
		
		return angle;
	}
}
