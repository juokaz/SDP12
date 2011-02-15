import lejos.nxt.*;

public class Forward2 {

	 public static void main(String [] args) {
 
 		Motor.A.setSpeed(1000);
		Motor.C.setSpeed(1000);
         try {  
			Motor.A.forward();
			Motor.C.forward();
			Thread.sleep(1000);
			Motor.A.stop();
			Motor.C.stop();
		} catch (InterruptedException e) {}
   }

}