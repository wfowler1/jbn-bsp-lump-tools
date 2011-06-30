// Lump09 class

// Stores references to each face object in a BSP.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump09 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int numFaces=0;
	private Face[] faces;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump09(String in) {
		data=new File(in);
		try {
			numFaces=getNumElements();
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
			faces=new Face[numFaces];
			populateFaceList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// METHODS
	
	// -populateFaceList()
	// Creates an array of all the faces in the lump using the instance data.
	private void populateFaceList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numFaces;i++) {
				byte[] datain=new byte[48];
				reader.read(datain);
				faces[i]=new Face(datain);
			}
		} catch(InvalidFaceException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last face.");
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
	
	// add(Lump01)
	// Adds every face in another Lump09 object.
	public void add(Lump09 in) {
		Face[] newList=new Face[numFaces+in.getNumElements()];
		File myLump01=new File(data.getParent()+"//01 - Planes.hex");
		int sizeL01=(int)myLump01.length()/20;
		File myLump02=new File(data.getParent()+"//02 - Textures.hex");
		int sizeL02=(int)myLump02.length()/64;
		File myLump03=new File(data.getParent()+"//03 - Materials.hex");
		int sizeL03=(int)myLump03.length()/64;
		File myLump04=new File(data.getParent()+"//04 - Vertices.hex");
		int sizeL04=(int)myLump04.length()/12;
		File myLump06=new File(data.getParent()+"//06 - Indices.hex");
		int sizeL06=(int)myLump06.length()/4;
		File myLump10=new File(data.getParent()+"//10 - Lighting.hex");
		int sizeL10=(int)myLump10.length(); // lighting is referenced by offset
		File myLump17=new File(data.getParent()+"//17 - Texmatrix.hex");
		int sizeL17=(int)myLump17.length()/32;
		for(int i=0;i<numFaces;i++) {
			newList[i]=faces[i];
		}
		for(int i=0;i<in.getNumElements();i++) {
			// All of this MUST be done for every face added from the second map. Therefore,
			// it is important (but not essential) that the user add a smaller map to a bigger
			// one, not the other way around.
			newList[i+numFaces]=in.getFace(i);
			newList[i+numFaces].setPlane(newList[i+numFaces].getPlane()+sizeL01);
			newList[i+numFaces].setVert(newList[i+numFaces].getVert()+sizeL04);
			newList[i+numFaces].setMeshs(newList[i+numFaces].getMeshs()+sizeL06);
			newList[i+numFaces].setTexture(newList[i+numFaces].getTexture()+sizeL02);
			newList[i+numFaces].setMaterial(newList[i+numFaces].getMaterial()+sizeL03);
			newList[i+numFaces].setTexStyle(newList[i+numFaces].getTexStyle()+sizeL17);
			newList[i+numFaces].setLgtMaps(newList[i+numFaces].getLgtMaps()+sizeL10);
		}
		numFaces=numFaces+in.getNumElements();
		faces=newList;
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
			for(int i=0;i<numFaces;i++) {
				// This is MUCH faster than using DataOutputStream
				int out=faces[i].getPlane();
				byte[] output={(byte)((out >> 0) & 0xFF), (byte)((out >> 8) & 0xFF), (byte)((out >> 16) & 0xFF), (byte)((out >> 24) & 0xFF)};
				faceWriter.write(output);
				out=faces[i].getVert();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
				out=faces[i].getNumVerts();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
				out=faces[i].getMeshs();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
				out=faces[i].getNumMeshs();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
				out=faces[i].getType();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
				out=faces[i].getTexture();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
				out=faces[i].getMaterial();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
				out=faces[i].getTexStyle();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
				out=faces[i].getUnknown();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
				out=faces[i].getLgtStyles();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
				out=faces[i].getLgtMaps();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				faceWriter.write(output);
			}
			faceWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	// save()
	// Saves the lump, overwriting the one data was read from
	public void save() {
		save(data.getParent());
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
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of faces.
	public int getNumElements() {
		if(numFaces==0) {
			return (int)data.length()/48;
		} else {
			return numFaces;
		}
	}
	
	public Face getFace(int i) {
		return faces[i];
	}
	
	public Face[] getFaces() {
		return faces;
	}
}
