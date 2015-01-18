package Application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*	ABSTRACT CLASS ZKeyboard
 * 	@Zanice
 * 
 * 	This class acts as a basic key event handler, redirecting
 * 	key events in a two-variable format to a user-defined method.
 */

public abstract class ZKeyboard implements KeyListener{
	//Display pointer variable, for reference back to the object's implementer.
	private ZDisplay reference;
	
	public ZKeyboard() {
		super();
	}
	
	//Get method for the display pointer.
	public ZDisplay getReference() { return reference; }
	
	//The equivalent of a set method for the display pointer.
	public void configure(ZDisplay display) { reference = display; }
	
	//Implemented method, redirects a pressed key event to an implemented typeEvent() method.
	//The boolean parameter signifies the type of event. In this case, the parameter is true for a key press.
	public void keyPressed(KeyEvent e) {
		reference.typeEvent(true, e.getKeyCode());
	}
	
	//Implemented method, redirects a released key event to an implemented typeEvent() method.
	//The boolean parameter signifies the type of event. In this case, the parameter is false for a key release.
	public void keyReleased(KeyEvent e) {
		reference.typeEvent(false, e.getKeyCode());
	}
	
	//Implemented method, but is not used since keyPressed() and keyReleased() are in use.
	public void keyTyped(KeyEvent e) {;}
}

