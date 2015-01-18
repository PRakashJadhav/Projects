package Generator;

import java.util.Random;

/*  CLASS RunGenerator
 *  @Zanice
 * 
 * 	This class runs the application and contains the logic behind
 * 	the recursive generation of the map. After a map is generated,
 * 	this class also manages any "cleanup" that needs to be done to
 * 	make the map more playable.
 */

public class RunGenerator {
	//Window and display variables.
	public static Window w;
	public static DisplayRMG d;
	public static TextWindow tw;
	
	//Window dimension variables, used to scale the window depending on map dimension changes.
	public static int windowx;
	public static int windowy;
	
	//Map dimension and size variables.
	public static int mapx;
	public static int mapy;
	public static int mapsize;
	
	//Map element variables.
	public static Node[][] map;
	public static Corner[][] corners;
	public static ZCompressedArray<Wall> walls;
	public static ZCompressedArray<Node> ends;
	public static ZCompressedArray<Node> circleends;
	public static Node root;
	public static boolean openedstart;
	
	//Option variables.
	public static boolean randomstart;
	public static boolean cleanup;
	
	public static void main(String[] args) {
		//Initialize variables and set up the application.
		w = new Window("RMG - Single Root");
		d = new DisplayRMG(new Mouse(), new Keyboard());
		w.add(d);
		windowx = 220;
		windowy = 100;
		mapx = 5;
		mapy = 5;
		randomstart = false;
		cleanup = true;

		//Run the reset method, which will adjust the window and set up the map element arrays.
		reset();
	}
	
	//Resets the map generator by adjusting the window and reinitializing variable arrays.
	public static void reset() {
		//Based on the map dimensions, calculate the window size.
		windowx = 20 + (mapx * 40);
		if (windowx < 500)
			windowx = 500;
		windowy = 60 + (mapy * 40);
		
		//Set the window size.
		w.setSmartSize(windowx, windowy);
		
		//(Re)Initialize the map element arrays and the root.
		map = new Node[mapx][mapy];
		for (int i = 0; i < mapx; i++) {
			for (int j = 0; j < mapy; j++)
				map[i][j] = new Node(-1, i, j);
		}
		mapsize = 0;
		corners = new Corner[mapx + 1][mapy + 1];
		walls = new ZCompressedArray<Wall>((mapx * mapy) / 2);
		ends = new ZCompressedArray<Node>(mapx + mapy);
		circleends = new ZCompressedArray<Node>(mapx);
		root = new Node(-1, -1, -1);
		
		//Reset the 'openedstart' boolean.
		openedstart = false;
		
		//Reset and update the display.
		d.reset();
		d.repaint();
	}
	
	//Runs the map generation, running the process again if the final product is not acceptible.
	public static void generateMap() {
		//Run reset so no lingering information exists from the last generation.
		reset();
		
		//Set the starting node via the root, depending on if its position is randomized.
		if (randomstart) {
			Random ran = new Random();
			int ranx = ran.nextInt(mapx);
			int rany = ran.nextInt(mapy);
			root.addConnection(map[ranx][rany]);
		}
		else
			root.addConnection(map[mapx / 2][mapy / 2]);
		
		//Recursively build the map from the root.
		drawRec(root.connections[0]);
		
		//Build all possible corners for the map.
		for (int i = 0; i < mapx + 1; i++) {
			for (int j = 0; j < mapy + 1; j++)
				corners[i][j] = new Corner((i * 10) - 5, (j * 10) - 5);
		}
		
		//Recursively find "circles" in branches and remove corners within these circles.
		circleRec(root.connections[0]);
		
		//Finally, fill the walls based on corners.
		fillWalls();
		
		//If cleanup is enabled, open the map by deleting forward walls of non-circle ends.
		if (cleanup)
			openMap();
		
		//After map generation is complete, determine the map's size. If it's too small, rebuild.
		double mapfilled = (double) (mapsize) / (double) (mapx * mapy);
		if (mapfilled > .6)
			d.repaint(); //If map is valid, refresh the display.
		else
			generateMap();
	}
	
