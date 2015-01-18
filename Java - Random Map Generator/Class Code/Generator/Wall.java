package Generator;

/*  CLASS Wall
 *  @Zanice
 * 
 * 	Wall objects hold the irregular coordinates of walls
 * 	generated in the map.
 */

public class Wall {
	//Coordinates variables.
	public int x;
	public int y;
	
	public Wall(int xStepCoord, int yStepCoord) {
		//Initialize the variables.
		x = xStepCoord;
		y = yStepCoord;
	}
}
