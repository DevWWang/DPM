import lejos.nxt.*;

/* 
 * OdometryCorrection.java
 */


//ECSE-211 LAB-2
//Group 11
//Wei Wang  260580783
//Tsai-Jung (Jessy) Yu  260509495


public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	
	private int lightValue = 0;
	private static final int lightThreshold = 350;
	private final double distanceToCenter = 4.5;
	private final double tileWidth = 30.48;
	private int lineCount = 0;
	double x, y;
	private ColorSensor sensor = new ColorSensor(SensorPort.S2);
	
	private Odometer odometer;

	// constructor

	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		sensor.setFloodlight(true);
		sensor.setFloodlight(ColorSensor.Color.BLUE);
		
		while (true) {
			correctionStart = System.currentTimeMillis();
			
			// put your correction code here
			
			//The fact is that the robot's starting position is (0.0, 0.0) by subtracting from its initial (x, y)
			//Taking into consideration the distance of 4.5cm (the sensor is placed on the front)
			//Based on the robot's orientation, the value of x and y coordinates is updated by adding or subtracting 4.5cm
			//The x-coordinate is vertical and y- coordinate is horizontal. 
			
			lightValue = sensor.getNormalizedLightValue();
			if (lightValue  < lightThreshold) {
				lineCount++;
				LCD.drawString("Count: " + lineCount, 0, 5);
				Sound.beep();
				
					if (lineCount == 1){
						//The robot is going forward
						//As the x is the vertical, the x value varies while the robot detect the first line
						x = .5 * tileWidth - distanceToCenter;
						y = 0;
						odometer.setY(y);
						odometer.setX(x);
					}
					if (lineCount == 2){
						//As the x is the vertical, the x value varies by subtracting the distance between the center of the wheel axis and the color sensor while the robot detect the second line
						x = 1.5 * tileWidth - distanceToCenter;
						odometer.setX(x);
					}
					if (lineCount == 3){
						//The robot turns and keeps going forward; it crosses the third line
						//As the robot is moving horizontally, the y-coordinates is changing by subtracting the distance between the center of the wheel axis and the color sensor.
						x = .5 * tileWidth - distanceToCenter;
						odometer.setY(y);
					}
					if (lineCount == 4){
						//The robot detects the fourth line and it is moving straight
						//As the robot is moving horizontally, the y-coordinates varies
						//and so on the the further coding
						y = 1.5 * tileWidth - distanceToCenter;
						odometer.setY(y);
					}
					if (lineCount == 5){
						x = 1.5 * tileWidth + distanceToCenter;
						odometer.setX(x);
					}
					if (lineCount == 6){
						x = .5 * tileWidth + distanceToCenter;
						odometer.setX(x);
					}
					if (lineCount == 7){
						y = 1.5 * tileWidth + distanceToCenter;
						odometer.setY(y);
					}
					if (lineCount == 8){
						y = .5 * tileWidth + distanceToCenter;
						odometer.setY(y);
					}			
				
				try {
					sleep(500);
				} catch (InterruptedException ex) {

				}
		}

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}