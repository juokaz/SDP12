import lejos.nxt.*;

public class Turn2 {

	 public static void main(String [] args) {
 
 		Motor.A.setSpeed(1000);
		Motor.C.setSpeed(1000);
         try {  
			Motor.A.forward();
			Motor.C.backward();
			Thread.sleep(2000);
			Motor.A.stop();
			Motor.C.stop();
		} catch (InterruptedException e) {}
   }

}