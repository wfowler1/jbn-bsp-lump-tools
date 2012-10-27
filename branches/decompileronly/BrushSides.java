// BrushSides class

// Maintains an array of BrushSides.

import java.io.FileInputStream;
import java.io.File;

public class BrushSides {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int length;
	private BrushSide[] elements;
	
	private int structLength;

	// CONSTRUCTORS
	
	// Accepts a filepath as a String
	public BrushSides(String in, int type) {
		new BrushSides(new File(in), type);
	}
	
	// This one accepts the input file path as a File
	public BrushSides(File in, int type) {
		data=in;
		try {
			FileInputStream fileReader=new FileInputStream(data);
			byte[] temp=new byte[(int)data.length()];
			fileReader.read(temp);
			new BrushSides(temp, type);
			fileReader.close();
		} catch(java.io.FileNotFoundException e) {
			Window.println("ERROR: File "+data.getPath()+" not found!",Window.VERBOSITY_ALWAYS);
		} catch(java.io.IOException e) {
			Window.println("ERROR: File "+data.getPath()+" could not be read, ensure the file is not open in another program",Window.VERBOSITY_ALWAYS);
		}
	}
	
	// Takes a byte array, as if read from a FileInputStream
	public BrushSides(byte[] in, int type) {
		switch(type) {
			case BrushSide.TYPE_QUAKE2:
				structLength=4;
				break;
			case BrushSide.TYPE_COD:
			case BrushSide.TYPE_SIN:
			case BrushSide.TYPE_NIGHTFIRE:
			case BrushSide.TYPE_QUAKE3:
			case BrushSide.TYPE_SOURCE:
				structLength=8;
				break;
			case BrushSide.TYPE_MOHAA:
			case BrushSide.TYPE_RAVEN:
				structLength=12;
				break;
			default:
				structLength=0; // This will cause the shit to hit the fan.
		}
		int offset=0;
		length=in.length;
		elements=new BrushSide[in.length/structLength];
		byte[] bytes=new byte[structLength];
		for(int i=0;i<elements.length;i++) {
			for(int j=0;j<structLength;j++) {
				bytes[j]=in[offset+j];
			}
			elements[i]=new BrushSide(bytes, type);
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
	
	public BrushSide getElement(int i) {
		return elements[i];
	}
	
	public BrushSide[] getElements() {
		return elements;
	}
}