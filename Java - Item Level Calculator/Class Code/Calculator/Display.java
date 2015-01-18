package Calculator;

import java.awt.Color;
import java.awt.Graphics;
import Application.ZDisplay;
import Application.ZKeyboard;
import Application.ZMouse;

/*  CLASS Display EXTENDS ZDisplay
 *  @Zanice
 * 
 * 	This class implements the display control and interface of ZDisplay to
 * 	allow easy creation/management of buttons and fields, along with access
 * 	to the mouse and keyboard handlers bound to this interface. In addition,
 * 	this class is tailored to display and manage the other interfaces of the
 * 	application, along with editing the parameters of the fields to allow for
 * 	information/color updates.
 */

public class Display extends ZDisplay {
	//Dimensional variables to adjust the layout of the item level fields and statistics.
	final int PANEL_X = 20;
	final int PANEL_Y = 20;
	final int INSET = 220;
	final int STAT_LEFT = 320;
	
	//Color boolean variable, toggled by user for optional colored fields.
	public boolean colors;
	
	//Message variables for broadcasting to lower panel of application.
	public String message;
	public int messagetype; //Dictates color of message to signify confirmation, error, etc.
	
	public Display(ZMouse mouseListener, ZKeyboard keyListener) {
		super(mouseListener, keyListener);
		//Initializing variables.
		colors = true;
		message = "";
		messagetype = 1;
	}

