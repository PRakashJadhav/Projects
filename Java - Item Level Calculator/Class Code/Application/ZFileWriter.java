package Application;

import java.io.*;
import java.util.*;
import java.lang.*;

/*	ABSTRACT CLASS ZFileWriter
 * 	@Zanice
 * 
 * 	This class serves as a basis for writing files. This class
 * 	contains the same list of methods as ZFileReader, but uses
 * 	a Formatter rather than a Scanner so the file can be written
 * 	to.
 */

public abstract class ZFileWriter {
	//Formatter variable, used to open and write to files.
	private Formatter writer;
	
	//Get method for the formatter.
	public Formatter getFormatter() { return writer; }
	
	//Attempt to open the specified file, returning a boolean for its success.
	public boolean openFile(String file) {
		try {
			writer = new Formatter(file);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	//Method to close the file after operating on it.
	public void closeFile() {
		writer.close();
	}
}
