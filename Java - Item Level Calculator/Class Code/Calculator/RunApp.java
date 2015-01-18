package Calculator;

/*  CLASS RunApp
 *  @Zanice
 * 
 * 	This class starts up the application on run and controls miscellaneous
 * 	tasks between classes, such as calculating statistics, loading/saving files,
 * 	and handling text entries.
 */

public class RunApp {
	//Class object variables
	public static Window w;
	public static Display d;
	public static TextWindow tw;
	public static FileReader fr;
	public static FileWriter fw;
	
	//File variables, for loading and saving character level files
	public static boolean loadedfile;
	public static boolean loadbrush;
	public static String file;
	
	//Edit Mode boolean variable
	public static boolean editmode;
	
	//Current Mode character level statistics, to show current statistics.
	public static int ctotal;
	public static int chigh;
	public static int clow;
	public static int cdisp;
	public static double cavg;
	public static int cfinal;
	public static int clevels;
	
	//Edit Mode character level statistics, to view effects of possible changes.
	public static int etotal;
	public static int ehigh;
	public static int elow;
	public static int edisp;
	public static double eavg;
	public static int efinal;
	public static int elevels;
	
	public static void main(String[] args) {
		//Initializing file variables.
		loadedfile = false;
		loadbrush = false;
		file = null;
		
		//Creating application.
		w = new Window();
		d = new Display(new Mouse(), new Keyboard());
		w.add(d);
		
		//Initializing text window for later use.
		tw = null;
		
		//Start application in Current Mode.
		editmode = false;
	}
	
	//Called by Display, resets all Edit Mode item level values to zero.
	public static void clearEdit() {
		Item current;
		for (int i = 0; i < 18; i++) {
			current  = (Item) d.getFields().get(i).getElement();
			current.setEdit(current.getCurrent());
		}
	}
	
	//Calculates the statistics of Current Mode item levels and Edit Mode item levels,
	//then prompts the display to refresh.
	public static void calculateStats() {
		//Declare a pointer for items under examination, and reset variables for recalculating statistics.
		Item current;
		ctotal = 0;
		etotal = 0;
		clow = -1;
		elow = -1;
		chigh = 0;
		ehigh = 0;
		
		//Find the highest and lowest values for both modes.
		for (int i = 0; i < 18; i++) {
			current  = (Item) d.getFields().get(i).getElement();
			//If the item is enabled, check to see if its levels are the highest/lowest for
			//their modes and update variables accordingly.
			if (current.getEnabled()) {
				ctotal += current.getCurrent();
				etotal += current.getEdit();
				if ((current.getCurrent() < clow)||(clow == -1))
					clow = current.getCurrent();
				if (current.getCurrent() > chigh)
					chigh = current.getCurrent();
				if ((current.getEdit() < elow)||(elow == -1))
					elow = current.getEdit();
				if (current.getEdit() > ehigh)
					ehigh = current.getEdit();
			}
		}
		
		//Calculate the disparity/range for both modes.
		cdisp = chigh - clow;
		edisp = ehigh - elow;
		
		//Find the raw mean/average item level for both modes, depending on whether one-handed
		//or two-handed items are in use.
		if (((Item) d.getFields().get(17).getElement()).getEnabled()) {
			cavg = ((double) ctotal) / 16;
			eavg = ((double) etotal) / 16;
		}
		else {
			cavg = ((double) ctotal) / 15;
			eavg = ((double) etotal) / 15;
		}
		
		//Round and clean up the means/averages so they on include two decimal points.
		if ((int) (cavg * 1000) % 10 >= 5)
			cavg += .01;
		if ((int) (eavg * 1000) % 10 >= 5)
			eavg += .01;
		int tempc = (int) (cavg * 100);
		int tempe = (int) (eavg * 100);
		cavg = ((double) tempc) / 100;
		eavg = ((double) tempe) / 100;
		
		//Use truncation to get the final item level, as reported in game.
		cfinal = (int) cavg;
		efinal = (int) eavg;
		
		//Calculate the levels needed to reach the next final item level, again based on weapons.
		if (((Item) d.getFields().get(17).getElement()).getEnabled()) {
			clevels = ((cfinal + 1) * 16) - ctotal;
			elevels = ((efinal + 1) * 16) - etotal;
		}
		else {
			clevels = ((cfinal + 1) * 15) - ctotal;
			elevels = ((efinal + 1) * 15) - etotal;
		}
		
		//Update the display's field colors.
		d.updateFieldColors();
		
		//Save the file, prompting the display to repaint afterwards.
		saveFile();
	}
	
