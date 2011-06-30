// Lump07 class

// This class holds an array of boolean values that are the PVSes for a specific
// cluster of leaves. This lump will complicate things if leaves are removed from
// the map. Therefore, if that happens we may have to replace the entire lump with
// TRUE (FF) to avoid visibility problems, unless a way can be found to mathematically
// find and remove the correct visibility boolean. Sounds like a big pain in the ass
// to me, especially since I have no idea where the leaf "clusters" are defined. It
// could be just different leaves referencing the same visibility data.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump07 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numPVS=0; // PVS stands for Potentially Visible Set. Every leaf cluster
	                      // has its own PVS, which is the set of leaf clusters which are
								 // potentially visible from within that leaf.
	private int lengthOfData;
	private byte[][] PVSes; // First dimension is the specific set, second is the byte in that set
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump07(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		lengthOfData=getLengthOfData();
		numPVS=getNumElements();
		PVSes=new byte[numPVS][lengthOfData];
		populatePVSList();
	}
	
	// This one accepts the input file path as a File
	public Lump07(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		lengthOfData=getLengthOfData();
		numPVS=getNumElements();
		PVSes=new byte[numPVS][lengthOfData];
		populatePVSList();
	}
	
	// METHODS
	
	// -populatePVSList()
	// Creates a list of all the Potentially Visible Sets. The indexing is virtually useless
	// since this lump is only ever referenced by offset, and how the engine determines the
	// exact size of the PVS of the leaves in a map is unknown.
	private void populatePVSList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numPVS;i++) {
			reader.read(PVSes[i]);
		}
		reader.close();
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of visibility groups, or PVSes.
	public int getNumElements() throws java.io.FileNotFoundException, java.io.IOException {
		if(numPVS==0) {
			return (int)data.length()/lengthOfData;
		} else {
			return numPVS;
		}
	}
	
	// Since this lump doesn't have a set data length, we need to find it.
	public int getLengthOfData() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream lump07LengthGrabber=new FileInputStream(data.getParent()+"\\11 - Leaves.hex");
		byte[] lenL07AsByteArray=new byte[4];
		lump07LengthGrabber.skip(100);
		lump07LengthGrabber.read(lenL07AsByteArray);
		int lenL07 = lenL07AsByteArray[0] + lenL07AsByteArray[1]*256 + lenL07AsByteArray[2]*65536 + lenL07AsByteArray[3]*16777216;
		lump07LengthGrabber.close();
		return lenL07;
	}
}