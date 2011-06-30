// Lump14 class

// This class holds an array of Model objects. Each model is a crazy
// amount of data.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump14 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numModels=0;
	private Model[] models;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump14(String in) {
		data=new File(in);
		try {
			numModels=getNumElements();
			models=new Model[numModels];
			populateModelList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump14(File in) {
		data=in;
		try {
			numModels=getNumElements();
			models=new Model[numModels];
			populateModelList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	public Lump14(Model[] in) {
		models=in;
		numModels=models.length;
	}
	
	// METHODS
	
	// -populateModelList()
	// This method uses the data file in the instance data to
	// populate an array of Model objects which are the models
	// of the map.
	private void populateModelList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numModels;i++) {
				byte[] datain=new byte[56];
				reader.read(datain);
				models[i]=new Model(datain);
			}
			reader.close();
		} catch(InvalidModelException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last model.");
		}
	}
	
	// add(Model)
	// Adds a model which is already a Model object. Easiest to do.
	public void add(Model in) {
		Model[] newList=new Model[numModels+1];
		for(int i=0;i<numModels;i++) {
			newList[i]=models[i];
		}
		newList[numModels]=in;
		numModels++;
		models=newList;
	}
	
	// add (float float float float float float int int int int int int int int)
	// Adds a model defined by data alone. Still easy.
	public void add(float inMinX, float inMinY, float inMinZ, float inMaxX, float inMaxY, float inMaxZ, int inUnk0, int inUnk1, int inUnk2, int inUnk3,
	                int inLeaf, int inNumLeafs, int inFace, int inNumFaces) {
		add(new Model(inMinX, inMinY, inMinZ, inMaxX, inMaxY, inMaxZ, inUnk0, inUnk1, inUnk2, inUnk3, inLeaf, inNumLeafs, inFace, inNumFaces));
	}
	
	// save(String)
	// Saves the lump to the specified path.
	public void save(String path) {
		File newFile=new File(path+"\\14 - Models.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream modelWriter=new FileOutputStream(newFile);
			byte[] data=new byte[numModels*56];
			for(int i=0;i<numModels;i++) {
				// This is MUCH faster than using DataOutputStream
				int out=Float.floatToRawIntBits(models[i].getMinX());
				data[(i*56)+3]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+2]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+1]=(byte)((out >> 8) & 0xFF);
				data[i*56]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(models[i].getMinY());
				data[(i*56)+7]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+6]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+5]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+4]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(models[i].getMinZ());
				data[(i*56)+11]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+10]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+9]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+8]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(models[i].getMaxX());
				data[(i*56)+15]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+14]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+13]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+12]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(models[i].getMaxY());
				data[(i*56)+19]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+18]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+17]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+16]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits(models[i].getMaxZ());
				data[(i*56)+23]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+22]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+21]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+20]=(byte)((out >> 0) & 0xFF);
				out=models[i].getUnk0();
				data[(i*56)+27]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+26]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+25]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+24]=(byte)((out >> 0) & 0xFF);
				out=models[i].getUnk1();
				data[(i*56)+31]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+30]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+29]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+28]=(byte)((out >> 0) & 0xFF);
				out=models[i].getUnk2();
				data[(i*56)+35]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+34]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+33]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+32]=(byte)((out >> 0) & 0xFF);
				out=models[i].getUnk3();
				data[(i*56)+39]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+38]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+37]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+36]=(byte)((out >> 0) & 0xFF);
				out=models[i].getLeaf();
				data[(i*56)+43]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+42]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+41]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+40]=(byte)((out >> 0) & 0xFF);
				out=models[i].getNumLeafs();
				data[(i*56)+47]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+46]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+45]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+44]=(byte)((out >> 0) & 0xFF);
				out=models[i].getFace();
				data[(i*56)+51]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+50]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+49]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+48]=(byte)((out >> 0) & 0xFF);
				out=models[i].getNumFaces();
				data[(i*56)+55]=(byte)((out >> 24) & 0xFF);
				data[(i*56)+54]=(byte)((out >> 16) & 0xFF);
				data[(i*56)+53]=(byte)((out >> 8) & 0xFF);
				data[(i*56)+52]=(byte)((out >> 0) & 0xFF);
			}
			modelWriter.write(data);
			modelWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of models.
	public int getNumElements() {
		if(numModels==0) {
			return (int)data.length()/56;
		} else {
			return numModels;
		}
	}
	
	public Model[] getModels() {
		return models;
	}
	
	public Model getModel(int i) {
		return models[i];
	}
	
	public void setModel(int i, Model in) {
		models[i]=in;
	}
}
