package Generator;

import java.awt.Color;
import java.awt.Graphics;

import Application.ZDisplay;
import Application.ZKeyboard;
import Application.ZMouse;

/*  CLASS DisplayRMGMultiroot EXTENDS ZDisplay
 *  @Zanice
 * 
 * 	This is the display used for the multiroot version of the random map
 * 	generator. It manages the buttons and fields of the application, along
 * 	with visually constructing the map.
 */

public class DisplayRMGMultiroot extends ZDisplay {
	public DisplayRMGMultiroot(ZMouse mouseListener, ZKeyboard keyListener) {
		super(mouseListener, keyListener);
	}

	public void setup() {
		//Based on the window size, determine the location of the button row.
		int buttonx = RunGeneratorMultiroot.windowx / 2;
		int buttony = RunGeneratorMultiroot.windowy - 40;
		
		//Create buttons along the row.
		getButtons().addNewLockedButton(1, "New Map", 23, buttonx - 50, buttony, 100, 30, new Color(0, 150, 0), Color.WHITE);
		getButtons().addNewLockedButton(2, "Set X", 3, buttonx - 100, buttony, 40, 30, new Color(150, 0, 0), Color.WHITE);
		getButtons().addNewLockedButton(3, "Set Y", 4, buttonx + 60, buttony, 40, 30, new Color(150, 0, 0), Color.WHITE);
		getButtons().addNewLockedButton(4, "Set Starts", 17, 10, buttony, 90, 30, new Color(0, 0, 150), Color.WHITE);
		if (RunGeneratorMultiroot.cleanup)
			getButtons().addNewLockedButton(5, "Cleanup: True", 4, RunGeneratorMultiroot.windowx - 100, buttony, 90, 30, new Color(0, 0, 150), Color.WHITE);
		else
			getButtons().addNewLockedButton(5, "Cleanup: False", 2, RunGeneratorMultiroot.windowx - 100, buttony, 90, 30, new Color(0, 0, 150), Color.WHITE);
	}
	
	//Method that recreates the fields and buttons of the application.
	public void reset() {
		//Clear the current fields and buttons.
		getFields().clearFields();
		getButtons().clearButtons();
		
		//Recreate the fields and call for recreation of the buttons.
		for (int i = 0; i < RunGeneratorMultiroot.mapx; i++) {
			for (int j = 0; j < RunGeneratorMultiroot.mapy; j++)
				getFields().addNewLockedField((i * 100) + j, RunGeneratorMultiroot.map[i][j], (RunGeneratorMultiroot.windowx / 2) - (RunGeneratorMultiroot.mapx * 20) + (i * 40), (RunGeneratorMultiroot.windowy / 2) - (RunGeneratorMultiroot.mapy * 20) + (j * 40) - 20, 40, 40, Color.BLACK, Color.WHITE);
		}
		setup();
	}

	//Paints the buttons, fields, and map elements.
	public void paintComponent(Graphics g) {
		//Paint the background.
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, RunGeneratorMultiroot.windowx, RunGeneratorMultiroot.windowy);
		
		//Paint the buttons and fields.
		getButtons().paintButtons(g);
		getFields().paintFields(g);
		
		//Paint the map's corners.
		g.setColor(new Color(255, 130, 0));
		for (int i = 0; i < RunGeneratorMultiroot.mapx + 1; i++) {
			for (int j = 0; j < RunGeneratorMultiroot.mapy + 1; j++) {
				if (RunGeneratorMultiroot.corners[i][j] != null) {
					g.fillRect((RunGeneratorMultiroot.windowx / 2) - (RunGeneratorMultiroot.mapx * 20) + (i * 40) - 2, (RunGeneratorMultiroot.windowy / 2) - (RunGeneratorMultiroot.mapy * 20) + (j * 40) - 22, 5, 5);
				}
			}
		}
		
		//Paint the map's walls.
		for (int i = 0; i < RunGeneratorMultiroot.walls.size(); i++) {
			//Vertical
			if ((RunGeneratorMultiroot.walls.get(i).x % 10 == 5)||(RunGeneratorMultiroot.walls.get(i).x % 10 == -5))
				g.fillRect((RunGeneratorMultiroot.windowx / 2) - (RunGeneratorMultiroot.mapx * 20) + (((RunGeneratorMultiroot.walls.get(i).x + 5) / 10) * 40) - 2, (RunGeneratorMultiroot.windowy / 2) - (RunGeneratorMultiroot.mapy * 20) + (((RunGeneratorMultiroot.walls.get(i).y + 5) / 10) * 40) - 22, 5, 40);
			//Horizontal
			else
				g.fillRect((RunGeneratorMultiroot.windowx / 2) - (RunGeneratorMultiroot.mapx * 20) + (((RunGeneratorMultiroot.walls.get(i).x + 5) / 10) * 40) - 2, (RunGeneratorMultiroot.windowy / 2) - (RunGeneratorMultiroot.mapy * 20) + (((RunGeneratorMultiroot.walls.get(i).y + 5) / 10) * 40) - 22, 40, 5);
		}
		
