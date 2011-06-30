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
	
	// add(Lump11)
	// Adds every face in another Lump11 object.
	// TODO: Put all world leaves before model leaves. These can be determined by looking
	// at model 0 in Lump14, which is the world model. Models only reference leaves by
	// index and amount, so leaves must be in the right places for referencing to work.
	public void add(Lump11 in) {
		Leaf[] newList=new Leaf[numLeaves+in.getNumElements()];
		// TODO: determine the feasibility of actually adding visibility data. Seems
		// impossible without completely recompiling the map
		// File myLump07=new File(data.getParent()+"//08 - Visibility.hex");
		// int sizeL07=(int)myLump07.length()/leaves[1].getVisibility();
		File myLump12=new File(data.getParent()+"//12 - Mark Surfaces.hex");
		int sizeL12=(int)myLump12.length()/4;
		File myLump13=new File(data.getParent()+"//13 - Mark Brushes.hex");
		int sizeL13=(int)myLump13.length()/4;
		for(int i=0;i<numLeaves;i++) {
			newList[i]=leaves[i];
		}
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+numLeaves]=in.getLeaf(i);
			newList[i+numLeaves].setMarkSurface(newList[i+numLeaves].getMarkSurface()+sizeL12);
			newList[i+numLeaves].setMarkBrush(newList[i+numLeaves].getMarkBrush()+sizeL13);
			newList[i+numLeaves].setPVS(0);
		}
		numLeaves=numLeaves+in.getNumElements();
		leaves=newList;
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
			for(int i=0;i<numLeaves;i++) {
				// This is MUCH faster than using DataOutputStream
				int out=leaves[i].getType();
				byte[] output={(byte)((out >> 0) & 0xFF), (byte)((out >> 8) & 0xFF), (byte)((out >> 16) & 0xFF), (byte)((out >> 24) & 0xFF)};
				leafWriter.write(output);
				out=leaves[i].getPVS();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
				out=Float.floatToRawIntBits(leaves[i].getMinX());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
				out=Float.floatToRawIntBits(leaves[i].getMinY());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
				out=Float.floatToRawIntBits(leaves[i].getMinZ());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
				out=Float.floatToRawIntBits(leaves[i].getMaxX());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
				out=Float.floatToRawIntBits(leaves[i].getMaxY());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
				out=Float.floatToRawIntBits(leaves[i].getMaxZ());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
				out=leaves[i].getMarkSurface();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
				out=leaves[i].getNumMarkSurfaces();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
				out=leaves[i].getMarkBrush();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
				out=leaves[i].getNumMarkBrushes();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				leafWriter.write(output);
			}
			leafWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	// save()
	// Saves the lump, overwriting the one data was read from
	public void save() {
		save(data.getParent());
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
