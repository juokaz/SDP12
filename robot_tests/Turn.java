import lejos.nxt.*;

public class Turn {

	 public static void main(String [] args) {
 
 		Motor.A.setSpeed(360);
		Motor.C.setSpeed(360);
         try {  
			Motor.A.backward();
			Motor.C.forward();
			Thread.sleep(2000);
			Motor.A.stop();
			Motor.C.stop();
		} catch (InterruptedException e) {}
   }

}