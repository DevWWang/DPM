/*
 * Navigation.java
 */

//Group 11
//Wei Wang 260580783
//Jessy Yu 260509495

import lejos.nxt.*;

public class Navigation extends Thread {

	Odometer odometer;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	final private double wheelRadius = 2.003;
	final private double width = 14.738;

	private double theta, currentT, deltaTheta, deltaY, deltaX;

	private boolean isRotating, isTraveling;

	public Navigation(Odometer odometer) {
		this.odometer = odometer;
		isRotating = false;
		isTraveling = false;
	}

	//The robot travels to 4 specific points which are (60.30), (30,30), (30, 60), and (60, 30)
	public void run() {
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}

		travelTo(60.0, 30.0);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		travelTo(30.0, 30.0);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		travelTo(30.0, 60.0);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		travelTo(60.0, 0);
	}

	public void travelTo(double x, double y) {
		
		//using the current position of robot (x,y) to determine its angle of rotation
		double newX = odometer.getX();
		double newY = odometer.getY();
		deltaX = x - newX;
		deltaY = y - newY;

		//using trigonometric math, calculate the angle which the robot must turn in order to reach further destination
		if (deltaY >= 0) {
			theta = Math.atan(deltaX / deltaY);
		}

		else if (deltaY < 0 && deltaX > 0) {
			theta = Math.atan(deltaX / deltaY) + Math.PI;
		} else {
			theta = Math.atan(deltaX / deltaY) - Math.PI;
		}

		turnTo(theta * (180 / Math.PI));
		//robot is traveling.
		//Then, set the speed of both wheels
		isTraveling = true;
		Motor.A.setSpeed(FORWARD_SPEED);
		Motor.B.setSpeed(FORWARD_SPEED);
		//define the distance that the robot need to travel
		Motor.A.rotate(convertDistance(wheelRadius, Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))), true);
		Motor.B.rotate(convertDistance(wheelRadius, Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))), false);
		//boolean isTraveling is set to false in order to indicate the robot is stopped
		isTraveling = false;
	}

	public void turnTo(double theta) {
		//while the method is called, the robot will rotate and boolean isRotating set to true
		currentT = odometer.getTheta();
		deltaTheta = theta - ((currentT) * (360 / (2 * Math.PI)));
		if (deltaTheta < -180) {// this will make sure the robot rotates through the
						// minimum angle to the desired point.
			deltaTheta = deltaTheta + 360;
		}

		else if (deltaTheta > 180) {
			deltaTheta = deltaTheta - 360;
		}

		isRotating = true;
		//as the robot start rotating, the wheel speed is changed to rotational speed
		Motor.A.setSpeed(ROTATE_SPEED);
		Motor.B.setSpeed(ROTATE_SPEED);
		Motor.A.rotate(convertAngle(wheelRadius, width, deltaTheta), true);
		Motor.B.rotate(-convertAngle(wheelRadius, width, deltaTheta), false);
		
		isRotating = false;
	}

	//This method returns the status of the robot.
	//isNavigation() returns true when any of two methods,including isTraveling() and isRotating(), is executing
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
