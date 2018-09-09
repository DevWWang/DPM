//Group-11
//Wei Wang 260580783
//Jessy Yu 260509495

import lejos.nxt.*;

public class Lab4 {

	public static void main(String[] args) {
		
		Button.waitForAnyPress();
		
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
		ColorSensor cs = new ColorSensor(SensorPort.S2);
		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
		usl.doLocalization();
		
		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(odo, cs);
		lsl.doLocalization();			
		
		Button.waitForAnyPress();
	}

}