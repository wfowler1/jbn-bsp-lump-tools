// Lump10 class

// This class simply holds an array of three-byte RGB pixels.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump10 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numLightPixels=0;
	// Java is RETARDED when it comes to unsigned numbers. I'm not putting
	// forth the effort into handling these things as unsigned, someone
	// can just port this whole program into C++ instead.
	private byte[][] pixels;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump10(String in) {
		data=new File(in);
		numLightPixels=getNumElements();
		pixels=new byte[numLightPixels][3];
	}
	
	// This one accepts the input file path as a File
	public Lump10(File in) {
		data=in;
		numLightPixels=getNumElements();
		pixels=new byte[numLightPixels][3];
	}
	
	// METHODS
	
	// -populatePixelList()
	// Populates the array with the pixels
	private void populatePixelList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numLightPixels;i++) {
			reader.read(pixels[i]);
		}
		reader.close();
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of RGB pixels.
	public int getNumElements() {
		if(numLightPixels==0) {
			return (int)data.length()/3;
		} else {
			return numLightPixels;
		}
	}
}