//Group-11
//Wei Wang 260580783
//Jessy Yu 260509495

import lejos.nxt.*;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor cs;
	
	private int LIGHT_THRESHOLD = 55;
	private double [] pos = new double[3]; 
	//create an new array to store the angles value
	private double [] thetas = new double[4];
	//set a variable to count the line detected
	private int lineCount;
	private final double distanceToSensor = 12.0;
	
	private final double ROTATION_SPEED = 30;
	
	private boolean heading = true;
	
	public LightLocalizer(Odometer odo, ColorSensor cs) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.cs = cs;
		
		// turn on the light
		cs.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		int sensorReading = cs.getNormalizedLightValue();
		
		lineCount = 0;
		//get robot position
		odo.getPosition(pos, new boolean[] { true, true, true});
		//rotate counter-clockwise
		robot.setSpeeds(0.0, -ROTATION_SPEED);

		while (lineCount < 4 && heading){
			try { Thread.sleep(75); } catch (InterruptedException e) {}

			if (sensorReading - cs.getNormalizedLightValue() > LIGHT_THRESHOLD){
				//while it detects a line, update the position
				//store the angle in the thetas[]
				//make a double-beep sound
				odo.getPosition(pos, new boolean[] {true, true, true});
				thetas[lineCount] = pos[Odometer.THETA] ;
				Sound.twoBeeps();
				lineCount ++;	
			}
		}
		robot.stop();
	
		pos[Odometer.X] = -(distanceToSensor * Math.cos(Math.toRadians((thetas[2] - thetas[0]) / 2)));
		pos[Odometer.Y] = -(distanceToSensor * Math.cos(Math.toRadians((thetas[3] - thetas[1]) / 2)));
		pos[Odometer.THETA] = Odometer.fixDegAngle((270 + (thetas[2] - thetas[0]) / 2 + thetas[0]) - pos[Odometer.THETA]);
		//make angle abs value always smaller than 180
		if (pos[Odometer.THETA] > 180){
			pos[Odometer.THETA] = -(360 - pos[Odometer.THETA]);
		}
		
		odo.setPosition(pos, new boolean [] {true, true, false});
		//travel to (0,0) and turn to o deg
		odo.getNavigation().travelTo(0, 0);
		odo.getNavigation().turnTo(0);
	}

}
