import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

//ECSE-211 LAB-1
//Group 11
//Wei Wang  260580783
//Tsai-Jung (Jessy) Yu  260509495


public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 200;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	private int distance;
	private int currentLeftSpeed;
	
	//private int error = 0;
	//detect how many gaps there are
	private int filterControl = 0;
	//set a number for maximum number of gaps. For exaple, if the robot detect more than 30 gaps, it turns
	private int FILTER_OUT = 30;
	//a constant motor speed in order to determine the wheel speed change
	private int delta = 100;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		
		//the difference distance and BandCenter +- Bandwith
		//distance and error will constantly be updated according to thelast reading
		int error = (bandCenter - this.distance);
		// print out what is the distance, error, and number of gaps on the screen
		LCD.clear();
		LCD.drawString("distance: " + distance, 0, 1);
		LCD.drawString("error: " + error, 0, 3);
		LCD.drawString("filterControl: " + filterControl, 0, 5);
		//As the robot goes to far away from the wall and it can not detect any object in the area, filterControl increases
		if (error < -bandwith){
			filterControl++;
			//there are too many gaps, wall follower turns left
			if (filterControl > FILTER_OUT){
				//in order to turn left, left wheel speed is reduced and right wheel speed is increased
				// Left Speed is 100 rad/s; Right Speed is 300 rad/s
				leftMotor.setSpeed(motorStraight - delta);
				rightMotor.setSpeed(motorStraight + delta);

				//robot starts with the new applied speeds
				leftMotor.forward();
				rightMotor.forward();
			}
		}
		//If the robot is getting close to the wall, it means the erro is less than bandCenter
		//so there is no gaps and turn right
		else if (error > bandwith){
			filterControl = 0;
			//in order to turn right, left wheel speed is increased and right wheel speed is decreased by 100 rad/s
			// Left Speed is 300 rad/s; Right Speed is 100 rad/s
			leftMotor.setSpeed(motorStraight + delta);
			rightMotor.setSpeed(motorStraight - delta);
			
			//robot starts with the new speed, but for better rotation the right wheel is going backward instead of going forward
			leftMotor.forward();
			rightMotor.backward();
		}
		//the robot is set to go straight at speed motorStraight (200 rad/s) while it is traveling between bandCenter and bandWidth
		else {
			filterControl = 0;
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			
			leftMotor.forward();
			rightMotor.forward();
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}