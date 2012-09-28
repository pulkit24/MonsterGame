/** @Pulkit
 * Stores the entire game grid. Each cell is represented by a Cell object.
 * Although cells are stored in a double-dimension array (for quick search and retrieval), 
 * coordinates are handled using the Coordinates class.
 * GameMap provides useful functions such as:
 * 		getMoveCoords - returns the appropriate coordinates to move to in your desired direction
 * 		isValidMove - checks whether the move is valid
 * 		executeMove - moves a player/monster to a new cell
 * It also offers several handy coordinates once initialized - centre and all four corners!
 * Note: Always use with "synchronized" - use the handy gameMapLock as monitor.
 */
package components.grid;

import java.io.Serializable;
import components.Debug;

public class GameMap implements Serializable{
	private static final long serialVersionUID = -6388138282449703914L;
	private int lastMoveBy = 0; // stores the player id of the last move

	/* Cells are numbered from bottom left */
	protected Cell cells[][];
	protected int gridSquareSize;

	/* Direction types */
	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RESET = 4; // reset to the centre of the map

	/* Useful locations */
	public Coordinates CENTRE = Coordinates.ZERO; // overridden by constructors
	public Coordinates CORNER_NE = Coordinates.ZERO; // overridden by constructors
	public Coordinates CORNER_NW = Coordinates.ZERO; // overridden by constructors
	public Coordinates CORNER_SE = Coordinates.ZERO; // overridden by constructors
	public Coordinates CORNER_SW = Coordinates.ZERO; // overridden by constructors

	/* Test map */
	public static final Boolean TEST = true;
	public static final int TEST_1 = 9;
	public static final int TEST_2 = 13;

	/* Sync lock */
	public static Object gameMapLock = new Object(); // must always use with synchronized

	public GameMap(Boolean isTestMap, int testType) throws IllegalArgumentException{
		this(testType);
		if(isTestMap){
			if(testType == GameMap.TEST_1){
				for(int x = 0; x < 9; x++){
					for(int y = 0; y < 9; y++){
						/* All cells of 1st, 5th and 9th rows are valid and empty */
						if(x == 0 || x == 4 || x == 8) cells[x][y] = new Cell(Cell.EMPTY);
						/* All cells of 1st, 5th and 9th columns are valid and empty */
						else if(y == 0 || y == 4 || y == 8) cells[x][y] = new Cell(Cell.EMPTY);
						/* All other cells are invalid */
						else cells[x][y] = new Cell(Cell.INVALID);
					}
				}
			}else if(testType == GameMap.TEST_2){
				for(int x = 0; x < 13; x++){
					for(int y = 0; y < 13; y++){
						/* Diagonals */
						if(x + y == 12) cells[x][y] = new Cell(Cell.EMPTY);
						else if(x + y == 11) cells[x][y] = new Cell(Cell.EMPTY);
						else if(x + y == 13) cells[x][y] = new Cell(Cell.EMPTY);
						else if(x == y) cells[x][y] = new Cell(Cell.EMPTY);
						else if(x == y + 1) cells[x][y] = new Cell(Cell.EMPTY);
						else if(x == y - 1) cells[x][y] = new Cell(Cell.EMPTY);
						/* Big fat corners */
						else if(x < 3 && y < 3) cells[x][y] = new Cell(Cell.EMPTY);
						else if(x < 3 && y > 9) cells[x][y] = new Cell(Cell.EMPTY);
						else if(x > 9 && y < 3) cells[x][y] = new Cell(Cell.EMPTY);
						else if(x > 9 && y > 9) cells[x][y] = new Cell(Cell.EMPTY);
						/* All other cells are invalid */
						else cells[x][y] = new Cell(Cell.INVALID);
					}
				}
			}
		}else throw new IllegalArgumentException(
				"Coding error: use GameMap.TEST as argument when creating new GameMap using GameMap(Boolean) constructor.");
	}

	public GameMap(Cell[][] cells){
		/* Copy cells */
		gridSquareSize = cells.length;
		this.cells = new Cell[gridSquareSize][gridSquareSize];
		for(int i = 0; i < gridSquareSize; i++){
			/* Copy each array - not just point to it */
			cells[i] = new Cell[gridSquareSize];
			System.arraycopy(cells[i], 0, this.cells[i], 0, gridSquareSize);
		}
		setUsefulCoordinates();
	}

	public GameMap(int gridSquareSize){
		/* Initialize blank array - user must manually fill all cells later */
		this.gridSquareSize = gridSquareSize;
		this.cells = new Cell[gridSquareSize][gridSquareSize];
		for(int i = 0; i < gridSquareSize; i++){
			cells[i] = new Cell[gridSquareSize];
		}
		setUsefulCoordinates();
	}

	public GameMap(GameMap gameMap){
		/* Copy map */
		this(gameMap.getCells());
	}

	public void makeMove(int playerId, Coordinates fromCoords, Coordinates toCoords){
		Cell fromCell = cells[fromCoords.getX()][fromCoords.getY()];
		Cell toCell = cells[toCoords.getX()][toCoords.getY()];
		fromCell.setStatus(Cell.EMPTY);
		toCell.setOccupant(playerId);
		lastMoveBy = playerId;
	}

