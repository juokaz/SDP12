import java.io.DataInputStream;
import lejos.robotics.navigation.Pilot;
import lejos.robotics.navigation.TachoPilot;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * Create a connection to Roboto from the computer.
 * execute commands send from the computer
 * test out movements of Roboto.
 * 
 * @author s0815695
 */
public class roboto {

	private static final int EXIT = 0;

	private static final int KICK = 1;
	
	private static final int MOVE = 2;
	
	private static final int ROTATE = 3;
	
	private static final int STOP = 4;
	
	private static final int CELEBRATE = 5;
	
	private static final int PENALTY = 6;
	

	
	/**
	 * Main program
	 * @param args
	 */
	public static void main(String[] args) {
		
		TouchSensor touchRight = new TouchSensor(SensorPort.S1);
		TouchSensor touchLeft = new TouchSensor(SensorPort.S2);
		
		while (true) {
			// Enter button click will halt the program
			if (Button.ENTER.isPressed())
				break;
			
			drawMessage("Connecting...");
	
			BTConnection connection = Bluetooth.waitForConnection();
	
			drawMessage("Connected");
			
			DataInputStream input = connection.openDataInputStream();
			
			Pilot pilot = new TachoPilot(15.2f, 8.27f, Motor.A, Motor.C, true); //parameters in mm
			
			mainLoop:
			while (true) {
				// Enter button click will halt the program
				if (Button.ENTER.isPressed())
					break;
				
				try {
					int command = input.readInt();
					
					if (touchLeft.isPressed() || touchRight.isPressed()){
						Motor.A.setSpeed(200);
						Motor.C.setSpeed(200);
						Motor.A.forward();
						Motor.C.forward();
						Thread.sleep(400);
					} 
					
					// no input available
					while(input.available() == 0) {
						// Enter button click will halt the program
						if (Button.ENTER.isPressed())
							break mainLoop;
					}
					
					switch (command) {
					case EXIT:
						// stop
						break mainLoop;
					case MOVE:
						try {
							//read further two values for the parameters of MOVE
							int leftVel = input.readInt();
							int rightVel = input.readInt();
							drawMessage(leftVel + " " + rightVel);
							if (leftVel < 0) {
								leftVel = leftVel*-1;
								Motor.C.forward();
							}
							else {
								Motor.C.backward();
							}
							Motor.C.setSpeed(leftVel);
							if (rightVel < 0) {
								rightVel = rightVel*-1;
								Motor.A.forward();
							}
							else {
								Motor.A.backward();
							}
							Motor.A.setSpeed(rightVel);
						break;
						} catch (Exception e) {
							drawMessage("Error in MOVE: " + e);
						}
					case ROTATE:
						//read the parameter for rotate
						int theta = input.readInt();
						//pilot class will calculate the rotate for us
						pilot.rotate(theta);
						break;
					case KICK:
						try {
							Motor.B.setSpeed(1020);
							Motor.B.forward();
							Thread.sleep(120);
							Motor.B.stop();
							Motor.B.backward();
							Thread.sleep(150);
							Motor.B.stop();
						} catch (InterruptedException e) {
							drawMessage("Error executing kick");
						}
						break;
					case CELEBRATE:
						Tune.Tune();
						break;
					case PENALTY:
						Motor.A.setSpeed(360);
						Motor.C.setSpeed(360);
						int rotateAmount = 200;
						
						if (Math.random() < 0.5) {
							try {  
								//rotate right
								Motor.A.backward();
								Motor.C.forward();
								Thread.sleep(rotateAmount);
								Motor.A.stop();
								Motor.C.stop();
								
								//kick
								Motor.B.setSpeed(1020);
								Motor.B.forward();
								Thread.sleep(120);
								Motor.B.stop();
								Motor.B.backward();
								Thread.sleep(150);
								Motor.B.stop();
								
							} catch (InterruptedException e) {
								drawMessage("Error in Penalty: " + e);
							}
						} else {
							try {  
								//rotate left
								Motor.A.forward();
								Motor.C.backward();
								Thread.sleep(rotateAmount);
								Motor.A.stop();
								Motor.C.stop();
								
								//kick
								Motor.B.setSpeed(1020);
								Motor.B.forward();
								Thread.sleep(120);
								Motor.B.stop();
								Motor.B.backward();
								Thread.sleep(150);
								Motor.B.stop();
								
							} catch (InterruptedException e) {
								drawMessage("Error in Penalty: " + e);
							}
						
						}
					case STOP:
						Motor.A.stop();
						Motor.B.stop();
						Motor.C.stop();
						break;
					default:
						// No command input
						break;
					}
			
				} catch (Exception e1) {
					drawMessage("Error " + e1.getMessage());
				}
			}
			
			connection.close();
		}
	}
	
	private static void drawMessage(String message) {
		LCD.clear();
		LCD.drawString(message, 0, 0);
		LCD.refresh();
	}
	
}
