// Lump03 class

// This class holds an array of Strings which is a list of materials. These
// do not end in ".RMT" because the game engine reads them with no extension.

import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;

public class Lump03 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numMtrls=0;
	private String[] materials;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump03(String in) {
		data=new File(in);
		try {
			numMtrls=getNumElements();
			materials=new String[numMtrls];
			populateMaterialList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump03(File in) {
		data=in;
		try {
			numMtrls=getNumElements();
			materials=new String[numMtrls];
			populateMaterialList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		}
	}
	
	public Lump03(byte[] in) {
		int offset=0;
		numMtrls=in.length/64;
		materials=new String[numMtrls];
		for(int i=0;i<numMtrls;i++) {
			materials[i]=(char)in[offset]+""; // must do this first. Doing += right away adds "null" to the beginning
			for(int j=1;j<64;j++) {
				if(in[offset+j]==0x00) {
					break;
				} // else
				materials[i]+=(char)in[offset+j]+"";
			}
			offset+=64;
		}
	}
	
	// METHODS
	
	// -populateMaterialList()
	// Uses the file defined in the instance data to populate a list of materials.
	private void populateMaterialList() throws java.io.FileNotFoundException {
		Scanner reader=new Scanner(data);
		reader.useDelimiter((char)0x00+"");
		int current=0;
		while(current<numMtrls) {
			materials[current]=reader.next();
			if(!materials[current].equals("")) {
				current++;
			}
		}
	}
	
	// add(String)
	// Adds a texture to the array as a String
	public void add(String in) {
		String[] newList=new String[numMtrls+1];
		for(int i=0;i<numMtrls;i++) {
			newList[i]=materials[i];
		}
		newList[numMtrls]=in;
		numMtrls++;
		materials=newList;
	}
	
	// add(Lump03)
	// Adds all materials from another Lump03 object to this one.
	// THERE WILL BE DUPLICATES.
	public void add(Lump03 in) {
		String[] newList=new String[numMtrls+in.getNumElements()];
		for(int i=0;i<numMtrls;i++) {
			newList[i]=materials[i];
		}
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+numMtrls]=in.getMaterial(i);
		}
		numMtrls=numMtrls+in.getNumElements();
		materials=newList;
	}
	
	// Save(String)
	// Saves the lump to the specified path.
	// This method is fast enough for the materials lump, but generally speaking, handling
	// file I/O with Strings is a bad idea.
	public void save(String path) {
		File newFile=new File(path+"\\03 - Materials.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			PrintWriter materialWriter=new PrintWriter(newFile);
			for(int i=0;i<numMtrls;i++) {
				String out=materials[i];
				while(out.length()<64) {
					out+=(char)0x00+""; // Pad the output to 64 bytes
				}
				materialWriter.print(out);
			}
			materialWriter.close();
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
		byte[] out=new byte[numMtrls*64];
		for(int i=0;i<numMtrls;i++) {
			String mat=materials[i];
			while(mat.length()<64) {
				mat+=(char)0x00+""; // Pad the output to 64 bytes
			}
			for(int j=0;j<64;j++) {
				out[(i*64)+j]=(byte)mat.charAt(j);
			}
		}
		return out;
	}
	
	// Deletes a material from the array by index
	public void delete(int index) {
		String[] newList=new String[numMtrls-1];
		for(int i=0;i<numMtrls-1;i++) {
			if(i<index) {
				newList[i]=materials[i];
			} else {
				newList[i]=materials[i+1];
			}
		}
		numMtrls--;
		materials=newList;
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return numMtrls*64;
	}
	
	// Returns the number of materials.
	public int getNumElements() {
		if(numMtrls==0) {
			return (int)data.length()/64;
		} else {
			return numMtrls;
		}
	}
	
	public String getMaterial(int i) {
		return materials[i];
	}
	
	public String[] getMaterials() {
		return materials;
	}
	
	public void setMaterial(int i, String in) {
		materials[i]=in;
	}
}