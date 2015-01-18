package Application;

import java.awt.Color;
import java.awt.Graphics;

/*  CLASS ZFields
 *  @Zanice
 * 
 * 	This object serves as the manager of a group of fields used in a
 * 	display. This class object and the Field objects themselves serve
 * 	as interfaces that can store elements/objects/some form of data and
 * 	can be interacted with. They follow most of the same properties as
 * 	Buttons.
 */

public class ZFields {
	//Display pointer variable, for reference back to the object's implementer.
	private ZDisplay reference;
	
	//Field array variables.
	private Field[] fields;
	private int size;
	
	//Mouse event variables for the fields.
	private int moused;
	private int pressed;
	
	public ZFields(ZDisplay parent) {
		//Initialize the variables.
		reference = parent;
		fields = new Field[1];
		size = 0;
		moused = -1;
		pressed = -1;
	}
	
	//Get method for 'size'.
	public int numberOfFields() { return size; }
	
	//Get method for 'moused'.
	public int getMousedFieldID() { return moused; }
	
	//Get and set methods for 'pressed'.
	public int getPressedFieldID() { return pressed; }
	public void setPressedFieldID(int i) { pressed = i; }
	
	//Recreates 'fields' for a higher capacity.
	private void addCapacity(int i) {
		//Create a temporary pointer for 'fields', reinstantiate 'fields' with the increased capacity,
		//and copy the contents of the pointer into the new 'fields'.
		Field[] copy = fields;
		fields = new Field[fields.length + i];
		for (int index = 0; index < size; index++)
			fields[index] = copy[index];
	}
	
	//Creates a new Field, with its real ID assigned via the current size of 'fields', and stores it.
	public void addNewField(int assignedID, Object element, int x, int y, int width, int height, Color field, Color border) {
		Field f = new Field(assignedID, false, element, x, y, width, height, field, border);
		if (size == fields.length)
			addCapacity(1);
		f.id = size;
		fields[size] = f;
		size++;
	}
	

	//Creates a new locked field, with real ID assigned via the current size of 'fields', and stores it.
	//A locked field's position does not change if the offset values of the display are changed.
	public void addNewLockedField(int assignedID, Object element, int x, int y, int width, int height, Color field, Color border) {
		Field f = new Field(assignedID, true, element, x, y, width, height, field, border);
		if (size == fields.length)
			addCapacity(1);
		f.id = size;
		fields[size] = f;
		size++;
	}
	
	//Returns the field with the specified ID, which is found at the corresponding index of 'fields'.
	public Field get(int id) throws NullPointerException {
		if (size != 0) {
			if ((id >= 0)&&(id < size)) 
				return fields[id];
			else 
				throw new IndexOutOfBoundsException();
		}
		else throw new NullPointerException();
	}
	
	//Called in the display, this method uses the Graphics component of the display to do
	//the work of painting and updating the appearance of the fields.
	public void paintFields(Graphics g) {
		//Declare variables used in this process.
		Field f; int x; int y; int width; int height; Color field; Color border;
		
		for (int i = 0; i < size; i++) {
			f = fields[i];
			x = f.x;
			y = f.y;
			
			//If the field is not a locked field, adjust its position via the display's offset.
			if (!f.locked) {
				x += reference.getXOffset();
				y += reference.getYOffset();
			}
			
			//Get the dimensions and colors of the field.
			width = f.width;
			height = f.height;
			field = f.fieldcolor;
			border = f.bordercolor;
			
			//Depending on mouse actions with the field, adjust the appearance.
			if (moused == f.id)
				field = field.brighter();
			if (pressed == f.id) {
				field = field.brighter();
			}
			
			//Finally, draw the components of the field.
			g.setColor(field);
			g.fillRect(x + 1, y, width - 1, height);
			g.setColor(border);
			g.drawRect(x, y, width, height);
			reference.paintElements(g, f.getElement(), x, y, f.id, f.idA);
		}
	}
	
	//Determines, using the mouse handler's data, if a field is currently moused over.
	public void updateMoused(int x, int y) {
		Field f;
		int offsetx = x - reference.getXOffset();
		int offsety = y - reference.getYOffset();
		
		//For each field, adjusting if the field is locked, see if the mouse coordinates fall on the field.
		for (int i = 0; i <= size; i++) {
			//If the mouse does not lay over any field, 'moused' defaults to -1.
			if (i == size)
				moused = -1;
			else {
				f = fields[i];
				if (f.locked) {
					if ((x >= f.x + 1)&&(x < f.x + f.width)&&(y >= f.y + 1)&&(y < f.y + f.height)) {
						moused = i;
						break;
					}
				}
				else {
					if ((offsetx >= f.x + 1)&&(offsetx < f.x + f.width)&&(offsety >= f.y + 1)&&(offsety < f.y + f.height)) {
						moused = i;
						break;
					}
				}
			}
		}
	}
	
	//Clears 'fields' of all fields.
	public void clearFields() {
		fields = new Field[1];
		size = 0;
	}
	
	/*	SUBCLASS Field
	 * 	@Zanice
	 * 
	 * 	This is the underlying object for the ZField class and its operations.
	 * 	Field objects are used to store the data of each field, including
	 * 	positioning, dimensions, color, and whether or not the field is affected 
	 * 	by the display's offset variables.
	 */
	
	public class Field {
		private int id;
		private int idA;
		private boolean locked;
		private Object element;
		private int x;
		private int y;
		private int width;
		private int height;
		private Color fieldcolor;
		private Color bordercolor;
		
		public Field(int assignedID, boolean locked, Object element, int x, int y, int width, int height, Color field, Color border) {
			//Initialize the variables.
			id = -1;
			idA = assignedID;
			this.locked = locked;
			this.element = element;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			fieldcolor = field;
			bordercolor = border;
		}
		
		//Get and set methods for the variables of the Field object.
		
		public int getID() { return id; }
		
		public int getAssignedID() { return idA; }
		public void setAssignedID(int i) { idA = i; } 
		
		public boolean isLocked() { return locked; }
		public void setLocked(boolean b) { locked = b; }
		
		public Object getElement() { return element; }
		public void setElement(Object o) { element = o; }
		
		public int getX() { return x; }
		public void setX(int i) { x = i; }
		
		public int getY() { return y; }
		public void setY(int i) { y = i; }
		
		public int getWidth() { return width; }
		public void setWidth(int i) { width = i; }
		
		public int getHeight() { return height; }
		public void setHeight(int i) { height = i; }
		
		public Color getFieldColor() { return fieldcolor; }
		public void setFieldColor(Color color) { fieldcolor = color; }
		
		public Color getBorderColor() { return bordercolor; }
		public void setBorderColor(Color color) { bordercolor = color; }
	}
}
