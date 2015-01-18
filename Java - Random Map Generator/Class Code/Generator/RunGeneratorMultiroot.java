package Generator;

import java.util.Random;

/*  CLASS RunGenerator
 *  @Zanice
 * 	
 * 	This is the multiroot version of the random map generator.
 * 	This class runs the application and contains the logic behind
 * 	the generation of the map. After a map is generated, this class 
 * 	also manages any "cleanup" that needs to be done to make the 
 * 	map more playable.
 */

public class RunGeneratorMultiroot {
	//Window and display variables.
	public static Window w;
	public static DisplayRMGMultiroot d;
	public static TextWindowMultiroot tw;
	
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
	public static int roots;
	public static Node[] rootarray;
	
	//Cleanup option variable.
	public static boolean cleanup;
	
	public static void main(String[] args) {
		//Initialize variables and set up the application.
		w = new Window("RMG - Multiroot");
		d = new DisplayRMGMultiroot(new Mouse(), new Keyboard());
		w.add(d);
		windowx = 220;
		windowy = 100;
		mapx = 5;
		mapy = 5;
		roots = 2;
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
		
		//(Re)Initialize the map element arrays.
		map = new Node[mapx][mapy];
		for (int i = 0; i < mapx; i++) {
			for (int j = 0; j < mapy; j++)
				map[i][j] = new Node(-1, i, j);
		}
		corners = new Corner[mapx + 1][mapy + 1];
		mapsize = 0;
		walls = new ZCompressedArray<Wall>((mapx * mapy) / 2);
		ends = new ZCompressedArray<Node>(mapx + mapy);
		circleends = new ZCompressedArray<Node>(mapx);
		rootarray = new Node[roots];
		for (int l = 0; l < roots; l++)
            rootarray[l] = new Node(l, -l - 1, -l - 1);
		
		//Reset and update the display.
		d.reset();
		d.repaint();
	}
	
	//Runs the map generation, running the process again if the final product is not acceptible.
	public static void generateMap() {
		//Run reset so no lingering information exists from the last generation.
		reset();
			
		//Find random starting nodes.
		findRandomStarts();
		
		//Build the maps from the roots.
		drawMap();
		
		//Build all possible corners for the map.
		for (int i = 0; i < mapx + 1; i++) {
			for (int j = 0; j < mapy + 1; j++)
				corners[i][j] = new Corner((i * 10) - 5, (j * 10) - 5);
		}
		
		//Recursively find "circles" in branches and removes corners within these circles.
		for (int i = 0; i < roots; i++)
            circleRec(rootarray[i].connections[0]);
		
		//Finally, fill the walls based on corners.
		fillWalls();
		
		//If cleanup is enabled, open the map by deleting forward walls of non-circle ends.
		if (cleanup)
			openMap();
		
		//After map generation is complete, determine the map's size.
		double mapfilled = (double) (mapsize) / (double) (mapx * mapy);
		
		//If map isn't fully connected, or if map is too small/stretched, rebuild.
		if (roots > 1) {
			boolean connected = true;
			for (int i = 0; i < roots; i++) {
				if (rootarray[i].bridge == null)
					connected = false;
			}
			if (!connected) {
				generateMap();
				return;
			}
		}
		if (mapfilled <= .6) {
			generateMap();
			return;
		}
		
		//Refresh the display after the map generation is complete.
		d.repaint();
	}
	
	//Randomly determines the starting nodes and links them to the roots.
	private static void findRandomStarts() {
		//Declare the variables used.
		Random ran = new Random();
        int x = 0;
        int y = 0;
        int startsfound = 0;
        
        //Find the necessary amount of starts.
        while (startsfound < roots) {
            x = ran.nextInt(mapx);
            y = ran.nextInt(mapy);
            
            //If the random coordinates are for a non-start node, set it as a start with a unique ID.
            if (map[x][y].parent == null) {
                rootarray[startsfound].addConnection(map[x][y]);
                map[x][y].start = true;
                map[x][y].id = startsfound;
                startsfound++;
            }
        }
    }
	
