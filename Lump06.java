// Lump06 class

// This lump is really a list of ints. These ints are indices to a specific part
// of the lump04 vertices. It's all handled by lump09.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump06 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numMeshes=0;
	private int[] meshes;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump06(String in) {
		data=new File(in);
		try {
			numMeshes=getNumElements();
			meshes=new int[numMeshes];
			populateMeshList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump06(File in) {
		data=in;
		try {
			numMeshes=getNumElements();
			meshes=new int[numMeshes];
			populateMeshList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
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
	public void save(String path) {
		File newFile=new File(path+"\\06 - Indices.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream meshWriter=new FileOutputStream(newFile);
			for(int i=0;i<numMeshes;i++) {
				// This is MUCH faster than using DataOutputStream
				byte[] output={(byte)((meshes[i] >> 0) & 0xFF), (byte)((meshes[i] >> 8) & 0xFF), (byte)((meshes[i] >> 16) & 0xFF), (byte)((meshes[i] >> 24) & 0xFF)};
				meshWriter.write(output);
			}
			meshWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	// save()
	// Saves the lump, overwriting the one data was read from
	public void save() {
		save(data.getParent());
	}
	
	// delete(int)
	// Deletes the mesh at the passed index
	public void delete(int index) {
		int[] newList=new int[numMeshes-1];
		for(int i=0;i<numMeshes-1;i++) {
			if(i<index) {
				newList[i]=meshes[i];
			} else {
				newList[i]=meshes[i+1];
			}
		}
		meshes=newList;
		numMeshes-=1;
	}
	
	// delete(int, int)
	// Deletes an amount of meshes starting at the mesh at the passed index
	public void delete(int index, int amount) {
		int[] newList=new int[numMeshes-amount];
		for(int i=0;i<numMeshes-amount;i++) {
			if(i<index) {
				newList[i]=meshes[i];
			} else {
				newList[i]=meshes[i+amount];
			}
		}
		meshes=newList;
		numMeshes-=amount;
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
	
	public int[] getMeshes() {
		return meshes;
	}
	
	public void setMesh(int i, int in) {
		meshes[i]=in;
	}
	
	public void setMeshes(int[] in) {
		meshes=in;
		numMeshes=in.length;
	}
}
