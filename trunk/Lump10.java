// Lump10 class

// This class simply holds an array of three-byte RGB pixels.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump10 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	public final int R=0;
	public final int G=1;
	public final int B=2;
	
	private File data;
	private int numLightPixels=0;
	// Java is RETARDED when it comes to unsigned numbers. I'm not putting
	// forth the effort into handling these things as unsigned, someone
	// can just port this whole program into C++ instead. Please do.
	private byte[][] pixels; // creates an array of byte[3] arrays
	// TODO: since I bothered giving vertices their own class for nothing but
	// float3s, I should probably do the same for this.
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump10(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numLightPixels=getNumElements();
		pixels=new byte[numLightPixels][3];
		populatePixelList();
	}
	
	// This one accepts the input file path as a File
	public Lump10(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numLightPixels=getNumElements();
		pixels=new byte[numLightPixels][3];
		populatePixelList();
	}
	
	// METHODS
	
	// -populatePixelList()
	// Populates the array with the pixels
	private void populatePixelList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numLightPixels;i++) {
			reader.read(pixels[i]);
		}
		reader.close();
	}
	
	// +add(byte, byte, byte)
	// Adds a pixel as three bytes
	public void add(byte inR, byte inG, byte inB) {
		byte[][] newList=new byte[numLightPixels+1][3];
		for(int i=0;i<numLightPixels;i++) {
			newList[i]=pixels[i];
		}
		newList[numLightPixels][R]=inR;
		newList[numLightPixels][G]=inG;
		newList[numLightPixels][B]=inB;
		numLightPixels++;
		pixels=newList;
	}
	
	// +add(byte[])
	// Adds a pixel as an array of bytes, there should be three
	public void add(byte[] in) {
		byte[][] newList=new byte[numLightPixels+1][3];
		for(int i=0;i<numLightPixels;i++) {
			newList[i]=pixels[i];
		}
		newList[numLightPixels]=in;
		numLightPixels++;
		pixels=newList;
	}
	
	// +add(Lump10)
	// adds every pixel from another lump10 object.
	public void add(Lump10 in) {
		byte[][] newList=new byte[numLightPixels+in.getNumElements()][3];
		for(int i=0;i<numLightPixels;i++) {
			newList[i]=pixels[i];
		}
		for(int i=0;i<in.getNumElements(); i++) {
			newList[i+numLightPixels]=in.getPixel(i);
		}
		numLightPixels=numLightPixels+in.getNumElements();
		pixels=newList;
	}
	
	// save(String)
	// Saves the data to the specified path as the lump.
	public void save(String path) {
		try {
			File newFile=new File(path+"\\10 - Lighting.hex");
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream lightWriter=new FileOutputStream(newFile);
			for(int i=0;i<numLightPixels;i++) {
				lightWriter.write(pixels[i]);
			}
			lightWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("Unknown error saving "+data+", lump probably not saved!");
		}
	}

	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of RGB pixels.
	public int getNumElements() {
		if(numLightPixels==0) {
			return (int)data.length()/3;
		} else {
			return numLightPixels;
		}
	}
	
	public byte[] getPixel(int i) {
		return pixels[i];
	}
	
	public void setPixel(int i, byte[] in) {
		pixels[i]=in;
	}
}
