//Group 11
//Wei Wang 260580783
//Jessy Yu 260509495

import lejos.nxt.*;

public class Lab3{

	public static void main(String[] args){

		Odometer odometer = new Odometer();
		UltrasonicSensor usSensor = new UltrasonicSensor(SensorPort.S1);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);
		int option;

		do{
			LCD.clear();

			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString("  Part | Part   ", 0, 2);
			LCD.drawString("   A   |   B    ", 0, 3);

			option = Button.waitForAnyPress();
		}
		while (option != Button.ID_LEFT && option != Button.ID_RIGHT);

		if (option == Button.ID_LEFT){

			Navigation navigator = new Navigation(odometer);
			odometer.start();
			odometryDisplay.start();
			navigator.start();
		}
		else {

			NavigationWithUsSensor navigator = new NavigationWithUsSensor(odometer, usSensor);
			odometer.start();
			odometryDisplay.start();
			navigator.start();
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}