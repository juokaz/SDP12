package sdp12.simulator;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class RobotT implements ActionListener {

	// Image of robot
	private BufferedImage robotImage;
	
	private Rectangle pitch;
	
	// Position and orientation of robot
	private double xPos;
	private double yPos;
	private double theta;
	
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
	public static final int DEFAULT_TIMER_DELAY = 10;
	public static final int DEFAULT_DISTANCE = 200;
	
	public RobotT(String file, double xPos, double yPos, double theta) {
		
		try {
			
			robotImage = ImageIO.read(RobotT.class.getResource(file));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		defineRobot(xPos, yPos, Math.toRadians(theta));
		
		pitch = new Rectangle(30, 107, 705, 370);
		
	}
	
	public void defineRobot(double xPos, double yPos, double theta) {
		
		setXPos(xPos);
		setYPos(yPos);
		setTheta(theta);
		
	}
	
	public void move(double leftWheelSpeed, double rightWheelSpeed) {
		
		resetMovement();
		this.leftWheelSpeed = leftWheelSpeed;
		this.rightWheelSpeed = rightWheelSpeed;
		startTimer();
		
	}
	
	public void animate2(double fraction) {
		
		if(leftWheelSpeed == rightWheelSpeed)
			rightWheelSpeed -= 0.00000001;
		
		//System.out.printf("%d %f %f - ", getHeight(), Math.sin(theta), Math.sin(startTheta));
		double theta = ((leftWheelSpeed-rightWheelSpeed)*fraction)/getHeight() + startTheta;
		double ratio = 
			(getHeight()*(leftWheelSpeed+rightWheelSpeed))/(2*(leftWheelSpeed-rightWheelSpeed));
		double x = startX + ratio*(Math.sin(theta) - Math.sin(startTheta));
		double y = startY - ratio*(Math.cos(theta) - Math.cos(startTheta));
		
		setXPos(x);
		setYPos(y);
		setTheta(theta);
		//System.out.printf("%f %f %f\n", x, y, theta);
		
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
		
		//System.out.println("reset");
		//System.out.printf("Before reset: %f %f %f\n", startX, startY, startTheta);
		startX = getXPos();
		startY = getYPos();
		startTheta = getTheta();
		//System.out.printf("After reset: %f %f %f\n", startX, startY, startTheta);
		
	}
	
	/*
	 * START AND STOP TIMER
	 */
	
	public void startTimer() {
		
		timer = new Timer(DEFAULT_TIMER_DELAY, this);
		timer.setInitialDelay(0);
		animationStartTime = System.currentTimeMillis();
		timer.start();
		
	}
	
	public void stop() {
		
		timer.stop();
		
	}
	
	/*
	 *  GETTERS AND SETTERS BELOW
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
