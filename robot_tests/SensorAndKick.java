import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;

public class SensorAndKick {
	
	//Initiate the Light Sensor
	static final LightSensor ls = new LightSensor(SensorPort.S1, true);
	
	public static void main(String args[]) throws Exception {
	
		ls.setHigh(323);
		ls.setLow(357);
		boolean kick = true;
		int val = 0;
		
		// main loop		
		while (!Button.ENTER.isPressed()) {
			
			val = ls.readValue();		
			
			if (val <= 50 && kick) {
				Motor.B.setSpeed(1020);
				Motor.B.forward();
				Thread.sleep(120);
				Motor.B.stop();
				Motor.B.backward();
				Thread.sleep(150);
				Motor.B.stop();
				Thread.sleep(500); //leave enough time for the kicker to reset before reading more light values
			}		
			
		}
	}
	
}