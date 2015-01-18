package Generator;

/*  CLASS Node
 *  @Zanice
 * 
 * 	A Node is an object storing all of the data of a piece of the
 * 	map's grid. Each Node has an ID (for multiroot generation),
 * 	coordinates on the grid, connections with other nodes, and
 * 	property variables that describe how the Node influenced/resulted
 * 	from map generation.
 */

public class Node {
	//ID variable, used in multiroot generation.
	public int id;
	
	//Coordinate variables for its location on the map's grid.
	public int x;
	public int y;
	
	//Connection variables, used to control the recursive generation.
	public Node parent;
	public Node[] connections;
	public int connectionsize;
	public Node bridge;
	
	//Property variables.
	public boolean start;
	public boolean breached;
	public boolean end;
	public boolean circle;
	
	public Node(int idnum, int xCoord, int yCoord) {
		//Initialize the variables.
		id = idnum;
		x = xCoord;
		y = yCoord;
		parent = null;
		connections = new Node[3];
		connectionsize = 0;
		bridge = null;
		start = false;
		breached = false;
		end = false;
		circle = false;
	}
	
	//Adds the specified node to 'connections', making the specified node's parent this node.
	public void addConnection(Node n) {
		connections[connectionsize] = n;
		connectionsize++;
		n.parent = this;
	}
	
	//Sets the 'bridge' of both nodes to each other.
	public void addBridge(Node n) {
		n.bridge = this;
		bridge = n;
	}
}
