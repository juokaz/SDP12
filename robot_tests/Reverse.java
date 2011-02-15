import lejos.nxt.*;

public class Reverse {

	 public static void main(String [] args) {
 
 		Motor.A.setSpeed(720);
		Motor.C.setSpeed(720);
         try {  
			Motor.A.forward();
			Motor.C.forward();
			Thread.sleep(2000);
			Motor.A.stop();
			Motor.C.stop();
		} catch (InterruptedException e) {}
   }

}