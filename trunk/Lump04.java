// Lump04 class

// This class holds an array of vertices of the Vector3D class. Really it's an array
// of float3 but that's how it is for consistency's sake.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump04 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	public final int X=0;
	public final int Y=1;
	public final int Z=2;
	
	private File data;
	private int numVerts=0;
	private Vector3D[] vertices;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump04(String in) {
		data=new File(in);
		try {
			numVerts=getNumElements();
			vertices=new Vector3D[numVerts];
			populateVertexList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump04(File in) {
		data=in;
		try {
			numVerts=getNumElements();
			vertices=new Vector3D[numVerts];
			populateVertexList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	public Lump04(byte[] in) {
		int offset=0;
		numVerts=in.length/12;
		vertices=new Vector3D[numVerts];
		for(int i=0;i<numVerts;i++) {
			byte[] vertexBytes=new byte[12];
			for(int j=0;j<12;j++) {
				vertexBytes[j]=in[offset+j];
			}
			vertices[i]=new Vector3D(vertexBytes);
			offset+=12;
		}
	}
	
	// METHODS
	
	// +populateVertexList()
	// Parses all data into an array of Vector3D.
	public void populateVertexList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numVerts;i++) {
			byte[] datain=new byte[12];
			reader.read(datain);
			vertices[i]=new Vector3D(datain);
		}
		reader.close();
	}
	
	// add(Vector3D)
	// Adds a vertex which is already a Vector3D object. Easiest to do.
	public void add(Vector3D in) {
		Vector3D[] newList=new Vector3D[numVerts+1];
		for(int i=0;i<numVerts;i++) {
			newList[i]=vertices[i];
		}
		newList[numVerts]=in;
		numVerts++;
		vertices=newList;
	}
	
	// add (float float float)
	// Adds a vertex defined by data alone. Still easy.
	public void add(float inx, float iny, float inz) {
		add(new Vector3D(inx, iny, inz));
	}
	
	// add(Lump04)
	// Adds every vertex in another Lump04 object. This is actually much easier
	// than most other lumps since this one doesn't reference another lump.
	public void add(Lump04 in) {
		Vector3D[] newList=new Vector3D[numVerts+in.getNumElements()];
		for(int i=0;i<numVerts;i++) {
			newList[i]=vertices[i];
		}
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+numVerts]=in.getVertex(i);
		}
		numVerts=numVerts+in.getNumElements();
		vertices=newList;
	}
	
	// Save(String)
	// Saves the lump to the specified path.
	public void save(String path) {
		File newFile=new File(path+"\\04 - Vertices.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream vertexWriter=new FileOutputStream(newFile);
			byte[] data=new byte[numVerts*12];
			for(int i=0;i<numVerts;i++) {
				// This is MUCH faster than using DataOutputStream
				int out=Float.floatToRawIntBits((float)vertices[i].getX());
				data[(i*12)+3]=(byte)((out >> 24) & 0xFF);
				data[(i*12)+2]=(byte)((out >> 16) & 0xFF);
				data[(i*12)+1]=(byte)((out >> 8) & 0xFF);
				data[i*12]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits((float)vertices[i].getY());
				data[(i*12)+7]=(byte)((out >> 24) & 0xFF);
				data[(i*12)+6]=(byte)((out >> 16) & 0xFF);
				data[(i*12)+5]=(byte)((out >> 8) & 0xFF);
				data[(i*12)+4]=(byte)((out >> 0) & 0xFF);
				out=Float.floatToRawIntBits((float)vertices[i].getZ());
				data[(i*12)+11]=(byte)((out >> 24) & 0xFF);
				data[(i*12)+10]=(byte)((out >> 16) & 0xFF);
				data[(i*12)+9]=(byte)((out >> 8) & 0xFF);
				data[(i*12)+8]=(byte)((out >> 0) & 0xFF);
			}
			vertexWriter.write(data);
			vertexWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	public byte[] toByteArray() {
		byte[] data=new byte[numVerts*12];
		for(int i=0;i<numVerts;i++) {
			// This is MUCH faster than using DataOutputStream
			int out=Float.floatToRawIntBits((float)vertices[i].getX());
			data[(i*12)+3]=(byte)((out >> 24) & 0xFF);
			data[(i*12)+2]=(byte)((out >> 16) & 0xFF);
			data[(i*12)+1]=(byte)((out >> 8) & 0xFF);
			data[i*12]=(byte)((out >> 0) & 0xFF);
			out=Float.floatToRawIntBits((float)vertices[i].getY());
			data[(i*12)+7]=(byte)((out >> 24) & 0xFF);
			data[(i*12)+6]=(byte)((out >> 16) & 0xFF);
			data[(i*12)+5]=(byte)((out >> 8) & 0xFF);
			data[(i*12)+4]=(byte)((out >> 0) & 0xFF);
			out=Float.floatToRawIntBits((float)vertices[i].getZ());
			data[(i*12)+11]=(byte)((out >> 24) & 0xFF);
			data[(i*12)+10]=(byte)((out >> 16) & 0xFF);
			data[(i*12)+9]=(byte)((out >> 8) & 0xFF);
			data[(i*12)+8]=(byte)((out >> 0) & 0xFF);
		}
		return data;
	}
	
	// save()
	// Saves the lump, overwriting the one data was read from
	public void save() {
		save(data.getParent());
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return numVerts*12;
	}
	
	// Returns the number of vertices.
	public int getNumElements() {
		if(numVerts==0) {
			return (int)data.length()/12;
		} else {
			return numVerts;
		}
	}
	
	public Vector3D getVertex(int i) {
		return vertices[i];
	}
	
	public Vector3D[] getVertices() {
		return vertices;
	}
}