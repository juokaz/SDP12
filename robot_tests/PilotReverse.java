import lejos.nxt.*;
import lejos.robotics.navigation.*;

public class PilotReverse {

	 public static void main(String [] args) {
 
		TachoPilot pilot = new TachoPilot(186.7f, 155f, Motor.A, Motor.C, true); 
		//186.7 = 56 * (10 / 3)
		//this is the wheel diameter multiplied by the gear ratio
		pilot.setSpeed(500); //speed is mm per second
		pilot.travel(-1500); //go x mm
		pilot.stop();
	
   }

}