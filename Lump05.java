// Lump05 class

// Really all this class does is store the length of the "normals" lump. Since
// this lump is all zeroes there is really nothing to store but this.

import java.io.File;
import java.io.FileOutputStream;

public class Lump05 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int length;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump05(String in) {
		data=new File(in);
		length=(int)getLength();
	}
	
	// This one accepts the input file path as a File
	public Lump05(File in) {
		data=in;
		length=(int)getLength();
	}
	
	// METHODS
	
	// The add methods are completely unnecessary here, but here
	// they are for consistency's sake.
	public void add(int len) {
		setLength(len+length);
	}
	
	public void add(Lump05 in) {
		setLength((int)in.getLength()+length);
	}
	
	public void save(String path) {
		try{
			File newFile=new File(path+"\\05 - Normals.hex");
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream normWriter = new FileOutputStream(newFile);
			byte[] norms=new byte[length]; // Hoping this defaults to 0s
			normWriter.write(norms);
			normWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("Unknown error saving normals, lump probably not saved!");
		}
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Since there is no data structure, this just returns 0.
	public int getNumElements() {
		return 0;
	}
	
	// Allows setting of length. There's no point to storing a bunch
	// of 0x00s. This is how the add() method is implemented for this
	// lump.
	public void setLength(int len) {
		length=len;
	}
}