	//RECURSIVE: Takes the passed node and tries to branch from it.
	private static void drawRec(Node n) {
		//For every node that is added to the map, increment the map's size.
		mapsize++;
		
		//Declare variables used in the process of branching out.
		int iterations = 0;
		int current;
		int x = 0;
		int y = 0;
		Random ran = new Random();
		
		//Three Attempts: Randomly choose an adjacent node and attempt to branch out.
		while (iterations < 3) {
			current = ran.nextInt(4) + 1;
			switch (current) {
			case 1:
				x = 1;
				y = 0;
				break;
			case 2:
				x = -1;
				y = 0;
				break;
			case 3:
				x = 0;
				y = 1;
				break;
			case 4:
				x = 0;
				y = -1;
				break;
			}
			
			//If this randomly chosen node lies on the map (if it is not out of bounds)...
			if ((n.x + x != -1)&&(n.x + x != mapx)&&(n.y + y != -1)&&(n.y + y != mapy)) {
				//...and if the node is not already part of a branch, add it as a connection
				//and continue the recursive building of the map.
				if (map[n.x + x][n.y + y].parent == null) {
					n.addConnection(map[n.x + x][n.y + y]);
					drawRec(map[n.x + x][n.y + y]);
				}
			}
			
			//Regardless of success, increment 'iterations' for each attempt.
			iterations++;
		}
		
		//If the current node has not branched out further, mark it as a branch ending.
		if (n.connectionsize == 0) {
			n.end = true;
			ends.add(n);
		}
	}
	
	//RECURSIVE: Find circles along the branches of the map.
	private static void circleRec(Node n) {
		//Declare "change in _" varaibles used.
		int dx;
		int dy;
		
		//If the node is not the starting node, test for circles/loops along its branches.
		//(We do not want to include the starting node in any circles/loops.)
		if (n.parent != root) {
			//For every connection of the root, every connection of that connection, and every
			//connection of the connection of that connection (for every path of four nodes,
			//starting with the current node), if the last node is adjacent to the first,
			//then a circle/loop exists.
			for (int i = 0; i < n.connectionsize; i++) {
				for (int j = 0; j < n.connections[i].connectionsize; j++) {
					for (int k = 0; k < n.connections[i].connections[j].connectionsize; k++) {
						dx = n.x - n.connections[i].connections[j].connections[k].x;
						dy = n.y - n.connections[i].connections[j].connections[k].y;
						
						//If the last node is adjacent to the first, label the nodes as part of a circle.
						if (((dy == 0)&&((dx == 1)||(dx == -1)))||((dx == 0)&&((dy == 1)||(dy == -1)))) {
							n.circle = true;
							n.connections[i].circle = true;
							n.connections[i].connections[j].circle = true;
							n.connections[i].connections[j].connections[k].circle = true;
							
							//If the end of this circle is also the end of a branch, it is now a "circle end".
							if (n.connections[i].connections[j].connections[k].end) {
								ends.remove(n.connections[i].connections[j].connections[k]);
								circleends.add(n.connections[i].connections[j].connections[k]);
							}
							
							//Determine the coordinates of the corner in the middle of the circle and remove it.
							int cornerx;
							int cornery;
							if (n.connections[i].connections[j].x > n.x)
								cornerx = n.x + 1;
							else
								cornerx = n.x;
							if (n.connections[i].connections[j].y > n.y)
								cornery = n.y + 1;
							else
								cornery = n.y;
							corners[cornerx][cornery] = null;
						}
					}
				}
			}
		}
		
		//Recursively continue the search for circles via the node's connections.
		for (int l = 0; l < n.connectionsize; l++)
			circleRec(n.connections[l]);
	}
	