	//Uses a queue to take nodes and try to branch from them.
	private static void drawMap() {
		//Declare the variables used.
        ZQueue<Node> queue = new ZQueue<Node>();
        Random ran = new Random();
        Node current = null;
        int iterations = 0;
        int random = 0;
        int x = 0;
        int y = 0;
        
        //Begin the queue with the starting nodes.
        for (int i = 0; i < roots; i++) {
            queue.add(rootarray[i].connections[0]);
            mapsize++;
        }
        
        //Until all branching attempts fail and no nodes are left to attempt branching,
        //take the next node and try to branch with it.
        while (queue.peek() != null) {
            iterations = 0;
            current = queue.next();
            
            //Three Attempts: Randomly choose an adjacent node and attempt to branch out.
            while (iterations < 3) {
                random = ran.nextInt(4);
                switch (random) {
                case 0:
                    x = 1;
                    y = 0;
                    break;
                case 1:
                    x = -1;
                    y = 0;
                    break;
                case 2:
                    x = 0;
                    y = 1;
                    break;
                case 3:
                    x = 0;
                    y = -1;
                    break;
                }
                
                //If this randomly chosen node lies on the map (if it is not out of bounds)...
                if ((current.x + x != -1)&&(current.x + x != mapx)&&(current.y + y != -1)&&(current.y + y != mapy)) {
                	//...and if the node is not already part of a branch, add it as a connection.
                	if (map[current.x + x][current.y + y].parent == null) {
                        current.addConnection(map[current.x + x][current.y + y]);
                        queue.add(map[current.x + x][current.y + y]);
                        map[current.x + x][current.y + y].id = current.id;
                        mapsize++;
                    }
                	//Otherwise, if the chosen node is part of a branch of a different root...
                    else if (map[current.x + x][current.y + y].id != current.id) {
                    	//...and if neither nodes are starting nodes, bridge the two nodes.
                    	if ((!map[current.x + x][current.y + y].start)&&(!current.start)) {
	                    	current.addBridge(map[current.x + x][current.y + y]);
	                        rootarray[map[current.x + x][current.y + y].id].addBridge(rootarray[0]);
	                        map[current.x + x][current.y + y].end = false;
                    	}
                    }
                }
                
                //Regardless of success, increment 'iterations' for each attempt.
                iterations++;
            }
            
            //If the current node has not branched out further, mark it as a branch ending.
            if ((current.connectionsize == 0)&&(current.bridge == null)) {
                current.end = true;
                ends.add(current);
            }
        }
    }
	
	//RECURSIVE: Find circles along the branches of the map.
	private static void circleRec(Node n) {
		//Declare "change in _" varaibles used.
		int dx;
		int dy;
		
		
		//If the node is not the starting node, test for circles/loops along its branches.
		//(We do not want to include the starting node in any circles/loops.)
		if (!n.start) {
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
										if (((n1.parent != n2)&&(n2.parent != n1))&&(n1.bridge != n2))
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
			if (!current.start) {
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
								
								//If the forward node and current node have different ID's, bridge them.
								if (current.id != map[dx][dy].id) {
									current.addBridge(map[dx][dy]);
									rootarray[map[dx][dy].id].bridge = rootarray[0];
									rootarray[current.id].bridge = rootarray[0];
								}
								
								//Label both nodes as breached.
								map[dx][dy].breached = true;
                                current.breached = true;
                                
                                //If the forward node was a circle end, remove it as an ending.
								if ((map[dx][dy].end)&&(map[dx][dy].circle)) {
									circleends.remove(map[dx][dy]);
									map[dx][dy].end = false;
								}
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
		tw = new TextWindowMultiroot(buttonID, assignedID);
		if (assignedID == 2)
			tw.setText(mapx);
		else if (assignedID == 3)
			tw.setText(mapy);
		else
			tw.setText(roots);
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
			else if (assignedID == 3) {
				mapy = Integer.parseInt(entry);
				if (mapy <= 0)
					mapy = 1;
			}
			else {
				roots = Integer.parseInt(entry);
				if (roots < 0)
					roots = 0;
				
				//Make sure the number of roots is below an ideal limit, depending on the map size.
				if (roots > (mapx * mapy) / 5) {
					roots = (mapx * mapy) / 5;
					if (roots == 0)
						roots = 1;
				}
			}
			reset();
		} catch (Exception e) {
			;
		}
	}
}
