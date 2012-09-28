/** @Pulkit 
 * Stores the status of each cell in the grid
 * Cell status:
 * 	INVALID - if the cell cannot be occupied by the players in the game
 * 	EMPTY or OCCUPIED - for valid cells. In the case of the latter, the occupant id is also stored.
 * Get cell status:
 * 	You can use the handy functions isValid, isEmpty and getOccupant for your perusal.
 * Set cell status:
 * 		setStatus() - use this to set status to Cell.INVALID, Cell.EMPTY
 * 		setOccupant() - when setting status to Cell.OCCUPIED, use this to set the occupant id
 * 			(automatically sets status to Cell.OCCUPIED)
 */
package components.grid;

import java.io.Serializable;

public class Cell implements Serializable{
	private static final long serialVersionUID = 5912704240005036935L;

	private int status = EMPTY; // is the cell invalid, empty or occupied?
	private int occupant = -1; // stores id of the occupant

	/* Possible Statuses */
	public static int INVALID = 0;
	public static int EMPTY = 1;
	public static int OCCUPIED = 2;

	public Cell(){
		/* Defaults: cell is empty and we don't care about the value of occupant */
	}

	public Cell(int status){
		/* Used for invalid or empty cells - again, no need to bother with occupant */
		this.status = status;
	}

	public Cell(Boolean isValid){
		/* Quick way to declare a invalid or empty valid cell */
		this.status = isValid ? EMPTY : INVALID;
	}

	public Cell(int status, int occupant){
		this.status = status;
		this.occupant = occupant;
	}

	public Cell(Cell cell){
		/* Copy constructor */
		this.status = cell.getStatus();
		this.occupant = cell.getOccupant();
	}

	public int getStatus(){
		return status;
	}

	public int getOccupant(){
		return occupant;
	}

	public Boolean isValid(){
		return status != INVALID;
	}

	public Boolean isEmpty(){
		return status == EMPTY;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public void setOccupant(int occupant){
		this.occupant = occupant;
		setStatus(Cell.OCCUPIED);
	}

	public String toString(){
		if(!isValid()) return "x";
		else if(isEmpty()) return " ";
		else return getOccupant() + "";
	}
}
