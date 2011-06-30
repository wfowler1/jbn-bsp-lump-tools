// Lump12 class

// This class holds an array of integers which are ALL indices into
// lump 09. This lump is RETARDED, the only reason I could see for
// them to set it up this way is so faces can be referenced out of
// order.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump12 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numMSurfs=0;
	private int[] marksurfaces;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump12(String in) {
		data=new File(in);
		try {
			numMSurfs=getNumElements();
			marksurfaces=new int[numMSurfs];
			populateMSurfList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump12(File in) {
		data=in;
		try {
			numMSurfs=getNumElements();
			marksurfaces=new int[numMSurfs];
			populateMSurfList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// METHODS
	
	// -populateMSurfList()
	// Uses the file in the instance data to populate the list of indices
	private void populateMSurfList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numMSurfs;i++) {
			byte[] datain=new byte[4];
			reader.read(datain);
			marksurfaces[i]=(datain[3] << 24) | ((datain[2] & 0xff) << 16) | ((datain[1] & 0xff) << 8) | (datain[0] & 0xff);
		}
		reader.close();
	}
	
	// +add(int)
	// adds a single int to the list
	public void add(int in) {
		int[] newList=new int[numMSurfs+1];
		for(int i=0;i<numMSurfs;i++) {
			newList[i]=marksurfaces[i];
		}
		newList[numMSurfs]=in;
		numMSurfs++;
		marksurfaces=newList;
	}	
	
	// +add(Lump12)
	// adds every item from another lump12 object.
	public void add(Lump12 in) {
		int[] newList=new int[numMSurfs+in.getNumElements()];
		File myLump09=new File(data.getParent()+"//09 - Faces.hex");
		int sizeL09=(int)myLump09.length()/48;
		for(int i=0;i<numMSurfs;i++) {
			newList[i]=marksurfaces[i];
		}
		for(int i=0;i<in.getNumElements(); i++) {
			newList[i+numMSurfs]=in.getMarkSurface(i)+sizeL09;
		}
		numMSurfs=numMSurfs+in.getNumElements();
		marksurfaces=newList;
	}
	
	// save(String)
	// Saves the lump to the specified path.
	public void save(String path) {
		File newFile=new File(path+"\\12 - Mark Surfaces.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream mSurfaceWriter=new FileOutputStream(newFile);
			byte[] data=new byte[numMSurfs*4];
			for(int i=0;i<numMSurfs;i++) {
				// This is MUCH faster than using DataOutputStream
				data[(i*4)+3]=(byte)((marksurfaces[i] >> 24) & 0xFF);
				data[(i*4)+2]=(byte)((marksurfaces[i] >> 16) & 0xFF);
				data[(i*4)+1]=(byte)((marksurfaces[i] >> 8) & 0xFF);
				data[i*4]=(byte)((marksurfaces[i] >> 0) & 0xFF);
			}
			mSurfaceWriter.write(data);
			mSurfaceWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	// save()
	// Saves the lump, overwriting the one data was read from
	public void save() {
		save(data.getParent());
	}

	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of face indices. This lump is RETARDED.
	public int getNumElements() {
		if(numMSurfs==0) {
			return (int)data.length()/4;
		} else {
			return numMSurfs;
		}
	}
	
	public void setMarkSurface(int i, int in) {
		marksurfaces[i]=in;
	}
	
	public int getMarkSurface(int i) {
		return marksurfaces[i];
	}
	
	public int[] getMarkSurfaces() {
		return marksurfaces;
	}
}
