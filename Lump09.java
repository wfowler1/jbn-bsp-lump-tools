// Lump09 class

// Stores references to each face object in a BSP.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump09 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numFaces=0;
	private Face[] faces;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump09(String in) {
		data=new File(in);
		numFaces=getNumElements();
	}
	
	// This one accepts the input file path as a File
	public Lump09(File in) {
		data=in;
		numFaces=getNumElements();
	}
	
	// METHODS
	
	// -populateFaceList()
	// Creates an array of all the faces in the lump using the instance data.
	private void populateFaceList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numFaces;i++) {
			byte[] datain=new byte[48];
			reader.read(datain);
			faces[i]=new Face(datain);
		}
		reader.close();
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of faces.
	public int getNumElements() {
		if(numFaces==0) {
			return (int)data.length()/48;
		} else {
			return numFaces;
		}
	}
}