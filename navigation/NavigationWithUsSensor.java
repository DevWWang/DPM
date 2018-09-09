/*
 * NavigationWithObstacle.java
 */

//Group 11
//Wei Wang 260580783
//Jessy Yu 260509495

import lejos.nxt.*;

public class NavigationWithUsSensor extends Thread {
	Odometer odometer;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B, usMotor = Motor.C;
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	final private double wheelRadius = 2.003;
	final private double width = 14.738;

	private double theta, currentTheta, deltaTheta, deltaY, deltaX;
	//return whether the robot is traveling or in the act of rotating
	private boolean isRotating, isTraveling;

	private UltrasonicSensor usSensor;
	private int recursionCount;
	
	//similar to the navigation class but in this case, the robot will avoid the block while it is traveling
	public NavigationWithUsSensor(Odometer odometer, UltrasonicSensor usSensor) {
		this.odometer = odometer;

		this.usSensor = usSensor;
		isRotating = false;
		isTraveling = false;
	}

	public void run() {

		travelTo(0, 60);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		travelTo(60, 0);

	}
	
	public void travelTo(double x, double y) {
		double newX = odometer.getX();
		double newY = odometer.getY();
		deltaX = x - newX;
		deltaY = y - newY;

		if (deltaY >= 0) {
			theta = Math.atan(deltaX / deltaY);
		}

		else if (deltaY < 0 && deltaX > 0) {
			theta = Math.atan(deltaX / deltaY) + Math.PI;
		} else {
			theta = Math.atan(deltaX / deltaY) - Math.PI;
		}

		turnTo(theta * (180 / Math.PI));
		isTraveling = true;
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		//sensor will travel in a straight line if there is no block in its path
		if (usSensor.getDistance() > 50 ) {
			leftMotor.rotate(convertDistance(wheelRadius, Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))), true);
			rightMotor.rotate(convertDistance(wheelRadius, Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))), false);

		} 
		//otherwise, the robot will turn in order to avoid the block
		//let the robot be 10 cm away from the block
		//hardcoded the path of robot to avoid the block
		else {
			leftMotor.rotate(convertDistance(wheelRadius, usSensor.getDistance() - 10), true);
			rightMotor.rotate(convertDistance(wheelRadius, usSensor.getDistance() - 10), false);
			//make the robot turn 90 degrees to its left 
			leftMotor.rotate(-convertAngle(wheelRadius, width, 90.0), true);
			rightMotor.rotate(convertAngle(wheelRadius, width, 90.0), false);
			//then the robot will go straight
			leftMotor.rotate(convertDistance(wheelRadius, 20), true);
			rightMotor.rotate(convertDistance(wheelRadius, 20), false);
			//turn the robot back 90 degrees
			leftMotor.rotate(convertAngle(wheelRadius, width, 90.0), true);
			rightMotor.rotate(-convertAngle(wheelRadius, width, 90.0), false);
			//make the robot go and pass the block
			leftMotor.rotate(convertDistance(wheelRadius, 40), true);
			rightMotor.rotate(convertDistance(wheelRadius, 40), false);
			//the robot is going to turn 90 degrees to its right
			leftMotor.rotate(convertAngle(wheelRadius, width, 90.0), true);
			rightMotor.rotate(-convertAngle(wheelRadius, width, 90.0), false);
			//the robot travels along the block
			leftMotor.rotate(convertDistance(wheelRadius, 28), true);
			rightMotor.rotate(convertDistance(wheelRadius, 28), false);
			//the robot rotate back to its initial angle
			leftMotor.rotate(-convertAngle(wheelRadius, width, 90.0), true);
			rightMotor.rotate(convertAngle(wheelRadius, width, 90.0), false);
			
			//recall the travelTo
			travelTo(x, y);

		}
		isTraveling = false;
	}

	public void turnTo(double theta) {// same as Navigation class.
		currentTheta = odometer.getTheta();
		deltaTheta = theta - ((currentTheta) * (180 / Math.PI));

		if (deltaTheta < -180) {
			deltaTheta = deltaTheta + 360;
		}

		else if (deltaTheta > 180) {
			deltaTheta = deltaTheta - 360;
		}

		isRotating = true;
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(convertAngle(wheelRadius, width, deltaTheta), true);
		rightMotor.rotate(-convertAngle(wheelRadius, width, deltaTheta), false);
		
		isRotating = false;
	}

	public boolean isNavigating() {
		return isTraveling || isRotating;
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
