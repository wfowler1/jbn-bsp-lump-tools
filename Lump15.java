// Lump15 class

// This class holds an array of Brush objects.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump15 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numBrshs=0;
	private Brush[] brushes;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump15(String in) {
		data=new File(in);
		try {
			numBrshs=getNumElements();
			brushes=new Brush[numBrshs];
			populateBrushList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump15(File in) {
		data=in;
		try {
			numBrshs=getNumElements();
			brushes=new Brush[numBrshs];
			populateBrushList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// METHODS
	
	// -populateBrushList()
	// Uses the data file in the instance data to populate the array
	// of Brush objects
	private void populateBrushList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numBrshs;i++) {
				byte[] datain=new byte[12];
				reader.read(datain);
				brushes[i]=new Brush(datain);
			}
			reader.close();
		} catch(InvalidBrushException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last brush.");
		}
	}
	
	// add(Brush)
	// Adds a brush which is already a Brush object. Easiest to do.
	public void add(Brush in) {
		Brush[] newList=new Brush[numBrshs+1];
		for(int i=0;i<numBrshs;i++) {
			newList[i]=brushes[i];
		}
		newList[numBrshs]=in;
		numBrshs++;
		brushes=newList;
	}
	
	// add (int int int)
	// Adds a brush defined by data alone. Still easy.
	public void add(int inAttributes, int inFirstSide, int inNumSides) {
		add(new Brush(inAttributes, inFirstSide, inNumSides));
	}
	
	// add(Lump15)
	// Adds every brush in another Lump15 object.
	public void add(Lump15 in) {
		Brush[] newList=new Brush[numBrshs+in.getNumElements()];
		File myLump16=new File(data.getParent()+"//16 - Brushsides.hex");
		int sizeL16=(int)myLump16.length()/8;
		for(int i=0;i<numBrshs;i++) {
			newList[i]=brushes[i];
		}
		
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+numBrshs]=in.getBrush(i);
			newList[i+numBrshs].setFirstSide(newList[i+numBrshs].getFirstSide()+sizeL16);
		}
		numBrshs=numBrshs+in.getNumElements();
		brushes=newList;
	}

	// save(String)
	// Saves the lump to the specified path.
	public void save(String path) {
		File newFile=new File(path+"\\15 - Brushes.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream brushWriter=new FileOutputStream(newFile);
			byte[] data=new byte[numBrshs*12];
			for(int i=0;i<numBrshs;i++) {
				// This is MUCH faster than using DataOutputStream. But you knew that already.
				data[(i*12)+3]=(byte)((brushes[i].getAttributes() >> 24) & 0xFF);
				data[(i*12)+2]=(byte)((brushes[i].getAttributes() >> 16) & 0xFF);
				data[(i*12)+1]=(byte)((brushes[i].getAttributes() >> 8) & 0xFF);
				data[i*12]=(byte)((brushes[i].getAttributes() >> 0) & 0xFF);
				data[(i*12)+7]=(byte)((brushes[i].getFirstSide() >> 24) & 0xFF);
				data[(i*12)+6]=(byte)((brushes[i].getFirstSide() >> 16) & 0xFF);
				data[(i*12)+5]=(byte)((brushes[i].getFirstSide() >> 8) & 0xFF);
				data[(i*12)+4]=(byte)((brushes[i].getFirstSide() >> 0) & 0xFF);
				data[(i*12)+11]=(byte)((brushes[i].getNumSides() >> 24) & 0xFF);
				data[(i*12)+10]=(byte)((brushes[i].getNumSides() >> 16) & 0xFF);
				data[(i*12)+9]=(byte)((brushes[i].getNumSides() >> 8) & 0xFF);
				data[(i*12)+8]=(byte)((brushes[i].getNumSides() >> 0) & 0xFF);
			}
			brushWriter.write(data);
			brushWriter.close();
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
	
	// Returns the number of brushes.
	public int getNumElements() {
		if(numBrshs==0) {
			return (int)data.length()/12;
		} else {
			return numBrshs;
		}
	}
	
	public Brush getBrush(int i) {
		return brushes[i];
	}
	
	public Brush[] getBrushes() {
		return brushes;
	}
	
	public void setBrush(int i, Brush in) {
		brushes[i]=in;
	}
}