	//Constructs walls based on branches and circles created.
	private static void fillWalls() {
		//Declare the variables used in this process.
		Corner c;
		Node n1;
		Node n2;
		
		//Examine every possible corner.
		for (int i = 0; i < mapx + 1; i++) {
			for (int j = 0; j < mapy + 1; j++) {
				c = corners[i][j];
				
				//If this corner has not been removed by finding circles, check to see if
				//walls can be added to the right of and below the corner.
				if (c != null) {
					//Try to build a wall to the right.
					if (i != mapx) {
						//If a corner exists on the other side of the proposed wall...
						if (corners[i + 1][j] != null) {
							//...and the wall would not exist along the exterior of the map...
							if ((j != 0)&&(j != mapy)) {
								n1 = map[i][j - 1];
								n2 = map[i][j];
								//...and the wall would not block connected nodes, build it.
								if ((n1.parent != n2)&&(n2.parent != n1))
									walls.add(new Wall(i * 10, (j * 10) - 5));
							}
							//Otherwise, if the wall would be along the exterior, build it.
							else
								walls.add(new Wall(i * 10, (j * 10) - 5));
						}
					}
					
					//Try to build a wall below.
					if (j != mapy) {
						//If a corner exists on the other side of the proposed wall...
						if (corners[i][j + 1] != null) {
							//...and the wall would not exist along the exterior of the map...
							if ((i != 0)&&(i != mapx)) {
								n1 = map[i - 1][j];
								n2 = map[i][j];
								//...and the wall would not block connected nodes, build it.
								if ((n1.parent != n2)&&(n2.parent != n1))
									walls.add(new Wall((i * 10) - 5, j * 10));
							}
							//Otherwise, if the wall would be along the exterior, build it.
							else
								walls.add(new Wall((i * 10) - 5, j * 10));
						}
					}
				}
			}
		}
	}
	
	//For enabled cleanup, make the map more open and less maze-like by extending
	//certain branch endings.
	private static void openMap() {
		//Declare the variables used.
		Node current;
		Node parent;
		int dx;
		int dy;
		int xcoord;
		int ycoord;
		
		//For every branch end, see if the ending can be "extended".
		for (int i = 0; i < ends.size(); i++) {
			//Set pointers for the current node and its parent.
			current = ends.get(i);
			parent = current.parent;
			
			//Set dx, dy to the coordinates of the "forward" node, the node 
			//"in front of" the branch end.
			dx = (current.x * 2) - parent.x;
			dy = (current.y * 2) - parent.y;
			
			//If the node in question is not the start node...
			if (parent != root) {
				//...and we are not considering an out-of-bounds case...
				if ((dx != -1)&&(dx != mapx)&&(dy != -1)&&(dy != mapy)) {
					//...and the forward node is part of another branch, find the coordinates of 
					//the wall in between these two nodes.
					if (map[dx][dy].parent != null) {
						//If the forward node is above/below the current node, find the
						//horizontal wall in between.
						if (dx - current.x == 0) {
							xcoord = current.x * 10;
							if (dy - current.y == 1)
								ycoord = (current.y * 10) + 5;
							else
								ycoord = (current.y * 10) - 5;
						}
						//Otherwise, the forward node is to the left/right of the current mode.
						//Find the vertical wall in between.
						else {
							ycoord = current.y * 10;
							if (dx - current.x == 1)
								xcoord = (current.x * 10) + 5;
							else
								xcoord = (current.x * 10) - 5;
						}
						
						//With the coordinates found, find the corresponding wall and remove it.
						for (int j = 0; j < walls.size(); j++) {
							if ((walls.get(j).x == xcoord)&&(walls.get(j).y == ycoord)) {
								walls.remove(j);
								
								//If the forward node was a circle end, remove it as an ending.
								if ((map[dx][dy].end)&&(map[dx][dy].circle)) {
									circleends.remove(map[dx][dy]);
									map[dx][dy].end = false;
								}
								//Otherwise, if the forward node was the start, label it as a breached start.
								else if (map[dx][dy].parent == root)
									openedstart = true;
							}
						}
					}
				}
			}
		}
	}
	
	//Pops a text window for value entry.
	public static void popTextWindow(int buttonID, int assignedID) {
		//If a text window already exists, destroy it.
		if (tw != null)
			tw.dispose();
		
		//Create a text window, and set its text to show the current value of the variable being changed.
		tw = new TextWindow(buttonID, assignedID);
		if (assignedID == 2)
			tw.setText(mapx);
		else
			tw.setText(mapy);
	}
	
	//Handles text entry via a text window.
	public static void onTextEnter(String entry, int buttonID, int assignedID) {
		//Destroy the text window.
		tw.dispose();
		
		//Based on what variable is being set, attempt to set the variable and reset the map accordingly.
		try {
			if (assignedID == 2) {
				mapx = Integer.parseInt(entry);
				if (mapx <= 0)
					mapx = 1;
			}
			else {
				mapy = Integer.parseInt(entry);
				if (mapy <= 0)
					mapy = 1;
			}
			reset();
		} catch (Exception e) {
			;
		}
	}
}
