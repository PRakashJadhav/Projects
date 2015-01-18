package Application;

import java.awt.Color;
import java.awt.Graphics;

/*  CLASS ZButtons
 *  @Zanice
 * 
 * 	This object serves as the manager of a group of buttons used in a
 * 	display. This class object and the Button objects themselves serve
 * 	to mask underlying configuration of a custom button object, with 
 * 	the goal of allowing for more workable, customizable buttons.
 */

public class ZButtons {
	//Display pointer variable, for reference back to the object's implementer.
	private ZDisplay reference;
	
	//Button array variables.
	private Button[] buttons;
	private int size;
	
	//Mouse event variables for the buttons.
	private int moused;
	private int pressed;
	
	public ZButtons(ZDisplay parent) {
		//Initialize the variables.
		reference = parent;
		buttons = new Button[1];
		size = 0;
		moused = -1;
		pressed = -1;
	}
	
	//Get method for 'size'.
	public int numberOfButtons() { return size; }
	
	//Get method for 'moused'.
	public int getMousedButtonID() { return moused; }
	
	//Get and set methods for 'pressed'.
	public int getPressedButtonID() { return pressed; }
	public void setPressedButtonID(int i) { pressed = i; }
	
	//Recreates 'buttons' for a higher capacity.
	private void addCapacity(int i) {
		//Create a temporary pointer for 'buttons', reinstantiate 'buttons' with the increased capacity,
		//and copy the contents of the pointer into the new 'buttons'.
		Button[] copy = buttons;
		buttons = new Button[buttons.length + i];
		for (int index = 0; index < size; index++)
			buttons[index] = copy[index];
	}
	
	//Creates a new Button, with its real ID assigned via the current size of 'buttons', and stores it.
	public void addNewButton(int assignedID, String label, int labeloffset, int x, int y, int width, int height, Color button, Color border) {
		Button b = new Button(assignedID, false, label, labeloffset, x, y, width, height, button, border);
		if (size == buttons.length)
			addCapacity(1);
		b.id = size;
		buttons[size] = b;
		size++;
	}
	
	//Creates a new locked button, with real ID assigned via the current size of 'buttons', and stores it.
	//A locked button's position does not change if the offset values of the display are changed.
	public void addNewLockedButton(int assignedID, String label, int labeloffset, int x, int y, int width, int height, Color button, Color border) {
		Button b = new Button(assignedID, true, label, labeloffset, x, y, width, height, button, border);
		if (size == buttons.length)
			addCapacity(1);
		b.id = size;
		buttons[size] = b;
		size++;
	}
	
	//Returns the button with the specified ID, which is found at the corresponding index of 'buttons'.
	public Button get(int id) throws NullPointerException {
		if (size != 0) {
			if ((id >= 0)&&(id < size)) 
				return buttons[id];
			else 
				throw new IndexOutOfBoundsException();
		}
		else throw new NullPointerException();
	}
	
	//Called in the display, this method uses the Graphics component of the display to do
	//the work of painting and updating the appearance of the buttons.
	public void paintButtons(Graphics g) {
		//Declare variables used in this process.
		Button b; String label; int offset; int x; int y; int width; int height; Color button; Color border;
		
		for (int i = 0; i < size; i++) {
			b = buttons[i];
			label = b.name;
			offset = b.offset;
			x = b.x;
			y = b.y;
			
			//If the button is not a locked button, adjust its position via the display's offset.
			if (!b.locked) {
				x += reference.getXOffset();
				y += reference.getYOffset();
			}
			
			//Get the dimensions and colors of the button.
			width = b.width;
			height = b.height;
			button = b.buttoncolor;
			border = b.bordercolor;
			
			//Depending on mouse actions with the button, adjust the appearance.
			if (moused == b.id)
                button = button.brighter();
            if (pressed == b.id) {
                g.setColor(button.darker());
                g.fillRect(x + 1, y, width - 1, height);
                x += 4;
                y += 4;
                width -= 8;
                height -= 8;
            }
            
            //Finally, draw the components of the button, adjusting for if the button is pressed and/or locked.
            g.setColor(button);
            g.fillRect(x + 1, y, width - 1, height);
            g.setColor(border);
            if (pressed == b.id) {
            	if (!b.locked) {
                	g.drawRect(b.x + reference.getXOffset(), b.y + reference.getYOffset(), b.width, b.height);
                	g.drawString(label, b.x + reference.getXOffset() + offset + 2, b.y + reference.getYOffset() + (b.height / 2) + 4);
                }
                else {
                	g.drawRect(b.x, b.y, b.width, b.height);
                	g.drawString(label, b.x + offset + 2, b.y + (b.height / 2) + 4);
                }
            }
            else {
                g.drawRect(x, y, width, height);
                g.drawString(label, x + offset + 2, y + (height / 2) + 4);
            }
		}
	}

