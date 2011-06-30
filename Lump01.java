import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump01 {
	
	File data;
	int numPlns=0;
	
	//CONSTRUCTORS:
	
	// This one accepts the lump path as a String
	public Lump01(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numPlns=getNumElements();
	}
	
	// This one accepts the input file path as a File
	public Lump01(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numPlns=getNumElements();
	}
	
	// Accessors/mutators
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of entities.
	public int getNumElements() throws java.io.FileNotFoundException, java.io.IOException {
		if(numPlns==0) {
			return (int)data.length()/20;
		} else {
			return numPlns;
		}
	}
}