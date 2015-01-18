package Application;

import java.io.*;
import java.util.*;

/*	ABSTRACT CLASS ZFileReader
 * 	@Zanice
 * 
 * 	This class serves as a basis for loading files, and includes
 * 	defined methods that hide some complexity of the process.
 */

public abstract class ZFileReader {
	//Scanner variable, used to open and parse through files.
	private Scanner scan;
	
	//Get method for the scanner.
	public Scanner getScanner() { return scan; }
	
	//Attempt to open the specified file, returning a boolean for its success.
	public boolean openFile(String file) {
		try {
			scan = new Scanner(new File(file));
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	//Method to close the file after operating on it.
	public void closeFile() {
		scan.close();
	}
}
