//Group-11
//Wei Wang 260580783
//Jessy Yu 260509495

import java.util.Random;

import lejos.nxt.*;

public class Orienteering {

	//public enum OrientationType {DETERMINISTIC, STOCHASTIC};
	
	private TwoWheeledRobot robot;
	private Odometer odometer;
	private UltrasonicSensor us;

	private final double FORWARD_SPEED = 80;
	private final double ROTATION_SPEED = 100;
	private Random random = new Random();
	private boolean[][] cardinalPointsOfEachStartingTile = new boolean[12][4];
	public static int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public double[] pos = new double[3];
	
	public Orienteering(Odometer odometer, UltrasonicSensor us) {
		
		this.odometer = odometer;
		this.robot = odometer.getTwoWheeledRobot();
		this.us = us;		

		us.off();
	}

//	public void doOrientation() {
//
//		while (true) {
//			//Deterministic algorithm
//			if (oriType == OrientationType.DETERMINISTIC){
//				//wall detected
//				if (getFilteredData() < 25){
//					odometer.getNavigation().turnTo(-90);
//				} else {
//					odometer.getNavigation().goForward(29);
//				}
//			} else { //Schotastic algorithm
//				if (getFilteredData() < 20){
//					odometer.getNavigation().turnTo(-90);
//				} else {
//					int option = random.nextInt(1);
//					//the robot chooses randomly whether to
//					//travel forward or turn 90 degrees counterclockwise
//					//when no wall is detected
//					if (option == 0){
//						odometer.getNavigation().goForward(29);
//					} else {
//						odometer.getNavigation().turnTo(-90);
//					}
//				}
//			}
//		}
//	}
	
	//initial position of robot
	public void initialPosition() {

		int tile = 0, direction = 0;
		String movement = "";
		CardinalDirections possibleDirections = new CardinalDirections();

		//initially, turn CCW to three other directions 
		//to collect data of its surrounding
//		for (int i = 0; i < 3; i++) {
//			//if wall detected, remove all directions with no wall, and vice versa
//			if (getFilteredData() < 25) {
//				possibleDirections.removeSimulated(movement, true);
//			} else {
//				possibleDirections.removeSimulated(movement, false);
//			}
//			
//			LCD.drawString("possibilties: " + possibleDirections.getNumberOfPositionsLeft() + "    end.", 0, 5);
//			
//			odometer.getNavigation().turnTo(-89);
//			movement = movement + "L";
//			LCD.drawString(movement, 0, 4);
//		}
		
		int possibilities = possibleDirections.getNumberOfPositionsLeft();
		LCD.drawString("possibilties: " + possibilities + "    end.", 0, 5);
		
		//while the possible initial position is more than 1
		//if the robot is facing to the wall, it turns counterclockwise and write the letter 'L' into the string to record its movement
		// deterministic: there is nothing in front of it; keep going.
		while (possibilities != 1) {
			if (getFilteredData() < 25) {
				possibleDirections.removeSimulated(movement, true);
				odometer.getNavigation().turnTo(-89);
				movement = movement + "L";
				LCD.drawString(movement, 0, 4);
			} else {
				possibleDirections.removeSimulated(movement, false);
				odometer.getNavigation().goForward(29);
				movement = movement + "F";
				LCD.drawString(movement, 0, 4);
			}
			
			possibilities = possibleDirections.getNumberOfPositionsLeft();
			possibleDirections.printPossibilities();
			LCD.drawString("possibilties: " + possibleDirections.getNumberOfPositionsLeft() + "    end.", 0, 5);
			
			if (possibilities == 0) {
				LCD.drawString("No possible position.", 0, 5);
				break;
			}
		}
		//as the possible initial position is limited to one. The initial position is found
		//reload the x and y coordinates 
		if (possibilities == 1) {
			tile = possibleDirections.getPossibleTile();
			direction = possibleDirections.getPossibleDirection();
			while (tile < 12){
				if (tile == 0){
					pos[Odometer.X] = 15.0;
					pos[Odometer.Y] =  75.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});
				} else if (tile == 1){
					pos[Odometer.X] = 45.0;
					pos[Odometer.Y] =  75.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});				
				} else if (tile == 2){
					pos[Odometer.X] = 75.0;
					pos[Odometer.Y] =  75.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});
				} else if (tile == 3){
					pos[Odometer.X] = -15.0;
					pos[Odometer.Y] =  45.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});				
				} else if (tile == 4){
					pos[Odometer.X] = 15.0;
					pos[Odometer.Y] = 45.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});
				} else if (tile == 5){
					pos[Odometer.X] = 15.0;
					pos[Odometer.Y] = -15.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});
				} else if (tile == 6){
					pos[Odometer.X] = 15.0;
					pos[Odometer.Y] = 15.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});
				} else if (tile == 7){
					pos[Odometer.X] = 45.0;
					pos[Odometer.Y] = 15.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});
				} else if (tile == 8){
					pos[Odometer.X] = 75.0;
					pos[Odometer.Y] = 15.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});
				} else if (tile == 9){
					pos[Odometer.X] = -15.0;
					pos[Odometer.Y] = -15.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});
				} else if (tile == 10){
					pos[Odometer.X] = 45.0;
					pos[Odometer.Y] = -15.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});
				} else if (tile == 11){
					pos[Odometer.X] = 75.0;
					pos[Odometer.Y] = -15.0;
					odometer.setPosition(pos,  new boolean[] {true, true, false});
				}
			}
		}
	}
		
	private int getFilteredData() {
		int distance;

		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try {
			Thread.sleep(50);
		} catch (InterruptedException e ) {
		}
		
		//There will be a delay here.
		distance = us.getDistance();
		
		// filter out large values
		if (distance > 50) {
			distance = 255;
		}
		return distance;
	}

}