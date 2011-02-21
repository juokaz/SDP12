package sdp12.simulator;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class LegoSimulator {

	JFrame simulatorFrame;
	
	// Hold pitch
	Pitch pitch;
	
	RobotT robot1;
	RobotT robot2;
	Ball ball;
	
	// Constructor for LegoSimulator
	public LegoSimulator() {
		
		pitch = new Pitch();
		
	}
	
	public LegoSimulator(RobotT robot1, RobotT robot2) {
		
		this.robot1 = robot1;
		this.robot2 = robot2;
		
		pitch = new Pitch(robot1, robot2);
		
	}
	
	public LegoSimulator(RobotT robot1, RobotT robot2, Ball ball) {
		
		this.robot1 = robot1;
		this.robot2 = robot2;
		this.ball = ball;
		
		pitch = new Pitch(robot1, robot2, ball);
		
	}
	
	public void close() {
		
		simulatorFrame.dispose();
		
	}
	
	public void writeToFile() {
		

	    FileWriter fstream;
		try {
			
			fstream = new FileWriter("data/out.txt");
		    BufferedWriter writer = new BufferedWriter(fstream);
		    
		    String outputString = "";
		    
		    outputString = outputString.concat(robot1.getXPos() + " ");
		    outputString = outputString.concat(robot1.getYPos() + " ");
		    outputString = outputString.concat((float) Math.toDegrees(robot1.getTheta()) + " ");
		    
		    outputString = outputString.concat(ball.getXPos() + " ");
		    outputString = outputString.concat(ball.getYPos() + " ");
		    
		    outputString = outputString.concat(robot2.getXPos() + " ");
		    outputString = outputString.concat(robot2.getYPos() + " ");
		    outputString = outputString.concat((float) Math.toDegrees(robot2.getTheta()) + " ");

		    writer.write(outputString + System.getProperty("line.separator"));
		    
		    writer.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		
	}
	
	// Set GUI properties
	public void createAndShowGUI() {
		
		simulatorFrame = new JFrame("Simulator");
		simulatorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulatorFrame.setSize(pitch.getPitchWidth() + 250, pitch.getPitchHeight());
		simulatorFrame.add(pitch);
		simulatorFrame.setVisible(true);
		
	}
	public static int i = 0;
	public static void main(String[] args) {
		
		Runnable doCreateAndShowGUI = new Runnable() {
			
			public void run() {
				
				final RobotT robot1 = new RobotT("images/ty.jpg", 500, 400, 90);
				final RobotT robot2 = new RobotT("images/ty.jpg", 700, -200, 180);
				final Ball ball = new Ball("images/ball.jpg", 768/2, 576/2);
				
				LegoSimulator legoSimulator = new LegoSimulator(robot1, robot2, ball);
				legoSimulator.createAndShowGUI();
				
				Action u = new AbstractAction() {
					public void actionPerformed (ActionEvent e) {
						
//						Random rand = new Random();
//						if(i%2==0) robot1.move(rand.nextInt(100),rand.nextInt(100));
//						else robot1.move(Math.pow(-1, i%5)*rand.nextInt(100),Math.pow(-1, i%5)*rand.nextInt(100));
//						i++;
//						if(i%5==0) {robot1.kick();};
						//if(i==1) ball.kick(Math.toRadians(60));
//						if(i==0)robot1.rotate(720);i++;
//						robot1.kick(); 
//						if(i==2)robot1.rotate(-50);
						
						double dirAngle = Math.atan2(ball.getYPos()-robot1.getYPos(),
														ball.getXPos()-robot1.getXPos());
						double dy = robot1.getYPos() - ball.getYPos();
						double dx = robot1.getXPos() - ball.getXPos();
						double distance = Math.sqrt(dx*dx + dy*dy);
						
						int a=1;
						int b=1;
						if(dirAngle > robot1.getTheta()) {
							
							a=-1;
							b=1;
							
						} 
						
						if (dirAngle < robot1.getTheta()) {
							
							a=1;
							b=-1;
							
						}
						
						if((Math.abs(dirAngle - robot1.getTheta()) % Math.toRadians(360)) 
												< Math.toRadians(30)) {
							
							a = (int) (1*distance)/35;
							b = (int) (1*distance)/35;		
							
						}
						
						robot1.move(a*50, b*50);
						
					}
				};
				Timer timer = new Timer(10,u);
				timer.setRepeats(true);
				timer.start();
				
			}
			
		};
		
		SwingUtilities.invokeLater(doCreateAndShowGUI);
		
	}
	
}
