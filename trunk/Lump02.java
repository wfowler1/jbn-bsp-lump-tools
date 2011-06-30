import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump02 {
	
	File data;
	int numTxts=0;
	
	//CONSTRUCTORS:
	
	// This one accepts the lump path as a String
	public Lump02(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numTxts=getNumElements();
	}
	
	// This one accepts the input file path as a File
	public Lump02(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numTxts=getNumElements();
	}
	
	// Accessors/mutators
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of entities.
	public int getNumElements() throws java.io.FileNotFoundException, java.io.IOException {
		if(numTxts==0) {
			return (int)data.length()/64;
		} else {
			return numTxts;
		}
	}
}