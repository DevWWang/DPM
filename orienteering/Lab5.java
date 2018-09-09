//Group-11
//Wei Wang 260580783
//Jessy Yu 260509495

import lejos.nxt.*;

public class Lab5 {

	public static void main(String[] args) {
		Button.waitForAnyPress();

		TwoWheeledRobot robot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odometer = new Odometer(robot, true);
		LCDInfo lcd = new LCDInfo(odometer);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);

		//performs the orienteering
		Orienteering ori = new Orienteering(odometer, us);
		ori.initialPosition();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

}