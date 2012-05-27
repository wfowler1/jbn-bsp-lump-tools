// Lump09 class

// Stores references to each face object in a BSP.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump09 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numFaces=0;
	// There are two types of faces, world and model. The BSP compiler and the game engine
	// fail to make the distinction, instead reading all world faces into model 0, and all
	// model faces are read by models 1-numModels. Model 0 is the world model, dereference
	// all the faces/leaves for it and the world disappears visually but is still physically
	// there.
	private int numWorldFaces=0;
	private int numModelFaces=0;
	private Face[] faces;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump09(String in) {
		data=new File(in);
		try {
			numFaces=getNumElements();
			numWorldFaces=getNumWorldFaces();
			numModelFaces=numFaces-numWorldFaces;
			faces=new Face[numFaces];
			populateFaceList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump09(File in) {
		data=in;
		try {
			numFaces=getNumElements();
			numWorldFaces=getNumWorldFaces();
			numModelFaces=numFaces-numWorldFaces;
			faces=new Face[numFaces];
			populateFaceList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// Accepts an array of Face objects and sets the entire lump to it
	public Lump09(Face[] in) {
		faces=in;
		numFaces=faces.length;
	}
	
	// Accepts an array of Face objects and sets the entire lump to it, and allows specification of world vs. model faces
	public Lump09(Face[] in, int newNumWorldFaces, int newNumModelFaces) {
		faces=in;
		numWorldFaces=newNumWorldFaces;
		numModelFaces=newNumModelFaces;
		numFaces=faces.length;
	}
	
	public Lump09(byte[] in, int numWorldFaces) {
		int offset=0;
		numFaces=in.length/48;
		faces=new Face[numFaces];
		for(int i=0;i<numFaces;i++) {
			byte[] bytes=new byte[48];
			for(int j=0;j<48;j++) {
				bytes[j]=in[offset+j];
			}
			faces[i]=new Face(bytes);
			offset+=48;
		}
		this.numWorldFaces=numWorldFaces;
		this.numModelFaces=numFaces-numWorldFaces;
	}
	
	// METHODS
	
	// -populateFaceList()
	// Creates an array of all the faces in the lump using the instance data.
	private void populateFaceList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numFaces;i++) {
			byte[] datain=new byte[48];
			reader.read(datain);
			faces[i]=new Face(datain);
		}
		reader.close();
	}
	
	// add(Face)
	// Adds a face which is already a Face object. Easiest to do.
	public void add(Face in) {
		Face[] newList=new Face[numFaces+1];
		for(int i=0;i<numFaces;i++) {
			newList[i]=faces[i];
		}
		newList[numFaces]=in;
		numFaces++;
		faces=newList;
	}
	
	// add (int int int int int int int int int int int int)
	// Adds a face defined by data alone. Still easy.
	public void add(int inPlane, int inVert, int inNumVerts, int inMeshs, int inNumMeshs, int inType,
	                int inTexture, int inMaterial, int inTexStyle, int inUnk, int inLgtStyles, int inLgtMaps) {
		add(new Face(inPlane, inVert, inNumVerts, inMeshs, inNumMeshs, inType, inTexture, inMaterial, inTexStyle, inUnk, inLgtStyles, inLgtMaps));
	}
	
	// save(String)
	// Saves the lump to the specified path.
	public void save(String path) {
		File newFile=new File(path+"\\09 - Faces.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream faceWriter=new FileOutputStream(newFile);
			byte[] data=toByteArray();
			faceWriter.write(data);
			faceWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	// setAllToUnlit()
	// Sets lightmap styles to 0 and lightmaps to 0, as if compiling skipped RAD.
	// Effectively destroys lighting and makes everything fullbright.
	// Only use as a last resort, it's essentially impossible to get this
	// data back. I mostly wrote this to test the BSP combining method,
	// since lightmaps were causing complications.
	//
	// The one upside of this is Lump10 can be completely empty.
	public void setAllToUnlit() {
		for(int i=0;i<numFaces;i++) {
			faces[i].setLgtMaps(0);
			faces[i].setLgtStyles(0);
		}
	}
	
	public byte[] toByteArray() {
		byte[] data=new byte[numFaces*48];
		for(int i=0;i<numFaces;i++) {
			// This is MUCH faster than using DataOutputStream
			int out=faces[i].getPlane();
			data[(i*48)+3]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+2]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+1]=(byte)((out >> 8) & 0xFF);
			data[i*48]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getVert();
			data[(i*48)+7]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+6]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+5]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+4]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getNumVerts();
			data[(i*48)+11]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+10]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+9]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+8]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getMeshs();
			data[(i*48)+15]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+14]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+13]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+12]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getNumMeshs();
			data[(i*48)+19]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+18]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+17]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+16]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getType();
			data[(i*48)+23]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+22]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+21]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+20]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getTexture();
			data[(i*48)+27]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+26]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+25]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+24]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getMaterial();
			data[(i*48)+31]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+30]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+29]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+28]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getTexStyle();
			data[(i*48)+35]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+34]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+33]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+32]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getUnknown();
			data[(i*48)+39]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+38]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+37]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+36]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getLgtStyles();
			data[(i*48)+43]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+42]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+41]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+40]=(byte)((out >> 0) & 0xFF);
			out=faces[i].getLgtMaps();
			data[(i*48)+47]=(byte)((out >> 24) & 0xFF);
			data[(i*48)+46]=(byte)((out >> 16) & 0xFF);
			data[(i*48)+45]=(byte)((out >> 8) & 0xFF);
			data[(i*48)+44]=(byte)((out >> 0) & 0xFF);
		}
		return data;
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return numFaces*48;
	}
	
	// Returns the number of faces.
	public int getNumElements() {
		if(numFaces==0) {
			return (int)data.length()/48;
		} else {
			return numFaces;
		}
	}
	
	// The only way to separate world faces from model faces is to check lump14.
	// One limitation: This depends on the world faces starting from face 0, as
	// done by all compilers. This could complicate things with manually added
	// faces.
	public int getNumWorldFaces() {
		if(numWorldFaces!=0) {
			return numWorldFaces;
		} // else
		try {
			FileInputStream numWorldFaceGrabber=new FileInputStream(data.getParent()+"\\14 - Models.hex");
			byte[] numWorldFacesAsByteArray=new byte[4];
			numWorldFaceGrabber.skip(52);
			numWorldFaceGrabber.read(numWorldFacesAsByteArray);
			int numWF = numWorldFacesAsByteArray[0] + numWorldFacesAsByteArray[1]*256 + numWorldFacesAsByteArray[2]*65536 + numWorldFacesAsByteArray[3]*16777216;
			numWorldFaceGrabber.close();
			return numWF;
		} catch(java.io.IOException e) { // If this is thrown, there's a problem accessing the models file. There's no other way, so set it to SOMETHING valid.
			System.out.println("Warning: Problem accessing models lump, unknown number of world/model faces!");
			return numFaces;
		}
	}
	
	public int getNumModelFaces() {
		return numModelFaces;
	}
	
	// Set number of world or model faces. This is useful if a new Lump09 object was created,
	// since it's new there's no way to know unless explicitly stated, since there's no corresponding
	// Lump 14 file to reference.
	public void setNumWorldFaces(int in) {
		numWorldFaces=in;
	}
	
	public void setNumModelFaces(int in) {
		numModelFaces=in;
	}
	
	public Face getFace(int i) {
		return faces[i];
	}
	
	public Face[] getFaces() {
		return faces;
	}
}
