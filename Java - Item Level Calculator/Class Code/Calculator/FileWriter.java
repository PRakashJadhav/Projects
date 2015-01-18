package Calculator;

import Application.ZFileWriter;

/*  CLASS FileWriter EXTENDS ZFileWriter
 *  @Zanice
 * 
 * 	This class manages the saving of files in the correct format. It
 * 	formats the file as a list of integers that will be read and loaded
 * 	loaded correctly by FileReader if the file's format is not otherwise
 * 	tampered with.
 */

public class FileWriter extends ZFileWriter {
	//File name variable.
	String filename;
	
	public FileWriter(String file) {
		//Initialize the variable as the passed file name with proper extension.
		filename = file + ".txt";
	}
	
	//Method that attempts to write the file.
	public boolean writeFile() {
		//If the file exists, overwrite the file with the application's data.
		//If the file does not exists, openFile() creates a file of the passed name.
		if (openFile(filename)) {
			//For each item, write the current and edit levels to the file.
			Item current;
			for (int i = 0; i < 18; i++) {
				current = (Item) RunApp.d.getFields().get(i).getElement();
				getFormatter().format("%d\n%d\n", current.getCurrent(), current.getEdit());
				//If all items have been written, write the one-handed/two-handed and color boolean states.
				if (i == 17) {
					if (current.getEnabled())
						getFormatter().format("%d\n", 1);
					else
						getFormatter().format("%d\n", 0);
					if (RunApp.d.colors)
						getFormatter().format("%d", 1);
					else
						getFormatter().format("%d", 0);
				}
			}
			closeFile();
			return true;
		}
		//Otherwise, if the file does not exist, return false.
		return false;
	}
}
