package Generator;

/*  CLASS Corner
 *  @Zanice
 * 
 * 	Corner objects store the coordinates of physical markers that walls will be
 * 	connected to.
 */

public class Corner {
	//Coordinate variables.
	public int x;
	public int y;
	
	public Corner(int xStepCoord, int yStepCoord) {
		//Initialize the variables.
		x = xStepCoord;
		y = yStepCoord;
	}
}