	//Implemented method, runs after construction of object instance.
	public void setup() {
		//Sets up item level fields of the left column.
		getFields().addNewLockedField(1, new Item("Head", true), PANEL_X, PANEL_Y, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(2, new Item("Neck", true), PANEL_X, PANEL_Y + 70, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(3, new Item("Shoulder", true), PANEL_X, PANEL_Y + 140, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(4, new Item("Back", true), PANEL_X, PANEL_Y + 210, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(5, new Item("Chest", true), PANEL_X, PANEL_Y + 280, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(6, new Item("Shirt", false), PANEL_X, PANEL_Y + 350, 60, 60, Color.BLACK, Color.WHITE);
		getFields().addNewLockedField(7, new Item("Tabard", false), PANEL_X, PANEL_Y + 420, 60, 60, Color.BLACK, Color.WHITE);
		getFields().addNewLockedField(8, new Item("Wrists", true), PANEL_X, PANEL_Y + 490, 60, 60, Color.GRAY, Color.WHITE);
		
		//Sets up item level fields of the right column.
		getFields().addNewLockedField(9, new Item("Hands", true), PANEL_X + INSET, PANEL_Y, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(10, new Item("Waist", true), PANEL_X + INSET, PANEL_Y + 70, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(11, new Item("Legs", true), PANEL_X + INSET, PANEL_Y + 140, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(12, new Item("Feet", true), PANEL_X + INSET, PANEL_Y + 210, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(13, new Item("Ring 1", true), PANEL_X + INSET, PANEL_Y + 280, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(14, new Item("Ring 2", true), PANEL_X + INSET, PANEL_Y + 350, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(15, new Item("Trinket 1", true), PANEL_X + INSET, PANEL_Y + 420, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(16, new Item("Trinket 2", true), PANEL_X + INSET, PANEL_Y + 490, 60, 60, Color.GRAY, Color.WHITE);
		
		//Sets up item level fields of the weapons.
		getFields().addNewLockedField(17, new Item("Mainhand", true), PANEL_X + 75, PANEL_Y + 540, 60, 60, Color.GRAY, Color.WHITE);
		getFields().addNewLockedField(18, new Item("Offhand", true), PANEL_X + 145, PANEL_Y + 540, 60, 60, Color.GRAY, Color.WHITE);
		
		//Sets up button to allow user to toggle the use of one-handed or two-handed items for the character.
		getButtons().addNewLockedButton(1, "Switch to Two-Handed", 7, PANEL_X + 70, PANEL_Y + 610, 140, 30, new Color(200, 0, 0), Color.WHITE);
		
		//Sets up buttons to allow user to change the mode, clear changes made in Edit Mode, and to set all levels of that mode.
		getButtons().addNewLockedButton(3, "Mode: Current", 13, 630, 15, 105, 30, Color.GRAY, Color.WHITE);
		getButtons().addNewLockedButton(2, "Clear Edit", 23, 630, 95, 105, 30, Color.GRAY, Color.WHITE);
		getButtons().addNewLockedButton(100, "Set All Levels", 15, 630, 175, 105, 30, Color.GRAY, Color.WHITE);
		
		//Sets up button to allow user to toggle the item level interface colors.
		getButtons().addNewLockedButton(6, "Toggle iL Colors", 5, 630, 335, 105, 30, new Color(0, 110, 0), Color.WHITE);
		
		//Sets up button to allow user to create new files, load existing files, and reset the item level values of a file to 0.
		getButtons().addNewLockedButton(4, "New File", 5, 630, 480, 105, 30, new Color(50, 50, 200), Color.WHITE);
		getButtons().addNewLockedButton(5, "Load File", 5, 630, 560, 105, 30, new Color(50, 50, 200), Color.WHITE);
		getButtons().addNewLockedButton(10, "Reset File", 5, 630, 640, 105, 30, new Color(50, 50, 200), Color.WHITE);
	}
	
	//Method to update the colors of the item level fields, based on changes made to values or color toggle.
	public void updateFieldColors() {
		//If colors are active, go through process of setting colors.
		if (colors) {
			//Create variables used for this process.
			int difference;
			int factor;
			int benchmark;
			Item current;
			
			//Determine the number of items that are contributing to the item level.
			if (((Item) getFields().get(17).getElement()).getEnabled())
				factor = 16;
			else
				factor = 15;
			
			//Determine the average item level to use, depending on the mode.
			if (RunApp.editmode)
				benchmark = RunApp.efinal;
			else
				benchmark = RunApp.cfinal;
			
			//For each field, find the difference between the current level value and the average.
			//Scale the colors of the fields accordingly.
			for (int i  = 0; i < 18; i++) {
				current = (Item) getFields().get(i).getElement();
				//If the field is enabled, find its color.
				if (current.getEnabled()) {
					if (RunApp.editmode)
						difference = current.getEdit() - benchmark;
					else
						difference = current.getCurrent() - benchmark;
					
					//Set the current field's new color, scaling if it falls between a threshold and the average
					//level and defaulting to a set color otherwise.
					if (difference == 0)
						getFields().get(i).setFieldColor(new Color(55, 55, 255));
					else if (difference > 0) {
						if (difference > factor * 2)
							getFields().get(i).setFieldColor(new Color(155, 0, 255));
						else
							getFields().get(i).setFieldColor(new Color(55 + (int) (100 * (((double) difference) / (factor * 2))), 55 - (int) (55 * (((double) difference) / (factor * 2))), 255));
					}
					else {
						if (difference < -factor * 2)
							getFields().get(i).setFieldColor(new Color(55, 155, 55));
						else
							getFields().get(i).setFieldColor(new Color(55, 55 + (int) (100 * (((double) -difference) / (factor * 2))), 255 - (int) (200 * (((double) -difference) / (factor * 2)))));
					}
				}
				//Otherwise, if the field is not enabled, set the color to black.
				else
					getFields().get(i).setFieldColor(Color.BLACK);
			}
		}
		//Otherwise, if colors are not active, set the fields' colors to gray.
		else {
			//For each field, if the field is enabled, color it gray. If not, color it black.
			for (int i  = 0; i < 18; i++) {
				if (((Item) getFields().get(i).getElement()).getEnabled())
					getFields().get(i).setFieldColor(Color.GRAY);
				else
					getFields().get(i).setFieldColor(Color.BLACK);
			}
		}
	}
	
	//Implemented method, used to paint objects on the display.
	public void paintComponent(Graphics g) {
		//Paints Backdrop; Blue Frame
		g.setColor(new Color(0, 0, 150));
		g.fillRect(0, 0, 750, 700);
		
		//Paints Backdrop; Background
		g.setColor(Color.BLACK);
		g.fillRect(4, 4, 742, 692);
		
		//Paints Backdrop; White Trim
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, 749, 699);
		g.drawRect(4, 4, 741, 691);
		
		//Paints Stat/Button Panel; Background
		g.setColor(new Color(50, 50, 50));
		g.fillRect(0, 680, 750, 20);
		g.fillRect(STAT_LEFT, 5, 300, 676);
		g.fillRect(620, 5, 125, 675);
		
		//Paints Stat/Button Panel; Current Banner
		g.setColor(Color.GRAY);
		g.fillRect(STAT_LEFT, 5, 300, 20);
		
		//Paints Stat/Button Panel; Mode Banner
		g.setColor(new Color(200, 0, 0));
		g.fillRect(STAT_LEFT, 345, 300, 20);
		
		//Paints Stat/Button Panel; Whire Trim
		g.setColor(Color.WHITE);
		g.drawRect(0, 680, 749, 19);
		g.drawRect(STAT_LEFT, 4, 300, 676);
		g.drawRect(STAT_LEFT, 4, 300, 20);
		g.drawRect(STAT_LEFT, 344, 300, 20);
		g.drawLine(STAT_LEFT, 205, STAT_LEFT + 300, 205);
		g.drawLine(STAT_LEFT, 545, STAT_LEFT + 300, 545);
		
		//Paints Current Mode Statistics Text
		g.setColor(Color.WHITE);
		g.drawString("Current Statistics: ", STAT_LEFT + 6, 20);
		g.drawString("Total iL", STAT_LEFT + 6, 40);
		g.drawString("iL Disparity", STAT_LEFT + 6, 60);
		g.drawString("Highest iL", STAT_LEFT + 6, 80);
		g.drawString("Lowest iL", STAT_LEFT + 6, 100);
		g.drawString("Average iL", STAT_LEFT + 6, 120);
		g.drawString("Final iL", STAT_LEFT + 6, 160);
		g.drawString("Levels to Next iL Point", STAT_LEFT + 6, 180);
		g.drawString("Focus on Replacing:", STAT_LEFT + 6, 220);
		
		//Paints Edit Mode Statistics Text
		g.drawString("Edit Statistics: ", STAT_LEFT + 6, 360);
		g.drawString("Total iL", STAT_LEFT + 6, 380);
		g.drawString("iL Disparity", STAT_LEFT + 6, 400);
		g.drawString("Highest iL", STAT_LEFT + 6, 420);
		g.drawString("Lowest iL", STAT_LEFT + 6, 440);
		g.drawString("Average iL", STAT_LEFT + 6, 460);
		g.drawString("Final iL", STAT_LEFT + 6, 500);
		g.drawString("Levels to Next iL Point", STAT_LEFT + 6, 520);
		g.drawString("Focus on Replacing:", STAT_LEFT + 6, 560);
		
		//Paints Current Mode Statistics
		g.drawString(RunApp.ctotal + "", STAT_LEFT + 190, 40);
		g.drawString(RunApp.cdisp + "", STAT_LEFT + 190, 60);
		g.drawString(RunApp.chigh + "", STAT_LEFT + 190, 80);
		g.drawString(RunApp.clow + "", STAT_LEFT + 190, 100);
		if ((int) (RunApp.cavg * 100) % 10 == 0)
			g.drawString(RunApp.cavg + "0", STAT_LEFT + 190, 120);
		else
			g.drawString(RunApp.cavg + "", STAT_LEFT + 190, 120);
		g.drawString(RunApp.cfinal + "", STAT_LEFT + 190, 160);
		g.drawString(RunApp.clevels + "", STAT_LEFT + 190, 180);
		
		//Paitns Edit Mode Statistics
		g.drawString(RunApp.etotal + "", STAT_LEFT + 190, 380);
		g.drawString(RunApp.edisp + "", STAT_LEFT + 190, 400);
		g.drawString(RunApp.ehigh + "", STAT_LEFT + 190, 420);
		g.drawString(RunApp.elow + "", STAT_LEFT + 190, 440);
		if ((int) (RunApp.eavg * 100) % 10 == 0)
			g.drawString(RunApp.eavg + "0", STAT_LEFT + 190, 460);
		else
			g.drawString(RunApp.eavg + "", STAT_LEFT + 190, 460);
		g.drawString(RunApp.efinal + "", STAT_LEFT + 190, 500);
		g.drawString(RunApp.elevels + "", STAT_LEFT + 190, 520);
		
		//Paints Recommendations, based on lowest individual item level.
		g.setColor(new Color(150, 150, 0));
		Item current;
		int counterc = 0;
		int countere = 0;
		//For each item, if it's level is among the lowest, print the item in the recommendations
		//section, adjusting position to keep the item names involved orderly.
		for (int i = 0; i < 18; i++) {
			current = (Item) getFields().get(i).getElement();
			if (current.getEnabled()) {
				if (current.getCurrent() == RunApp.clow) {
					g.drawString(current.getLabel(), STAT_LEFT + 16 + (70 * (counterc % 4)), 260 + (20 * (counterc / 4)));
					counterc++;
				}
				if (current.getEdit() == RunApp.elow) {
					g.drawString(current.getLabel(), STAT_LEFT + 16 + (70 * (countere % 4)), 600 + (20 * (countere / 4)));
					countere++;
				}
			}
		}
		
		//Paint Buttons
		getButtons().paintButtons(g);
		
		//If item level information can be displayed, paint the fields.
		if (RunApp.loadedfile)
			getFields().paintFields(g);
		//Otherwise, if item level information is not available, hide the fields and certain buttons.
		else {
			//Hide buttons that require/manipulate item level information.
			g.setColor(new Color(50, 50, 50));
			g.fillRect(621, 5, 124, 435);
			g.fillRect(621, 600, 124, 80);
			
			//Hide the item level fields.
			g.setColor(Color.BLACK);
			g.fillRect(80, 520, 200, 150);
		}
		
		//Print the current message, its color depending on 'messagetype'
		switch (messagetype) {
		case 1:
			g.setColor(Color.WHITE);
			break;
		case 2:
			g.setColor(new Color(200, 30, 30));
			break;
		case 3:
			g.setColor(new Color(125, 125, 200));
			break;
		}
		g.drawString(message, 10, 695);
	}
	
	//Inherited method, dictates how the elements of the fields used are painted.
	public void paintElements(Graphics g, Object obj, int x, int y, int fieldID, int fieldAssignedID) {
		//Paints Field; Label
		g.setColor(new Color(50, 50, 50));
		g.fillRect(x, y, 60, 20);
		
		//Paitns Field; Label Text and Trim
		g.setColor(Color.WHITE);
		g.drawRect(x, y, 60, 20);
		g.drawString(((Item) obj).getLabel(), x + 3, y + 15);
		
		//If the object is enabled, print its item level values.
		if (((Item) obj).getEnabled()) {
			g.drawString("" + ((Item) obj).getCurrent(), x + 3, y + 35);
			//If the edit item level is not the same as the current, signify the edit by painting a red
			//rectangle around the level.
			if (((Item) obj).getEdit() != ((Item) obj).getCurrent()) {
				g.setColor(new Color(200, 0, 0, 100));
				g.fillRect(x + 1, y + 38, 59, 15);
			}
			g.setColor(Color.WHITE);
			g.drawString("(" + ((Item) obj).getEdit() + ")", x + 3, y + 50);
		}
	}

	//Implemented level, dictates the handling of button events based on their ID's.
	public void buttonEvent(int buttonID, int assignedID) {
		//If a file is loaded and information is present, make these buttons respond to use.
		//If not, no not trigger their use since. (They will also be painted over above.)
		if (RunApp.loadedfile) {
			switch (assignedID) {
			//If the weapon swapping button was pressed, change the present configurations to accomodate
			//the change from one/two weapon(s) to two/one.
			case 1:
				Item offhand = (Item) getFields().get(17).getElement();
				offhand.setEnabled(!offhand.getEnabled());
				//Adjust the button for its new text and text alignment, along with the offhand field.
				if (offhand.getEnabled()) {
					getFields().get(17).setFieldColor(Color.GRAY);
					getButtons().get(buttonID).setLabel("Switch to Two-Handed");
					getButtons().get(buttonID).setLabelOffset(7);
				}
				else {
					getFields().get(17).setFieldColor(Color.BLACK);
					getButtons().get(buttonID).setLabel("Switch to One-Handeds");
					getButtons().get(buttonID).setLabelOffset(4);
				}
				repaint();
				break;
			//If the "Clear Edit" button was pressed, run RunApp's clearEdit().
			case 2:
				RunApp.clearEdit();
				break;
			//If the mode swapping button was pressed, swap the mode and the button's text/color.
			case 3:
				RunApp.editmode = !RunApp.editmode;
				if (RunApp.editmode) {
					getButtons().get(buttonID).setLabel("Mode: Edit");
					getButtons().get(buttonID).setLabelOffset(23);
					getButtons().get(buttonID).setButtonColor(new Color(200, 0, 0));
				}
				else {
					getButtons().get(buttonID).setLabel("Mode: Current");
					getButtons().get(buttonID).setLabelOffset(13);
					getButtons().get(buttonID).setButtonColor(Color.GRAY);
				}
				break;
			//If the "Toggle iL Colors" button was pressed, toggle the item level field colors.
			case 6:
				colors = !colors;
				break;
			//If the "Reset File" button was pressed, change all item level values to zero.
			case 10:
				Item current;
				for (int i = 0; i < 18; i++) {
					current = (Item) getFields().get(i).getElement();
					current.setCurrent(0);
					current.setEdit(0);
					if (i == 17)
						current.setEnabled(true);
				}
				break;
			//If the "Set All Levels" button was pressed, pop a text window for the value.
			case 100:
				RunApp.popTextWindow(buttonID, assignedID);
				break;
			}
			RunApp.calculateStats();
		}
		//If the "New File" button was pressed, pop a text window for the new file's name.
		if (assignedID == 4) {
			RunApp.popTextWindow(1004, 1004); //1004 signifies this event's specific ID for handling later.
		}
		//If the "Load File" button was pressed, pop a text window for the file's name.
		else if (assignedID == 5) {
			RunApp.popTextWindow(1005, 1005); //1005 signifies this event's specific ID for handling later.
		}
	}

	//Implemented method, dictates how clicks on fields are handled.
	public void fieldEvent(int fieldID, int assignedID) {
		//If a file is loaded and the field is enabled, pop a text window so a
		//new item level value can be entered.
		if (RunApp.loadedfile) {
			if (((Item) getFields().get(fieldID).getElement()).getEnabled())
				RunApp.popTextWindow(fieldID, assignedID);
		}
	}

	//Implemented method, dictates how key events are handled.
	public void typeEvent(boolean pressed, int keycode) {
		;
	}
	
	//Relays a message to be displayed in the application.
	public void broadcast(String s, int type) {
		message = s;
		messagetype = type;
	}
}
