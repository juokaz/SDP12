package main.gui;

import java.awt.image.BufferedImage;

public class BaseEntity {

	protected double xPos;
	protected double yPos;
	protected double theta;

	protected double xOffset = 10;
	protected double xRatio = 1.3;
	protected double yOffset = 120;
	protected double yRatio = 1.1;
	
	protected BufferedImage image;
	
	public BufferedImage getImage() {
		return image;
	}
	public int getHeight() {
		return image.getHeight();
	}
	public int getWidth() {
		return image.getWidth();
	}
	public double getCenterX() {
		// return center of the robot on x axis
		return (2 * this.xPos + this.getWidth()) / 2;
	}

	public double getCenterY() {
		// return center of the robot on y axis
		return (2 * this.yPos + this.getHeight()) / 2;
	}
	public double getCenterXRemapped() {
		return getCenterX()/xRatio - xOffset;
	}
	public double getCenterYRemapped() {
		return getCenterY()/yRatio - yOffset;
	}
	public void setXPosRemapped(double xPos) {
		this.xPos = xPos*xRatio + xOffset;
	}
	public void setYPosRemapped(double yPos) {
		this.yPos = yPos*yRatio + yOffset;
	}
	public double getXPosRemapped() {
		return xPos/xRatio - xOffset;
	}
	public double getYPosRemapped() {
		return yPos/yRatio - yOffset;
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
