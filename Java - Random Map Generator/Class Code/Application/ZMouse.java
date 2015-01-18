package Application;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*	ABSTRACT CLASS ZKeyboard
 * 	@Zanice
 * 
 * 	This class acts as a mouse handler for button and field objects,
 * 	relaying changes in the mouse's position and controlling button/field
 * 	states depending on if a mouse button is pressed.
 */

public abstract class ZMouse implements MouseListener, MouseMotionListener {
	//Display pointer variable, for reference back to the object's implementer.
	private ZDisplay reference;
	
	//Button and field pointer variables.
	private ZButtons buttons;
	private ZFields fields;
	
	//Coordinate variables for mouse events.
	private int mouseX;
	private int mouseY;
	private int eventX;
	private int eventY;
	
	public ZMouse() {
		super();
	}
	
	//Get method for the display pointer.
	public ZDisplay getReference() { return reference; }
	
	//Sets the display, button, and field pointers via a display.
	public void configure(ZDisplay display) {
		reference = display;
		buttons = display.getButtons();
		fields = display.getFields();
	}
	
	//Get methods for the mouse coordinates of the handler.
	public int getMouseX() { return mouseX; }
	public int getMouseY() { return mouseY; }
	public int getEventX() { return eventX; }
	public int getEventY() { return eventY; }
	
	//Implemented method, handler for mouse movement.
	public void mouseMoved(MouseEvent e) {
		//Update mouse position coordinates.
		mouseX = e.getX();
		mouseY = e.getY();
		
		//Relay coordinate changes to the display and its button/field managers.
		fields.updateMoused(mouseX, mouseY);
		buttons.updateMoused(mouseX, mouseY);
		
		//Repaint the display.
		reference.repaint();
	}
	
	//Implemented method, handler for mouse button pressing.
	public void mousePressed(MouseEvent e) {
		//Update mouse event position coordinates.
		eventX = e.getX();
		eventY = e.getY();
		
		//If a field or button is moused over, that field/button is now pressed.
		int moused = fields.getMousedFieldID();
		if (moused != -1)
			fields.setPressedFieldID(moused);
		else {
			moused = buttons.getMousedButtonID();
			if (moused != -1)
				buttons.setPressedButtonID(moused);
		}
		
		//Repaint the display.
		reference.repaint();
	}
	
	//Implemented method, handler for mouse movement while a mouse button is pressed.
	public void mouseDragged(MouseEvent e) {
		//Update mouse event position coordinates.
		eventX = e.getX();
		eventY = e.getY();
		
		//Relay coordinate changes to the display and its button/field managers.
		buttons.updateMoused(e.getX(), e.getY());
		fields.updateMoused(e.getX(), e.getY());
		
		//Repaint the display.
		reference.repaint();
	}
	
	//Implemented method, handler for release of a mouse button.
	public void mouseReleased(MouseEvent e) {
		//If a field or button is currently pressed AND is still moused over, trigger a field/button event based on its ID.
		if (fields.getPressedFieldID() != -1) {
			if (fields.getMousedFieldID() == fields.getPressedFieldID())
				reference.fieldEvent(fields.getPressedFieldID(), fields.get(fields.getPressedFieldID()).getAssignedID());
		}
		else if (buttons.getPressedButtonID() != -1) {
			if (buttons.getMousedButtonID() == buttons.getPressedButtonID())
				reference.buttonEvent(buttons.getPressedButtonID(), buttons.get(buttons.getPressedButtonID()).getAssignedID());
		}
		
		//Update the managers for no pressed buttons or fields.
		fields.setPressedFieldID(-1);
		buttons.setPressedButtonID(-1);
		
		//Repaint the display.
		reference.repaint();
	}
	
	//Other implemented methods that remain unnecessary, but can be overridden if need me.
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
