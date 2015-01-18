package Calculator;

import Application.ZFileReader;

/*  CLASS FileReader EXTENDS ZFileReader
 *  @Zanice
 * 
 * 	This class manages the loading of files and handles the events
 * 	where the targeted file is non-existent or where the file
 * 	is formatted incorrectly and does not load properly.
 */

public class FileReader extends ZFileReader {
	//File name variable.
	String filename;
	
	public FileReader(String file) {
		//Initialize the variable as the passed file name with proper extension.
		filename = file + ".txt";
	}
	
	//Method that attempts to load a file.
	public boolean readFile() {
		//If the file exists, load the file's contents into the corresponding data values.
		if (openFile(filename)) {
			Item current;
			int iterations = 0;
			//Load the data, keeping track of how many load iterations there
			//should be if file has correct format.
			while (getScanner().hasNext()) {
				current = (Item) RunApp.d.getFields().get(iterations).getElement();
				current.setCurrent(getScanner().nextInt());
				current.setEdit(getScanner().nextInt());
				//Adjust loading process for last two data: Toggled variables.
				if (iterations == 17) {
					int onehand = getScanner().nextInt();
					current.setEnabled(onehand == 1);
					int color = getScanner().nextInt();
					RunApp.d.colors = (color == 1);
				}
				iterations++;
				//If nineteen iterations have occurred, the file is not correctly formatted. Return false.
				if (iterations == 19)
					return false;
			}
			closeFile();
			//If less than eighteen iterations have occurred, the file is not correctly formatted. Return false.
			if (iterations < 18)
				return false;
			return true;
		}
		//Otherwise, if file doesn't exist, return false.
		return false;
	}
}
