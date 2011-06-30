// Lump14 class

// This class holds an array of Model objects. Each model is a crazy
// amount of data.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump14 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numModels=0;
	private Model[] models;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump14(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numModels=getNumElements();
		models=new Model[numModels];
		populateModelList();
	}
	
	// This one accepts the input file path as a File
	public Lump14(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numModels=getNumElements();
		models=new Model[numModels];
		populateModelList();
	}
	
	// METHODS
	
	// -populateModelList()
	// This method uses the data file in the instance data to
	// populate an array of Model objects which are the models
	// of the map.
	private void populateModelList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numModels;i++) {
			byte[] datain=new byte[56];
			reader.read(datain);
			models[i]=new Model(datain);
		}
		reader.close();
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
}