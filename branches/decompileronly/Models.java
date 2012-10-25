// Models class

// Maintains an array of Models.

import java.io.FileInputStream;
import java.io.File;

public class Models {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int length;
	private Model[] elements;
	
	private int structLength;

	// CONSTRUCTORS
	
	// Accepts a filepath as a String
	public Models(String in, int type) {
		new Models(new File(in), type);
	}
	
	// This one accepts the input file path as a File
	public Models(File in, int type) {
		data=in;
		try {
			FileInputStream fileReader=new FileInputStream(data);
			byte[] temp=new byte[(int)data.length()];
			fileReader.read(temp);
			new Models(temp, type);
			fileReader.close();
		} catch(java.io.FileNotFoundException e) {
			Window.println("ERROR: File "+data.getPath()+" not found!",Window.VERBOSITY_ALWAYS);
		} catch(java.io.IOException e) {
			Window.println("ERROR: File "+data.getPath()+" could not be read, ensure the file is not open in another program",Window.VERBOSITY_ALWAYS);
		}
	}
	
	// Takes a byte array, as if read from a FileInputStream
	public Models(byte[] in, int type) {
		switch(type) {
			case Model.TYPE_QUAKE:
				structLength=64;
				break;
			case Model.TYPE_QUAKE2:
			case Model.TYPE_COD:
				structLength=48;
				break;
			case Model.TYPE_NIGHTFIRE:
				structLength=56;
				break;
			case Model.TYPE_QUAKE3:
				structLength=40;
				break;
			default:
				structLength=0; // This will cause the shit to hit the fan.
		}
		int offset=0;
		length=in.length;
		elements=new Model[in.length/structLength];
		byte[] bytes=new byte[structLength];
		for(int i=0;i<elements.length;i++) {
			for(int j=0;j<structLength;j++) {
				bytes[j]=in[offset+j];
			}
			elements[i]=new Model(bytes, type);
			offset+=structLength;
		}
	}
	
	// METHODS
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public int getLength() {
		return length;
	}
	
	// Returns the number of elements.
	public int length() {
		if(elements.length==0) {
			return length/structLength;
		} else {
			return elements.length;
		}
	}
	
	public Model getElement(int i) {
		return elements[i];
	}
	
	public Model[] getElements() {
		return elements;
	}
}