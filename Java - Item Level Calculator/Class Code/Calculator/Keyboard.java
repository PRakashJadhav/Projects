package Calculator;

import java.awt.event.KeyEvent;
import Application.ZKeyboard;

/*  CLASS Keyboard EXTENDS ZKeyboard
 *  @Zanice
 * 
 * 	This class manages key events for the application. While
 * 	the class exists for the implementation of Display, no
 * 	special key events need to be defined in this class since
 * 	the application does not use any key events.
 */

public class Keyboard extends ZKeyboard {
	//Override method for implementing special events when a key is pressed.
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
	}
	
	//Override method for implementing special events when a key is released.
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
	}
}
