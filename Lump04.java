// Lump04 class

// This class holds an array of vertices of the Vertex class. Really it's an array
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
	private Vertex[] vertices;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump04(String in) {
		data=new File(in);
		try {
			numVerts=getNumElements();
			vertices=new Vertex[numVerts];
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
			vertices=new Vertex[numVerts];
			populateVertexList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// METHODS
	
	// +populateVertexList()
	// Parses all data into an array of Vertex.
	public void populateVertexList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numVerts;i++) {
				byte[] datain=new byte[12];
				reader.read(datain);
				vertices[i]=new Vertex(datain);
			}
		} catch(InvalidVertexException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last vertex.");
		}
		reader.close();
	}
	
	// add(Vertex)
	// Adds a vertex which is already a Vertex object. Easiest to do.
	public void add(Vertex in) {
		Vertex[] newList=new Vertex[numVerts+1];
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
		add(new Vertex(inx, iny, inz));
	}
	
	// add(Lump04)
	// Adds every vertex in another Lump04 object. This is actually much easier
	// than most other lumps since this one doesn't reference another lump.
	public void add(Lump04 in) {
		Vertex[] newList=new Vertex[numVerts+in.getNumElements()];
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
			for(int i=0;i<numVerts;i++) {
				// This is MUCH faster than using DataOutputStream
				int out=Float.floatToRawIntBits(vertices[i].getX());
				byte[] output={(byte)((out >> 0) & 0xFF), (byte)((out >> 8) & 0xFF), (byte)((out >> 16) & 0xFF), (byte)((out >> 24) & 0xFF)};
				vertexWriter.write(output);
				out=Float.floatToRawIntBits(vertices[i].getY());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				vertexWriter.write(output);
				out=Float.floatToRawIntBits(vertices[i].getZ());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				vertexWriter.write(output);
			}
			vertexWriter.close();
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
	
	// Returns the number of vertices.
	public int getNumElements() {
		if(numVerts==0) {
			return (int)data.length()/12;
		} else {
			return numVerts;
		}
	}
	
	public Vertex getVertex(int i) {
		return vertices[i];
	}
	
	public Vertex[] getVertices() {
		return vertices;
	}
}