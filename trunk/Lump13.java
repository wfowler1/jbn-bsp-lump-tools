// Lump13 class

// This class holds an array of integers which are ALL indices into
// lump 15. This lump is about as retarded as lump 12.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump13 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numMBrshs=0;
	private int[] markbrushes;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump13(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numMBrshs=getNumElements();
		markbrushes=new int[numMBrshs];
		populateMBrshList();
	}
	
	// This one accepts the input file path as a File
	public Lump13(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numMBrshs=getNumElements();
		markbrushes=new int[numMBrshs];
		populateMBrshList();
	}
	
	// METHODS
	
	// -populateMBrshList()
	// Uses the file in the instance data to populate the list of indices
	private void populateMBrshList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numMBrshs;i++) {
			byte[] datain=new byte[4];
			reader.read(datain);
			markbrushes[i]=(datain[3] << 24) | ((datain[2] & 0xff) << 16) | ((datain[1] & 0xff) << 8) | (datain[0] & 0xff);
		}
		reader.close();
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of brush indices. This lump is RETARDED.
	public int getNumElements() {
		if(numMBrshs==0) {
			return (int)data.length()/4;
		} else {
			return numMBrshs;
		}
	}
}