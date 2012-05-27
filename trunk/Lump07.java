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

public class Lump07 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numPVS=-1; // PVS stands for Potentially Visible Set. Every leaf cluster
	                      // has its own PVS, which is the set of leaf clusters which are
								 // potentially visible from within that leaf.
	private int lengthOfData;
	private byte[][] PVSes; // First dimension is the specific set, second is the byte in that set
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump07(String in) {
		data=new File(in);
		try {
			lengthOfData=getLengthOfData();
			numPVS=getNumElements();
			if(numPVS>0) {
				PVSes=new byte[numPVS][lengthOfData];
				populatePVSList();
			} else {
				PVSes=new byte[0][0];
			}
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump07(File in) {
		data=in;
		try {
			lengthOfData=getLengthOfData();
			numPVS=getNumElements();
			PVSes=new byte[numPVS][lengthOfData];
			populatePVSList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	public Lump07(byte[] in, int length) {
		lengthOfData=length;
		numPVS=in.length/lengthOfData;
		PVSes = new byte[numPVS][lengthOfData];
		for(int i=0;i<numPVS;i++) {
			for(int j=0;j<lengthOfData;j++) {
				PVSes[i][j]=in[(i*lengthOfData)+j];
			}
		}
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
	
	// +setAllToTrue()
	// Last resort when visibility must be changed but can't be determined. Sets every PVS to
	// true for all leaves at all times. Can severely slow down the game engine, but sometimes
	// visibility cannot be truely determined without a complete recompile.
	public void setAllToTrue() {
		for(int i=0;i<PVSes.length;i++) {
			for(int j=0;j<PVSes[i].length;i++) {
				PVSes[i][j]=(byte)0xFF;
			}
		}
	}
	
	// save(String)
	// Saves the lump to the specified path
	public void save(String path) {
		File newFile=new File(path+"\\07 - Visibility.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream visWriter=new FileOutputStream(newFile);
			for(int i=0;i<numPVS;i++) {
				// These writes are probably faster than copying the entire lump into a single array then writing it.
				// Since the data is already in smaller byte arrays, I just write those one at a time.
				visWriter.write(PVSes[i]);
			}
			visWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	// save()
	// Saves the lump, overwriting the one data was read from
	public void save() {
		save(data.getParent());
	}
	
	public byte[] toByteArray() {
		byte[] allPVSs=new byte[lengthOfData*numPVS];
		for(int i=0;i<numPVS;i++) {
			for(int j=0;j<lengthOfData;j++) {
				allPVSs[(i*lengthOfData)+j]=PVSes[i][j];
			}
		}
		return allPVSs;
	}
	
	// equals(int, byte[])
	// Checks if the passed byte array has the same data as the byte array at the passed index.
	public boolean equals(int index, byte[] in) {
		boolean isEqual=true;
		for(int i=0;i<lengthOfData;i++) {
			if(PVSes[index][i]!=in[i]) {
				isEqual=false;
				break;
			}
		}
		return isEqual;
	}
	
	// delete(int)
	// Deletes the PVS at the passed index
	public void delete(int index) {
		byte[][] newList=new byte[numPVS-1][lengthOfData];
		for(int i=0;i<numPVS-1;i++) {
			if(i<index) {
				for(int j=0;j<lengthOfData;j++) {
					newList[i][j]=PVSes[i][j];
				}
			} else {
				for(int j=0;j<lengthOfData;j++) {
					newList[i][j]=PVSes[i+1][j];
				}
			}
		}
		numPVS-=1;
		PVSes=newList;
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return numPVS*lengthOfData;
	}
	
	// Returns the number of visibility groups, or PVSes.
	public int getNumElements() {
		lengthOfData=getLengthOfData();
		if(numPVS==-1) {
			try {
				return (int)data.length()/lengthOfData;
			} catch(java.lang.ArithmeticException e) {
				return 0;
			}
		} else {
			return numPVS;
		}
	}
	
	// Since this lump doesn't have a set data length, we need to find it.
	public int getLengthOfData() {
		if(lengthOfData!=0) {
			return lengthOfData;
		} // else
		try {
			FileInputStream lump07LengthGrabber=new FileInputStream(data.getParent()+"\\11 - Leaves.hex");
			byte[] lenL07AsByteArray=new byte[4];
			lump07LengthGrabber.skip(100);
			lump07LengthGrabber.read(lenL07AsByteArray);
			int lenL07 = (lenL07AsByteArray[3] << 24) | ((lenL07AsByteArray[2] & 0xff) << 16) | ((lenL07AsByteArray[1] & 0xff) << 8) | (lenL07AsByteArray[0] & 0xff);
			lump07LengthGrabber.close();
			if(lenL07==(int)0xFFFFFFFF) {
				return 0;
			} else {
				return lenL07;
			}
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
			return 0;
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
			return 0;
		}
	}
	
	public byte getVisibilityByte(int i, int j) {
		return PVSes[i][j];
	}
	
	public void setVisibilityByte(int i, int j, byte in) {
		PVSes[i][j]=in;
	}
	
	public byte[] getPVS(int i) {
		return PVSes[i];
	}
	
	public void setPVS(int i, byte[] in) {
		if(in.length!=lengthOfData) {
			System.out.println("WARNING: input PVS is wrong size, PVS remains the same!");
		} else {
			PVSes[i]=in;
		}
	}
	
	public byte[][] getPVSes() {
		return PVSes;
	}
	
	public void setPVSes(byte[][] in) {
		PVSes=in;
		numPVS=in.length;
		if(numPVS==0) {
			lengthOfData=0; // This actually happens when in.length=0, an empty array
		} else {
			lengthOfData=in[0].length;
		}
	}
}