		//Paint the nodes of the map that are not part of a branch.
		if (RunGeneratorMultiroot.mapsize != 0) {
			for (int i = 0; i < RunGeneratorMultiroot.mapx; i++) {
				for (int j = 0; j < RunGeneratorMultiroot.mapy; j++) {
					if (RunGeneratorMultiroot.map[i][j].parent == null)
						g.fillRect((RunGeneratorMultiroot.windowx / 2) - (RunGeneratorMultiroot.mapx * 20) + (i * 40) + 2, (RunGeneratorMultiroot.windowy / 2) - (RunGeneratorMultiroot.mapy * 20) + (j * 40) - 18, 36, 36);
				}
			}
		}
	}

	//Use the Node object of each field to paint each section of the grid accordingly.
	public void paintElements(Graphics g, Object obj, int x, int y, int fieldID, int fieldAssignedID) {
		Node n = (Node) obj;
		
		//If the node is a part of the map created, determine what color it will be and paint it.
		if (n.parent != null) {
			//Branch ending in a circle => Light red.
			if ((n.circle)&&(n.end))
				g.setColor(new Color(255, 100, 100));
			else if (n.start) {
				//Breached start => Gray.
				if (n.breached)
					g.setColor(new Color(100, 100, 100));
				//Non-breached start => Light green.
				else
					g.setColor(new Color(100, 255, 100));
			}
			//Branch ending => Light blue.
			else if (n.end)
				g.setColor(new Color(100, 100, 255));
			//Loop member => Lighter version of the standard color.
			else if (n.circle)
				g.setColor(new Color(100, 150, 50));
			//Non-special node => Standard color.
			else
				g.setColor(new Color(100, 100, 0));
			
			//Paint the color of the node.
			g.fillRect(x + 1, y + 1, 39, 39);
		}
		
		//Paint connections to show how the node branched out.
		g.setColor(Color.WHITE);
		Node con;
		for (int i = 0; i < n.connectionsize; i++) {
			con = n.connections[i];
			if (con.x - n.x != 0) {
				if (con.x - n.x == 1)
					g.fillRect(x + 35, y + 20, 4, 4);
				else
					g.fillRect(x + 3, y + 20, 4, 4);
			}
			else {
				if (con.y - n.y == 1)
					g.fillRect(x + 20, y + 34, 4, 4);
				else
					g.fillRect(x + 20, y + 2, 4, 4);
			}
		}
	}

	public void buttonEvent(int buttonID, int assignedID) {
		switch (assignedID) {
		//If "New Map" was pressed, generate a new map.
		case 1:
			RunGeneratorMultiroot.generateMap();
			break;
		//If "Set X" was pressed, pop a text window for the new x dimension.
		case 2:
			RunGeneratorMultiroot.popTextWindow(buttonID, assignedID);
			break;
		//If "Set Y" was pressed, pop a text window for the new y dimension.
		case 3:
			RunGeneratorMultiroot.popTextWindow(buttonID, assignedID);
			break;
		//If "Set Starts" was pressed, pop a text window for the new number of starts.
		case 4:
			RunGeneratorMultiroot.popTextWindow(buttonID, assignedID);
			break;
		//If the Cleanup option button was pressed, toggle the cleanup variable and the button's text.
		case 5:
			RunGeneratorMultiroot.cleanup = !RunGeneratorMultiroot.cleanup;
			if (RunGeneratorMultiroot.cleanup) {
				getButtons().get(buttonID).setLabel("Cleanup: True");
				getButtons().get(buttonID).setLabelOffset(4);
			}
			else {
				getButtons().get(buttonID).setLabel("Cleanup: False");
				getButtons().get(buttonID).setLabelOffset(2);
			}
			break;
		}
	}

	//Implemented method, though it is not used.
	public void fieldEvent(int fieldID, int assignedID) {
		;
	}

	//Implemented method, though it is not used.
	public void typeEvent(boolean pressed, int keycode) {
		;
	}

}
