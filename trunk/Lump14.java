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
	
	// add(Lump14)
	// Adds every model in another Lump14 object, minus the first one. Instead, the mins and
	// maxs from the first one are set to the lower or higher values between the two maps, respectively.
	// This is because the first model is the world, and since two worlds are being combined, their
	// models must be combined as well. The rest of the models are copied with their indices reduced
	// by 1. This is already accounted for in the entities add() method.
	// TODO: Fix the references to Lump11 by reorganizing that lump. This will involve a FUCKTON of work.
	// The same must be done for Lump09, which is a nightmare of covering all your bases with all the lumps
	// which reference that one. It's doable, but extremely tedious and difficult.
	public void add(Lump14 in) {
		Model[] newList=new Model[numModels+in.getNumElements()];
		File myLump09=new File(data.getParent()+"//09 - Faces.hex");
		int sizeL09=(int)myLump09.length()/48;
		File myLump11=new File(data.getParent()+"//11 - Leaves.hex");
		int sizeL11=(int)myLump11.length()/48;
		for(int i=0;i<numModels;i++) {
			newList[i]=models[i];
		}
		
		// mins/maxs fixing
		// Since this is only run once per combining operation, it can be as inefficient as can be. It's O(1).
		if(newList[0].getMinX()>in.getModel(0).getMinX()) {
			newList[0].setMinX(in.getModel(0).getMinX());
		}
		if(newList[0].getMinY()>in.getModel(0).getMinY()) {
			newList[0].setMinY(in.getModel(0).getMinY());
		}
		if(newList[0].getMinZ()>in.getModel(0).getMinZ()) {
			newList[0].setMinZ(in.getModel(0).getMinZ());
		}
		if(newList[0].getMaxX()<in.getModel(0).getMaxX()) {
			newList[0].setMaxX(in.getModel(0).getMaxX());
		}
		if(newList[0].getMaxY()<in.getModel(0).getMaxY()) {
			newList[0].setMaxY(in.getModel(0).getMaxY());
		}
		if(newList[0].getMaxZ()<in.getModel(0).getMaxZ()) {
			newList[0].setMaxZ(in.getModel(0).getMaxZ());
		}
		
		// Leaf/face index/items fixing here
		
		for(int i=1;i<in.getNumElements();i++) { // Start with i=1 since the 0th one is handled above
			newList[i+numModels-1]=in.getModel(i);
			// TODO: These will have to be corrected too after reorganizing. This is the part that's the PITA.
			newList[i+numModels-1].setLeaf(newList[i+numModels-1].getLeaf()+sizeL11);
			newList[i+numModels-1].setFace(newList[i+numModels-1].getFace()+sizeL09);
		}
		numModels=numModels+in.getNumElements()-1;
		models=newList;
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
			for(int i=0;i<numModels;i++) {
				// This is MUCH faster than using DataOutputStream
				int out=Float.floatToRawIntBits(models[i].getMinX());
				byte[] output={(byte)((out >> 0) & 0xFF), (byte)((out >> 8) & 0xFF), (byte)((out >> 16) & 0xFF), (byte)((out >> 24) & 0xFF)};
				modelWriter.write(output);
				out=Float.floatToRawIntBits(models[i].getMinY());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=Float.floatToRawIntBits(models[i].getMinZ());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=Float.floatToRawIntBits(models[i].getMaxX());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=Float.floatToRawIntBits(models[i].getMaxY());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=Float.floatToRawIntBits(models[i].getMaxZ());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=models[i].getUnk0();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=models[i].getUnk1();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=models[i].getUnk2();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=models[i].getUnk3();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=models[i].getLeaf();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=models[i].getNumLeafs();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=models[i].getFace();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
				out=models[i].getNumFaces();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				modelWriter.write(output);
			}
			modelWriter.close();
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