	public Cell getCell(Coordinates coords){
		/* Return a cell at particular coordinates */
		return cells[coords.getX()][coords.getY()];
	}

	public Cell[][] getCells(){
		/* Return the whole cell array */
		return cells;
	}

	public int getGridSquareSize(){
		/* Return the width/height of the square game board */
		return gridSquareSize;
	}

	public Coordinates getMoveCoords(Coordinates baseCoords, int direction){
		/* Compute the new coordinates to move into in a particular direction based on the current coordinates. 
		 * If these desired coordinates are invalid, the current position baseCoords is returned as it is.
		 */
		Debug.log("Map", baseCoords + " " + direction);
		Coordinates newCoords = new Coordinates(baseCoords);
		switch(direction){
		case UP:
			newCoords.setY(newCoords.getY() + 1);
			break;
		case RIGHT:
			newCoords.setX(newCoords.getX() + 1);
			break;
		case DOWN:
			newCoords.setY(newCoords.getY() - 1);
			break;
		case LEFT:
			newCoords.setX(newCoords.getX() - 1);
		}
		Debug.log("Map", newCoords.toString());
		return isValidMove(baseCoords, newCoords) ? newCoords : baseCoords;
	}

	public Coordinates getNearestPlayerCoordinates(int myId, Coordinates myCoords){
		int minDistance = 0;
		Coordinates targetCoords = myCoords;
		for(int y = gridSquareSize - 1; y >= 0; y--){
			for(int x = 0; x < gridSquareSize; x++){
				/* Iterate through all cells occupied by players other than myself */
				Debug.log("Map", "looking at cell " + x + ", " + y + ": " + cells[x][y].getOccupant());
				if(cells[x][y].getStatus() == Cell.OCCUPIED && cells[x][y].getOccupant() != myId){
					Coordinates tentativeTargetCoords = new Coordinates(x, y);
					Debug.log("Map", "A player spotted at " + tentativeTargetCoords);
					int distance = myCoords.getDistanceManhattan(tentativeTargetCoords);
					if(distance < minDistance || minDistance <= 0){
						minDistance = distance; // minimize or set for the first time
						targetCoords = tentativeTargetCoords;
					}
				}
			}
		}
		return targetCoords;
	}

	public Coordinates getDirection(Coordinates baseCoords, Coordinates targetCoords){
		int minDistance = 0; // init with 0
		Coordinates bestMove = baseCoords;

		if(targetCoords.equals(baseCoords)) return baseCoords; // quit if no move required

		/* Lets move in each direction */
		int dirs[] = {GameMap.UP, GameMap.RIGHT, GameMap.DOWN, GameMap.LEFT};
		for(int i = 0; i < 4; i++){

			/* Can we move in that direction? */
			Coordinates nextMove = getMoveCoords(baseCoords, dirs[i]);

			/* If no, try another move */
			if(nextMove.equals(baseCoords)) continue;

			/* What would be the distance to the target after we move in that direction */
			int distance = nextMove.getDistanceManhattan(targetCoords);
			Debug.log("Map", "distance in dir " + dirs[i] + " " + distance);

			/* Is it a shorter distance? */
			if(distance < minDistance || minDistance <= 0){
				minDistance = distance;
				bestMove = nextMove; // store this as the best direction to move towards
				Debug.log("Map", "this is the current minimum distance");
			}
		}

		return bestMove;
	}

	public static boolean isValidDirection(int moveDirection){
		if(moveDirection == GameMap.UP || moveDirection == GameMap.RIGHT || moveDirection == GameMap.DOWN || moveDirection == GameMap.LEFT) return true;
		else return false;
	}

	public Boolean isValidMove(Coordinates from, Coordinates to){
		try{
			Cell targetCell = cells[to.getX()][to.getY()];
			return targetCell.isEmpty(); // && from.getDistanceManhattan(to)==1;
		}catch(ArrayIndexOutOfBoundsException e){
			/* Requested cell is beyond the bounds of the grid */
			return false;
		}
	}

	public Boolean isLastMoveBy(int playerId){
		return lastMoveBy == playerId;
	}

	private void setUsefulCoordinates(){
		CENTRE = new Coordinates((gridSquareSize - 1) / 2, (gridSquareSize - 1) / 2);
		CORNER_NE = new Coordinates(gridSquareSize - 1, gridSquareSize - 1);
		CORNER_NW = new Coordinates(0, gridSquareSize - 1);
		CORNER_SE = new Coordinates(gridSquareSize - 1, 0);
		CORNER_SW = new Coordinates(0, 0);
	}

	public void setCells(Cell[][] cells){
		this.cells = cells;
	}

	public void setCell(Coordinates coords, Cell cell){
		this.cells[coords.getX()][coords.getY()] = new Cell(cell);
	}

	public void setLastMoveBy(int playerId){
		this.lastMoveBy = playerId;
	}
}
