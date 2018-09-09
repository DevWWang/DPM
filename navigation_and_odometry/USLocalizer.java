//Group-11
//Wei Wang 260580783
//Jessy Yu 260509495

import lejos.nxt.*;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double FORWARD_SPEED = 7;
	public static double ROTATION_SPEED = 30;
	
	private final int THRESHOLD = 30;
	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			
			//rotate the robot until it sees no wall
			robot.setRotationSpeed(ROTATION_SPEED);
			while (getFilteredData() < THRESHOLD);
			
			//keep rotating until the robot sees a wall, then latch the angle
			while (getFilteredData() > (THRESHOLD - 5));
			Sound.beep();
			odo.getPosition(pos, new boolean[] {true, true, true});
			//report the angle to angleA
			angleA = pos[2];
			
			//switch direction and wait until it sees no wall
			robot.setRotationSpeed(-ROTATION_SPEED);
			while(getFilteredData() < THRESHOLD);
			
			//keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() > (THRESHOLD - 5));
			Sound.beep();
			odo.getPosition(pos, new boolean[] {true, true, true});
			
			//report the angle to angleB 
			angleB = pos[2];
			
			//angleA is clockwise from angleB, so assume the average of the
			//angles to the right angleB is 45 degrees past 'north'
			if (angleA < angleB){
				odo.getNavigation().turnTo((angleA + angleB + 270) / 2);
			}
			else {
				odo.getNavigation().turnTo((angleA + angleB - 85) / 2);
			}
			//update the odometer position
			odo.setPosition(new double[] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});

		} else {

			//rotate until it sees a wall
			robot.setRotationSpeed(ROTATION_SPEED);
			while(getFilteredData() > (THRESHOLD + 5));
			
			//keep rotating until we sees no wall, then latch the angle
			while(getFilteredData() < THRESHOLD);
			Sound.beep();
			odo.getPosition(pos, new boolean[] {true, true, true});
			
			//report the angle to angleA
			angleA = pos[2];
			
			//switch direction and wait until it sees a wall
			robot.setRotationSpeed(-ROTATION_SPEED);
			while(getFilteredData() > (THRESHOLD + 5));
			
			//keep rotating until the robot sees no wall, then latch the angle
			robot.setRotationSpeed(-ROTATION_SPEED);
			while(getFilteredData() < THRESHOLD);
			Sound.beep();
			odo.getPosition(pos, new boolean[] {true, true, true});
			
			//report the angle to angleB
			angleB = pos[2];
			
			//rotate to north
			if (angleA > angleB){
				odo.getNavigation().turnTo((angleA + angleB + 270) / 2);
			}
			else{
				odo.getNavigation().turnTo((angleA + angleB - 85) / 2);
			}
			//update the position
			odo.setPosition(new double[] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		}
		//travel closer to the gridlines
		robot.setSpeeds(FORWARD_SPEED, ROTATION_SPEED);
		odo.getNavigation().travelTo(9,9);
	}	

	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}	
		
		//There will be a delay here.
		distance = us.getDistance();
		
		// filter out large values
		if (distance > 50){
			distance = 50;
		}
		return distance;	
	}
}