	//Displays a text window so the user may input text.
	public static void popTextWindow(int realID, int assignedID) {
		//If a window already exists, destroy it.
		if (tw != null)
			tw.dispose();
		tw = new TextWindow(realID, assignedID);
	}
	
	//Destroys a text window if it exists.
	public static void closeTextWindow() {
		if (tw != null)
			tw.dispose();
	}
	
	//Called as text is submitted via a text window. Handles text based on ID parameters.
	public static void onTextEnter(String entry, int realID, int assignedID) {
		//The current text window is no longer needed. Destroy it.
		tw.dispose();
		
		//If this text was for a "New File" button event, create a new, clean file.
		if (assignedID == 1004) {
			//Reset all item level values to zero (but do not save to current file).
			Item current;
			for (int i = 0; i < 18; i++) {
				current = (Item) d.getFields().get(i).getElement();
				current.setCurrent(0);
				current.setEdit(0);
				if (i == 17)
					current.setEnabled(true);
			}
			
			//If the new file is created successfully, save the levels to it.
			fw = new FileWriter(entry);
			if (fw.writeFile()) {
				fr = new FileReader(entry);
				loadedfile = true;
				loadFile(entry, file);
			}
			return;
		}
		//If this text was for a "Load File" button event, attempt to load the specified file.
		else if (assignedID == 1005) {
			fw = new FileWriter(entry);
			fr = new FileReader(entry);
			loadFile(entry, file);
			return;
		}
		
		//Otherwise, only an integer input can be accepted for the remaining cases.
		//Attempt to convert the string to an integer.
		int input = -1;
		try {
			input = Integer.parseInt(entry);
		} catch (Exception e) {
			;
		}
		//Proceed if the string was valid for integer conversion and the integer entered is in bounds.
		if (input >= 0) {
			//If this text was for a "Set All Levels" button event, set all item levels depending on the mode.
			if (assignedID == 100) {
				for (int i = 0; i < 18; i++) {
					if (((Item) d.getFields().get(i).getElement()).getEnabled()) {
						((Item) d.getFields().get(i).getElement()).setEdit(input);
						if (!editmode)
							((Item) d.getFields().get(i).getElement()).setCurrent(input);
					}
				}
			}
			//Otherwise, the entered integer was for a item level field. Set the field according to the mode.
			else {
				((Item) d.getFields().get(realID).getElement()).setEdit(input);
				if (!editmode)
					((Item) d.getFields().get(realID).getElement()).setCurrent(input);
			}
		}
		//Otherwise, send an error message to be displayed to the user.
		else
			d.broadcast("Error: Invalid input! Please enter an interger of zero or greater.", 2);
		
		//To account for any and all changes, recalculate statistics and refresh the display.
		calculateStats();
		d.repaint();
	}
	
	//Attempts to load a given file, and reverts to the old file if the process fails.
	public static void loadFile(String newfile, String oldfile) {
		//Try to load the new file. If load is successful, update file, display,
		//and message variable and recalculate statistics.
		loadbrush = fr.readFile();
		if (loadbrush) {
			file = newfile;
			loadedfile = true;
			w.setLabel(" - " + newfile);
			//Calculate new statistics (prompts a refresh of the display).
			calculateStats();
			//Relay a message for the user.
			d.broadcast("File " + newfile + ".txt loaded successfully!", 3);
		}
		//Otherwise, the attempt failed. Revert back to the old file.
		else {
			System.out.println("Reading failed");
			if (loadedfile) {
				fw = new FileWriter(oldfile);
				fr = new FileReader(oldfile);
				fr.readFile();
			}
			//Relay a message for the user.
			d.broadcast("Error: File " + newfile + ".txt does not exist.", 2);
			//Refresh the display.
			d.repaint();
		}
	}
	
	//Attempts to write to a file.
	public static void saveFile() {
		if (fw.writeFile())
			d.broadcast("Changes saved successfully.", 1);
		else
			d.broadcast("Error: File saving failed!", 2);
		//Refresh display to show broadcasted message.
		d.repaint();
	}
}
