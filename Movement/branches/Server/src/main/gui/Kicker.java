package main.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.Timer;

public class Kicker extends AbstractSimulatedObject
						implements ActionListener, CollisionListener {
	private final String TYPE = "Kicker";

	private double kickerPartShown;
	private double fraction;
	private long animationStartTime;
	private long animationDuration = 300;
	private Timer timer;
	
	public static final int DEFAULT_TIMER_DELAY = 15;
	
	private Rectangle kicker;
	
	public Kicker(double xPos, double yPos, double theta, int robotWidth, int robotHeight) {
		
		setHeight(robotHeight);
		setWidth(robotWidth);
		
		kickerPartShown = getWidth()/10;
		
		/*
		 * TODO
		 * WARNING - not true at the moment, should be fixed
		 * |-y
		 * *** x  
		 * 0**--- 0 marks the origin (0,0); * asterisks show where 
		 * *** x							the kicker rectangle lies
		 * | y
		 * 
		 * This is needed to make the (x,y) position
		 * 	the centre of the kicker's rectangle
		 */
		kicker = new Rectangle(0, 0, getWidth()/10, getHeight()/2);
		
		updateLocation(xPos, yPos, theta);
		
		timer = new Timer(DEFAULT_TIMER_DELAY, this);
		timer.setInitialDelay(0);
		
	}
	
	public void updateLocation(double xPos, double yPos, double theta) {
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.theta = theta;
		
	}
	
	public void kick() {
		
		animationStartTime = System.currentTimeMillis();
		timer.start();
		System.out.println("kick");
		
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
	}
	
//	public void checkCollisions() {
//		
//		if(getShape().intersects(ball.getRectangle())) {
//			
//			ball.kick(getTheta());
//			
//		}
//		
//	}
//	
	@Override
	public void draw(Graphics2D g2d) {
		g2d.draw(getShape());
	}
	
	@Override
	public Shape getShape() {
		
		AffineTransform xform = new AffineTransform();
		xform.rotate(getTheta(), getXPos(), getYPos());
		xform.translate(getXPos() + getWidth()/2 - getWidth()/10 + fraction*kickerPartShown, 
						getYPos() - getHeight()/4);
		
		return xform.createTransformedShape(kicker);
		
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public void collisionDetected(Collision collision) {
		// TODO Handle collisions
		
	}
	
}
