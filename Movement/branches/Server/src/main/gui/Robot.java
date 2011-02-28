package main.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Robot extends AbstractSimulatedObject implements CollisionListener {
	private final String TYPE = "Robot";
	
	private double xPrevPos;
	private double yPrevPos;
	private double prevTheta;
	
	private double leftWheelSpeed;
	private double rightWheelSpeed;
	
	private int operation;
	
	// Default values
	public static final int OPERATION_MOVE = 0;
	public static final int OPERATION_MOVE_FADE = 1;
	
	public static final float UPDATE_SPEED = 0.05f;
	public static final float EROSION_RATE = 0.92f;
	
	Kicker kicker;
	
	/**
	 * Constructor for RobotT 
	 * 	- loads the image used for the robot
	 * 	- set position and orientation of robot
	 * 
	 * @param file name to get image from
	 * @param xPos of robot
	 * @param yPos of robot
	 * @param theta orientation
	 */
	public Robot(String file, double xPos, double yPos, double theta) {
		loadAndSetImage(file);
		
		defineRobot(xPos, yPos, Math.toRadians(theta));
		isEnabled = true;
		kicker = new Kicker(getXPos(), getYPos(), getTheta(), getWidth(), getHeight());
	}
	
	/**
	 * Get a bounding rectangle of the robot
	 * 
	 * @return
	 */
	public Rectangle getRectangle() {
		
		return new Rectangle(
				(int) getXPos(),
				(int) getYPos(),
				getImage().getWidth(),
				getImage().getHeight()
				);
		
	}
	
	/**
	 * Update the location of the robot based on the set wheel speeds
	 */
	public void updateLocation() {
		// Set the current positions as previous in case of collision
		setPreviousPositions();
		
		if(!isEnabled) {
			return;
		}
		
		if(operation == OPERATION_MOVE_FADE) {
			erodeWheelsSpeed(EROSION_RATE);
		}
		
		// A hack to avoid division by zero
		if(leftWheelSpeed == rightWheelSpeed)
			leftWheelSpeed -= 0.00000001;
		
		double theta = UPDATE_SPEED*(leftWheelSpeed-rightWheelSpeed)/getHeight()
							+ getTheta();
		double ratio = (getHeight()*(leftWheelSpeed + rightWheelSpeed)) / 
							(2*(leftWheelSpeed - rightWheelSpeed));
		double x = getXPos() + ratio*(Math.sin(theta) - Math.sin(getTheta()));
		double y = getYPos() - ratio*(Math.cos(theta) - Math.cos(getTheta()));
		
		defineRobot(x, y, theta);
		kicker.updateLocation(getXPos(), getYPos(), getTheta());
	}
	
	private void erodeWheelsSpeed(float erosionRate) {
		setWheelsSpeed(erosionRate*leftWheelSpeed, erosionRate*rightWheelSpeed);
	}
	
	/**
	 * Store the current position and orientation of the robot
	 * 	in case a collision occurs and there is a need to revert
	 */
	private void setPreviousPositions() {
		xPrevPos = xPos;
		yPrevPos = yPos;
		prevTheta = theta;
	}
	
	/**
	 * Use positions set as previous
	 */
	public void usePreviousPositions() {
		defineRobot(xPrevPos, yPrevPos, prevTheta);
	}
	
	/**
	 * Shortcut to set all three variables that define the robot
	 * 
	 * @param xPos
	 * @param yPos
	 * @param theta
	 */
	public void defineRobot(double xPos, double yPos, double theta) {
		setXPos(xPos);
		setYPos(yPos);
		setTheta(theta);
	}
	
	/**
	 * Set the speed of the wheels of the robot
	 * 
	 * @param leftWheelSpeed
	 * @param rightWheelSpeed
	 */
	public void setWheelsSpeed(double leftWheelSpeed, double rightWheelSpeed) {
		this.leftWheelSpeed = leftWheelSpeed;
		this.rightWheelSpeed = rightWheelSpeed;
	}
	
	public void move(double leftWheelSpeed, double rightWheelSpeed) {
		setWheelsSpeed(leftWheelSpeed, rightWheelSpeed);
		setOperation(OPERATION_MOVE);
	}
	
	private void moveFade(double leftWheelSpeed, double rightWheelSpeed) {
		setWheelsSpeed(leftWheelSpeed, rightWheelSpeed);
		setOperation(OPERATION_MOVE_FADE);
	}
	
	/**
	 * Enable and disable the robot
	 */
	public void toggleCommandReceiving() {
		if(isEnabled) {
			isEnabled = false;
		} else {
			isEnabled = true;
		}
	}
	
	public void kick() {
		kicker.kick();
	}

	public Kicker getKicker() {
		return kicker;
	}
	
	@Override
	public void draw(Graphics2D g2d) {
		kicker.draw(g2d);
		super.draw(g2d);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public void collisionDetected(Collision collision) {
		//System.out.println(getType() + " colliding with " + collision.getCollidedObjectType());
		if(collision.getCollidedObjectType() == TYPE) {
			// No particular reason to use these values, just looks alright
			//moveFade(-5, -5);
		}
	}
	
	private void setOperation(int operation) {
		this.operation = operation;
	}
}
