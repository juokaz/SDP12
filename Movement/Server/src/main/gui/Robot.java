package main.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class Robot implements ActionListener {

	// Image of robot
	private BufferedImage robotImage;

	private ArrayList<Wall> walls;
	private Robot opponent;
	Pitch pitch;

	ArrayList<Drawable> drawables;

	// Position and orientation of robot
	private double xPos;
	private double yPos;
	private double theta;

	private double xPrevPos;
	private double yPrevPos;
	private double prevTheta;

	private boolean isEnabled;

	// Animation variables
	private Timer timer;
	private int animationDuration = 1000;
	private long animationStartTime;
	private double startX;
	private double startY;
	private double startTheta;
	private double endTheta;
	private double leftWheelSpeed;
	private double rightWheelSpeed;
	private int operation;

	// Default values
	public static final int DEFAULT_TIMER_DELAY = 15;
	public static final int DEFAULT_DISTANCE = 200;
	public static final int OPERATION_DEFAULT = 0;
	public static final int OPERATION_ROTATE = 1;

	Kicker kicker;
	Ball ball;
	ArrayList<Point2D> points;

	/**
	 * Constructor for RobotT - loads the image used for the robot - set
	 * position and orientation of robot - instantiate timer
	 * 
	 * @param file
	 *            name to get image from
	 * @param xPos
	 *            of robot
	 * @param yPos
	 *            of robot
	 * @param theta
	 *            orientation
	 */
	public Robot(String file, double xPos, double yPos, double theta) {

		try {

			robotImage = ImageIO.read(Robot.class.getResource(file));

		} catch (IOException e) {

			e.printStackTrace();

		}

		defineRobot(xPos, yPos, Math.toRadians(theta));
		isEnabled = true;
		kicker = new Kicker(getXPos(), getYPos(), getTheta(), getHeight(),
				getWidth());
		// pitch = new Rectangle(30, 107, 705, 370);
		timer = new Timer(DEFAULT_TIMER_DELAY, this);
		timer.setInitialDelay(0);
	}

	/**
	 * Tell the robot and the kicker which object the ball is
	 * 
	 * @param ball
	 */
	public void setBall(Ball ball) {

		this.ball = ball;
		kicker.setBall(this.ball);

	}

	/**
	 * Set the opponent of this robot
	 * 
	 * @param opponent
	 */
	public void setOpponent(Robot opponent) {

		this.opponent = opponent;

	}

	/**
	 * Set boundaries of the pitch
	 * 
	 * @param walls
	 */
	public void setWalls(ArrayList<Wall> walls) {

		this.walls = walls;

	}

	/**
	 * Get a bounding rectangle of the robot
	 * 
	 * @return
	 */
	public Rectangle getRectangle() {

		return new Rectangle((int) getXPos(), (int) getYPos(), getImage()
				.getWidth(), getImage().getHeight());

	}

	/**
	 * Get the corners of the rotated robot Used to check for collisions
	 * 
	 * @return
	 */
	public Point2D[] getCorners() {

		double x = (-getWidth() / 2) * Math.cos(getTheta())
				- (-getHeight() / 2) * Math.sin(getTheta()) + getXPos()
				+ getWidth() / 2;
		double y = (-getWidth() / 2) * Math.sin(getTheta())
				+ (-getHeight() / 2) * Math.cos(getTheta()) + getYPos()
				+ getHeight() / 2;
		Point2D topLeft = new Point2D.Double(x, y);

		x = (getWidth() / 2) * Math.cos(getTheta()) - (-getHeight() / 2)
				* Math.sin(getTheta()) + getXPos() + getWidth() / 2;
		y = (getWidth() / 2) * Math.sin(getTheta()) + (-getHeight() / 2)
				* Math.cos(getTheta()) + getYPos() + getHeight() / 2;
		Point2D topRight = new Point2D.Double(x, y);

		x = (-getWidth() / 2) * Math.cos(getTheta()) - (getHeight() / 2)
				* Math.sin(getTheta()) + getXPos() + getWidth() / 2;
		y = (-getWidth() / 2) * Math.sin(getTheta()) + (getHeight() / 2)
				* Math.cos(getTheta()) + getYPos() + getHeight() / 2;
		Point2D bottomLeft = new Point2D.Double(x, y);

		x = (getWidth() / 2) * Math.cos(getTheta()) - (getHeight() / 2)
				* Math.sin(getTheta()) + getXPos() + getWidth() / 2;
		y = (getWidth() / 2) * Math.sin(getTheta()) + (getHeight() / 2)
				* Math.cos(getTheta()) + getYPos() + getHeight() / 2;
		Point2D bottomRight = new Point2D.Double(x, y);

		Point2D[] corners = { topLeft, topRight, bottomLeft, bottomRight };

		return corners;

	}

	/**
	 * Get the shape of the rotated robot
	 * 
	 * @param x
	 * @param y
	 * @param theta
	 * @return
	 */
	public Shape getShape(double x, double y, double theta) {

		Shape robotShape = new Rectangle2D.Double(x, y, getImage().getWidth(),
				getImage().getHeight());
		AffineTransform xform = new AffineTransform();
		xform.rotate(getTheta(), getXPos() + getWidth() / 2, getYPos()
				+ getHeight() / 2);

		return xform.createTransformedShape(robotShape);

	}

	public Shape getShape() {

		return getShape(getXPos(), getYPos(), getTheta());

	}

	/**
	 * Draw everything that needs drawing here
	 * 
	 * @param g2d
	 */
	public void draw(Graphics2D g2d) {

		// Draw kicker
		kicker.draw(g2d);

		// Draw robot by rotating it theta degrees around its center and
		// translating it
		AffineTransform xform = new AffineTransform();
		xform.rotate(getTheta(), getXPos() + getWidth() / 2, getYPos()
				+ getHeight() / 2);
		xform.translate(getXPos(), getYPos());

		g2d.drawImage(getImage(), xform, null);

		// Draw the rotated corners of the robot
		Point2D[] corners = getCorners();

		for (Point2D corner : corners) {

			g2d.drawOval((int) corner.getX(), (int) corner.getY(), 1, 1);

		}

		if (drawables != null) {

			for (Drawable drawable : drawables) {

				drawable.draw(g2d);

			}

		}

	}

	public void setPoints(ArrayList<Point2D> points) {

		this.points = points;

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

	public void move(double leftWheelSpeed, double rightWheelSpeed) {

		resetMovement();
		updateWheelSpeeds(leftWheelSpeed, rightWheelSpeed);
		startTimer();

	}

	public void kick() {

		kicker.kick();

	}

	public double getCenterX() {
		// return center of the robot on x axis
		return (2 * this.xPos + this.getWidth()) / 2;
	}

	public double getCenterY() {
		// return center of the robot on y axis
		return (2 * this.yPos + this.getHeight()) / 2;
	}

	public synchronized boolean isCollided(Shape robotShape) {

		// Area robotArea = new Area(robotShape);
		Point2D[] corners = getCorners();

		for (Wall wall : walls) {

			// if(robotArea.intersects(wall.getWallRectangle())) {
			for (Point2D corner : corners) {

				if (wall.getWallRectangle().contains(corner))
					return true;

			}

		}

		// Point2D[] corners = getCorners();
		for (Point2D corner : corners) {

			if (opponent.getShape().contains(corner)) {

				return true;

			}

		}

		setPreviousPositions();
		return false;

	}

	public void rotate(double angleRadians) {

		endTheta = getTheta() - angleRadians;
		operation = OPERATION_ROTATE;

		if (angleRadians > 0) {

			move(-25, 25);

		} else {

			move(25, -25);

		}

	}

	public synchronized void animate2(double fraction) {

		if (leftWheelSpeed == rightWheelSpeed)
			rightWheelSpeed -= 0.00000001;

		double theta = ((leftWheelSpeed - rightWheelSpeed) * fraction)
				/ getHeight() + startTheta;

		if (operation == OPERATION_ROTATE) {

			if (Math.abs(theta) > Math.abs(endTheta)) {

				stop();
				theta = endTheta;
				operation = OPERATION_DEFAULT;

			}

		}

		double ratio = (getHeight() * (leftWheelSpeed + rightWheelSpeed))
				/ (2 * (leftWheelSpeed - rightWheelSpeed));
		double x = startX + ratio * (Math.sin(theta) - Math.sin(startTheta));
		double y = startY - ratio * (Math.cos(theta) - Math.cos(startTheta));

		if (!isCollided(getShape(x, y, theta))) {

			setXPos(x);
			setYPos(y);
			setTheta(theta % Math.toRadians(360)); // keep theta within bounds
													// 2*pi

		} else {

			resetMovement();
			usePreviousPositions();
			stop();
			move(-5, -5);

		}

		kicker.updateLocation(x, y, theta);

	}

	public void actionPerformed(ActionEvent e) {

		if (isEnabled) {

			long currentTime = System.currentTimeMillis();
			long totalTime = currentTime - animationStartTime;

			float fraction = (float) totalTime / animationDuration;
			fraction = Math.min(1.0f, fraction);
			animate2(fraction);

			if (totalTime > animationDuration) {

				resetMovement();
				animationStartTime = currentTime;

			}

		} else {

			kicker.updateLocation(getXPos(), getYPos(), getTheta());
			stop();

		}

	}

	public void resetMovement() {

		startX = getXPos();
		startY = getYPos();
		startTheta = getTheta();

	}

	public void toggleCommandReceiving() {

		if (isEnabled) {

			isEnabled = false;

		} else {

			isEnabled = true;

		}

	}

	/*
	 * START AND STOP TIMER
	 */

	public void startTimer() {

		animationStartTime = System.currentTimeMillis();
		timer.start();

	}

	public void stop() {

		timer.stop();

	}

	/*
	 * GETTERS AND SETTERS
	 */

	public void setDrawables(ArrayList<Drawable> drawables) {

		this.drawables = drawables;

	}

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

	public double getThetaDegrees() {

		return Math.toDegrees(theta);

	}

	public void setTheta(double theta) {

		this.theta = theta;

	}
	
	public Kicker getKicker() {
		return kicker;
	}

}
