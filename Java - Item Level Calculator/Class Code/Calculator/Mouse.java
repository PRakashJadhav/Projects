package Calculator;

import java.awt.event.MouseEvent;
import Application.ZMouse;

/*  CLASS Mouse EXTENDS ZMouse
 *  @Zanice
 * 
 * 	This class manages mouse events for the application. While
 * 	the class exists for the implementation of Display, no
 * 	special mouse events need to be defined in this class since
 * 	the application's mouse event dependencies are already
 * 	defined in this class' parent.
 */

public class Mouse extends ZMouse {
	//Override method for implementing special events when the mouse is moved.
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
	}
	
	//Override method for implementing special events when a mouse button is pressed.
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
	}
	
	//Override method for implementing special events when the mouse moves while a mouse button is pressed.
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
	}
	
	//Override method for implementing special events when the mouse is moved.
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
	}
}
