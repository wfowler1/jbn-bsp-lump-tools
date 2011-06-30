// Lump12 class

// This class holds an array of integers which are ALL indices into
// lump 09. This lump is RETARDED, the only reason I could see for
// them to set it up this way is so faces can be referenced out of
// order.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump12 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numMSurfs=0;
	private int[] marksurfaces;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump12(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numMSurfs=getNumElements();
		marksurfaces=new int[numMSurfs];
		populateMSurfList();
	}
	
	// This one accepts the input file path as a File
	public Lump12(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numMSurfs=getNumElements();
		marksurfaces=new int[numMSurfs];
		populateMSurfList();
	}
	
	// METHODS
	
	// -populateMSurfList()
	// Uses the file in the instance data to populate the list of indices
	private void populateMSurfList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numMSurfs;i++) {
			byte[] datain=new byte[4];
			reader.read(datain);
			marksurfaces[i]=(datain[3] << 24) | ((datain[2] & 0xff) << 16) | ((datain[1] & 0xff) << 8) | (datain[0] & 0xff);
		}
		reader.close();
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of face indices. This lump is RETARDED.
	public int getNumElements() {
		if(numMSurfs==0) {
			return (int)data.length()/4;
		} else {
			return numMSurfs;
		}
	}
}