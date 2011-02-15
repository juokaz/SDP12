import lejos.nxt.*;
import lejos.robotics.navigation.*;

public class Pilot {

	 public static void main(String [] args) {
 
		TachoPilot pilot = new TachoPilot(186.7f, 155f, Motor.A, Motor.C, true); 
		//186.7 = 56 * (10 / 3)
		//this is the wheel diameter multiplied by the gear ratio
		pilot.setSpeed(100); //speed is mm per second
		pilot.travel(500); //go x mm
		pilot.stop();
	
   }

}