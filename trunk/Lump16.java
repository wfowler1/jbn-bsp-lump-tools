// Lump16 class

// This class holds references to all the brush sides defined
// by the map. These are referenced directly by the previous
// lump (Lump15).

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump16 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numBrshsds=0;
	private BrushSide[] brushsides;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump16(String in) {
		data=new File(in);
		try {
			numBrshsds=getNumElements();
			brushsides=new BrushSide[numBrshsds];
			populateBrushSideList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump16(File in) {
		data=in;
		try {
			numBrshsds=getNumElements();
			brushsides=new BrushSide[numBrshsds];
			populateBrushSideList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	public Lump16(BrushSide[] in) {
		brushsides=in;
		numBrshsds=brushsides.length;
	}
	
	// METHODS
	
	// -populateBrushSideList()
	// Uses the data file in the instance data to populate the
	// array of BrushSide objects with the data from the file
	private void populateBrushSideList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numBrshsds;i++) {
				byte[] datain=new byte[8];
				reader.read(datain);
				brushsides[i]=new BrushSide(datain);
			}
			reader.close();
		} catch(InvalidBrushSideException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last brush side.");
		}
	}
	
	// add(BrushSide)
	// Adds a brush side which is already a BrushSide object. Easiest to do.
	public void add(BrushSide in) {
		BrushSide[] newList=new BrushSide[numBrshsds+1];
		for(int i=0;i<numBrshsds;i++) {
			newList[i]=brushsides[i];
		}
		newList[numBrshsds]=in;
		numBrshsds++;
		brushsides=newList;
	}
	
	// add (int int)
	// Adds a brush side defined by data alone. Still easy.
	public void add(int inFace, int inPlane) {
		add(new BrushSide(inFace, inPlane));
	}

	// save(String)
	// Saves the lump to the specified path.
	public void save(String path) {
		File newFile=new File(path+"\\16 - Brushsides.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream brushsideWriter=new FileOutputStream(newFile);
			byte[] data=new byte[numBrshsds*8];
			for(int i=0;i<numBrshsds;i++) {
				// This is MUCH faster than using DataOutputStream.
				data[(i*8)+3]=(byte)((brushsides[i].getFace() >> 24) & 0xFF);
				data[(i*8)+2]=(byte)((brushsides[i].getFace() >> 16) & 0xFF);
				data[(i*8)+1]=(byte)((brushsides[i].getFace() >> 8) & 0xFF);
				data[i*8]=(byte)((brushsides[i].getFace() >> 0) & 0xFF);
				data[(i*8)+7]=(byte)((brushsides[i].getPlane() >> 24) & 0xFF);
				data[(i*8)+6]=(byte)((brushsides[i].getPlane() >> 16) & 0xFF);
				data[(i*8)+5]=(byte)((brushsides[i].getPlane() >> 8) & 0xFF);
				data[(i*8)+4]=(byte)((brushsides[i].getPlane() >> 0) & 0xFF);
			}
			brushsideWriter.write(data);
			brushsideWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	// ACCESSORS/MUTATORS
	
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
	
	public BrushSide getBrushSide(int i) {
		return brushsides[i];
	}
	
	public BrushSide[] getBrushSides() {
		return brushsides;
	}
	
	public void setBrushSide(int i, BrushSide in) {
		brushsides[i]=in;
	}
}