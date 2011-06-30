// Lump06 class

// This lump is really a list of ints. These ints are indices to a specific part
// of the lump04 vertices. It's all handled by lump09.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.*;

public class Lump06 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numMeshes=0;
	private int[] meshes;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump06(String in) throws java.io.IOException, java.io.IOException {
		data=new File(in);
		numMeshes=getNumElements();
		meshes=new int[numMeshes];
		populateMeshList();
	}
	
	// This one accepts the input file path as a File
	public Lump06(File in) throws java.io.IOException, java.io.IOException {
		data=in;
		numMeshes=getNumElements();
		meshes=new int[numMeshes];
		populateMeshList();
	}
	
	// METHODS
	
	// -populateMeshList()
	// Populates a list of meshes, which are really indices to a specific set of
	// vertices determined by lump09.
	private void populateMeshList() throws java.io.IOException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		byte[] in=new byte[4];
		for(int i=0;i<numMeshes;i++) {
			reader.read(in);
			meshes[i]=(in[3] << 24) | ((in[2] & 0xff) << 16) | ((in[1] & 0xff) << 8) | (in[0] & 0xff);
		}
		reader.close();
	}
	
	// add(int)
	// Adds a single mesh to the lump. Given the complexity of
	// the way this lump is handled this method seems stupid really.
	public void add(int in) {
		int[] newList = new int[numMeshes+1];
		for(int i=0;i<numMeshes;i++) {
			newList[i]=meshes[i];
		}
		newList[numMeshes]=in;
		numMeshes++;
		meshes=newList;
	}
	
	// add(Lump06)
	// Adds the data from another Lump06 object to this one.
	// Even though the numbers are indices into Lump04, these numbers are not adjusted
	// to reflect that, since the actual referencing of Lump04 is done in Lump09.
	public void add(Lump06 in) {
		int[] newList=new int[numMeshes+in.getNumElements()];
		for(int i=0;i<numMeshes;i++) {
			newList[i]=meshes[i];
		}
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+numMeshes]=in.getMesh(i);
		}
		numMeshes=numMeshes+in.getNumElements();
		meshes=newList;
	}
	
	// Save(String)
	// Saves the lump to the specified path.
	// TODO: FIND A FASTER METHOD FOR DOING THIS, DataOutputStream is SLOW
	public void save(String path) {
		try {
			File newFile=new File(path+"\\06 - Indices.hex");
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream meshFile=new FileOutputStream(newFile);
			DataOutputStream meshWriter=new DataOutputStream(meshFile);
			for(int i=0;i<numMeshes;i++) {
				meshWriter.writeInt(meshes[i]);
			}
			meshWriter.close();
			meshFile.close();
		} catch(java.io.IOException e) {
			System.out.println("Unknown error saving meshes, lump probably not saved!");
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
	
	// Returns the number of meshes.
	public int getNumElements() {
		if(numMeshes==0) {
			return (int)data.length()/4;
		} else {
			return numMeshes;
		}
	}
	
	public int getMesh(int i) {
		return meshes[i];
	}
	
	public int[] getMesh() {
		return meshes;
	}
}