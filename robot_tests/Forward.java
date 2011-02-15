import lejos.nxt.*;

public class Forward {

	 public static void main(String [] args) {
 
         try {  
			Motor.A.setSpeed(550);
			Motor.C.setSpeed(550);
			Motor.A.backward();
			Motor.C.backward();
			Thread.sleep(4000);
			/*for (int i = 500; i<=500; i+=100){
				Motor.A.setSpeed(i);
				Motor.C.setSpeed(i);
				Thread.sleep(1000);
			}*/
			Motor.A.stop();
			Motor.C.stop();
		} catch (InterruptedException e) {}
   }

}