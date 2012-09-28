/** @Pulkit
 * Encapsulator for the (x, y) coordinates - no need to use int[2] arrays every time!
 * Contains handy Coordinates.ZERO for quick reference to origin. See GameMap for more quick references.
 */
package components.grid;

import java.io.Serializable;

public class Coordinates implements Serializable{
	private static final long serialVersionUID = 7826161716087413357L;

	private int x;
	private int y;

	/* Some default coordinates */
	public static final Coordinates ZERO = new Coordinates(0, 0);

	public Coordinates(int x, int y){
		this.x = x;
		this.y = y;
	}

	public Coordinates(Coordinates coords){
		this.x = coords.getX();
		this.y = coords.getY();
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public int[] getCoordinates(){
		int coordinates[] = {x, y};
		return coordinates;
	}
	
	public double getDistanceEuclidean(Coordinates target){
		/* Return Euclidean distance between two coordinates */
		double xDistance = Math.abs(target.getX() - x);
		double yDistance = Math.abs(target.getY() - y);
		return Math.abs(Math.sqrt(xDistance*xDistance + yDistance*yDistance));
	}
	
	public int getDistanceManhattan(Coordinates target){
		/* Return Manhattan distance between two coordinates */
		int xDistance = Math.abs(target.getX() - x);
		int yDistance = Math.abs(target.getY() - y);
		return xDistance + yDistance;
	}
	
	public void setX(int x){
		this.x = x;
	}

	public void setY(int y){
		this.y = y;
	}

	public void setCoordinates(int x, int y){
		this.x = x;
		this.y = y;
	}

	public Boolean equals(Coordinates otherCoords){
		return (this.x == otherCoords.getX()) && (this.y == otherCoords.getY());
	}
	public String toString(){
		return "(" + x + ", " + y + ")";
	}
}
