// Lump02 class

// This class holds an array of Strings which is a list of textures. These
// do not end in ".PNG" because the game engine reads them with no extension.

import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;

public class Lump02 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numTxts=0;
	private String[] textures;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump02(String in) {
		data=new File(in);
		try {
			numTxts=getNumElements();
			textures=new String[numTxts];
			populateTextureList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump02(File in) {
		data=in;
		try {
			numTxts=getNumElements();
			textures=new String[numTxts];
			populateTextureList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		}
	}
	
	public Lump02(byte[] in) {
		int offset=0;
		numTxts=in.length/64;
		textures=new String[numTxts];
		for(int i=0;i<numTxts;i++) {
			textures[i]=(char)in[offset]+""; // must do this first. Doing += right away adds "null" to the beginning
			for(int j=1;j<64;j++) {
				if(in[offset+j]==0x00) {
					break;
				} // else
				textures[i]+=(char)in[offset+j]+"";
			}
			offset+=64;
		}
	}
	
	// METHODS
	
	// -populateTextureList()
	// Uses the file provided in the instance data to populate an array of String.
	// This array is really a list of textures.
	private void populateTextureList() throws java.io.FileNotFoundException {
		Scanner reader=new Scanner(data);
		reader.useDelimiter((char)0x00+"");
		int current=0;
		while(current<numTxts) {
			textures[current]=reader.next();
			if(!textures[current].equals("")) {
				current++;
			}
		}
	}
	
	// add(String)
	// Adds a texture to the array as a String
	public void add(String in) {
		String[] newList=new String[numTxts+1];
		for(int i=0;i<numTxts;i++) {
			newList[i]=textures[i];
		}
		newList[numTxts]=in;
		numTxts++;
		textures=newList;
	}
	
	// add(Lump02)
	// Adds all textures from another Lump02 object to this one.
	// THERE WILL BE DUPLICATES.
	public void add(Lump02 in) {
		String[] newList=new String[numTxts+in.getNumElements()];
		for(int i=0;i<numTxts;i++) {
			newList[i]=textures[i];
		}
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+numTxts]=in.getTexture(i);
		}
		numTxts=numTxts+in.getNumElements();
		textures=newList;
	}
	
	// Save(String)
	// Saves the lump to the specified path.
	// This method is fast enough for the textures lump, but generally speaking, handling
	// file I/O with Strings is a bad idea.
	public void save(String path) {
		File newFile=new File(path+"\\02 - Textures.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			PrintWriter textureWriter=new PrintWriter(newFile);
			for(int i=0;i<numTxts;i++) {
				String out=textures[i];
				while(out.length()<64) {
					out+=(char)0x00+""; // Pad the output to 64 bytes
				}
				textureWriter.print(out);
			}
			textureWriter.close();
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
		byte[] out=new byte[numTxts*64];
		for(int i=0;i<numTxts;i++) {
			String tex=textures[i];
			while(tex.length()<64) {
				tex+=(char)0x00+""; // Pad the output to 64 bytes
			}
			for(int j=0;j<64;j++) {
				out[(i*64)+j]=(byte)tex.charAt(j);
			}
		}
		return out;
	}
	
	// Deletes a texture from the array by index
	public void delete(int index) {
		String[] newList=new String[numTxts-1];
		for(int i=0;i<numTxts-1;i++) {
			if(i<index) {
				newList[i]=textures[i];
			} else {
				newList[i]=textures[i+1];
			}
		}
		numTxts--;
		textures=newList;
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return numTxts*64;
	}
	
	// Returns the number of textures.
	public int getNumElements() {
		if(numTxts==0) {
			return (int)data.length()/64;
		} else {
			return numTxts;
		}
	}
	
	// Returns the texture as a String
	public String getTexture(int i) {
		return textures[i];
	}
	
	public String[] getTextures() {
		return textures;
	}
	
	public void setTexture(int i, String in) {
		textures[i]=in;
	}
}