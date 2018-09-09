import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class PController implements UltrasonicController {
 
 private final int bandCenter, bandwidth;
 private final int motorStraight = 150, FILTER_OUT = 10; //FILTER_OUT = error??
 private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B, sensor = Motor.C; 
 private int distance;
 private int currentLeftSpeed;
 private int filterControl;
 
 private int rightMotorSpeed;
 private int leftMotorSpeed;
 private static final int minSpeed = 50;
 private static final int maxSpeed = 450;
 private static final int slowAcceleration = 8;
 private static final int fastAcceleration = 20;
 private int direction = 0;
 
// private int gapCount = 0;
// private final int gapTimeout = 10;
 
 public PController(int bandCenter, int bandwidth) {
  //Default Constructor
  this.bandCenter = bandCenter; //distance we wish to keep between the robot and the wall
  this.bandwidth = bandwidth; //tolerance?
  
  leftMotor.setSpeed(leftMotorSpeed);
  rightMotor.setSpeed(motorStraight);
  
  leftMotor.forward();
  rightMotor.forward();
  
  currentLeftSpeed = 0;
  
  filterControl = 0;
  
  reset();
 }
 
 @Override
 public void processUSData(int usDistance) {
  this.distance = usDistance;
  /*also called C in the slides, 
   *proportional to the error (r-y).
   *"delta" is difference between the 
   *'detected distance' and the 'expected ideal distance'*/
  int delta = distance - bandCenter; 
  //bandCenter = 32;
  
  LCD.clear();
  LCD.drawString("distance: " + distance, 0, 0);
  LCD.drawString("band center: " + bandCenter, 0, 1);
  LCD.drawString("delta: " + delta, 0, 2);
  LCD.drawString("bandwidth: " + bandwidth, 0, 3);
  LCD.drawString("direction: " + direction, 0, 4);
  
  //too far
  if (delta > bandwidth) {
	  filterControl ++;
	  if (direction < 0) {
		  reset();
	  }
	  if (filterControl >= FILTER_OUT) {
		  left();
	  }
  //too close
  } else if (delta < -bandwidth){
	  filterControl = 0;
	  if (direction > 0) {
		  reset();
	  }
	  right();
  //just right
  } else {
	  filterControl = 0;
	  reset();
  }
  // TODO: process a movement based on the us distance passed in (P style)
  //new motor speed
  leftMotor.setSpeed(leftMotorSpeed);
  rightMotor.setSpeed(rightMotorSpeed);
 }
 
 //speed up right motor to turn left
 private void left() {
   direction = 1;
   rightMotorSpeed = Math.min(rightMotorSpeed + slowAcceleration, maxSpeed);
   leftMotorSpeed = motorStraight;
   leftMotor.forward();
   rightMotor.forward();
 }
 
 //slow down right motor to turn right
 private void right() {
   direction = -1;
   rightMotorSpeed = Math.max(rightMotorSpeed - fastAcceleration, minSpeed);
   leftMotorSpeed = motorStraight;
   leftMotor.forward();
   rightMotor.backward();
 }
 private void reset() {
   direction = 0;
   rightMotorSpeed = motorStraight;
   leftMotorSpeed = motorStraight;
   leftMotor.forward();
   rightMotor.forward();
 }
 @Override
 public int readUSDistance() {
  return this.distance;
 }

}