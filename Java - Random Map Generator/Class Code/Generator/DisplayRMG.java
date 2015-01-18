package Generator;

import java.awt.Color;
import java.awt.Graphics;

import Application.ZDisplay;
import Application.ZKeyboard;
import Application.ZMouse;

/*  CLASS DisplayRMG EXTENDS ZDisplay
 *  @Zanice
 * 
 * 	This is the display used for the random map generator. It manages the
 * 	buttons and fields of the application, along with visually constructing 
 * 	the map.
 */

public class DisplayRMG extends ZDisplay {
	public DisplayRMG(ZMouse mouseListener, ZKeyboard keyListener) {
		super(mouseListener, keyListener);
	}

	public void setup() {
		//Based on the window size, determine the location of the button row.
		int buttonx = RunGenerator.windowx / 2;
		int buttony = RunGenerator.windowy - 40;
		
		//Create buttons along the row.
		getButtons().addNewLockedButton(1, "New Map", 23, buttonx - 50, buttony, 100, 30, new Color(0, 150, 0), Color.WHITE);
		getButtons().addNewLockedButton(2, "Set X", 3, buttonx - 100, buttony, 40, 30, new Color(150, 0, 0), Color.WHITE);
		getButtons().addNewLockedButton(3, "Set Y", 4, buttonx + 60, buttony, 40, 30, new Color(150, 0, 0), Color.WHITE);
		if (RunGenerator.randomstart)
			getButtons().addNewLockedButton(4, "Random Start", 5, 10, buttony, 90, 30, new Color(0, 0, 150), Color.WHITE);
		else
			getButtons().addNewLockedButton(4, "Middle Start", 11, 10, buttony, 90, 30, new Color(0, 0, 150), Color.WHITE);
		if (RunGenerator.cleanup)
			getButtons().addNewLockedButton(5, "Cleanup: True", 4, RunGenerator.windowx - 100, buttony, 90, 30, new Color(0, 0, 150), Color.WHITE);
		else
			getButtons().addNewLockedButton(5, "Cleanup: False", 2, RunGenerator.windowx - 100, buttony, 90, 30, new Color(0, 0, 150), Color.WHITE);
	}
	
	//Method that recreates the fields and buttons of the application.
	public void reset() {
		//Clear the current fields and buttons.
		getFields().clearFields();
		getButtons().clearButtons();
		
		//Recreate the fields and call for recreation of the buttons.
		for (int i = 0; i < RunGenerator.mapx; i++) {
			for (int j = 0; j < RunGenerator.mapy; j++)
				getFields().addNewLockedField((i * 100) + j, RunGenerator.map[i][j], (RunGenerator.windowx / 2) - (RunGenerator.mapx * 20) + (i * 40), (RunGenerator.windowy / 2) - (RunGenerator.mapy * 20) + (j * 40) - 20, 40, 40, Color.BLACK, Color.WHITE);
		}
		setup();
	}

	//Paints the buttons, fields, and map elements.
	public void paintComponent(Graphics g) {
		//Paint the background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, RunGenerator.windowx, RunGenerator.windowy);
		
		//Paint the buttons and fields.
		getButtons().paintButtons(g);
		getFields().paintFields(g);
		
		//Paint the map's corners.
		g.setColor(new Color(255, 130, 0));
		for (int i = 0; i < RunGenerator.mapx + 1; i++) {
			for (int j = 0; j < RunGenerator.mapy + 1; j++) {
				if (RunGenerator.corners[i][j] != null) {
					g.fillRect((RunGenerator.windowx / 2) - (RunGenerator.mapx * 20) + (i * 40) - 2, (RunGenerator.windowy / 2) - (RunGenerator.mapy * 20) + (j * 40) - 22, 5, 5);
				}
			}
		}
		
		//Paint the map's walls.
		for (int i = 0; i < RunGenerator.walls.size(); i++) {
			//Painting of vertical walls, orientation determined by coordinates.
			if ((RunGenerator.walls.get(i).x % 10 == 5)||(RunGenerator.walls.get(i).x % 10 == -5))
				g.fillRect((RunGenerator.windowx / 2) - (RunGenerator.mapx * 20) + (((RunGenerator.walls.get(i).x + 5) / 10) * 40) - 2, (RunGenerator.windowy / 2) - (RunGenerator.mapy * 20) + (((RunGenerator.walls.get(i).y + 5) / 10) * 40) - 22, 5, 40);
			//Painting of horizontal walls, orientation determined by coordinates.
			else
				g.fillRect((RunGenerator.windowx / 2) - (RunGenerator.mapx * 20) + (((RunGenerator.walls.get(i).x + 5) / 10) * 40) - 2, (RunGenerator.windowy / 2) - (RunGenerator.mapy * 20) + (((RunGenerator.walls.get(i).y + 5) / 10) * 40) - 22, 40, 5);
		}
		
		//Paint the nodes of the map that are not part of a branch.
		if (RunGenerator.root.connectionsize != 0) {
			for (int i = 0; i < RunGenerator.mapx; i++) {
				for (int j = 0; j < RunGenerator.mapy; j++) {
					if (RunGenerator.map[i][j].parent == null)
						g.fillRect((RunGenerator.windowx / 2) - (RunGenerator.mapx * 20) + (i * 40) + 2, (RunGenerator.windowy / 2) - (RunGenerator.mapy * 20) + (j * 40) - 18, 36, 36);
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
			else if (n.parent == RunGenerator.root) {
				//Breached start => Gray.
				if (RunGenerator.openedstart)
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

	//Handle button events based on the ID of the button pressed.
	public void buttonEvent(int buttonID, int assignedID) {
		switch (assignedID) {
		//If "New Map" was pressed, generate a new map.
		case 1:
			RunGenerator.generateMap();
			break;
		//If "Set X" was pressed, pop a text window for the new x dimension.
		case 2:
			RunGenerator.popTextWindow(buttonID, assignedID);
			break;
		//If "Set Y" was pressed, pop a text window for the new y dimension.
		case 3:
			RunGenerator.popTextWindow(buttonID, assignedID);
			break;
		//If "Random Start" was pressed, toggle the random start variable and the button's text.
		case 4:
			RunGenerator.randomstart = !RunGenerator.randomstart;
			if (RunGenerator.randomstart) {
				getButtons().get(buttonID).setLabel("Random Start");
				getButtons().get(buttonID).setLabelOffset(5);
			}
			else {
				getButtons().get(buttonID).setLabel("Middle Start");
				getButtons().get(buttonID).setLabelOffset(11);
			}
			break;
		//If the Cleanup option button was pressed, toggle the cleanup variable and the button's text.
		case 5:
			RunGenerator.cleanup = !RunGenerator.cleanup;
			if (RunGenerator.cleanup) {
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

	//Implemented method, though is not used.
	public void fieldEvent(int fieldID, int assignedID) {
		;
	}

	//Implemented method, though is not used.
	public void typeEvent(boolean pressed, int keycode) {
		;
	}
}
