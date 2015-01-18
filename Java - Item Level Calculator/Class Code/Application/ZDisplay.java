package Application;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*	ABSTRACT CLASS ZDisplay
 * 	@Zanice
 * 
 * 	This class manages a unique JPanel for painting images, along with
 * 	holding usable buttons and fields. Most methods are abstract, to be
 * 	defined through inheritance depending on the desired use.
 */

public abstract class ZDisplay extends JPanel {
	//Input handler variables.
	private ZMouse m;
	private ZKeyboard k;
	
	//Interface manager variables.
	private ZButtons b;
	private ZFields f;
	
	//Offset variables, used for scenarios like a scrolling screen. 
	private int oX;
	private int oY;
	
	public ZDisplay(ZMouse mouseListener, ZKeyboard keyListener) {
		//Initialize the variables, and configure inputs and display.
		super();
		oX = 0;
		oY = 0;
		b = new ZButtons(this);
		f = new ZFields(this);
		m = mouseListener;
		m.configure(this);
		k = keyListener;
		k.configure(this);
		addMouseListener(m);
		addMouseMotionListener(m);
		addKeyListener(k);
		setFocusable(true);
		setEnabled(true);
		setup();
	}
	
	//Get methods for the variables of ZDisplay.
	public ZMouse getMouseListener() { return m; }
	public ZKeyboard getKeyListener() { return k; }
	public ZButtons getButtons() { return b; }
	public ZFields getFields() { return f; }
	public int getXOffset() { return oX; }
	public int getYOffset() {return oY; }
	
	//Set variables for the offsets.
	public void setXOffset(int x) { oX = x; }
	public void setYOffset(int y) { oY = y; } 
	
	//Abstract methods, to be implemented by subclass.
	public abstract void setup();
	public abstract void paintComponent(Graphics g);
	public abstract void paintElements(Graphics g, Object obj, int x, int y, int fieldID, int fieldAssignedID);
	public abstract void buttonEvent(int buttonID, int assignedID);
	public abstract void fieldEvent(int fieldID, int assignedID);
	public abstract void typeEvent(boolean pressed, int keycode);
}
