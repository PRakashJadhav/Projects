package Application;

import java.awt.Color;
import javax.swing.JFrame;

/*	ABSTRACT CLASS ZWindow
 * 	@Zanice
 * 
 * 	This abstract class allows for easy creation of windows that
 * 	provide a visual interface for the application. The window
 * 	holds a ZDisplay, can have its label changed, and will properly
 * 	adjust its size for a desired size.
 */

public abstract class ZWindow extends JFrame {
	public ZWindow(String name) {
		//Configure the window.
		super(name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.BLACK);
		setSize(216, 238);
		setVisible(true);
	}
	
	//Change the display object that the window holds.
	public void changeDisplay(ZDisplay display) {
		//If the window already has a display, remove it.
		if (getContentPane() != null) 
			remove(getContentPane());
		add(display);
	}
	
	//Sets the size of the window, adding pixel buffers for the window frame.
	public void setSmartSize(int x, int y) {
		setSize(x + 16, y + 38);
	}
	
	//Sets the label at the top of the window.
	public void setLabel(String s) {
		super.setTitle(s);
	}
}
