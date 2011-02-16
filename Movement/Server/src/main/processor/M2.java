package main.processor;

import lejos.robotics.proposal.UpdateablePose;
import main.Processor;
import main.data.Ball;
import main.data.Location;
import main.data.Robot;
import main.executor.Simulator;

public class M2 extends AbstractProcessor implements Processor {

	Simulator sim;
	int X1;
	int Y1;
	int X2;
	int Y2;
	int BallX;
	int BallY;
	public M2(Simulator sim,int X1,int Y1,int X2,int Y2,int BallX,int BallY)
	{
		this.sim=sim;
		this.X1=X1;
		this.Y1=Y1;
		this.X2=X2;
		this.Y2=Y2;
		this.BallX=BallX;
		this.BallY=BallY;
	}
	
	
}