	//Determines, using the mouse handler's data, if a button is currently moused over.
	public void updateMoused(int x, int y) {
		Button b;
		int offsetx = x - reference.getXOffset();
		int offsety = y - reference.getYOffset();
		
		//For each button, adjusting if the button is locked, see if the mouse coordinates fall on the button.
		for (int i = 0; i <= size; i++) {
			//If the mouse does not lay over any button, 'moused' defaults to -1.
			if (i == size)
				moused = -1;
			else {
				b = buttons[i];
				if (b.locked) {
					if ((x >= b.x + 1)&&(x < b.x + b.width)&&(y >= b.y + 1)&&(y < b.y + b.height)) {
						moused = i;
						break;
					}
				}
				else {
					if ((offsetx >= b.x + 1)&&(offsetx < b.x + b.width)&&(offsety >= b.y + 1)&&(offsety < b.y + b.height)) {
						moused = i;
						break;
					}
				}
			}
		}
	}
	
	//Clear 'buttons' of all buttons.
	public void clearButtons() {
		buttons = new Button[1];
		size = 0;
	}
	
	/*	SUBCLASS Button
	 * 	@Zanice
	 * 
	 * 	This is the underlying object for the ZButton class and its operations.
	 * 	Button objects are used to store the data of each button, including
	 * 	positioning, dimensions, color, a label, and whether or not the button
	 * 	is affected by the display's offset variables.
	 */
	
	public class Button {
		private int id;
		private int idA;
		private boolean locked;
		String name;
		private int offset;
		private int x;
		private int y;
		private int width;
		private int height;
		private Color buttoncolor;
		private Color bordercolor;
		
		public Button(int assignedID, boolean locked, String label, int labeloffset, int x, int y, int width, int height, Color button, Color border) {
			//Initialize the variables.
			id = -1;
			idA = assignedID;
			this.locked = locked;
			name = label;
			offset = labeloffset;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			buttoncolor = button;
			bordercolor = border;
		}
		
		//Get and set methods for the variables of Button objects.
		
		public int getID() { return id; }
		
		public int getAssignedID() { return idA; }
		public void setAssignedID(int i) { idA = i; } 
		
		public boolean isLocked() { return locked; }
		public void setLocked(boolean b) { locked = b; }
		
		public String getLabel() { return name; }
		public void setLabel(String s) { name = s; }
		
		public int getLabelOffset() { return offset; }
		public void setLabelOffset(int i) { offset = i; }
		
		public int getX() { return x; }
		public void setX(int i) { x = i; }
		
		public int getY() { return y; }
		public void setY(int i) { y = i; }
		
		public int getWidth() { return width; }
		public void setWidth(int i) { width = i; }
		
		public int getHeight() { return height; }
		public void setHeight(int i) { height = i; }
		
		public Color getButtonColor() { return buttoncolor; }
		public void setButtonColor(Color color) { buttoncolor = color; }
		
		public Color getBorderColor() { return bordercolor; }
		public void setBorderColor(Color color) { bordercolor = color; }
	}
}
