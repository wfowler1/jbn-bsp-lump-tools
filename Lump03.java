import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump03 {
	
	File data;
	int numMtrls=0;
	
	//CONSTRUCTORS:
	
	// This one accepts the lump path as a String
	public Lump03(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numMtrls=getNumElements();
	}
	
	// This one accepts the input file path as a File
	public Lump03(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numMtrls=getNumElements();
	}
	
	// Accessors/mutators
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of entities.
	public int getNumElements() throws java.io.FileNotFoundException, java.io.IOException {
		if(numMtrls==0) {
			return (int)data.length()/64;
		} else {
			return numMtrls;
		}
	}
}