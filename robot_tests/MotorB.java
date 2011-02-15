import lejos.nxt.*;

public class MotorB {

	 public static void main(String [] args) {
 
 		Motor.B.setSpeed(1020);
         try {  
			Motor.B.forward();
			Thread.sleep(120);
			Motor.B.stop();
			Motor.B.backward();
			Thread.sleep(150);
			Motor.B.stop();
		} catch (InterruptedException e) {}
   }

}