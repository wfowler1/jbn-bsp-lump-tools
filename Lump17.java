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
	
	// delete()
	// Deletes a texture scaling matrix from the list. Faces will need to be corrected.
	public void delete(int index) {
		TexMatrix[] newList=new TexMatrix[numTxmatxs-1];
		for(int i=0;i<numTxmatxs-1;i++) {
			if(i<index) {
				newList[i]=texturematrix[i];
			} else {
				newList[i]=texturematrix[i+1];
			}
		}
		texturematrix=newList;
		numTxmatxs--;
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
			byte[] data=new byte[numTxmatxs*32];
			for(int i=0;i<numTxmatxs;i++) {
				int out=Float.floatToRawIntBits(texturematrix[i].getUAxisX());
				// This is MUCH faster than using DataOutputStream.
				data[(i*32)+3]=(byte)((out >> 24) & 0xFF);
				data[(i*32)+2]=(byte)((out >> 16) & 0xFF);
				data[(i*32)+1]=(byte)((out >> 8) & 0xFF);
				data[i*32]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(texturematrix[i].getUAxisY());
				data[(i*32)+7]=(byte)((out >> 24) & 0xFF);
				data[(i*32)+6]=(byte)((out >> 16) & 0xFF);
				data[(i*32)+5]=(byte)((out >> 8) & 0xFF);
				data[(i*32)+4]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(texturematrix[i].getUAxisZ());
				data[(i*32)+11]=(byte)((out >> 24) & 0xFF);
				data[(i*32)+10]=(byte)((out >> 16) & 0xFF);
				data[(i*32)+9]=(byte)((out >> 8) & 0xFF);
				data[(i*32)+8]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(texturematrix[i].getUShift());
				data[(i*32)+15]=(byte)((out >> 24) & 0xFF);
				data[(i*32)+14]=(byte)((out >> 16) & 0xFF);
				data[(i*32)+13]=(byte)((out >> 8) & 0xFF);
				data[(i*32)+12]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(texturematrix[i].getVAxisX());
				data[(i*32)+19]=(byte)((out >> 24) & 0xFF);
				data[(i*32)+18]=(byte)((out >> 16) & 0xFF);
				data[(i*32)+17]=(byte)((out >> 8) & 0xFF);
				data[(i*32)+16]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(texturematrix[i].getVAxisY());
				data[(i*32)+23]=(byte)((out >> 24) & 0xFF);
				data[(i*32)+22]=(byte)((out >> 16) & 0xFF);
				data[(i*32)+21]=(byte)((out >> 8) & 0xFF);
				data[(i*32)+20]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(texturematrix[i].getVAxisZ());
				data[(i*32)+27]=(byte)((out >> 24) & 0xFF);
				data[(i*32)+26]=(byte)((out >> 16) & 0xFF);
				data[(i*32)+25]=(byte)((out >> 8) & 0xFF);
				data[(i*32)+24]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(texturematrix[i].getVShift());
				data[(i*32)+31]=(byte)((out >> 24) & 0xFF);
				data[(i*32)+30]=(byte)((out >> 16) & 0xFF);
				data[(i*32)+29]=(byte)((out >> 8) & 0xFF);
				data[(i*32)+28]=(byte)((out >> 0) & 0xFF);
			}
			texmatrixWriter.write(data);
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