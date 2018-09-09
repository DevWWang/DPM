//Group-11
//Wei Wang 260580783
//Jessy Yu 260509495

import java.lang.Math;

import lejos.nxt.*;

public class Navigation {
	// put your navigation code here 
	
	private Odometer odo;
	private TwoWheeledRobot robot;
	
	private final int FORWARD_SPEED = 80;
	private final int ROTATION_SPEED = 200;
	public double pos[]; 
	private NXTRegulatedMotor leftMotor = Motor.A;
	private NXTRegulatedMotor rightMotor = Motor.B;

	private final double NOISE = 1.0;
	
	public Navigation(Odometer odo) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		pos = new double [3];
	}
	
	public void travelTo(double x, double y) {

		//get the current position
		odo.getPosition(pos, new boolean[] {true, true, true});
		
		//the robot is at its target coordinates within 1.0 cm, it travels straight
		while (Math.abs(x - pos[Odometer.X]) >= NOISE && Math.abs(y - pos[Odometer.Y]) >= NOISE){
			turnTo(Math.toDegrees(Math.atan2(x - pos[Odometer.X], y - pos[Odometer.Y])));
		
			robot.setForwardSpeed(FORWARD_SPEED);
			robot.setRotationSpeed(0.0);
			odo.getPosition(pos, new boolean[] {true, true, true});
		}
		//when exit the loop, stop traveling and rotating
		robot.stop();
	}
	
	public void turnTo(double angle) {
		
		//find current position
		//determine the current heading according to the odometer
//		odo.getPosition(pos, new boolean[] {true, true, true});
//		double turnAngle = Odometer.minimumAngleFromTo(pos[Odometer.THETA], angle);
//		
//		//make sure that theta is within the error (NOISE)
//		if (Math.abs(turnAngle) <= NOISE){
//			return;
//		}
//		
//		//performs rotation
//		//if the turning angle is positive, the robot rotates clockwise
//		//otherwise, it rotates counterclockwise
//		if (turnAngle > 0){
//			robot.setForwardSpeed(0.0);
//			robot.setRotationSpeed(ROTATION_SPEED);
//		} else{
//			robot.setForwardSpeed(0.0);
//			robot.setRotationSpeed(-ROTATION_SPEED);
//		}
//		
//		while ( Math.abs(angle - pos[Odometer.THETA]) >= NOISE ){
//			odo.getPosition(pos, new boolean[] {true, true, true});
//		}
//		//exit the loop and stop the action
//		robot.stop();
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		
		leftMotor.rotate(convertAngle(2.003, 14.738, angle), true);
		rightMotor.rotate(-convertAngle(2.003, 14.738, angle), false);
	}
	
	public void goForward(double distance){
		leftMotor.rotate(convertDistance(2.003, distance), true);		
		rightMotor.rotate(convertDistance(2.003, distance), false);
	}
	
	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
