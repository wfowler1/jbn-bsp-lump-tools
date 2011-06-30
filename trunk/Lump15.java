// Lump15 class

// This class holds an array of Brush objects.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump15 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numBrshs=0;
	private Brush[] brushes;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump15(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numBrshs=getNumElements();
		brushes=new Brush[numBrshs];
		populateBrushList();
	}
	
	// This one accepts the input file path as a File
	public Lump15(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numBrshs=getNumElements();
		brushes=new Brush[numBrshs];
		populateBrushList();
	}
	
	// METHODS
	
	// -populateBrushList()
	// Uses the data file in the instance data to populate the array
	// of Brush objects
	private void populateBrushList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numBrshs;i++) {
			byte[] datain=new byte[12];
			reader.read(datain);
			brushes[i]=new Brush(datain);
		}
		reader.close();
	}
	
	// Accessors/mutators
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of brushes.
	public int getNumElements() {
		if(numBrshs==0) {
			return (int)data.length()/12;
		} else {
			return numBrshs;
		}
	}
}