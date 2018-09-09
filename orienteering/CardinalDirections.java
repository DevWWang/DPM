import lejos.nxt.LCD;


public class CardinalDirections {
	private Odometer odometer;
	
	private final int tiles = 12;
	private final int cardinalDirections = 4;
	public static final int NORTH = 0, WEST = 1, SOUTH = 2, EAST = 3;
	
	public double pos[] = new double[3]; 
	public final boolean wall = false, noWall = true;
	private boolean[][] initialPosition = new boolean[tiles][cardinalDirections];
	private boolean[][] possiblePositions = new boolean[tiles][cardinalDirections];
	private boolean[][] visited = new boolean[tiles][cardinalDirections];
	private boolean[][] path = new boolean[tiles][cardinalDirections];
	
	//code of environment on all the tiles, 
	//meaning whether there is an obstacle or wall in front of it or not.
	//the first array index indicates the tile number
	//while the second one indicates its cardinal direction
	CardinalDirections() {
		initialPosition[0][NORTH] = wall;
		initialPosition[0][WEST] = wall;
		initialPosition[0][SOUTH] = noWall;
		initialPosition[0][EAST] = noWall;
		
		initialPosition[1][NORTH] = wall;
		initialPosition[1][WEST] = noWall;
		initialPosition[1][SOUTH] = wall;
		initialPosition[1][EAST] = noWall;
		
		initialPosition[2][NORTH] = wall;
		initialPosition[2][WEST] = noWall;
		initialPosition[2][SOUTH] = wall;
		initialPosition[2][EAST] = wall;
		
		initialPosition[3][NORTH] = wall;
		initialPosition[3][WEST] = wall;
		initialPosition[3][SOUTH] = noWall;
		initialPosition[3][EAST] = noWall;
		
		initialPosition[4][NORTH] = noWall;
		initialPosition[4][WEST] = noWall;
		initialPosition[4][SOUTH] = noWall;
		initialPosition[4][EAST] = wall;
		
		initialPosition[5][NORTH] = noWall;
		initialPosition[5][WEST] = wall;
		initialPosition[5][SOUTH] = noWall;
		initialPosition[5][EAST] = noWall;
		
		initialPosition[6][NORTH] = noWall;
		initialPosition[6][WEST] = noWall;
		initialPosition[6][SOUTH] = wall;
		initialPosition[6][EAST] = noWall;
		
		initialPosition[7][NORTH] = wall;
		initialPosition[7][WEST] = noWall;
		initialPosition[7][SOUTH] = noWall;
		initialPosition[7][EAST] = noWall;
		
		initialPosition[8][NORTH] = wall;
		initialPosition[8][WEST] = noWall;
		initialPosition[8][SOUTH] = noWall;
		initialPosition[8][EAST] = wall;
		
		initialPosition[9][NORTH] = noWall;
		initialPosition[9][WEST] = wall;
		initialPosition[9][SOUTH] = wall;
		initialPosition[9][EAST] = wall;
		
		initialPosition[10][NORTH] = noWall;
		initialPosition[10][WEST] = wall;
		initialPosition[10][SOUTH] = wall;
		initialPosition[10][EAST] = noWall;
		
		initialPosition[11][NORTH] = noWall;
		initialPosition[11][WEST] = noWall;
		initialPosition[11][SOUTH] = wall;
		initialPosition[11][EAST] = wall;
		
		//before it begins to collect data, 
		//all the starting positions are possible
		for (int i = 0; i < tiles; i++) {
			for (int j = 0; j < cardinalDirections; j++) {
				possiblePositions[i][j] = true;
			}
		}
	}
//	public void removeAll(boolean noWall) {
//		for (int i = 0; i < tiles; i++) {
//			for (int j = 0; j < cardinalDirections; j++) {
//				if (initialPosition[i][j] == noWall) {
//					possiblePositions[i][j] = false;
//				}
//			}
//		}
//	}
//	\
	public void checkVisited(){
		for (int i = 0; i < tiles; i++) {
			for (int j = 0; j < cardinalDirections; j++) {
				if (!visited[i][j]){
					visited[i][j] = true;
				}
			}
		}
	}
	private int turnCounterClockwise(int direction) {
		switch (direction) {
			case NORTH:	return WEST;
			case WEST: return SOUTH;
			case SOUTH: return EAST;
			case EAST: return NORTH;
			default: return 5;
		}	
	}
	
