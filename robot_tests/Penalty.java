import lejos.nxt.*;

public class Penalty {

	 public static void main(String [] args) {
 		
		Motor.A.setSpeed(500);
		Motor.C.setSpeed(500);
		int rotateAmount = 100;
		
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
				
			} catch (InterruptedException e) {}
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
				
			} catch (InterruptedException e) {}
		
		}
		
   }

}
