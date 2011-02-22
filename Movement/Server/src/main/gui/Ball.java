package main.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class Ball extends BaseEntity implements ActionListener {
	
	private double xPrevPos;
	private double yPrevPos;
	
	private Timer timer;	
	private double fraction;
	private long animationStartTime;
	private long animationDuration = 15000;
	private double distance = DEFAULT_DISTANCE;
	
	public static final int DEFAULT_DISTANCE = 2;
	public static final int DEFAULT_TIMER_DELAY = 15;
	
	private ArrayList<Wall> walls;
	
	public Ball(String file, double xPos, double yPos) {
		
		try {
			
			image = ImageIO.read(Robot.class.getResource(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		setPreviousPositions();
		
		double x = getXPos() + (distance - (fraction)*distance)*Math.cos(getTheta());
		double y = getYPos() + (distance - (fraction)*distance)*Math.sin(getTheta());
		
		setXPos(x);
		setYPos(y);
		
		resolveCollisions();
		
		if(getXPos() + getImage().getWidth()*2 < 0 || getYPos() + getImage().getHeight()*2 < 0) {
			timer.stop();	
		}
	}

	public void resolveCollisions() {

		for (Wall wall : walls) {

			if (wall.getWallRectangle().intersects(getRectangle())) {

				if (wall.getName() == Wall.BOTTOM_WALL
						|| wall.getName() == Wall.TOP_WALL) {

					setTheta(getTheta() - 2 * getTheta());
					usePreviousPositions();

				}

				if (wall.getName() == Wall.LOWER_LEFT_WALL
						|| wall.getName() == Wall.UPPER_LEFT_WALL
						|| wall.getName() == Wall.LOWER_RIGHT_WALL
						|| wall.getName() == Wall.UPPER_RIGHT_WALL) {

					setTheta(Math.toRadians(180) - getTheta());
					usePreviousPositions();

				}

			}

		}

	}
	
	public void setPreviousPositions() {
		xPrevPos = xPos;
		yPrevPos = yPos;
	}
	
	public void usePreviousPositions() {
		xPos = xPrevPos;
		yPos = yPrevPos;
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
	
	public void draw(Graphics2D g2d) {
		g2d.drawImage(getImage(), null, (int) getXPos(), (int) getYPos());
	}
	
	/*
	 * GETTERS AND SETTERS
	 */

	public void setWalls(ArrayList<Wall> walls) {
		this.walls = walls;
	}

	public ArrayList<Wall> getWalls() {
		return walls;
	}

	public Timer getTimer() {
		return timer;
	}
}
