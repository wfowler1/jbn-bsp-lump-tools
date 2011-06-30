// Lump11 class

// This class keeps and maintains an array, which is a list
// of the leaves in the map.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump11 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numLeaves=0;
	private int numWorldLeaves=0;
	private int numModelLeaves=numLeaves-numWorldLeaves;
	private Leaf[] leaves;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump11(String in) {
		data=new File(in);
		try {
			numLeaves=getNumElements();
			leaves=new Leaf[numLeaves];
			populateLeafList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump11(File in) {
		data=in;
		try {
			numLeaves=getNumElements();
			leaves=new Leaf[numLeaves];
			populateLeafList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	public Lump11(Leaf[] in) {
		leaves=in;
		numLeaves=leaves.length;
	}
	
	// METHODS
	
	// -populateLeafList()
	// Uses the instance data to populate the array of Leaf
	private void populateLeafList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numLeaves;i++) {
				byte[] datain=new byte[48];
				reader.read(datain);
				leaves[i]=new Leaf(datain);
			}
			reader.close();
		} catch(InvalidLeafException e) {
			System.out.println("WARNING: funny lump size in "+data+", ignoring last leaf.");
		}
	}
	
	// add(Leaf)
	// Adds a leaf which is already a Leaf object. Easiest to do.
	public void add(Leaf in) {
		Leaf[] newList=new Leaf[numLeaves+1];
		for(int i=0;i<numLeaves;i++) {
			newList[i]=leaves[i];
		}
		newList[numLeaves]=in;
		numLeaves++;
		leaves=newList;
	}
	
	// add (int int float float float float float float int int int int)
	// Adds a leaf defined by data alone. Still easy.
	public void add(int inType, int inVis, float inMinX, float inMinY, float inMinZ, float inMaxX,
	                float inMaxY, float inMaxZ, int inMSurf, int inNumMSurfs, int inMBrshs, int inNumMBrshs) {
		add(new Leaf(inType, inVis, inMinX, inMinY, inMinZ, inMaxX, inMaxY, inMaxZ, inMSurf, inNumMSurfs, inMBrshs, inNumMBrshs));
	}

	// save(String)
	// Saves the lump to the specified path.
	public void save(String path) {
		File newFile=new File(path+"\\11 - Leaves.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream leafWriter=new FileOutputStream(newFile);
			byte[] data=new byte[numLeaves*48];
			for(int i=0;i<numLeaves;i++) {
				// This is MUCH faster than using DataOutputStream
				// Filling a huge data array is MUCH faster than writing to a hard drive 4 bytes at a time
				int out=leaves[i].getType();
				data[(i*48)+3]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+2]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+1]=(byte)((out >> 8) & 0xFF);
				data[i*48]=(byte)((out >> 0) & 0xFF);
				out=leaves[i].getPVS();
				data[(i*48)+7]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+6]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+5]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+4]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(leaves[i].getMinX());
				data[(i*48)+11]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+10]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+9]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+8]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(leaves[i].getMinY());
				data[(i*48)+15]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+14]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+13]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+12]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(leaves[i].getMinZ());
				data[(i*48)+19]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+18]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+17]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+16]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(leaves[i].getMaxX());
				data[(i*48)+23]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+22]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+21]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+20]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(leaves[i].getMaxY());
				data[(i*48)+27]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+26]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+25]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+24]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(leaves[i].getMaxZ());
				data[(i*48)+31]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+30]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+29]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+28]=(byte)((out >> 0) & 0xFF);
				out=leaves[i].getMarkSurface();
				data[(i*48)+35]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+34]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+33]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+32]=(byte)((out >> 0) & 0xFF);
				out=leaves[i].getNumMarkSurfaces();
				data[(i*48)+39]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+38]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+37]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+36]=(byte)((out >> 0) & 0xFF);
				out=leaves[i].getMarkBrush();
				data[(i*48)+43]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+42]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+41]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+40]=(byte)((out >> 0) & 0xFF);
				out=leaves[i].getNumMarkBrushes();
				data[(i*48)+47]=(byte)((out >> 24) & 0xFF);
				data[(i*48)+46]=(byte)((out >> 16) & 0xFF);
				data[(i*48)+45]=(byte)((out >> 8) & 0xFF);
				data[(i*48)+44]=(byte)((out >> 0) & 0xFF);
			}
			leafWriter.write(data);
			leafWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}

	// +setAllToVisible()
	// Sets the visibility reference for all leaves to FFFFFFFF. This is the same
	// thing the BBSP compiler does for all maps prior to running BVIS.
	public void setAllToVisible() {
		for(int i=0;i<numLeaves;i++) {
			leaves[i].setPVS(-1); // -1 is the signed int equivalent of FFFFFFFF
		}
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
	
	public Leaf getLeaf(int i) {
		return leaves[i];
	}
	
	public Leaf[] getLeaves() {
		return leaves;
	}
}
