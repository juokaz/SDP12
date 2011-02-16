import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;


/**
 * This simple NXT program changes drive state based on the current color value
 * 
 * @author Will Gorman
 */
public class Sensor {
	
	// the main light sensor for driving
	
	static final LightSensor center_light = new LightSensor(SensorPort.S1, true);

	public static void main(String args[]) throws Exception {
		
		Thread.sleep(1000);
		
		// take a measurement, expecting white
		
		LCD.clear();
		LCD.drawString("Place ball far", 0, 0);
		LCD.drawString("Then press ORANGE", 0, 1);
		LCD.refresh();
		
		Button.ENTER.waitForPressAndRelease();
		
		Thread.sleep(1000);
		
		int measure = center_light.readNormalizedValue(); //311
		center_light.calibrateHigh();

		// take a measurement, expecting black
		
		LCD.clear();
		LCD.drawString("Place ball close", 0, 0);
		LCD.drawString("Then press ORANGE", 0, 1);
		LCD.refresh();
		Button.ENTER.waitForPressAndRelease();
		Thread.sleep(1000);
		
		int next_measure = center_light.readNormalizedValue(); //403
		center_light.calibrateLow();
		
		LCD.clear();
		LCD.drawString("High value:", 0, 0);
		LCD.drawInt(next_measure, 0, 1);
		LCD.drawString("Low value:", 0, 2);
		LCD.drawInt(measure, 0, 3);
		LCD.drawString("Press ORANGE to cont.", 0, 4);
		LCD.refresh();
		
		Button.ENTER.waitForPressAndRelease();
		
		int val = 0;
		int rawval = 0;
		
		// main loop
		
		while (!Button.ENTER.isPressed()) {
			val = center_light.readValue();
			
			LCD.clear();
			LCD.drawInt(val, 0, 0);
			LCD.refresh();
			
			Thread.sleep(200);
			
		}
	}
}
