// Lump01 class

// This class holds references to an array of Plane classes which
// hold the data for all the planes.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump01 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numPlns=0;
	private Plane[] planes;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump01(String in) {
		data=new File(in);
		try {
			numPlns=getNumElements();
			planes=new Plane[numPlns];
			populatePlaneList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump01(File in) {
		data=in;
		try {
			numPlns=getNumElements();
			planes=new Plane[numPlns];
			populatePlaneList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// METHODS
	
	// -populatePlaneList()
	// Parses all data into an array of Plane.
	private void populatePlaneList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numPlns;i++) {
				byte[] datain=new byte[20];
				reader.read(datain);
				planes[i]=new Plane(datain);
			}
		} catch(InvalidPlaneException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last plane.");
		}
		reader.close();
	}
	
	// delete(int)
	// Deletes the plane at the passed index and moves all subsequent planes to
	// a new index to compensate. This has bigger ramifications than it seems
	// at first, since all references to planes must compensate for this change.
	public void delete(int index) {
		Plane[] newList=new Plane[numPlns-1];
		for(int i=0;i<numPlns-1;i++) {
			if(i<index) {
				newList[i]=planes[i];
			} else {
				newList[i]=planes[i+1];
			}
		}
		numPlns-=1;
		planes=newList;
	}
	
	// add(Plane)
	// Adds a plane which is already a Plane object. Easiest to do.
	public void add(Plane in) {
		Plane[] newList=new Plane[numPlns+1];
		for(int i=0;i<numPlns;i++) {
			newList[i]=planes[i];
		}
		newList[numPlns]=in;
		numPlns++;
		planes=newList;
	}
	
	// add (float float float float int)
	// Adds a plane defined by data alone. Still easy.
	public void add(float inx, float iny, float inz, float inDist, int inType) {
		add(new Plane(inx, iny, inz, inDist, inType));
	}
	
	// add(Lump01)
	// Adds every plane in another Lump01 object. This is actually much easier
	// than most other lumps since this one doesn't reference another lump.
	public void add(Lump01 in) {
		Plane[] newList=new Plane[numPlns+in.getNumElements()];
		for(int i=0;i<numPlns;i++) {
			newList[i]=planes[i];
		}
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+numPlns]=in.getPlane(i);
		}
		numPlns=numPlns+in.getNumElements();
		planes=newList;
	}
	
	// Save(String)
	// Saves the lump to the specified path.
	public void save(String path) {
		File newFile=new File(path+"\\01 - Planes.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream planeWriter=new FileOutputStream(newFile);
			byte[] data=new byte[numPlns*20];
			for(int i=0;i<numPlns;i++) {
				// This is MUCH FASTER than using a DataOutputStream
				int out=Float.floatToRawIntBits(planes[i].getX());
				data[(i*20)+3]=(byte)((out >> 24) & 0xFF);
				data[(i*20)+2]=(byte)((out >> 16) & 0xFF);
				data[(i*20)+1]=(byte)((out >> 8) & 0xFF);
				data[i*20]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(planes[i].getY());
				data[(i*20)+7]=(byte)((out >> 24) & 0xFF);
				data[(i*20)+6]=(byte)((out >> 16) & 0xFF);
				data[(i*20)+5]=(byte)((out >> 8) & 0xFF);
				data[(i*20)+4]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(planes[i].getZ());
				data[(i*20)+11]=(byte)((out >> 24) & 0xFF);
				data[(i*20)+10]=(byte)((out >> 16) & 0xFF);
				data[(i*20)+9]=(byte)((out >> 8) & 0xFF);
				data[(i*20)+8]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(planes[i].getDist());
				data[(i*20)+15]=(byte)((out >> 24) & 0xFF);
				data[(i*20)+14]=(byte)((out >> 16) & 0xFF);
				data[(i*20)+13]=(byte)((out >> 8) & 0xFF);
				data[(i*20)+12]=(byte)((out >> 0) & 0xFF);
				out=planes[i].getType();
				data[(i*20)+19]=(byte)((out >> 24) & 0xFF);
				data[(i*20)+18]=(byte)((out >> 16) & 0xFF);
				data[(i*20)+17]=(byte)((out >> 8) & 0xFF);
				data[(i*20)+16]=(byte)((out >> 0) & 0xFF);
			}
			planeWriter.write(data);
			planeWriter.close();
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
	
	// Returns the number of planes.
	public int getNumElements() {
		if(numPlns==0) {
			return (int)data.length()/20;
		} else {
			return numPlns;
		}
	}
	
	public Plane getPlane(int i) {
		return planes[i];
	}
	
	public Plane[] getPlanes() {
		return planes;
	}
}