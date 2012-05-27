// Lump10 class

// This class holds an array of pixels of the Pixel class. Really it's an array
// of byte3.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump10 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	public final int R=0;
	public final int G=1;
	public final int B=2;
	
	private File data;
	private int numLightPixels=0;
	private Pixel[] pixels;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump10(String in) {
		data=new File(in);
		try {
			numLightPixels=getNumElements();
			pixels=new Pixel[numLightPixels];
			populatePixelList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump10(File in) {
		data=in;
		try {
			numLightPixels=getNumElements();
			pixels=new Pixel[numLightPixels];
			populatePixelList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	public Lump10(byte[] in) {
		int offset=0;
		numLightPixels=in.length/3;
		pixels=new Pixel[numLightPixels];
		for(int i=0;i<numLightPixels;i++) {
			byte[] bytes=new byte[3];
			for(int j=0;j<3;j++) {
				bytes[j]=in[offset+j];
			}
			pixels[i]=new Pixel(bytes);
			offset+=3;
		}
	}
	
	// METHODS
	
	// +populatePixelList()
	// Parses all data into an array of Pixel.
	public void populatePixelList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numLightPixels;i++) {
			byte[] datain=new byte[3];
			reader.read(datain);
			pixels[i]=new Pixel(datain);
		}
		reader.close();
	}
	
	// add(Pixel)
	// Adds a pixel which is already a Pixel object. Easiest to do.
	public void add(Pixel in) {
		Pixel[] newList=new Pixel[numLightPixels+1];
		for(int i=0;i<numLightPixels;i++) {
			newList[i]=pixels[i];
		}
		newList[numLightPixels]=in;
		numLightPixels++;
		pixels=newList;
	}
	
	// add (byte byte byte)
	// Adds a pixel defined by data alone. Still easy.
	public void add(byte inR, byte inG, byte inB) {
		add(new Pixel(inR, inG, inB));
	}
	
	// add(Lump10)
	// Adds every pixel in another Lump10 object. This is actually much easier
	// than most other lumps since this one doesn't reference another lump.
	public void add(Lump10 in) {
		Pixel[] newList=new Pixel[numLightPixels+in.getNumElements()];
		for(int i=0;i<numLightPixels;i++) {
			newList[i]=pixels[i];
		}
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+numLightPixels]=in.getPixel(i);
		}
		numLightPixels=numLightPixels+in.getNumElements();
		pixels=newList;
	}
	
	// delAllPixels()
	// Replaces the Pixel array with an empty one, effectively deleting the list
	public void delAllPixels() {
		Pixel[] newList=new Pixel[0];
		pixels=newList;
		numLightPixels=0;
	}
	
	// Save(String)
	// Saves the lump to the specified path.
	// TODO: change this to use a buffer of maybe 1KB or something, it is faster to write
	// a few large chunks to the hard drive than a ton of small chunks.
	public void save(String path) {
		File newFile=new File(path+"\\10 - Lighting.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream pixelWriter=new FileOutputStream(newFile);
			byte[] data=toByteArray();
			pixelWriter.write(data);
			pixelWriter.close();
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
		byte[] data=new byte[(int)numLightPixels*3];
		for(int i=0;i<numLightPixels;i++) {
			// This is MUCH faster than using DataOutputStream
			data[(i*3)]=pixels[i].getR();
			data[(i*3)+1]=pixels[i].getG();
			data[(i*3)+2]=pixels[i].getB();
		}
		return data;
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return numLightPixels*3;
	}
	
	// Returns the number of pixels.
	public int getNumElements() {
		if(numLightPixels==0) {
			return (int)data.length()/3;
		} else {
			return numLightPixels;
		}
	}
	
	public Pixel getPixel(int i) {
		return pixels[i];
	}
	
	public Pixel[] getPixels() {
		return pixels;
	}
}
