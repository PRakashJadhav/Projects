package Calculator;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Application.ZWindow;

/*  CLASS TextWindow EXTENDS ZWindow
 *  @Zanice
 * 
 * 	This class is the object construction for a text window that
 * 	appears when the user needs to enter text. The object keeps
 * 	track of the event ID by which it was called so that the text
 * 	can be handled accordingly when entered.
 */

public class TextWindow extends ZWindow {
	//Window and text variables.
	private JPanel textpanel;
	private JTextField textentry;
	
	//Event ID variables.
	private int realID;
	private int assignedID;
	
	public TextWindow(int real, int assigned) {
		//Create the text window.
		super("Enter Level");
		super.setSmartSize(250, 30);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		textpanel = new JPanel();
		textpanel.setLayout(new FlowLayout());
		textentry = new JTextField(20);
		textentry.addActionListener(new TextHandler());
		textpanel.add(textentry);
		add(textpanel);
		setVisible(true);
		
		//Initialize the ID variables with the passed ID's of the event.
		realID = real;
		assignedID = assigned;
	}
	
	//Implemented methods, runs when the text is submitted.
	private class TextHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//Pass the text and the ID's of the original event to onTextEnter() method in RunApp.
			RunApp.onTextEnter(textentry.getText(), realID, assignedID);
		}
	}
}