	//code for the directions the robot 
	//needs to go in the cases of each direction
	//and on each tile
	private int goForwardDirection(int tile, int direction) {
		if (direction == NORTH) {
			switch (tile) {
				case 4: return 0;
				case 5: return 3;
				case 6: return 4;
				case 10: return 7;
				case 11: return 8;
				default: return 12;
			}
		}
		if (direction == WEST) {
			switch (tile) {
				case 1: return 0;
				case 2: return 1;
				case 4: return 3;
				case 6: return 5;
				case 7: return 6;
				case 8: return 7;
				case 11: return 10;
				default: return 12;
			}
		}
		if (direction == SOUTH) {
			switch (tile) {
				case 0: return 4;
				case 3: return 5;
				case 4: return 6;
				case 5: return 9;
				case 7: return 10;
				case 8: return 11;
				default: return 12;
			}
		}
		if (direction == EAST) {
			switch (tile) {
				case 0: return 1;
				case 1: return 2;
				case 3: return 4;
				case 5: return 6;
				case 6: return 7;
				case 7: return 8;
				case 10: return 11;
				default: return 12;
			}
		}
		
		return 12;
	}
	
	//method for removing simulated scenarios when don't match
	public void removeSimulated(String commands, boolean noWall) {
		
		int tile = 0;
		int direction = 0;
		
		for (int i = 0; i < tiles; i++) {
			for (int j = 0; j < cardinalDirections; j++) {
				if (possiblePositions[i][j] == true) {
					tile = i;
					direction = j;
					//goes forward if detected a printed 'F'
					//and turns CCW for 90 degs if detected a printed 'L'
					for (int k = 0; k < commands.length(); k++) {
						if(commands.charAt(k) == 'F') {
							tile = goForwardDirection(tile, direction);
						} else if(commands.charAt(k) == 'L') {
							direction = turnCounterClockwise(direction);
						}
					}

					//if error in the simulated scenarios
					//take off those considerations
					if (tile == 12 || direction == 5) {
						possiblePositions[i][j] = false;
					} else if (initialPosition[tile][direction] == noWall) {
						possiblePositions[i][j] = false;
					}
				}
			}
		}
	}
	
	public int getNumberOfPositionsLeft() {
		int positions = 0;
		for (int i = 0; i < tiles; i++) {
			for (int j = 0; j < cardinalDirections; j++) {
				if (possiblePositions[i][j] == true) {
					positions = positions + 1;
				}
			}
		}
		return positions;
	}
	
//	public int getNumberOfPositionsLeft2() {
//		int positions = 0;
//		
//		for (int i = 0; i < tiles; i++) {
//			for (int j = 0; j < cardinalDirections; j++) {
//				positions++;
//			}
//		}
//		return positions;
//	}
	
	public void printPossibilities() {
		int charPrinted = 0;
		for (int i = 0; i < tiles; i++) {
			for (int j = 0; j < cardinalDirections; j++) {
				if (possiblePositions[i][j] == true) {
					LCD.clear();
					LCD.drawString(i + "," + j + ";", 4 * charPrinted, 6);
					charPrinted++;
				}
			}
		}
	}
	
	//find out the possible tiles
	public int getPossibleTile() {
		int tile = 0;
		for (int i = 0; i < tiles; i++) {
			for (int j = 0; j < cardinalDirections; j++) {
				if (possiblePositions[i][j] == true) {
					tile = i;
				}
			}
		}
		return tile;
	}
	
	//find out the possible directions
	public int getPossibleDirection() {
		int direction = 0;
		for (int i = 0; i < tiles; i++) {
			for (int j = 0; j < cardinalDirections; j++) {
				if (possiblePositions[i][j] == true) {
					direction = j;
				}
			}
		}
		return direction;
	}
}