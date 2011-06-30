// Lump17 class

// Holds the information for texture scaling and alignment.
// Referenced only by faces. The data contained here could
// potentially be recycled.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump17 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numTxmatxs=0; // I really don't know what to call this lump
	private TexMatrix[] texturematrix;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump17(String in) {
		data=new File(in);
		try {
			numTxmatxs=getNumElements();
			texturematrix=new TexMatrix[numTxmatxs];
			populateTextureMatrixList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump17(File in) {
		data=in;
		try {
			numTxmatxs=getNumElements();
			texturematrix=new TexMatrix[numTxmatxs];
			populateTextureMatrixList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// METHODS
	
	// -populateTextureMatrixList()
	// If you don't know what this does by now then look at
	// the other lump classes.
	private void populateTextureMatrixList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numTxmatxs;i++) {
				byte[] datain=new byte[32];
				reader.read(datain);
				texturematrix[i]=new TexMatrix(datain);
			}
			reader.close();
		} catch(InvalidTextureMatrixException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last texture matrix.");
		}
	}
	
	// add(TexMatrix)
	// Adds a texture matrix which is already a TexMatrix object. Easiest to do.
	public void add(TexMatrix in) {
		TexMatrix[] newList=new TexMatrix[numTxmatxs+1];
		for(int i=0;i<numTxmatxs;i++) {
			newList[i]=texturematrix[i];
		}
		newList[numTxmatxs]=in;
		numTxmatxs++;
		texturematrix=newList;
	}
	
	// add (float float float float float float float float)
	// Adds a texture matrix defined by data alone. Still easy.
	public void add(float inUX, float inUY, float inUZ, float inUS, float inVX, float inVY, float inVZ, float inVS) {
		add(new TexMatrix(inUX, inUY, inUZ, inUS, inVX, inVY, inVZ, inVS));
	}
	
	// add(Lump17)
	// Adds every texture matrix in another Lump17 object.
	public void add(Lump17 in) {
		TexMatrix[] newList=new TexMatrix[numTxmatxs+in.getNumElements()];
		for(int i=0;i<numTxmatxs;i++) {
			newList[i]=texturematrix[i];
		}
		
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+numTxmatxs]=in.getTexMatrix(i);
		}
		numTxmatxs=numTxmatxs+in.getNumElements();
		texturematrix=newList;
	}

	// save(String)
	// Saves the lump to the specified path.
	public void save(String path) {
		File newFile=new File(path+"\\17 - Texmatrix.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream texmatrixWriter=new FileOutputStream(newFile);
			for(int i=0;i<numTxmatxs;i++) {
				int out=Float.floatToRawIntBits(texturematrix[i].getUAxisX());
				// This is MUCH faster than using DataOutputStream.
				byte[] output={(byte)((out >> 0) & 0xFF), (byte)((out >> 8) & 0xFF),
				               (byte)((out >> 16) & 0xFF), (byte)((out >> 24) & 0xFF)};
				texmatrixWriter.write(output);
				out=Float.floatToRawIntBits(texturematrix[i].getUAxisY());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				texmatrixWriter.write(output);
				out=Float.floatToRawIntBits(texturematrix[i].getUAxisZ());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				texmatrixWriter.write(output);
				out=Float.floatToRawIntBits(texturematrix[i].getUShift());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				texmatrixWriter.write(output);
				out=Float.floatToRawIntBits(texturematrix[i].getVAxisX());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				texmatrixWriter.write(output);
				out=Float.floatToRawIntBits(texturematrix[i].getVAxisY());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				texmatrixWriter.write(output);
				out=Float.floatToRawIntBits(texturematrix[i].getVAxisZ());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				texmatrixWriter.write(output);
				out=Float.floatToRawIntBits(texturematrix[i].getVShift());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				texmatrixWriter.write(output);
			}
			texmatrixWriter.close();
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
	
	// Returns the number of texture scales.
	public int getNumElements() {
		if(numTxmatxs==0) {
			return (int)data.length()/32;
		} else {
			return numTxmatxs;
		}
	}
	
	public TexMatrix getTexMatrix(int i) {
		return texturematrix[i];
	}
	
	public TexMatrix[] getTexMatrices() {
		return texturematrix;
	}
	
	public void setTexMatrix(int i, TexMatrix in) {
		texturematrix[i] = in;
	}
}