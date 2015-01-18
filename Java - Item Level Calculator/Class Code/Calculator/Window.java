package Calculator;

import Application.ZWindow;

/*  CLASS Window EXTENDS ZWindow
 *  @Zanice
 * 
 * 	This class is the window object that holds a display. As such, it has
 * 	a title on the top section but does not contain any deeper workings.
 */

public class Window extends ZWindow {
	//Title variable, for the top section of the window.
	public static String title = "iL Calculator v1.0";
	
	public Window() {
		//Construct the window.
		super(title);
		setSmartSize(750, 700);
	}
	
	//Override method, changes the window's title to include the file currently loaded,
	//passed in as s.
	public void setLabel(String s) {
		this.setTitle(title + s);
	}
}
