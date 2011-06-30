// Lump16 class

// This class holds references to all the brush sides defined
// by the map. These are referenced directly by the previous
// lump (Lump15).

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump16 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numBrshsds=0;
	private BrushSide[] brushsides;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump16(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numBrshsds=getNumElements();
		brushsides=new BrushSide[numBrshsds];
		populateBrushSideList();
	}
	
	// This one accepts the input file path as a File
	public Lump16(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numBrshsds=getNumElements();
		brushsides=new BrushSide[numBrshsds];
		populateBrushSideList();
	}
	
	// METHODS
	
	// -populateBrushSideList()
	// Uses the data file in the instance data to populate the
	// array of BrushSide objects with the data from the file
	private void populateBrushSideList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numBrshsds;i++) {
			byte[] datain=new byte[8];
			reader.read(datain);
			brushsides[i]=new BrushSide(datain);
		}
		reader.close();
	}
	
	// Accessors/mutators
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of brush sides.
	public int getNumElements() {
		if(numBrshsds==0) {
			return (int)data.length()/8;
		} else {
			return numBrshsds;
		}
	}
}