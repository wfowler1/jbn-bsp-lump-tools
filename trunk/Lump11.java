// Lump11 class

// This class keeps and maintains an array, which is a list
// of the leaves in the map.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump11 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numLeaves=0;
	private Leaf[] leaves;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump11(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numLeaves=getNumElements();
		leaves=new Leaf[numLeaves];
		populateLeafList();
	}
	
	// This one accepts the input file path as a File
	public Lump11(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numLeaves=getNumElements();
		leaves=new Leaf[numLeaves];
		populateLeafList();
	}
	
	// METHODS
	
	// -populateLeafList()
	// Uses the instance data to populate the array of Leaf
	private void populateLeafList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numLeaves;i++) {
			byte[] datain=new byte[48];
			reader.read(datain);
			leaves[i]=new Leaf(datain);
		}
		reader.close();
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of Leaves.
	public int getNumElements() {
		if(numLeaves==0) {
			return (int)data.length()/48;
		} else {
			return numLeaves;
		}
	}
}