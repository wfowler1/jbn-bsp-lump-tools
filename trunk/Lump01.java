// Lump01 class

// This class holds references to an array of Plane classes which
// hold the data for all the planes.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.File;

public class Lump01 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numPlns=0;
	private Plane[] planes;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump01(String in) throws java.io.FileNotFoundException {
		data=new File(in);
		numPlns=getNumElements();
		planes=new Plane[numPlns];
		populatePlaneList();
	}
	
	// This one accepts the input file path as a File
	public Lump01(File in) throws java.io.FileNotFoundException {
		data=in;
		numPlns=getNumElements();
		planes=new Plane[numPlns];
		populatePlaneList();
	}
	
	// METHODS
	
	// -populatePlaneList()
	// Parses all data into an array of Plane.
	private void populatePlaneList() throws java.io.FileNotFoundException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numPlns;i++) {
				byte[] datain=new byte[20];
				reader.read(datain);
				planes[i]=new Plane(datain);
			}
			reader.close();
		} catch(InvalidPlaneException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last plane.");
		} catch(java.io.IOException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last plane.");
		}
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
	// TODO: FIND A FASTER METHOD FOR DOING THIS, DataOutputStream is SLOW
	public void save(String path) {
		try {
			File newFile=new File(path+"\\01 - Planes.hex");
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream planeFile=new FileOutputStream(newFile);
			DataOutputStream planeWriter=new DataOutputStream(planeFile);
			for(int i=0;i<numPlns;i++) {
				planeWriter.writeFloat(planes[i].getX());
				planeWriter.writeFloat(planes[i].getY());
				planeWriter.writeFloat(planes[i].getZ());
				planeWriter.writeFloat(planes[i].getDist());
				planeWriter.writeInt(planes[i].getType());
			}
			planeWriter.close();
			planeFile.close();
		} catch(java.io.IOException e) {
			System.out.println("Unknown error saving planes, lump probably not saved!");
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