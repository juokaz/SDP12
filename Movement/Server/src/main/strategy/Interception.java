package main.strategy;

import main.Strategy;
import main.data.Ball;
import main.data.Goal;
import main.data.Location;
import main.data.Point;
import main.data.Robot;

public class Interception extends AbstractStrategy implements Strategy {
	
	private int ballCount = 0;
	private int countNeeded = 8;					//require 20 readings of ball position
	private boolean predictionPointSet = false;
	private int lineLength = 200;					//length of prediction line
	private Point intercept = new Point(0,0);
	
	@Override
	public void updateLocation(Location data) {
		
		Point optimum  = new Point(0,0);
		Goal goal = data.getGoal();
		Ball ball = data.getBall();
		Robot opponent = data.getOpponentRobot();
		Robot robot = data.getOurRobot();
		ballBuffer.addPoint(ball);
		
		addDrawables(robot, opponent, ball, optimum, goal);
		drawPoint(intercept,"Predict");
		
		//run kicker to get ball moving - comment out when testing on pitch
		//executor.kick();
		
		if (ballIsMoving(data.getBall()) && !predictionPointSet) {
			//read a certain number of values
			ballCount += 1;
			if (ballCount > countNeeded) {
				intercept = getPredictionPoint(ball, lineLength);
				predictionPointSet = true;
			}
			setIAmDoing("Getting ball positions");
		} else if (robot.isInPoint(intercept)){
			//reset to find new prediction point
			predictionPointSet = false;
			ballCount = 0;
			executor.stop();
			setIAmDoing("Predict point reached - stopping");
		} else if (predictionPointSet) {
			moveToPoint(robot, intercept);
			setIAmDoing("Going to point - predict");
		} else {
			//reset count of no. of values read.
			ballCount = 0;
			setIAmDoing("Resetting ball count");
		}
		
		setDrawables(drawables);
		
	}
	
	/**
	 * Checks when ball has moved since last reading
	 * @return
	 */
	private boolean ballIsMoving(Ball ball) {
		Point oldBall = new Point (ballBuffer.getXPosAt(ballBuffer.getLastPosition()),ballBuffer.getYPosAt(ballBuffer.getLastPosition()));
		if (oldBall.getDistanceBetweenPoints(ball) > 5) {
			return true;
		} else
			return false;
	}
	
	/**
	 * Gets a point that is LENGTH away from the ball based on it's previous positions
	 * 
	 * @param ball
	 * @param length
	 * @return
	 */
	protected Point getPredictionPoint(Ball ball, int length) {

		Predictor predictor = new Predictor();
		//for(int i = 0; i < ballBuffer.getBufferLength(); i++)
		//	drawPoint(ballBuffer.getPointAt(i), "");
		double[] parameters = new double[4];
		predictor.fitLine(parameters, ballBuffer.getXBuffer(), ballBuffer.getYBuffer(),
							null, null, ballBuffer.getBufferLength());
		
		//get the x offset value such that distance of will always equal 100
		int lineLength = length;
		int xOffset = (int) (lineLength / Math.sqrt(1 + parameters[1]*parameters[1]));
		
		//changed the offset if the ball is travelling right
		if(Math.abs(ballBuffer.getXPosAt(ballBuffer.getCurrentPosition())) -
				Math.abs(ballBuffer.getXPosAt(ballBuffer.getLastPosition())) > 0)
			xOffset = xOffset * -1;

		//define coordinates of the line to draw
		//int x1 = (int) ball.getX();
		//int y1 = (int) (parameters[1]*ball.getX() + parameters[0]);
		int x2 = (int) ball.getX()+xOffset;
		int y2 = (int) (parameters[1]*(ball.getX()+xOffset) + parameters[0]);
		
		//draw the line between (x1,y1) and (x2,y2)
		//drawables.add(new Drawable(Drawable.LINE, x1, y1, x2, y2, Color.CYAN, true));
		
		//define a point to move to based on x2, y2
		int x = x2;
		int y = y2;
		
		Point predictPoint = new Point(x,y);
		
		//check if the line is going out of the pitch
		while (predictPoint.isPointOutOfPitch()) {
			
			//use symmetry to get point in pitch
			if (x2 > PITCH_X_MAX) {
				predictPoint.setX(PITCH_X_MAX - Math.abs(x2 - PITCH_X_MAX));
			}
			if (x2 < PITCH_X_MIN) {
				predictPoint.setX(PITCH_X_MIN + Math.abs(x2 - PITCH_X_MIN));
			}
			if (y2 > PITCH_Y_MAX) {
				predictPoint.setY(PITCH_Y_MAX - Math.abs(y2 - PITCH_Y_MAX));
			}
			if (y2 < PITCH_Y_MIN) {
				predictPoint.setY(PITCH_Y_MIN + Math.abs(y2 - PITCH_Y_MIN));
			}	
				
		}
		
		return predictPoint;
	}

}
