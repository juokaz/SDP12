import lejos.nxt.*;

public class SpeedDemo {

	public static void main(String [] args) {
 
         try { 
			Motor.A.setSpeed(720);
			Motor.C.setSpeed(720);
			Motor.A.backward();
			Motor.C.backward();
			Thread.sleep(1000);
			Motor.A.stop();
			Motor.C.stop();
			Thread.sleep(500);
			Motor.A.forward();
			Motor.C.forward();
			Thread.sleep(1000);
			Motor.A.stop();
			Motor.C.stop();
			Thread.sleep(500);
			Motor.A.setSpeed(360);
			Motor.C.setSpeed(360);
			Motor.A.backward();
			Motor.C.forward();
			Thread.sleep(1000);
			Motor.A.stop();
			Motor.C.stop();
		} catch (InterruptedException e) {}
   }

}